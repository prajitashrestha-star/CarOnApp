package com.example.caronapp.test

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestPicker() {
    val state = rememberDateRangePickerState()
    DatePickerDialog(
        onDismissRequest = {},
        confirmButton = { TextButton(onClick = {}) { Text("OK") } }
    ) {
        DateRangePicker(state = state)
    }
}
