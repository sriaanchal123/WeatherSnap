package com.example.weathersnap

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weathersnap.data.model.City
import com.example.weathersnap.data.model.WeatherResponse
import com.example.weathersnap.ui.camera.CameraScreen
import com.example.weathersnap.ui.notes.NotesScreen
import com.example.weathersnap.ui.reports.ReportsScreen
import com.example.weathersnap.ui.theme.WeatherSnapTheme
import com.example.weathersnap.ui.weather.WeatherScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherSnapTheme {
                WeatherSnapAppNavigation()
            }
        }
    }
}

@Composable
fun WeatherSnapAppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    
    var selectedCity by remember { mutableStateOf<City?>(null) }
    var selectedWeather by remember { mutableStateOf<WeatherResponse?>(null) }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            navController.navigate("camera")
        } else {
            Toast.makeText(context, "Camera permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    NavHost(navController = navController, startDestination = "weather") {
        composable("weather") {
            WeatherScreen(
                onNavigateToCamera = { city, weather ->
                    selectedCity = city
                    selectedWeather = weather
                    capturedImageUri = null // Purani image clear karo
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        navController.navigate("camera")
                    } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                onNavigateToReports = {
                    navController.navigate("reports")
                }
            )
        }
        
        composable("camera") {
            CameraScreen(
                onImageCaptured = { uri ->
                    capturedImageUri = uri
                    navController.navigate("notes")
                },
                onError = { 
                    Toast.makeText(context, "Camera error: ${it.message}", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
            )
        }
        
        composable("notes") {
            if (selectedCity != null && selectedWeather != null && capturedImageUri != null) {
                NotesScreen(
                    city = selectedCity!!,
                    weather = selectedWeather!!,
                    imageUri = capturedImageUri!!,
                    onBack = { navController.popBackStack() },
                    onReportSaved = {
                        navController.navigate("weather") {
                            popUpTo("weather") { inclusive = true }
                        }
                    }
                )
            }
        }
        
        composable("reports") {
            ReportsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}