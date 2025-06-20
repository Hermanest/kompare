package ui

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

enum class ActionType {
    Find,
    Analyze
}

class ComparisonStartData {
    var filePath by mutableStateOf<String?>(null)
    var directoryPath by mutableStateOf("")
    var baseThreshold by mutableStateOf(0.7f)
    var action by mutableStateOf(ActionType.Find)
}