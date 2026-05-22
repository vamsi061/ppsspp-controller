import AppKit
import SwiftUI

class StatusBarController {
    private var statusItem: NSStatusItem
    private var popover: NSPopover
    private var serverManager: ServerManager

    init(serverManager: ServerManager) {
        self.serverManager = serverManager

        statusItem = NSStatusBar.system.statusItem(withLength: NSStatusItem.variableLength)
        popover = NSPopover()

        setupStatusBar()
        setupPopover()
    }

    private func setupStatusBar() {
        if let button = statusItem.button {
            button.image = NSImage(systemSymbolName: "gamecontroller.fill", accessibilityDescription: "Controller")
            button.action = #selector(togglePopover)
            button.target = self
        }

        updateStatusIcon()

        Timer.scheduledTimer(withTimeInterval: 1.0, repeats: true) { [weak self] _ in
            self?.updateStatusIcon()
        }
    }

    private func updateStatusIcon() {
        if let button = statusItem.button {
            let isConnected = serverManager.isConnected
            let imageName = isConnected ? "gamecontroller.fill" : "gamecontroller"
            button.image = NSImage(systemSymbolName: imageName, accessibilityDescription: "Controller")
            button.contentTintColor = isConnected ? .systemGreen : .secondaryLabelColor
        }
    }

    private func setupPopover() {
        popover.contentSize = NSSize(width: 300, height: 250)
        popover.behavior = .transient
        popover.contentViewController = NSHostingController(rootView: StatusView(serverManager: serverManager))
    }

    @objc func togglePopover() {
        if popover.isShown {
            popover.performClose(nil)
        } else if let button = statusItem.button {
            popover.show(relativeTo: button.bounds, of: button, preferredEdge: .minY)
            popover.contentViewController?.view.window?.makeKey()
        }
    }
}