package com.example.cashflow.presentation.ui.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.components.Legends
import co.yml.charts.common.model.LegendsConfig
import co.yml.charts.common.model.Point
import co.yml.charts.common.utils.DataUtils
import co.yml.charts.ui.barchart.GroupBarChart
import co.yml.charts.ui.barchart.models.BarData
import co.yml.charts.ui.barchart.models.BarPlotData
import co.yml.charts.ui.barchart.models.BarStyle
import co.yml.charts.ui.barchart.models.GroupBar
import co.yml.charts.ui.barchart.models.GroupBarChartData
import co.yml.charts.ui.barchart.models.GroupSeparatorConfig
import com.example.cashflow.domain.model.Expense
import com.example.cashflow.domain.model.Income
import kotlin.math.max

// Define 12 months
val months = listOf(
    "January",
    "February",
    "March",
    "April",
    "May",
    "June",
    "July",
    "August",
    "September",
    "October",
    "November",
    "December"
)

/**
 * A Composable function that renders a vertical grouped bar chart with legends.
 *
 * This function demonstrates how to create a grouped bar chart using a custom dataset,
 * configure X and Y axes, apply a color palette for bars, and display chart legends.
 */
@Composable
fun CashFlowVerticalGroupBarChart(incomes: List<Income>, expenses: List<Expense>) {
    val maxRange = max(incomes.maxOfOrNull { it.amount } ?: 0.0,
        expenses.maxOfOrNull { it.amount } ?: 0.0).toFloat() // Maximum value for the Y-axis
    val barSize = 2 // Number of bars in each group

    val yStepSize = 10 // Step size for Y-axis labels
    // Configuration for the X-axis
    val xAxisData = AxisData.Builder().axisStepSize(30.dp)   // Distance between X-axis labels
        .bottomPadding(5.dp)             // Padding below the X-axis
        .startDrawPadding(48.dp)         // Padding at the start of the X-axis
        .labelData { index ->
            index.toString()
        } // Label for each X-axis step
        .build()

    // Configuration for the Y-axis
    val yAxisData = AxisData.Builder()
        .steps(yStepSize)                       // Number of steps/divisions in Y-axis
        .labelAndAxisLinePadding(20.dp)       // Padding between axis line and labels
        .axisOffset(20.dp)                      // Offset of the Y-axis from chart edge
        .labelData { index ->
            (index * (maxRange / yStepSize)).toString()
        } // Y-axis label calculation
        .build()

    // Color palette for bars
    val colorPaletteList = DataUtils.getColorPaletteList(barSize)

    val allDates = (incomes.map { it.date } + expenses.map { it.date }).distinct().sorted()
    val groupBarData = allDates.map { date ->
        val totalIncome = incomes.filter { it.date == date }.sumOf { it.amount }.toFloat()
        val totalExpense = expenses.filter { it.date == date }.sumOf { it.amount }.toFloat()

        GroupBar(
            label = date, // shown below that group on X-axis
            barList = listOf(
                BarData(Point(0f, totalIncome)),   // first bar (e.g., Income)
                BarData(Point(0f, totalExpense))   // second bar (e.g., Expense)
            )
        )
    }

    // Bar plot data for the grouped bar chart
    val groupBarPlotData = BarPlotData(
        groupBarList = groupBarData,
        barStyle = BarStyle(barWidth = 30.dp),
        barColorPaletteList = listOf(
            MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary
        )
    )

    // Complete grouped bar chart data including axes and group separator
    val groupBarChartData = GroupBarChartData(
        barPlotData = groupBarPlotData,
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        groupSeparatorConfig = GroupSeparatorConfig(0.dp)
    )

    // Layout for chart and legends
    Column(Modifier.height(450.dp)) {// Total height of the chart + legends
        GroupBarChart(Modifier.height(400.dp), groupBarChartData = groupBarChartData)

        // Configuration for chart legends
        val legendsConfig = LegendsConfig(
            legendLabelList = DataUtils.getLegendsLabelData(
                colorPaletteList = listOf(
                    MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary
                )
            ), gridColumnCount = 2
        )
        Legends(legendsConfig = legendsConfig) // Render the legends below the chart
    }
}
