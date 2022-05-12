package ch.mobpro.exercicer.components.views


import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Chip
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.mobpro.exercicer.R
import ch.mobpro.exercicer.components.BaseCard
import ch.mobpro.exercicer.components.cards.AggregationLevel
import ch.mobpro.exercicer.components.date.DatePickerField
import ch.mobpro.exercicer.data.entity.Sport
import ch.mobpro.exercicer.data.entity.TrainingType
import ch.mobpro.exercicer.data.entity.mapping.DateAggregationLevel
import ch.mobpro.exercicer.data.entity.mapping.SummingWrapper
import ch.mobpro.exercicer.data.util.ReportingEntry
import ch.mobpro.exercicer.data.util.getFormattedDistance
import ch.mobpro.exercicer.data.util.getFormattedTime
import ch.mobpro.exercicer.data.util.groupBy
import ch.mobpro.exercicer.viewmodel.TrainingViewModel
import java.time.LocalDate
import java.util.Comparator

@Composable
fun ReportingPage(reportingViewModel: TrainingViewModel) {
    Column {
        var dataMap by remember {
            mutableStateOf<Map<out Any, Map<out Any, SummingWrapper>>>(mutableMapOf())
        }

        ReportingFilter { first, second, date ->
            val list = reportingViewModel.trainingList.value

            dataMap = when (first) {
                AggregationLevel.TRAINING_TYPE -> when (second) {
                    AggregationLevel.SPORT -> list.groupBy<TrainingType, Sport>()
                    AggregationLevel.DATE -> list.groupBy<TrainingType, String>(date)
                    else -> mutableMapOf()
                }

                AggregationLevel.SPORT -> when (second) {
                    AggregationLevel.TRAINING_TYPE -> list.groupBy<Sport, TrainingType>()
                    AggregationLevel.DATE -> list.groupBy<Sport, String>(date)
                    else -> mutableMapOf()
                }

                AggregationLevel.DATE -> when (second) {
                    AggregationLevel.TRAINING_TYPE -> list.groupBy<String, TrainingType>(date)
                    AggregationLevel.SPORT -> list.groupBy<String, Sport>(date)
                    else -> mutableMapOf()
                }
            }
        }
        ReportingList(dataMap = dataMap)
    }
}


@Composable
fun ReportingRow(bold: Boolean, vararg rowValues: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        // first row has weight of 1
        var weight = 1f
        var rightAligned = false
        rowValues.forEach {
            Text(it,
                modifier = Modifier.fillMaxWidth().weight(weight),
                fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
                textAlign = if (rightAligned) TextAlign.Right else TextAlign.Left
            )

            // following cols after first row have half weight
            weight = 0.5f

            // following cols after first are right aligned
            rightAligned = true
        }
    }
}

@Composable
fun ReportingList(dataMap: Map<out Any, Map<out Any, SummingWrapper>>) {
    LazyColumn {
        for (key in dataMap.keys) {
            val reportingEntries = mutableListOf<ReportingEntry>()
            val subMap = dataMap[key]!!
            var sumTime = 0
            var sumDistance = 0
            for (subKey in subMap.keys) {
                val subValue = subMap[subKey]!!
                sumTime += subValue.sumSeconds
                sumDistance += subValue.sumMeters
                reportingEntries.add(
                    ReportingEntry(subKey.toString(),
                        getFormattedDistance(subValue.sumMeters),
                        getFormattedTime(subValue.sumSeconds))
                )
            }
            this.item { ReportingCard(key.toString(), reportingEntries, sumDistance, sumTime) }
        }
    }
}


@Composable
fun ReportingCard(title: String, reportingEntries: List<ReportingEntry>, sumDistance: Int, sumTime: Int) {
    BaseCard {
        ReportingRow(true, title, getFormattedDistance(sumDistance), getFormattedTime(sumTime))
        reportingEntries.forEach {
            ReportingRow(false, it.description, it.formattedDistance, it.formattedTime)
        }
    }
}

