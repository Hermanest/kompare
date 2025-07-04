package utils

fun log(message: String, severity: String) {
    println("[$severity] $message")
}

fun logError(message: String) {
    log(message, "Error")
}

fun logCritical(message: Exception) {
    log(message.toString(), "Critical")
}

fun logInfo(message: String) {
    log(message, "Info")
}