package game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.GameSettings
import data.HistoryEntry
import data.HistoryStatus
import data.HistoryStorage
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import solver.ExpressionEvaluator
import solver.Solver24

data class GameUiState(
    val numbers: List<Int> = emptyList(),
    val score: Int = 0,
    val streak: Int = 0,
    val elapsedTime: Double = 0.0,
    val timerProgress: Float = 1f,
    val isTimerWarning: Boolean = false,
    val expression: String = "",
    val message: GameMessage = GameMessage.None,
    val roundActive: Boolean = false,
    val isAnimating: Boolean = false,
    val cardStates: List<CardState> = List(4) { CardState.FaceDown },
    val history: List<HistoryEntry> = emptyList(),
    val settings: GameSettings = GameSettings.NORMAL
)

sealed class GameMessage {
    object None : GameMessage()
    data class Success(val text: String) : GameMessage()
    data class Error(val text: String) : GameMessage()
    data class Info(val text: String) : GameMessage()
}

enum class CardState {
    FaceDown,   // 背面朝上
    Dealing,    // 发牌动画中
    FaceUp      // 正面朝上
}

class GameViewModel(
    private val historyStorage: HistoryStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private val timerDuration = 60 // seconds
    private val animationDuration = 1500L // milliseconds

    init {
        viewModelScope.launch {
            val savedHistory = historyStorage.load()
            _uiState.update { it.copy(history = savedHistory) }
        }
    }

    fun updateSettings(settings: GameSettings) {
        _uiState.update { it.copy(settings = settings) }
    }

    fun updateExpression(expr: String) {
        if (!_uiState.value.roundActive) return
        _uiState.update { it.copy(expression = expr) }
    }

    fun newGame() {
        if (_uiState.value.isAnimating) return

        stopTimer()
        val numbers = Solver24.generateSolvable(_uiState.value.settings.maxNumber)

        _uiState.update {
            it.copy(
                numbers = numbers,
                expression = "",
                message = GameMessage.None,
                roundActive = false,
                isAnimating = true,
                cardStates = List(4) { CardState.Dealing }
            )
        }

        // 模拟发牌动画完成
        viewModelScope.launch {
            delay(animationDuration)
            _uiState.update {
                it.copy(
                    cardStates = List(4) { CardState.FaceUp },
                    isAnimating = false,
                    roundActive = true
                )
            }
            startTimer()
        }
    }

    fun checkAnswer() {
        val state = _uiState.value
        if (!state.roundActive || state.isAnimating) return

        val input = state.expression.trim()
        if (input.isEmpty()) {
            _uiState.update { it.copy(message = GameMessage.Error("请输入算式")) }
            return
        }

        when (val result = ExpressionEvaluator.evaluate(input, state.numbers)) {
            is ExpressionEvaluator.EvalResult.Error -> {
                _uiState.update { it.copy(message = GameMessage.Error(result.message)) }
            }
            is ExpressionEvaluator.EvalResult.Success -> {
                if (kotlin.math.abs(result.value - 24.0) < 1e-9) {
                    handleCorrect(input)
                } else {
                    val rounded = kotlin.math.round(result.value * 10000) / 10000
                    _uiState.update {
                        it.copy(message = GameMessage.Error("结果为 $rounded，不是 24"))
                    }
                }
            }
        }
    }

    private fun handleCorrect(input: String) {
        stopTimer()
        val state = _uiState.value
        val points = kotlin.math.max(1, 11 - kotlin.math.floor(state.elapsedTime / 5).toInt())
        val newStreak = state.streak + 1
        val newScore = state.score + points

        val timeStr = "%.1f".format(state.elapsedTime)
        val msg = "正确！+$points 分   ${timeStr}s"

        _uiState.update {
            it.copy(
                score = newScore,
                streak = newStreak,
                roundActive = false,
                message = GameMessage.Success(msg)
            )
        }

        addHistory(state.numbers, input, HistoryStatus.PASS)
    }

    fun showHint() {
        val state = _uiState.value
        if (!state.roundActive || state.isAnimating) return

        val solution = Solver24.solve(state.numbers)
        _uiState.update {
            it.copy(
                message = if (solution != null) {
                    GameMessage.Info("提示: $solution")
                } else {
                    GameMessage.Info("此题无解")
                }
            )
        }
    }

    fun skipRound() {
        val state = _uiState.value
        if (!state.roundActive || state.isAnimating) return

        stopTimer()
        val solution = Solver24.solve(state.numbers)

        _uiState.update {
            it.copy(
                streak = 0,
                roundActive = false,
                message = GameMessage.Info("答案: ${solution ?: "无解"}")
            )
        }

        addHistory(state.numbers, "跳过", HistoryStatus.SKIP)
    }

    private fun addHistory(numbers: List<Int>, expression: String, status: HistoryStatus) {
        val entry = HistoryEntry(
            numbers = numbers,
            expression = expression,
            status = status,
            timestamp = getCurrentTimestamp()
        )
        val newHistory = (listOf(entry) + _uiState.value.history).take(20)
        _uiState.update { it.copy(history = newHistory) }

        viewModelScope.launch {
            historyStorage.save(newHistory)
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        val startTime = kotlinx.datetime.Clock.System.now()

        timerJob = viewModelScope.launch {
            _uiState.update {
                it.copy(elapsedTime = 0.0, timerProgress = 1f, isTimerWarning = false)
            }

            while (true) {
                delay(100)
                val elapsed = (kotlinx.datetime.Clock.System.now() - startTime).inWholeMilliseconds / 1000.0
                val progress = kotlin.math.max(0f, (1f - (elapsed / timerDuration)).toFloat())
                val warning = progress < 0.3f

                _uiState.update {
                    it.copy(
                        elapsedTime = elapsed,
                        timerProgress = progress,
                        isTimerWarning = warning
                    )
                }

                if (elapsed >= timerDuration) {
                    _uiState.update {
                        it.copy(
                            roundActive = false,
                            streak = 0,
                            message = GameMessage.Error("时间到！")
                        )
                    }
                    break
                }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    private fun getCurrentTimestamp(): Long {
        return kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}