@Composable
fun ReportingFilter(onFilterChange: (AggregationLevel, AggregationLevel, DateAggregationLevel) -> Unit) {
    var fromDate by remember {
        mutableStateOf(LocalDate.now())
    }

    var toDate by remember {
        mutableStateOf(LocalDate.now())
    }

    var firstAggregationLevel by remember {
        mutableStateOf<AggregationLevel?>(null)
    }

    var secondAggregationLevel by remember {
        mutableStateOf<AggregationLevel?>(null)
    }

    var dateAggregationLevel by remember {
        mutableStateOf(DateAggregationLevel.DAILY)
    }

    Column {
        // time filter
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp), horizontalArrangement = Arrangement.SpaceAround) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)) {
                DatePickerField("Von") { date -> fromDate = date }
            }

            Column(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)) {
                DatePickerField("Bis") { date -> toDate = date }
            }
        }

        var showChipGroupDateAggregation by remember {
            mutableStateOf(false)
        }

        ChipGroup("Zusammenfassung nach") {
            /*
            The first that gets clicked, gets an icon with the number one. The second chip gets
            an icon with the number two. If the one with the number one gets unselected, then the one
            with the number two gets the number one icon. If a third is selected it gets the number
            two icon.
             */

            NumberChip(text = "Trainingsart",
                numberDetermination = {
                    checkAggregationLevels(firstAggregationLevel, secondAggregationLevel, AggregationLevel.TRAINING_TYPE)
                },
                disableCheck = { checkEnableAggregationLevelChip(firstAggregationLevel, secondAggregationLevel, AggregationLevel.TRAINING_TYPE) }
            ) {
                if (firstAggregationLevel == AggregationLevel.TRAINING_TYPE) {
                    firstAggregationLevel = secondAggregationLevel
                    secondAggregationLevel = null
                }

                else if (secondAggregationLevel == AggregationLevel.TRAINING_TYPE) {
                    secondAggregationLevel = null
                }

                else if (firstAggregationLevel == null) {
                    firstAggregationLevel = AggregationLevel.TRAINING_TYPE
                }

                else if (secondAggregationLevel == null) {
                    secondAggregationLevel = AggregationLevel.TRAINING_TYPE
                    onFilterChange.invoke(firstAggregationLevel!!,
                        secondAggregationLevel!!, dateAggregationLevel)
                }
            }

            NumberChip(text = "Sportart",
                numberDetermination = {
                    checkAggregationLevels(firstAggregationLevel, secondAggregationLevel, AggregationLevel.SPORT)
                },
                disableCheck = { checkEnableAggregationLevelChip(firstAggregationLevel, secondAggregationLevel, AggregationLevel.SPORT) }
            ) {
                if (firstAggregationLevel == AggregationLevel.SPORT) {
                    firstAggregationLevel = secondAggregationLevel
                    secondAggregationLevel = null
                }

                else if (secondAggregationLevel == AggregationLevel.SPORT) {
                    secondAggregationLevel = null
                }

                else if (firstAggregationLevel == null) {
                    firstAggregationLevel = AggregationLevel.SPORT
                }

                else if (secondAggregationLevel == null) {
                    secondAggregationLevel = AggregationLevel.SPORT
                    onFilterChange.invoke(firstAggregationLevel!!,
                        secondAggregationLevel!!, dateAggregationLevel)
                }

            }

            NumberChip(text = "Datum",
                numberDetermination = {
                    checkAggregationLevels(firstAggregationLevel, secondAggregationLevel, AggregationLevel.DATE)
                },
                disableCheck = { checkEnableAggregationLevelChip(firstAggregationLevel, secondAggregationLevel, AggregationLevel.DATE) }
            ) {
                if (firstAggregationLevel == AggregationLevel.DATE) {
                    firstAggregationLevel = secondAggregationLevel
                    secondAggregationLevel = null
                    showChipGroupDateAggregation = false
                }

                else if (secondAggregationLevel == AggregationLevel.DATE) {
                    secondAggregationLevel = null
                    showChipGroupDateAggregation = false
                }

                else if (firstAggregationLevel == null) {
                    firstAggregationLevel = AggregationLevel.DATE
                    showChipGroupDateAggregation = true
                }

                else if (secondAggregationLevel == null) {
                    secondAggregationLevel = AggregationLevel.DATE
                    showChipGroupDateAggregation = true
                    onFilterChange.invoke(firstAggregationLevel!!,
                        secondAggregationLevel!!, dateAggregationLevel)
                }
            }
        }
        
        if (showChipGroupDateAggregation) {
            /*
            This should work as follows

            This ChipGroup should only be shown if the date chip in the other group is selected.

            Only one of them is selectable at once. If a new one is selected, then the current one
            gets deselected.
             */

            ChipGroup("Datumsebene") {
                DateLevelChip(text = "täglich", isChecked = { dateAggregationLevel == DateAggregationLevel.DAILY }) {
                    dateAggregationLevel = DateAggregationLevel.DAILY
                    if (secondAggregationLevel != null) {
                        onFilterChange.invoke(firstAggregationLevel!!,
                            secondAggregationLevel!!, dateAggregationLevel)
                    }
                }
                
                DateLevelChip(text = "wöchentlich", isChecked = { dateAggregationLevel == DateAggregationLevel.WEEKLY }) {
                    dateAggregationLevel = DateAggregationLevel.WEEKLY
                    if (secondAggregationLevel != null) {
                        onFilterChange.invoke(firstAggregationLevel!!,
                            secondAggregationLevel!!, dateAggregationLevel)
                    }
                }
                
                DateLevelChip(text = "monatlich", isChecked = { dateAggregationLevel == DateAggregationLevel.MONTHLY }) {
                    dateAggregationLevel = DateAggregationLevel.MONTHLY
                    if (secondAggregationLevel != null) {
                        onFilterChange.invoke(firstAggregationLevel!!,
                            secondAggregationLevel!!, dateAggregationLevel)
                    }
                }
            }
        }
    }
}

