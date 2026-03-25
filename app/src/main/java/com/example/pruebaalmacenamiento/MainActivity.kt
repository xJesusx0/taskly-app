package com.example.pruebaalmacenamiento

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pruebaalmacenamiento.repositories.UserPreferencesRepository
import com.example.pruebaalmacenamiento.ui.theme.PruebaAlmacenamientoTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PruebaAlmacenamientoTheme {
                AppNavigation()
            }
        }
    }
}

// ─────────────────────────────────────────────────
// Navegación manual (sin Navigation Component)
// ─────────────────────────────────────────────────

enum class Screen { LOGIN, REGISTER, TASKS }

@Composable
fun AppNavigation() {
    val context: Context = LocalContext.current
    val userRepo = remember(context) { UserPreferencesRepository(context) }
    val isLoggedIn by userRepo.isLoggedInFlow.collectAsState(initial = false)

    // Determina la pantalla inicial una vez que el estado se carga
    var initialized by remember { mutableStateOf(false) }
    var currentScreen by remember { mutableStateOf(Screen.LOGIN) }

    LaunchedEffect(isLoggedIn) {
        if (!initialized) {
            currentScreen = if (isLoggedIn) Screen.TASKS else Screen.LOGIN
            initialized = true
        }
    }

    if (!initialized) {
        // Splash mínimo mientras carga el estado
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BrandGreen),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
        return
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentScreen) {
                Screen.LOGIN -> LoginScreen(
                    onLoginSuccess = { currentScreen = Screen.TASKS },
                    onNavigateToRegister = { currentScreen = Screen.REGISTER }
                )
                Screen.REGISTER -> RegisterScreen(
                    onRegisterSuccess = { currentScreen = Screen.LOGIN },
                    onNavigateToLogin = { currentScreen = Screen.LOGIN }
                )
                Screen.TASKS -> TasksScreen(
                    onLogout = { currentScreen = Screen.LOGIN }
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────
// Brand colors (compartidas en todo el módulo UI)
// ─────────────────────────────────────────────────

val BrandGreen      = Color(0xFF1D9E75)
val BrandGreenLight = Color(0xFFE1F5EE)
val BrandGreenDark  = Color(0xFF0F6E56)