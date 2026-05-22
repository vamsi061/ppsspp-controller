import SwiftUI

struct StatusView: View {
    @ObservedObject var serverManager: ServerManager

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            HStack {
                Image(systemName: serverManager.isConnected ? "checkmark.circle.fill" : "xmark.circle.fill")
                    .foregroundColor(serverManager.isConnected ? .green : .red)
                Text(serverManager.isConnected ? "Connected" : "Disconnected")
                    .font(.headline)
            }

            Divider()

            VStack(alignment: .leading, spacing: 8) {
                Label("Device IP", systemImage: "desktopcomputer")
                Text(serverManager.connectedDeviceIP)
                    .foregroundColor(.secondary)
                    .font(.caption)
            }

            Divider()

            VStack(alignment: .leading, spacing: 8) {
                Label("Button Presses", systemImage: "hand.tap")
                Text("\(serverManager.buttonPressCount)")
                    .foregroundColor(.secondary)
                    .font(.caption)
            }

            Divider()

            VStack(alignment: .leading, spacing: 8) {
                Label("Ports", systemImage: "network")
                Text("TCP: 9001, UDP: 9000")
                    .foregroundColor(.secondary)
                    .font(.caption)
            }

            Spacer()

            Text("Android App: Gaming Controller")
                .font(.caption2)
                .foregroundColor(.secondary)
        }
        .padding()
        .frame(width: 280, height: 250)
    }
}