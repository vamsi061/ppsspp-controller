package com.gamingcontroller.app.data

import android.content.Context
import android.content.SharedPreferences
import com.gamingcontroller.app.domain.model.ControllerLayout
import com.gamingcontroller.app.domain.model.JoystickMode
import com.gamingcontroller.app.domain.model.LayoutElement
import com.gamingcontroller.app.domain.model.LayoutElementAdapter
import com.gamingcontroller.app.domain.model.LayoutPresets
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import javax.inject.Inject
import javax.inject.Singleton

interface LayoutRepository {
    fun getCurrentLayout(): ControllerLayout
    fun setCurrentLayout(layoutId: String)
    fun getAllLayouts(): List<ControllerLayout>
    fun saveLayout(layout: ControllerLayout)
    fun saveCustomLayout(layout: ControllerLayout)
    fun deleteCustomLayout(layoutId: String)
}

@Singleton
class LayoutRepositoryImpl @Inject constructor(
    context: Context
) : LayoutRepository {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME, Context.MODE_PRIVATE
    )
    private val gson = GsonBuilder()
        .registerTypeAdapter(LayoutElement::class.java, LayoutElementAdapter())
        .create()

    override fun getCurrentLayout(): ControllerLayout {
        try {
            val currentLayoutId = sharedPreferences.getString(KEY_CURRENT_LAYOUT, DEFAULT_LAYOUT_ID)
            val allLayouts = getAllLayouts()
            return allLayouts.find { it.id == currentLayoutId } 
                ?: allLayouts.firstOrNull() 
                ?: createDefaultLayout()
        } catch (e: Exception) {
            android.util.Log.e("LayoutRepository", "Error getting layout: ${e.message}")
            return createDefaultLayout()
        }
    }

    private fun createDefaultLayout(): ControllerLayout {
        return ControllerLayout(
            id = "default",
            name = "Default",
            elements = listOf(
                LayoutElement.Joystick(
                    id = "left_stick",
                    x = 0.2f,
                    y = 0.7f,
                    size = 0.15f,
                    mode = JoystickMode.MOVEMENT
                ),
                LayoutElement.Joystick(
                    id = "right_stick", 
                    x = 0.8f,
                    y = 0.7f,
                    size = 0.15f,
                    mode = JoystickMode.CAMERA
                ),
                LayoutElement.Button(
                    id = "a",
                    x = 0.9f,
                    y = 0.8f,
                    size = 0.08f,
                    label = "A",
                    color = "#4CAF50"
                ),
                LayoutElement.Button(
                    id = "b",
                    x = 0.95f,
                    y = 0.7f,
                    size = 0.08f,
                    label = "B",
                    color = "#F44336"
                ),
                LayoutElement.Button(
                    id = "x",
                    x = 0.85f,
                    y = 0.7f,
                    size = 0.08f,
                    label = "X",
                    color = "#2196F3"
                ),
                LayoutElement.Button(
                    id = "y",
                    x = 0.9f,
                    y = 0.6f,
                    size = 0.08f,
                    label = "Y",
                    color = "#FFEB3B"
                )
            )
        )
    }

    override fun setCurrentLayout(layoutId: String) {
        sharedPreferences.edit().putString(KEY_CURRENT_LAYOUT, layoutId).apply()
    }

    override fun getAllLayouts(): List<ControllerLayout> {
        val customLayoutsJson = sharedPreferences.getString(KEY_CUSTOM_LAYOUTS, null)
        val customLayouts: List<ControllerLayout> = if (customLayoutsJson != null) {
            val type = object : TypeToken<List<ControllerLayout>>() {}.type
            gson.fromJson(customLayoutsJson, type) ?: emptyList()
        } else {
            emptyList()
        }
        return LayoutPresets.allPresets + customLayouts
    }

    override fun saveLayout(layout: ControllerLayout) {
        saveCustomLayout(layout)
    }

    override fun saveCustomLayout(layout: ControllerLayout) {
        val customLayouts = getCustomLayouts().toMutableList()
        val existingIndex = customLayouts.indexOfFirst { it.id == layout.id }
        if (existingIndex >= 0) {
            customLayouts[existingIndex] = layout
        } else {
            customLayouts.add(layout)
        }
        val json = gson.toJson(customLayouts)
        sharedPreferences.edit().putString(KEY_CUSTOM_LAYOUTS, json).apply()
    }

    override fun deleteCustomLayout(layoutId: String) {
        val customLayouts = getCustomLayouts().toMutableList()
        customLayouts.removeAll { it.id == layoutId }
        val json = gson.toJson(customLayouts)
        sharedPreferences.edit().putString(KEY_CUSTOM_LAYOUTS, json).apply()

        val currentLayoutId = sharedPreferences.getString(KEY_CURRENT_LAYOUT, DEFAULT_LAYOUT_ID)
        if (currentLayoutId == layoutId) {
            setCurrentLayout(DEFAULT_LAYOUT_ID)
        }
    }

    private fun getCustomLayouts(): List<ControllerLayout> {
        val customLayoutsJson = sharedPreferences.getString(KEY_CUSTOM_LAYOUTS, null)
        return if (customLayoutsJson != null) {
            val type = object : TypeToken<List<ControllerLayout>>() {}.type
            gson.fromJson(customLayoutsJson, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    companion object {
        private const val PREFS_NAME = "controller_layouts"
        private const val KEY_CURRENT_LAYOUT = "current_layout"
        private const val KEY_CUSTOM_LAYOUTS = "custom_layouts"
        private const val DEFAULT_LAYOUT_ID = "ppsspp"
    }
}