package com.example.weathersnap.ui.notes

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.weathersnap.data.local.WeatherReport
import com.example.weathersnap.data.model.City
import com.example.weathersnap.data.model.WeatherResponse
import com.example.weathersnap.ui.weather.getCondition
import com.example.weathersnap.util.ImageUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    city: City,
    weather: WeatherResponse,
    imageUri: Uri,
    onBack: () -> Unit,
    onReportSaved: () -> Unit,
    viewModel: NotesViewModel = hiltViewModel()
) {
    var notes by remember { mutableStateOf("") }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Notes", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1B1C17))
            )
        },
        containerColor = Color(0xFF1B1C17)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Re-added the captured image preview
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Weather in ${city.name}",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter notes about the weather...", color = Color.Gray) },
                minLines = 4,
                maxLines = 6,
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF2C2D24),
                    unfocusedContainerColor = Color(0xFF2C2D24),
                    focusedIndicatorColor = Color(0xFFC5E1A5),
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val savedPath = ImageUtils.compressAndSaveImage(context, imageUri)
                    if (savedPath != null) {
                        val report = WeatherReport(
                            cityName = city.name,
                            temperature = weather.currentWeather?.temperature ?: 0.0,
                            condition = getCondition(weather.currentWeather?.weatherCode ?: 0),
                            humidity = weather.hourly?.humidity?.firstOrNull() ?: 0.0,
                            windSpeed = weather.currentWeather?.windspeed ?: 0.0,
                            pressure = weather.hourly?.pressure?.firstOrNull() ?: 0.0,
                            imagePath = savedPath,
                            notes = notes
                        )
                        viewModel.saveReport(report)
                        onReportSaved()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC5E1A5)),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text("Save Report", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}