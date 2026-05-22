import Foundation
import CoreGraphics
import ApplicationServices
import AppKit
import OSLog

struct KeyMappingConfig {
    let type: String
    let keyCode: String
    let modifiers: [String]?
}

class InputSimulator {
    private let log = OSLog(subsystem: "com.gamingcontroller.maccontrollerapp", category: "inputsimulator")
    private var mousePosition: CGPoint = .zero
    private var lastLeftStick: (x: Float, y: Float) = (0, 0)
    private var lastRightStick: (x: Float, y: Float) = (0, 0)

    var keyMapping: [String: KeyMappingConfig] = [:]

    private var pressedKeys: Set<CGKeyCode> = []
    private var leftStickLogCount = 0

    private var wPressed = false
    private var sPressed = false
    private var aPressed = false
    private var dPressed = false
    private var leftClickDown = false
    private var rightClickDown = false
    private var iPressed = false
    private var kPressed = false
    private var jPressed = false
    private var lPressed = false

    init() {
        mousePosition = NSEvent.mouseLocation
        setDefaultKeyMapping()
    }

    private func setDefaultKeyMapping() {
        keyMapping = [
            "button_a": KeyMappingConfig(type: "keyboard", keyCode: "z", modifiers: nil),
            "button_b": KeyMappingConfig(type: "keyboard", keyCode: "x", modifiers: nil),
            "button_x": KeyMappingConfig(type: "keyboard", keyCode: "a", modifiers: nil),
            "button_y": KeyMappingConfig(type: "keyboard", keyCode: "s", modifiers: nil),
            "button_l1": KeyMappingConfig(type: "keyboard", keyCode: "q", modifiers: nil),
            "button_r1": KeyMappingConfig(type: "keyboard", keyCode: "e", modifiers: nil),
            "button_start": KeyMappingConfig(type: "keyboard", keyCode: "space", modifiers: nil),
            "button_select": KeyMappingConfig(type: "keyboard", keyCode: "return", modifiers: nil),
            "button_esc": KeyMappingConfig(type: "keyboard", keyCode: "escape", modifiers: nil),
            "left_stick": KeyMappingConfig(type: "keyboard", keyCode: "wasd", modifiers: nil),
            "right_stick": KeyMappingConfig(type: "mouse", keyCode: "mouse", modifiers: nil),
            "dpad_up": KeyMappingConfig(type: "keyboard", keyCode: "up", modifiers: nil),
            "dpad_down": KeyMappingConfig(type: "keyboard", keyCode: "down", modifiers: nil),
            "dpad_left": KeyMappingConfig(type: "keyboard", keyCode: "left", modifiers: nil),
            "dpad_right": KeyMappingConfig(type: "keyboard", keyCode: "right", modifiers: nil)
        ]
    }

    func setKeyMapping(_ mapping: [String: KeyMappingConfig]) {
        keyMapping = mapping
    }

    func getCGKeyCode(from keyName: String) -> CGKeyCode? {
        switch keyName.lowercased() {
        case "a": return 0x00
        case "b": return 0x0B
        case "c": return 0x08
        case "d": return 0x02
        case "e": return 0x0E
        case "f": return 0x03
        case "g": return 0x05
        case "h": return 0x04
        case "i": return 0x22
        case "j": return 0x26
        case "k": return 0x28
        case "l": return 0x25
        case "m": return 0x2E
        case "n": return 0x2D
        case "o": return 0x1F
        case "p": return 0x23
        case "q": return 0x0C
        case "r": return 0x15
        case "s": return 0x01
        case "t": return 0x11
        case "u": return 0x20
        case "v": return 0x09
        case "w": return 0x0D
        case "x": return 0x07
        case "y": return 0x10
        case "z": return 0x06
        case "space": return 0x31
        case "shift": return 0x38
        case "ctrl": return 0x3B
        case "control": return 0x3B
        case "alt": return 0x3A
        case "option": return 0x3A
        case "up": return 0x7E
        case "down": return 0x7D
        case "left": return 0x7B
        case "right": return 0x7C
        case "return": return 0x24
        case "enter": return 0x24
        case "tab": return 0x30
        case "delete": return 0x33
        case "escape": return 0x35
        case "esc": return 0x35
        default: return nil
        }
    }

