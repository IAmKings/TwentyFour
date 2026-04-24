package com.twentyfour.cmp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import data.HistoryStorage
import data.SettingsStorage
import game.GameViewModel
import twentyfour.App

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App(
                gameViewModelFactory = {
                    GameViewModel(
                        historyStorage = HistoryStorage(this),
                        settingsStorage = SettingsStorage(this)
                    )
                }
            )
        }
    }
}
