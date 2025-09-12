package ui.views.start.analyze

import LocalComparatorFactory
import LocalNavController
import LocalProcessorProviderMutable
import androidx.compose.runtime.Composable
import kotlinx.serialization.Serializable
import ui.views.loading.LoadingRoute

@Serializable
object AnalyzeSettingsRoute

@Composable
fun AnalyzeSettingsView() {
    val navController = LocalNavController.current
    val factory = LocalComparatorFactory.current
    val processorProvider = LocalProcessorProviderMutable.current

    AnalyzeSettingsPanel(
        onCancel = {
            navController.popBackStack()
        }, 
        onProceed = {
            val processor = factory.createProcessor(it)
            processor.start()
            
            processorProvider.setProcessor(processor)
            navController.navigate(LoadingRoute)
        }
    )
}