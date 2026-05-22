package com.gamingcontroller.app.domain.model

data class ControllerState(
    val leftStick: StickPosition = StickPosition(0f, 0f),
    val rightStick: StickPosition = StickPosition(0f, 0f),
    val buttons: ButtonState = ButtonState(),
    val shoulders: ShoulderState = ShoulderState(),
    val triggers: TriggerState = TriggerState(),
    val dpad: DpadState = DpadState(),
    val layoutConfig: ControllerLayout = ControllerLayout()
)

data class StickPosition(
    val x: Float = 0f,
    val y: Float = 0f
)

data class ButtonState(
    val a: Boolean = false,
    val b: Boolean = false,
    val x: Boolean = false,
    val y: Boolean = false,
    val select: Boolean = false,
    val start: Boolean = false,
    val esc: Boolean = false
)

data class ShoulderState(
    val l1: Boolean = false,
    val r1: Boolean = false
)

data class TriggerState(
    val l2: Boolean = false,
    val r2: Boolean = false
)

data class DpadState(
    val up: Boolean = false,
    val down: Boolean = false,
    val left: Boolean = false,
    val right: Boolean = false
)