package data

import android.content.Context
import java.io.File

actual class HistoryStorage(private val context: Context) {
    private val file: File
        get() = File(context.filesDir, "game_history.json")

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
