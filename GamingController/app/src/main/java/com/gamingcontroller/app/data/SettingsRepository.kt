package com.gamingcontroller.app.data

import android.content.Context
import android.content.SharedPreferences
import com.gamingcontroller.app.domain.model.ConnectionSettings
import com.gamingcontroller.app.domain.model.JoystickSettings
import javax.inject.Inject
import javax.inject.Singleton

interface SettingsRepository {
    fun getJoystickSettings(): JoystickSettings
    fun saveJoystickSettings(settings: JoystickSettings)
    fun getConnectionSettings(): ConnectionSettings
    fun saveConnectionSettings(settings: ConnectionSettings)
    fun getCurrentLayoutId(): String
    fun resetToDefaults()
}

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    context: Context
) : SettingsRepository {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME, Context.MODE_PRIVATE
    )

    override fun getJoystickSettings(): JoystickSettings {
        return JoystickSettings(
            leftDeadzone = sharedPreferences.getFloat(KEY_LEFT_DEADZONE, DEFAULT_LEFT_DEADZONE),
            leftSensitivity = sharedPreferences.getFloat(KEY_LEFT_SENSITIVITY, DEFAULT_LEFT_SENSITIVITY),
            rightDeadzone = sharedPreferences.getFloat(KEY_RIGHT_DEADZONE, DEFAULT_RIGHT_DEADZONE),
            rightSensitivity = sharedPreferences.getFloat(KEY_RIGHT_SENSITIVITY, DEFAULT_RIGHT_SENSITIVITY)
        )
    }

    override fun saveJoystickSettings(settings: JoystickSettings) {
        sharedPreferences.edit().apply {
            putFloat(KEY_LEFT_DEADZONE, settings.leftDeadzone)
            putFloat(KEY_LEFT_SENSITIVITY, settings.leftSensitivity)
            putFloat(KEY_RIGHT_DEADZONE, settings.rightDeadzone)
            putFloat(KEY_RIGHT_SENSITIVITY, settings.rightSensitivity)
            apply()
        }
    }

    override fun getConnectionSettings(): ConnectionSettings {
        return ConnectionSettings(
            autoReconnect = sharedPreferences.getBoolean(KEY_AUTO_RECONNECT, DEFAULT_AUTO_RECONNECT),
            connectionTimeout = sharedPreferences.getInt(KEY_CONNECTION_TIMEOUT, DEFAULT_CONNECTION_TIMEOUT)
        )
    }

    override fun saveConnectionSettings(settings: ConnectionSettings) {
        sharedPreferences.edit().apply {
            putBoolean(KEY_AUTO_RECONNECT, settings.autoReconnect)
            putInt(KEY_CONNECTION_TIMEOUT, settings.connectionTimeout)
            apply()
        }
    }

    override fun getCurrentLayoutId(): String {
        return sharedPreferences.getString(KEY_CURRENT_LAYOUT, DEFAULT_LAYOUT_ID) ?: DEFAULT_LAYOUT_ID
    }

    override fun resetToDefaults() {
        sharedPreferences.edit().clear().apply()
    }

    companion object {
        private const val PREFS_NAME = "gaming_controller_settings"
        private const val KEY_LEFT_DEADZONE = "left_deadzone"
        private const val KEY_LEFT_SENSITIVITY = "left_sensitivity"
        private const val KEY_RIGHT_DEADZONE = "right_deadzone"
        private const val KEY_RIGHT_SENSITIVITY = "right_sensitivity"
        private const val KEY_AUTO_RECONNECT = "auto_reconnect"
        private const val KEY_CONNECTION_TIMEOUT = "connection_timeout"
        private const val KEY_CURRENT_LAYOUT = "current_layout"

        private const val DEFAULT_LEFT_DEADZONE = 0.1f
        private const val DEFAULT_LEFT_SENSITIVITY = 1.0f
        private const val DEFAULT_RIGHT_DEADZONE = 0.1f
        private const val DEFAULT_RIGHT_SENSITIVITY = 1.0f
        private const val DEFAULT_AUTO_RECONNECT = true
        private const val DEFAULT_CONNECTION_TIMEOUT = 15
        private const val DEFAULT_LAYOUT_ID = "standard"
    }
}