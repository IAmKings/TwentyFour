package data

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * 历史记录存储接口 - expect/actual 模式
 * commonMain 定义接口，各平台实现具体存储
 */
expect class HistoryStorage {
    suspend fun save(entries: List<HistoryEntry>)
    suspend fun load(): List<HistoryEntry>
}

private val json = Json {
    prettyPrint = false
    ignoreUnknownKeys = true
}

fun List<HistoryEntry>.toJson(): String = json.encodeToString(this)
fun String.toHistoryEntries(): List<HistoryEntry> =
    try {
        json.decodeFromString(this)
    } catch (e: Exception) {
        emptyList()
    }