    func handleLeftStick(x: Float, y: Float) {
        leftStickLogCount += 1
        os_log("LEFT STICK[%d]: x=%.4f y=%.4f I=%d K=%d J=%d L=%d (iP:%d kP:%d jP:%d lP:%d)", log: log, type: .default, leftStickLogCount, x, y, y > 0.15 ? 1 : 0, y < -0.15 ? 1 : 0, x < -0.15 ? 1 : 0, x > 0.15 ? 1 : 0, iPressed ? 1 : 0, kPressed ? 1 : 0, jPressed ? 1 : 0, lPressed ? 1 : 0)
        let threshold: Float = 0.15
        let wantsI = y > threshold
        let wantsK = y < -threshold
        let wantsJ = x < -threshold
        let wantsL = x > threshold
        if wantsI && !iPressed { pressKey(0x22); iPressed = true }
        else if !wantsI && iPressed { releaseKey(0x22); iPressed = false }
        if wantsK && !kPressed { pressKey(0x28); kPressed = true }
        else if !wantsK && kPressed { releaseKey(0x28); kPressed = false }
        if wantsJ && !jPressed { pressKey(0x26); jPressed = true }
        else if !wantsJ && jPressed { releaseKey(0x26); jPressed = false }
        if wantsL && !lPressed { pressKey(0x25); lPressed = true }
        else if !wantsL && lPressed { releaseKey(0x25); lPressed = false }
        lastLeftStick = (x, y)
    }

    func handleRightStick(x: Float, y: Float) {
        let threshold: Float = 0.1
        if abs(x) > threshold || abs(y) > threshold {
            let deltaX = CGFloat(x) * 15
            let deltaY = CGFloat(y) * 15
            mousePosition.x += deltaX
            mousePosition.y -= deltaY
            if let screen = NSScreen.main {
                mousePosition.x = max(0, min(screen.frame.width, mousePosition.x))
                mousePosition.y = max(0, min(screen.frame.height, mousePosition.y))
            }
            moveMouse(to: mousePosition)
        }
        lastRightStick = (x, y)
    }

    func handleButtons(a: Bool, b: Bool, x: Bool, y: Bool) {
        os_log("BUTTONS a=%d b=%d x=%d y=%d", log: log, type: .debug, a, b, x, y)
        handleKeyMapping("button_a", pressed: a)
        handleKeyMapping("button_b", pressed: b)
        handleKeyMapping("button_x", pressed: x)
        handleKeyMapping("button_y", pressed: y)
    }

    func handleShoulders(l1: Bool, r1: Bool) {
        handleKeyMapping("button_l1", pressed: l1)
        handleKeyMapping("button_r1", pressed: r1)
    }

    func handleTriggers(l2: Bool, r2: Bool) {
        if l2 && !leftClickDown { pressKey(0x0C); leftClickDown = true }
        else if !l2 && leftClickDown { releaseKey(0x0C); leftClickDown = false }
        if r2 && !rightClickDown { pressKey(0x0E); rightClickDown = true }
        else if !r2 && rightClickDown { releaseKey(0x0E); rightClickDown = false }
    }

    func handleDpad(up: Bool, down: Bool, left: Bool, right: Bool) {
        os_log("DPAD up=%d down=%d left=%d right=%d", log: log, type: .debug, up, down, left, right)
        handleKeyMapping("dpad_up", pressed: up)
        handleKeyMapping("dpad_down", pressed: down)
        handleKeyMapping("dpad_left", pressed: left)
        handleKeyMapping("dpad_right", pressed: right)
    }

    func handleKeyMapping(_ mappingKey: String, pressed: Bool) {
        guard let config = keyMapping[mappingKey] else { return }
        guard config.type == "keyboard" else { return }
        guard let keyCode = getCGKeyCode(from: config.keyCode) else { return }
        os_log("handleKeyMapping: %{public}@ pressed=%d keyCode=0x%02x", log: log, type: .debug, mappingKey, pressed, keyCode)
        if pressed {
            pressKey(keyCode)
        } else {
            releaseKey(keyCode)
        }
    }

    private func isAccessibilityTrusted() -> Bool {
        let trusted = AXIsProcessTrusted()
        if !trusted {
            os_log("ACCESSIBILITY PERMISSION MISSING - key events will NOT reach applications", log: log, type: .error)
        }
        return trusted
    }

    private func pressKey(_ keyCode: CGKeyCode) {
        os_log("PRESSING KEY: 0x%02x", log: log, type: .default, keyCode)
        guard !pressedKeys.contains(keyCode) else { return }
        guard isAccessibilityTrusted() else { return }
        let source = CGEventSource(stateID: .hidSystemState)
        if let keyDown = CGEvent(keyboardEventSource: source, virtualKey: keyCode, keyDown: true) {
            keyDown.post(tap: .cghidEventTap)
            pressedKeys.insert(keyCode)
        }
    }

