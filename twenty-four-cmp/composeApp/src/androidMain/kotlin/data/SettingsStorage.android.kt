package data

import android.content.Context
import java.io.File

actual class SettingsStorage(private val context: Context) {
    private val file: File
        get() = File(context.filesDir, "game_settings.json")

    actual suspend fun save(settings: GameSettings) {
        file.writeText(settings.toJson())
    }

    actual suspend fun load(): GameSettings {
        return if (file.exists()) {
            file.readText().toGameSettings()
        } else {
            GameSettings.NORMAL
        }
    }
}