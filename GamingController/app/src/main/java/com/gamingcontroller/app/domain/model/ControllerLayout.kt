package com.gamingcontroller.app.domain.model

data class ControllerLayout(
    val id: String = "",
    val name: String = "",
    val elements: List<LayoutElement> = emptyList(),
    val joystickSettings: JoystickSettings = JoystickSettings(),
    val keyMappings: Map<String, KeyAction> = emptyMap()
)

sealed class LayoutElement {
    abstract val id: String
    abstract val x: Float
    abstract val y: Float
    abstract val size: Float

    data class Button(
        override val id: String,
        override val x: Float,
        override val y: Float,
        override val size: Float,
        val width: Float? = null,
        val shape: ButtonShape = ButtonShape.CIRCLE,
        val keyAction: KeyAction? = null,
        val label: String? = null,
        val color: String? = null
    ) : LayoutElement()

    data class Joystick(
        override val id: String,
        override val x: Float,
        override val y: Float,
        override val size: Float,
        val mode: JoystickMode = JoystickMode.MOVEMENT,
        val isDynamic: Boolean = false,
        val color: String? = null
    ) : LayoutElement()

    data class Dpad(
        override val id: String,
        override val x: Float,
        override val y: Float,
        override val size: Float,
        val directions: List<DpadDirection> = DpadDirection.entries,
        val color: String? = null
    ) : LayoutElement()
}

enum class ButtonShape {
    CIRCLE,
    RECTANGLE,
    ROUNDED_RECTANGLE
}

enum class DpadDirection {
    UP,
    DOWN,
    LEFT,
    RIGHT
}

enum class JoystickMode {
    MOVEMENT,
    CAMERA
}

data class JoystickSettings(
    val leftDeadzone: Float = 0.1f,
    val leftSensitivity: Float = 1.0f,
    val rightDeadzone: Float = 0.1f,
    val rightSensitivity: Float = 1.0f,
    val invertY: Boolean = false,
    val maxRadius: Float = 1.0f
)

data class ConnectionSettings(
    val autoReconnect: Boolean = true,
    val connectionTimeout: Int = 15
)

enum class KeyActionType {
    KEYBOARD,
    MOUSE_CLICK,
    MODIFIER
}

data class KeyAction(
    val type: KeyActionType,
    val value: String,
    val modifiers: List<String> = emptyList()
)

enum class MouseButtonType {
    LEFT,
    RIGHT,
    MIDDLE
}

data class KeyMapping(
    val keyboardKey: String? = null,
    val mouseButton: MouseButtonType? = null,
    val modifier: String? = null
)

object AvailableKeys {
    val keyboardKeys = listOf(
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
        "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
        "F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9", "F10", "F11", "F12",
        "SPACE", "ENTER", "ESCAPE", "TAB", "BACKSPACE", "DELETE",
        "UP", "DOWN", "LEFT", "RIGHT", "SHIFT", "CTRL", "ALT", "META"
    )

    val mouseActions = listOf(
        "LEFT_CLICK", "RIGHT_CLICK", "MIDDLE_CLICK",
        "LEFT_DOUBLE_CLICK", "RIGHT_DOUBLE_CLICK",
        "SCROLL_UP", "SCROLL_DOWN"
    )

    val modifiers = listOf(
        "SHIFT", "CTRL", "ALT", "META"
    )
}