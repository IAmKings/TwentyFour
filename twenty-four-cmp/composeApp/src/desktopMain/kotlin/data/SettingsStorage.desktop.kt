package data

import java.io.File

actual class SettingsStorage {
    private val file: File by lazy {
        val home = System.getProperty("user.home")
        val dir = File(home, ".twenty-four-cmp")
        if (!dir.exists()) dir.mkdirs()
        File(dir, "game_settings.json")
    }

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