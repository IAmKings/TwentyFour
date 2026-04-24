package data

import kotlinx.serialization.Serializable

@Serializable
data class GameSettings(
    val maxNumber: Int = 13,  // 13 = 普通模式, 10 = 简单模式
    val timerDuration: Int = 60  // 秒
) {
    companion object {
        val EASY = GameSettings(maxNumber = 10, timerDuration = 60)
        val NORMAL = GameSettings(maxNumber = 13, timerDuration = 60)
        val HARD = GameSettings(maxNumber = 13, timerDuration = 45)
    }
}
