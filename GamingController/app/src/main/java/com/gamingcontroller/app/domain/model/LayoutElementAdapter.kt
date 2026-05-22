package com.gamingcontroller.app.domain.model

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

class LayoutElementAdapter : TypeAdapter<LayoutElement>() {
    private val gson = Gson()

    override fun write(out: JsonWriter, value: LayoutElement) {
        val jsonObject = when (value) {
            is LayoutElement.Button -> JsonObject().apply {
                addProperty("type", "button")
                addProperty("id", value.id)
                addProperty("x", value.x)
                addProperty("y", value.y)
                addProperty("size", value.size)
                value.width?.let { addProperty("width", it) }
                addProperty("shape", value.shape.name)
                value.keyAction?.let { add("keyAction", gson.toJsonTree(it)) }
                value.label?.let { addProperty("label", it) }
                value.color?.let { addProperty("color", it) }
            }
            is LayoutElement.Joystick -> JsonObject().apply {
                addProperty("type", "joystick")
                addProperty("id", value.id)
                addProperty("x", value.x)
                addProperty("y", value.y)
                addProperty("size", value.size)
                addProperty("mode", value.mode.name)
                addProperty("isDynamic", value.isDynamic)
                value.color?.let { addProperty("color", it) }
            }
            is LayoutElement.Dpad -> JsonObject().apply {
                addProperty("type", "dpad")
                addProperty("id", value.id)
                addProperty("x", value.x)
                addProperty("y", value.y)
                addProperty("size", value.size)
                value.color?.let { addProperty("color", it) }
            }
        }
        gson.toJson(jsonObject, out)
    }

    override fun read(`in`: JsonReader): LayoutElement {
        val obj = JsonParser.parseReader(`in`).asJsonObject
        val type = obj.get("type").asString
        val id = obj.get("id").asString
        val x = obj.get("x").asFloat
        val y = obj.get("y").asFloat
        val size = obj.get("size").asFloat

        return when (type) {
            "button" -> {
                val shape = obj.get("shape")?.asString?.let { ButtonShape.valueOf(it) } ?: ButtonShape.CIRCLE
                val label = obj.get("label")?.asString
                val color = obj.get("color")?.asString
                val width = obj.get("width")?.asFloat
                val keyAction = obj.get("keyAction")?.let { gson.fromJson(it, KeyAction::class.java) }
                LayoutElement.Button(id, x, y, size, width, shape, keyAction, label, color)
            }
            "joystick" -> {
                val mode = obj.get("mode")?.asString?.let { JoystickMode.valueOf(it) } ?: JoystickMode.MOVEMENT
                val isDynamic = obj.get("isDynamic")?.asBoolean ?: false
                val color = obj.get("color")?.asString
                LayoutElement.Joystick(id, x, y, size, mode, isDynamic, color)
            }
            "dpad" -> {
                val color = obj.get("color")?.asString
                LayoutElement.Dpad(id, x, y, size, color = color)
            }
            else -> throw JsonParseException("Unknown LayoutElement type: $type")
        }
    }
}
