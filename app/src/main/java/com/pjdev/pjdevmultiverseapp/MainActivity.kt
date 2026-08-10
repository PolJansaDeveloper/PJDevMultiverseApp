package com.pjdev.pjdevmultiverseapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.pjdev.presentation.theme.MultiverseTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            MultiverseTheme {
                // Application content will be provided by the presentation layer.
            }
        }
    }
}
