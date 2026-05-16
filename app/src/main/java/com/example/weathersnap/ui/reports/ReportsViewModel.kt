package com.example.weathersnap.ui.reports

import androidx.lifecycle.ViewModel
import com.example.weathersnap.data.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {
    val reports = repository.getAllReports()
}