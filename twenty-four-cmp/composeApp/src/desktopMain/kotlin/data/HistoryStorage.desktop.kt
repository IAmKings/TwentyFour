package data

import java.io.File

actual class HistoryStorage {
    private val file: File by lazy {
        val home = System.getProperty("user.home")
        val dir = File(home, ".twenty-four-cmp")
        if (!dir.exists()) dir.mkdirs()
        File(dir, "game_history.json")
    }

    actual suspend fun save(entries: List<HistoryEntry>) {
        file.writeText(entries.toJson())
    }

    actual suspend fun load(): List<HistoryEntry> {
        return if (file.exists()) {
            file.readText().toHistoryEntries()
        } else {
            emptyList()
        }
    }
}