    private func releaseKey(_ keyCode: CGKeyCode) {
        guard pressedKeys.contains(keyCode) else { return }
        guard isAccessibilityTrusted() else { return }
        let source = CGEventSource(stateID: .hidSystemState)
        if let keyUp = CGEvent(keyboardEventSource: source, virtualKey: keyCode, keyDown: false) {
            keyUp.post(tap: .cghidEventTap)
            pressedKeys.remove(keyCode)
        }
    }

    private func moveMouse(to position: CGPoint) {
        let source = CGEventSource(stateID: .hidSystemState)
        if let moveEvent = CGEvent(mouseEventSource: source, mouseType: .mouseMoved, mouseCursorPosition: position, mouseButton: .left) {
            moveEvent.post(tap: .cghidEventTap)
        }
    }

    private func mouseDown(button: MouseButton) {
        let source = CGEventSource(stateID: .hidSystemState)
        let eventType: CGEventType = button == .left ? .leftMouseDown : .rightMouseDown
        if let event = CGEvent(mouseEventSource: source, mouseType: eventType, mouseCursorPosition: mousePosition, mouseButton: .left) {
            event.post(tap: .cghidEventTap)
        }
    }

    private func mouseUp(button: MouseButton) {
        let source = CGEventSource(stateID: .hidSystemState)
        let eventType: CGEventType = button == .left ? .leftMouseUp : .rightMouseUp
        if let event = CGEvent(mouseEventSource: source, mouseType: eventType, mouseCursorPosition: mousePosition, mouseButton: .left) {
            event.post(tap: .cghidEventTap)
        }
    }
}

enum MouseButton {
    case left
    case right
}

extension InputSimulator {
    func parseAndSimulate(_ inputData: [String: Any]) {
        os_log("=== PARSE INPUT DATA ===", log: log, type: .default)
        let leftStickX = (inputData["leftStickX"] as? NSNumber)?.floatValue ?? 0
        let leftStickY = (inputData["leftStickY"] as? NSNumber)?.floatValue ?? 0
        handleLeftStick(x: leftStickX, y: leftStickY)
        let rightStickX = (inputData["rightStickX"] as? NSNumber)?.floatValue ?? 0
        let rightStickY = (inputData["rightStickY"] as? NSNumber)?.floatValue ?? 0
        handleRightStick(x: rightStickX, y: rightStickY)
        let buttonA = inputData["buttonA"] as? Bool ?? false
        let buttonB = inputData["buttonB"] as? Bool ?? false
        let buttonX = inputData["buttonX"] as? Bool ?? false
        let buttonY = inputData["buttonY"] as? Bool ?? false
        handleButtons(a: buttonA, b: buttonB, x: buttonX, y: buttonY)
        let buttonSelect = inputData["buttonSelect"] as? Bool ?? false
        let buttonStart = inputData["buttonStart"] as? Bool ?? false
        let buttonEscRaw = inputData["buttonEsc"]
        let buttonEsc = buttonEscRaw as? Bool ?? false
        if buttonEscRaw != nil {
            os_log("=== ESC RAW VALUE: %{public}@ ===", log: log, type: .default, String(describing: buttonEscRaw))
        }
        if buttonEsc {
            os_log("=== ESC BUTTON PRESSED ===", log: log, type: .default)
        }
        os_log("KEYS in inputData: %{public}@", log: log, type: .debug, inputData.keys.sorted().joined(separator: ","))
        handleKeyMapping("button_select", pressed: buttonSelect)
        handleKeyMapping("button_start", pressed: buttonStart)
        handleKeyMapping("button_esc", pressed: buttonEsc)
        let l1 = inputData["l1"] as? Bool ?? false
        let r1 = inputData["r1"] as? Bool ?? false
        handleShoulders(l1: l1, r1: r1)
        let l2 = inputData["l2"] as? Bool ?? false
        let r2 = inputData["r2"] as? Bool ?? false
        handleTriggers(l2: l2, r2: r2)
        let dpadUp = inputData["dpadUp"] as? Bool ?? false
        let dpadDown = inputData["dpadDown"] as? Bool ?? false
        let dpadLeft = inputData["dpadLeft"] as? Bool ?? false
        let dpadRight = inputData["dpadRight"] as? Bool ?? false
        handleDpad(up: dpadUp, down: dpadDown, left: dpadLeft, right: dpadRight)
    }
}
