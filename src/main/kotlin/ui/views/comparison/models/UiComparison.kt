package ui.views.comparison.models

import kotlin.io.path.Path
import kotlin.io.path.name
import kotlin.math.round

data class UiComparison(
    val path: String,
    val similarity: Double,
    val isDeleted: Boolean,
    val isFiltered: Boolean,
) {
    val name by lazy { Path(path).name }
    val percentage: Double = round(similarity * 10000) / 100
}