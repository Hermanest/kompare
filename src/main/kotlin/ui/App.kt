package ui

import LocalNavController
import LocalProcessorProvider
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import core.AnalyzeInitData
import core.AnalyzeResult
import ui.views.comparison.ComparisonRoute
import ui.views.comparison.ComparisonView
import ui.views.comparison.models.UiComparisonsList
import ui.views.loading.LoadingRoute
import ui.views.loading.LoadingView
import ui.views.start.StartRoute
import ui.views.start.StartView
import ui.views.start.analyze.AnalyzeSettingsRoute
import ui.views.start.analyze.AnalyzeSettingsView

@Composable
fun App() {
    val navController = LocalNavController.current
    
    val duration = 300
    val easing = FastOutSlowInEasing

    NavHost(
        navController = navController,
        startDestination = StartRoute,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(duration, easing = easing)
            )
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(duration, easing = easing)
            )
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(duration, easing = easing)
            )
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(duration, easing = easing)
            )
        },
    ) {
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
            val processor = LocalProcessorProvider.current.processor
            val result = processor.result as AnalyzeResult
            val initData = processor.initData as AnalyzeInitData

            val model = UiComparisonsList(initData.filterOffThreshold, result.results)

            ComparisonView(model)
        }
    }
}