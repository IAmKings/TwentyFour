package data

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * 设置存储接口 - expect/actual 模式
 * commonMain 定义接口，各平台实现具体存储
 */
expect class SettingsStorage {
    suspend fun save(settings: GameSettings)
    suspend fun load(): GameSettings
}

private val json = Json {
    ignoreUnknownKeys = true
}

fun GameSettings.toJson(): String = json.encodeToString(this)
fun String.toGameSettings(): GameSettings =
    try {
        json.decodeFromString(this)
    } catch (e: Exception) {
        GameSettings.NORMAL
    }