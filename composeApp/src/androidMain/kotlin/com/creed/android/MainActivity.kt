package com.creed.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.creed.android.di.presentationModule
import com.creed.android.navigation.CreedApp
import com.creed.android.theme.CreedTheme
import com.creed.di.androidModule
import com.creed.di.commonModule
import com.creed.di.dataModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        startKoin {
            androidContext(this@MainActivity)
            modules(dataModule, commonModule, androidModule, presentationModule)
        }
        setContent {
            CreedTheme {
                CreedApp()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}