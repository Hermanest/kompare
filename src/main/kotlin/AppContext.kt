import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import core.ComparatorFactory
import core.IComparatorFactory
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import platform.IPlatform
import platform.platform
import ui.adapters.IProcessorProvider
import ui.adapters.ProcessorProvider

val LocalPlatform = requiredCompositionLocal<IPlatform>()
val LocalLogger = requiredCompositionLocal<KLogger>()
val LocalComparatorFactory = requiredCompositionLocal<IComparatorFactory>()
val LocalProcessorProvider = requiredCompositionLocal<IProcessorProvider>()
val LocalProcessorProviderMutable = requiredCompositionLocal<ProcessorProvider>()
val LocalNavController = requiredCompositionLocal<NavHostController>()

@Composable
fun ProvideAppContext(content: @Composable () -> Unit) {
    val factory = ComparatorFactory()
    val processor = ProcessorProvider(factory)
    
    CompositionLocalProvider(
        LocalPlatform provides platform,
        LocalLogger provides KotlinLogging.logger {},
        LocalComparatorFactory provides factory,
        LocalProcessorProvider provides processor,
        LocalProcessorProviderMutable provides processor,
        LocalNavController provides rememberNavController(),
        content = content
    )
}

private inline fun <reified T> requiredCompositionLocal(): ProvidableCompositionLocal<T> {
    return staticCompositionLocalOf {
        error("No ${T::class.simpleName} provided!")
    }
}