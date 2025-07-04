package platform

interface IPlatform {
    fun load()
    fun openFilePicker(directory: Boolean): String?
    fun showFileInExplorer(path: String)
}

val platform: IPlatform by lazy {
    val osName = System.getProperty("os.name")

    when {
        osName.contains("Mac", ignoreCase = true) -> MacPlatform
        osName.contains("Windows", ignoreCase = true) -> WindowsPlatform

        else -> {
            throw Exception("Unsupported platform")
        }
    }
}