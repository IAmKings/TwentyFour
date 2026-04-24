import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import data.HistoryStorage
import data.SettingsStorage
import game.GameViewModel
import twentyfour.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "24点 - 心算挑战",
    ) {
        App(
            gameViewModelFactory = {
                GameViewModel(
                    historyStorage = HistoryStorage(),
                    settingsStorage = SettingsStorage()
                )
            }
        )
    }
}
