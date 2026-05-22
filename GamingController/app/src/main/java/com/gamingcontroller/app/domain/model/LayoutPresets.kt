package com.gamingcontroller.app.domain.model

object LayoutPresets {

    val STANDARD = ControllerLayout(
        id = "standard",
        name = "Standard Controller",
        elements = listOf(
            LayoutElement.Joystick(
                id = "left_stick",
                x = 0.17f,
                y = 0.70f,
                size = 0.14f,
                mode = JoystickMode.MOVEMENT
            ),
            LayoutElement.Joystick(
                id = "right_stick",
                x = 0.80f,
                y = 0.70f,
                size = 0.10f,
                mode = JoystickMode.CAMERA
            ),
            LayoutElement.Dpad(
                id = "dpad",
                x = 0.10f,
                y = 0.40f,
                size = 0.13f,
                directions = DpadDirection.entries
            ),
            LayoutElement.Button(
                id = "y",
                x = 0.78f,
                y = 0.33f,
                size = 0.09f,
                shape = ButtonShape.CIRCLE,
                label = "\u25b3", // Triangle
                color = "#4CAF50"
            ),
            LayoutElement.Button(
                id = "b",
                x = 0.88f,
                y = 0.43f,
                size = 0.09f,
                shape = ButtonShape.CIRCLE,
                label = "\u25cb", // Circle
                color = "#F44336"
            ),
            LayoutElement.Button(
                id = "a",
                x = 0.78f,
                y = 0.53f,
                size = 0.09f,
                shape = ButtonShape.CIRCLE,
                label = "\u2715", // Cross
                color = "#2196F3"
            ),
            LayoutElement.Button(
                id = "x",
                x = 0.68f,
                y = 0.43f,
                size = 0.09f,
                shape = ButtonShape.CIRCLE,
                label = "\u25a1", // Square
                color = "#E91E63"
            ),
            LayoutElement.Button(
                id = "l1",
                x = 0.10f,
                y = 0.12f,
                size = 0.05f,
                shape = ButtonShape.ROUNDED_RECTANGLE,
                label = "L"
            ),
            LayoutElement.Button(
                id = "r1",
                x = 0.70f,
                y = 0.12f,
                size = 0.05f,
                shape = ButtonShape.ROUNDED_RECTANGLE,
                label = "R"
            ),
            LayoutElement.Button(
                id = "select",
                x = 0.30f,
                y = 0.90f,
                size = 0.07f,
                width = 0.13f,
                shape = ButtonShape.ROUNDED_RECTANGLE,
                label = "SELECT"
            ),
            LayoutElement.Button(
                id = "start",
                x = 0.57f,
                y = 0.90f,
                size = 0.07f,
                width = 0.13f,
                shape = ButtonShape.ROUNDED_RECTANGLE,
                label = "START"
            ),
            LayoutElement.Button(
                id = "esc",
                x = 0.435f,
                y = 0.90f,
                size = 0.07f,
                width = 0.09f,
                shape = ButtonShape.ROUNDED_RECTANGLE,
                label = "ESC"
            )
        ),
        joystickSettings = JoystickSettings(
            leftDeadzone = 0.1f,
            leftSensitivity = 1.0f,
            rightDeadzone = 0.1f,
            rightSensitivity = 1.0f,
            invertY = false
        )
    )

