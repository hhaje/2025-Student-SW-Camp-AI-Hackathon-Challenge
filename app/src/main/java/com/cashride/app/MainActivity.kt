package com.cashride.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.cashride.app.ui.theme.CashRideTheme
import com.cashride.app.ui.screens.MainScreen
import com.cashride.app.data.UserProfile

class MainActivity : ComponentActivity() {
    // 실제 앱에서는 ViewModel이나 Repository 패턴 사용
    private val userProfile = UserProfile()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CashRideTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(userProfile = userProfile)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    CashRideTheme {
        MainScreen(userProfile = UserProfile())
    }
}