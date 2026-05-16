package com.example.weathersnap.ui.weather

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.weathersnap.data.model.City
import com.example.weathersnap.data.model.WeatherResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    onNavigateToCamera: (City, WeatherResponse) -> Unit,
    onNavigateToReports: () -> Unit,
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val suggestions by viewModel.suggestions.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = Color(0xFF0F100B)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFF98B66E), Color(0xFFC5E1A5))
                            )
                        )
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Column {
                        Text(
                            "WeatherSnap",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B1C17)
                        )
                        Text(
                            "Live weather reports with camera evidence",
                            fontSize = 12.sp,
                            color = Color(0xFF3E4F2E)
                        )
                    }
                    Button(
                        onClick = onNavigateToReports,
                        modifier = Modifier
                            .align(Alignment.CenterEnd),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF33352E)),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text("Reports", color = Color.White, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1C17))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.onSearchQueryChange(it) },
                            modifier = Modifier.weight(1f),
                            label = { Text("City", color = Color.Gray) },
                            placeholder = { Text("Faridabad, Haryana, India", color = Color.DarkGray) },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.LightGray,
                                unfocusedBorderColor = Color.DarkGray,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = { /* Search is automatic */ },
                            modifier = Modifier.height(56.dp).width(85.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC5E1A5))
                        ) {
                            Text("Search", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                    Text(
                        "Enter more than 2 letters to start city suggestions.",
                        color = Color.Gray,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }
            }

            if (suggestions.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1C17)),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                        items(suggestions) { city ->
                            Text(
                                text = city.displayName,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.onCitySelected(city) }
                                    .padding(16.dp),
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = uiState) {
                is WeatherUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFFC5E1A5))
                    }
                }
                is WeatherUiState.Success -> {
                    WeatherDetails(
                        city = state.city,
                        weather = state.weather,
                        onCreateReport = { onNavigateToCamera(state.city, state.weather) }
                    )
                }
                is WeatherUiState.Error -> {
                    Text("Error: ${state.message}", color = Color.Red, modifier = Modifier.padding(16.dp))
                }
                else -> {}
            }
        }
    }
}

@Composable
fun WeatherDetails(
    city: City,
    weather: WeatherResponse,
    onCreateReport: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1F1A))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        city.displayName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        getCondition(weather.currentWeather?.weatherCode ?: 0),
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Surface(
                    color = Color(0xFF333522),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "${weather.currentWeather?.temperature?.toInt() ?: 0}°C",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFC5E1A5)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                WeatherInfoBox(
                    label = "Humidity",
                    value = "${weather.hourly?.humidity?.firstOrNull()?.toInt() ?: 0}%",
                    valueColor = Color(0xFF4DB6AC),
                    bgColor = Color(0xFF1D2423),
                    modifier = Modifier.weight(1f)
                )
                WeatherInfoBox(
                    label = "Wind",
                    value = "${weather.currentWeather?.windspeed ?: 0.0} m/s",
                    valueColor = Color(0xFF64B5F6),
                    bgColor = Color(0xFF1D2128),
                    modifier = Modifier.weight(1f)
                )
                WeatherInfoBox(
                    label = "Pressure",
                    value = "${weather.hourly?.pressure?.firstOrNull()?.toInt() ?: 0}",
                    valueColor = Color(0xFFFFB74D),
                    bgColor = Color(0xFF24211D),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Readiness Bar
            Surface(
                color = Color(0xFF292B23),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Report readiness", color = Color.Gray, fontSize = 14.sp)
                    Text("Camera and Room DB enabled", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onCreateReport,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC5E1A5)),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text("Create Report", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun WeatherInfoBox(
    label: String,
    value: String,
    valueColor: Color,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.height(75.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(label, color = Color.Gray, fontSize = 11.sp)
            Text(value, color = valueColor, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

fun getCondition(code: Int): String {
    return when (code) {
        0 -> "Clear sky"
        1, 2, 3 -> "Partly cloudy"
        45, 48 -> "Fog"
        51, 53, 55 -> "Drizzle"
        61, 63, 65 -> "Rain"
        71, 73, 75 -> "Snow fall"
        95 -> "Thunderstorm"
        else -> "Overcast"
    }
}