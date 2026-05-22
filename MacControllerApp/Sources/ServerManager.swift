import Foundation
import Network
import OSLog

class ServerManager: ObservableObject {
    private let log = OSLog(subsystem: "com.gamingcontroller.maccontrollerapp", category: "server")
    @Published var isConnected = false
    @Published var connectedDeviceIP: String = "None"
    @Published var buttonPressCount: Int = 0

    private var tcpListener: NWListener?
    private var discoveryListener: NWListener?
    private var activeConnection: NWConnection?
    private var inputSimulator = InputSimulator()

    private let queue = DispatchQueue(label: "com.gamingcontroller.server")

    init() {
        startDiscoveryService()
        startTcpServer()
    }

    func start() {
        startDiscoveryService()
        startTcpServer()
    }

    func stop() {
        tcpListener?.cancel()
        discoveryListener?.cancel()
        activeConnection?.cancel()
    }

    private func startDiscoveryService() {
        do {
            let params = NWParameters.udp
            params.allowLocalEndpointReuse = true
            discoveryListener = try NWListener(using: params, on: 9000)
            discoveryListener?.stateUpdateHandler = { state in
                if case .failed(let error) = state {
                    print("UDP listener failed: \(error)")
                }
            }
            discoveryListener?.newConnectionHandler = { [weak self] connection in
                self?.handleUdpConnection(connection)
            }
            discoveryListener?.start(queue: queue)
            print("UDP Discovery listener ready on port 9000")
        } catch {
            print("Failed to start UDP listener: \(error)")
        }
    }

    private func handleUdpConnection(_ connection: NWConnection) {
        connection.stateUpdateHandler = { state in
            switch state {
            case .ready:
                self.receiveUdpMessage(on: connection)
            default:
                break
            }
        }
        connection.start(queue: self.queue)
    }

    private func receiveUdpMessage(on connection: NWConnection) {
        connection.receiveMessage { [weak self] data, _, _, error in
            if let data = data, let message = String(data: data, encoding: .utf8) {
                print("Received: \(message)")
                if message.contains("GAMING_CONTROLLER_DISCOVER") {
                    let remoteEndpoint = connection.endpoint
                    self?.sendAnnouncement(to: remoteEndpoint)
                }
            }
            self?.receiveUdpMessage(on: connection)
        }
    }

    private func sendAnnouncement(to endpoint: NWEndpoint) {
        let hostName = Host.current().localizedName ?? "Mac"
        let ip = getLocalIPAddress() ?? "127.0.0.1"

        let response: [String: Any] = [
            "type": "announce",
            "name": hostName,
            "ip": ip,
            "port": 9001
        ]

        if let jsonData = try? JSONSerialization.data(withJSONObject: response),
           let jsonString = String(data: jsonData, encoding: .utf8) {
            let params = NWParameters.udp
            let responseConnection = NWConnection(to: endpoint, using: params)
            responseConnection.stateUpdateHandler = { state in
                if case .failed(let error) = state {
                    print("UDP response connection failed: \(error)")
                }
            }
            responseConnection.start(queue: self.queue)
            responseConnection.send(content: jsonString.data(using: .utf8), completion: .contentProcessed { error in
                if let error = error {
                    print("Failed to send announcement: \(error)")
                } else {
                    print("Announcement sent to \(endpoint)")
                }
                responseConnection.cancel()
            })
        }
    }

    private func startTcpServer() {
        do {
            let params = NWParameters.tcp
            params.allowLocalEndpointReuse = true

            tcpListener = try NWListener(using: params, on: 9001)
            tcpListener?.stateUpdateHandler = { state in
                if case .failed(let error) = state {
                    print("TCP listener failed: \(error)")
                }
            }

            tcpListener?.newConnectionHandler = { [weak self] connection in
                self?.handleTcpConnection(connection)
            }

            tcpListener?.start(queue: queue)
            print("TCP Server listening on port 9001")
        } catch {
            print("Failed to start TCP server: \(error)")
        }
    }

    private func handleTcpConnection(_ connection: NWConnection) {
        print("New connection from: \(connection.endpoint)")

        DispatchQueue.main.async {
            self.connectedDeviceIP = "\(connection.endpoint)"
            self.buttonPressCount = 0
        }

        connection.stateUpdateHandler = { [weak self] state in
            switch state {
            case .ready:
                DispatchQueue.main.async {
                    self?.isConnected = true
                    self?.connectedDeviceIP = "\(connection.endpoint)"
                    self?.buttonPressCount = 0
                }
                self?.receiveTcpData(from: connection)
            case .failed, .cancelled:
                DispatchQueue.main.async {
                    self?.isConnected = false
                    self?.connectedDeviceIP = "None"
                }
            default:
                break
            }
        }

        connection.start(queue: queue)
    }

    private func receiveTcpData(from connection: NWConnection) {
        connection.receive(minimumIncompleteLength: 1, maximumLength: 65536) { [weak self] data, _, isComplete, error in
            if let data = data, !data.isEmpty {
                self?.processControllerData(data)
            }

            if !isComplete {
                self?.receiveTcpData(from: connection)
            }
        }
    }

    private func processControllerData(_ data: Data) {
        guard let jsonString = String(data: data, encoding: .utf8) else {
            os_log("=== FAILED TO DECODE DATA ===", log: log, type: .error)
            return
        }

        os_log("=== RECEIVED %d BYTES ===", log: log, type: .default, data.count)
        os_log("RAW JSON (first 200): %{public}@", log: log, type: .default, String(jsonString.prefix(200)))

        var parsedCount = 0
        let lines = jsonString.components(separatedBy: "\n")
        for line in lines {
            guard !line.isEmpty else { continue }

            if let jsonData = line.data(using: .utf8),
               let json = try? JSONSerialization.jsonObject(with: jsonData) as? [String: Any],
               let type = json["type"] as? String, type == "input",
               let inputData = json["data"] as? [String: Any] {

                parsedCount += 1
                DispatchQueue.main.async {
                    self.buttonPressCount += 1
                }

                inputSimulator.parseAndSimulate(inputData)
            }
        }
        if parsedCount == 0 {
            os_log("=== NO LINES PARSED SUCCESSFULLY (TCP SPLIT?) ===", log: log, type: .error)
        }
    }

    private func getLocalIPAddress() -> String? {
        var address: String?
        var ifaddr: UnsafeMutablePointer<ifaddrs>?

        guard getifaddrs(&ifaddr) == 0 else { return nil }
        guard let firstAddr = ifaddr else { return nil }

        for ifptr in sequence(first: firstAddr, next: { $0.pointee.ifa_next }) {
            let interface = ifptr.pointee
            let addrFamily = interface.ifa_addr.pointee.sa_family

            if addrFamily == UInt8(AF_INET) {
                let name = String(cString: interface.ifa_name)
                if name == "en0" || name == "en1" {
                    var hostname = [CChar](repeating: 0, count: Int(NI_MAXHOST))
                    getnameinfo(interface.ifa_addr, socklen_t(interface.ifa_addr.pointee.sa_len),
                               &hostname, socklen_t(hostname.count),
                               nil, socklen_t(0), NI_NUMERICHOST)
                    address = String(cString: hostname)
                }
            }
        }
        freeifaddrs(ifaddr)
        return address
    }
}