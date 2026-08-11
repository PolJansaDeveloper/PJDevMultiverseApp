package com.pjdev.pjdevmultiverseapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.pjdev.pjdevmultiverseapp.navigation.MultiverseNavHost
import com.pjdev.presentation.theme.MultiverseTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)


        setContent {
            MultiverseTheme {
                MultiverseNavHost()
            }
        }
    }
}
