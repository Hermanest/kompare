import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import core.ComparatorFactory
import core.IComparatorFactory
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import platform.IPlatform
import platform.platform

val LocalPlatform = requiredCompositionLocal<IPlatform>()
val LocalLogger = requiredCompositionLocal<KLogger>()
val LocalComparatorFactory = requiredCompositionLocal<IComparatorFactory>()

@Composable
fun ProvideAppContext(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalPlatform provides platform,
        LocalLogger provides KotlinLogging.logger {},
        LocalComparatorFactory provides ComparatorFactory(),
        content = content
    )
}

private inline fun <reified T> requiredCompositionLocal(): ProvidableCompositionLocal<T> {
    return staticCompositionLocalOf {
        error("No ${T::class.simpleName} provided!")
    }
}