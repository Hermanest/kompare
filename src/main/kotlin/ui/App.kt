package ui

import LocalNavController
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ui.views.comparison.ComparisonRoute
import ui.views.comparison.ComparisonView
import ui.views.loading.LoadingRoute
import ui.views.loading.LoadingView
import ui.views.start.StartRoute
import ui.views.start.StartView
import ui.views.start.analyze.AnalyzeSettingsRoute
import ui.views.start.analyze.AnalyzeSettingsView

@Composable
fun App() {
    val navController = LocalNavController.current
    
    NavHost(navController = navController, startDestination = StartRoute) {
        composable<StartRoute> {
            StartView()
        }
        
        composable<AnalyzeSettingsRoute> { 
            AnalyzeSettingsView()
        }
        
        composable<LoadingRoute> {
            LoadingView()
        }
        
        composable<ComparisonRoute> {  
            ComparisonView { group, path -> }
        }
    }
}