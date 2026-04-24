package data

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import solver.Solver24

@Serializable
data class HistoryEntry(
    val numbers: List<Int>,
    val expression: String,
    val status: HistoryStatus,
    val timestamp: Long = 0L
)

@Serializable
enum class HistoryStatus {
    PASS, SKIP, FAIL
}

fun HistoryEntry.displayNumbers(): String {
    return "[${numbers.joinToString(", ") { Solver24.displayLabel(it) }}]"
}