private fun checkAggregationLevels(first: AggregationLevel?, second: AggregationLevel?, toBeChecked: AggregationLevel): Int {
    return if (first == toBeChecked) 1 else if (second == toBeChecked) 2 else 0
}

private fun checkEnableAggregationLevelChip(first: AggregationLevel?, second: AggregationLevel?, toBeChecked: AggregationLevel): Boolean {
    if (first == null || second == null) return true
    val checkResult = checkAggregationLevels(first, second, toBeChecked)
    return checkResult == 1 || checkResult == 2
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NumberChip(text: String, numberDetermination: () -> Int, disableCheck: () -> Boolean, onClick: () -> Unit) {
    Chip(onClick = onClick,
        modifier = Modifier
            .padding(end = 12.dp),
        enabled = disableCheck(),
        leadingIcon = { if (numberDetermination() == 1) ChipIcon() else if (numberDetermination() == 2) ChipIcon(false) }
    ) {
        Text(text)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DateLevelChip(text: String, isChecked: () -> Boolean, onClick: () -> Unit) {
    Chip(onClick = onClick,
        modifier = Modifier.padding(end = 12.dp), 
        leadingIcon = { if (isChecked()) Icon(Icons.Default.Check, contentDescription = "checkmark") }
    ) {
        Text(text)
    }
}

@Composable
private fun ChipIcon(first: Boolean = true) {
    return if (first ) {
        Icon(painterResource(id = R.drawable.ic_baseline_looks_one_24), contentDescription = "first")
    } else {
        Icon(painterResource(id = R.drawable.ic_baseline_looks_two_24), contentDescription = "second")
    }
}

@Composable
fun ChipGroup(label: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(5.dp)) {
        Text(label, modifier = Modifier.padding(horizontal = 10.dp), color = Color.Gray, fontSize = 11.sp)
        Row(modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
}