import AppKit
import SwiftUI
import ApplicationServices
import OSLog

class AppDelegate: NSObject, NSApplicationDelegate {
    var statusBarController: StatusBarController?
    var serverManager: ServerManager?

    func applicationDidFinishLaunching(_ notification: Notification) {
        requestAccessibilityPermission()
        serverManager = ServerManager()
        serverManager?.start()
        statusBarController = StatusBarController(serverManager: serverManager!)
    }

    private func requestAccessibilityPermission() {
        let log = OSLog(subsystem: "com.gamingcontroller.maccontrollerapp", category: "appdelegate")
        let options = [kAXTrustedCheckOptionPrompt.takeRetainedValue() as String: true]
        let trusted = AXIsProcessTrustedWithOptions(options as CFDictionary)
        if trusted {
            os_log("Accessibility permission granted", log: log, type: .default)
        } else {
            os_log("Accessibility permission NOT granted - dialog shown. Grant it in System Settings > Privacy & Security > Accessibility then restart the app", log: log, type: .default)
        }
    }

    func applicationWillTerminate(_ notification: Notification) {
        serverManager?.stop()
    }

    func applicationSupportsSecureRestorableState(_ app: NSApplication) -> Bool {
        return true
    }
}