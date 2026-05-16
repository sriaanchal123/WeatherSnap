package com.example.weathersnap.ui.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathersnap.data.local.WeatherReport
import com.example.weathersnap.data.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    fun saveReport(report: WeatherReport) {
        viewModelScope.launch {
            repository.saveReport(report)
        }
    }
}