    val PPSSPP = ControllerLayout(
        id = "ppsspp",
        name = "PPSSPP Style",
        elements = listOf(
            LayoutElement.Dpad(
                id = "dpad",
                x = 0.15f,
                y = 0.45f,
                size = 0.15f,
                directions = DpadDirection.entries
            ),
            LayoutElement.Joystick(
                id = "left_stick",
                x = 0.15f,
                y = 0.75f,
                size = 0.12f,
                mode = JoystickMode.MOVEMENT
            ),
            LayoutElement.Button(
                id = "x",
                x = 0.78f,
                y = 0.55f,
                size = 0.09f,
                shape = ButtonShape.CIRCLE,
                label = "\u25a1", // Square
                color = "#E91E63"
            ),
            LayoutElement.Button(
                id = "y",
                x = 0.85f,
                y = 0.4f,
                size = 0.09f,
                shape = ButtonShape.CIRCLE,
                label = "\u25b3", // Triangle
                color = "#4CAF50"
            ),
            LayoutElement.Button(
                id = "a",
                x = 0.85f,
                y = 0.7f,
                size = 0.09f,
                shape = ButtonShape.CIRCLE,
                label = "\u2715", // Cross
                color = "#2196F3"
            ),
            LayoutElement.Button(
                id = "b",
                x = 0.92f,
                y = 0.55f,
                size = 0.09f,
                shape = ButtonShape.CIRCLE,
                label = "\u25cb", // Circle
                color = "#F44336"
            ),
            LayoutElement.Button(
                id = "l1",
                x = 0.10f,
                y = 0.12f,
                size = 0.05f,
                shape = ButtonShape.ROUNDED_RECTANGLE,
                label = "L"
            ),
            LayoutElement.Button(
                id = "r1",
                x = 0.70f,
                y = 0.12f,
                size = 0.05f,
                shape = ButtonShape.ROUNDED_RECTANGLE,
                label = "R"
            ),
            LayoutElement.Button(
                id = "select",
                x = 0.33f,
                y = 0.90f,
                size = 0.07f,
                width = 0.13f,
                shape = ButtonShape.ROUNDED_RECTANGLE,
                label = "SELECT"
            ),
            LayoutElement.Button(
                id = "start",
                x = 0.54f,
                y = 0.90f,
                size = 0.07f,
                width = 0.13f,
                shape = ButtonShape.ROUNDED_RECTANGLE,
                label = "START"
            ),
            LayoutElement.Button(
                id = "esc",
                x = 0.435f,
                y = 0.90f,
                size = 0.07f,
                width = 0.09f,
                shape = ButtonShape.ROUNDED_RECTANGLE,
                label = "ESC"
            )
        ),
        joystickSettings = JoystickSettings(
            leftDeadzone = 0.15f,
            leftSensitivity = 0.9f,
            rightDeadzone = 0.15f,
            rightSensitivity = 0.9f
        )
    )

    val FIGHTING = ControllerLayout(
        id = "fighting",
        name = "Fighting Arcade",
        elements = listOf(
            LayoutElement.Dpad(
                id = "dpad",
                x = 0.15f,
                y = 0.55f,
                size = 0.14f,
                directions = DpadDirection.entries
            ),
            LayoutElement.Button(
                id = "button_1",
                x = 0.7f,
                y = 0.2f,
                size = 0.07f,
                shape = ButtonShape.CIRCLE,
                label = "1"
            ),
            LayoutElement.Button(
                id = "button_2",
                x = 0.8f,
                y = 0.2f,
                size = 0.07f,
                shape = ButtonShape.CIRCLE,
                label = "2"
            ),
            LayoutElement.Button(
                id = "button_3",
                x = 0.9f,
                y = 0.2f,
                size = 0.07f,
                shape = ButtonShape.CIRCLE,
                label = "3"
            ),
            LayoutElement.Button(
                id = "button_4",
                x = 0.7f,
                y = 0.35f,
                size = 0.07f,
                shape = ButtonShape.CIRCLE,
                label = "4"
            ),
            LayoutElement.Button(
                id = "button_5",
                x = 0.8f,
                y = 0.35f,
                size = 0.07f,
                shape = ButtonShape.CIRCLE,
                label = "5"
            ),
            LayoutElement.Button(
                id = "button_6",
                x = 0.9f,
                y = 0.35f,
                size = 0.07f,
                shape = ButtonShape.CIRCLE,
                label = "6"
            ),
            LayoutElement.Button(
                id = "shoulder_l",
                x = 0.1f,
                y = 0.08f,
                size = 0.1f,
                shape = ButtonShape.ROUNDED_RECTANGLE,
                label = "LP"
            ),
            LayoutElement.Button(
                id = "shoulder_r",
                x = 0.9f,
                y = 0.08f,
                size = 0.1f,
                shape = ButtonShape.ROUNDED_RECTANGLE,
                label = "HP"
            )
        ),
        joystickSettings = JoystickSettings(
            leftDeadzone = 0.05f,
            leftSensitivity = 1.2f,
            rightDeadzone = 0.05f,
            rightSensitivity = 1.2f
        )
    )

    fun getPreset(name: String): ControllerLayout? {
        return when (name.uppercase()) {
            "STANDARD" -> STANDARD
            "PPSSPP" -> PPSSPP
            "FIGHTING" -> FIGHTING
            else -> null
        }
    }

    val allPresets = listOf(STANDARD, PPSSPP, FIGHTING)
}
