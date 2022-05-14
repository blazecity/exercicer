package ch.mobpro.exercicer.components.views.reporting


import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import ch.mobpro.exercicer.R
import ch.mobpro.exercicer.components.BaseCard
import ch.mobpro.exercicer.components.cards.AggregationLevel
import ch.mobpro.exercicer.components.date.DatePickerField
import ch.mobpro.exercicer.components.views.ScreenTitle
import ch.mobpro.exercicer.data.entity.Sport
import ch.mobpro.exercicer.data.entity.TrainingType
import ch.mobpro.exercicer.data.entity.mapping.DateAggregationLevel
import ch.mobpro.exercicer.data.entity.mapping.SummingWrapper
import ch.mobpro.exercicer.data.entity.mapping.TrainingSportTrainingTypeMapping
import ch.mobpro.exercicer.data.util.ReportingEntry
import ch.mobpro.exercicer.data.util.getFormattedDistance
import ch.mobpro.exercicer.data.util.getFormattedTime
import ch.mobpro.exercicer.data.util.groupBy
import ch.mobpro.exercicer.viewmodel.ReportingViewModel

@Composable
fun ReportingPage() {
    val reportingViewModel: ReportingViewModel = hiltViewModel()
    Column(modifier = Modifier.padding(10.dp)) {
        val list = reportingViewModel.filteredTrainings.observeAsState(emptyList())
        var groupByArgs by remember {
            mutableStateOf<Triple<AggregationLevel?, AggregationLevel?, DateAggregationLevel?>>(
                Triple(null, null, null)
            )
        }

        var filterOpen by remember {
            mutableStateOf(false)
        }
        
        ScreenTitle(title = "Reporting")

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(CornerSize(10.dp)),
            elevation = 10.dp
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember {
                                MutableInteractionSource()
                            },
                            indication = null
                        ) {
                            filterOpen = !filterOpen
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (filterOpen) Icons.Default.KeyboardArrowDown
                            else Icons.Default.KeyboardArrowRight,
                        contentDescription = "down arrow"
                    )

                    Text("Filter")
                }

                if (filterOpen) {
                    ReportingFilter(reportingViewModel) { first, second, dateAggr ->
                        groupByArgs = Triple(first, second, dateAggr)
                    }
                }
            }
        }

        ReportingList(list.value) {
            groupMap(it, groupByArgs)
        }
    }
}

private fun groupMap(
    list: List<TrainingSportTrainingTypeMapping>,
    groupByArgs: Triple<AggregationLevel?, AggregationLevel?, DateAggregationLevel?>
): Map<out Any, Map<out Any, SummingWrapper>>  {

    if (groupByArgs.first == null || groupByArgs.second == null || groupByArgs.third == null) {
        return mutableMapOf()
    }

    return when (groupByArgs.first) {
        AggregationLevel.TRAINING_TYPE -> when (groupByArgs.second) {
            AggregationLevel.SPORT -> list.groupBy<TrainingType, Sport>()
            AggregationLevel.DATE -> list.groupBy<TrainingType, String>(groupByArgs.third!!)
            else -> mutableMapOf()
        }

        AggregationLevel.SPORT -> when (groupByArgs.second) {
            AggregationLevel.TRAINING_TYPE -> list.groupBy<Sport, TrainingType>()
            AggregationLevel.DATE -> list.groupBy<Sport, String>(groupByArgs.third!!)
            else -> mutableMapOf()
        }

        AggregationLevel.DATE -> when (groupByArgs.second) {
            AggregationLevel.TRAINING_TYPE -> list.groupBy<String, TrainingType>(groupByArgs.third!!)
            AggregationLevel.SPORT -> list.groupBy<String, Sport>(groupByArgs.third!!)
            else -> mutableMapOf()
        }
        else -> mutableMapOf()
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
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(weight),
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
fun ReportingList(
    list: List<TrainingSportTrainingTypeMapping>,
    groupList: (List<TrainingSportTrainingTypeMapping>) -> Map<out Any, Map<out Any, SummingWrapper>>
) {
    val dataMap = groupList(list)
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
fun ReportingFilter(
    reportingViewModel: ReportingViewModel,
    onFilterChange: (AggregationLevel, AggregationLevel, DateAggregationLevel) -> Unit
) {
    var fromDate by remember {
        mutableStateOf(reportingViewModel.getFromDate())
    }

    var toDate by remember {
        mutableStateOf(reportingViewModel.getToDate())
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

    Column(modifier = Modifier.padding(start = 10.dp, end = 10.dp, bottom = 10.dp)) {
        // time filter
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)) {
                DatePickerField(fromDate, "Von") { date ->
                    reportingViewModel.setFromDate(date)
                    if (firstAggregationLevel != null && secondAggregationLevel != null) {
                        onFilterChange(firstAggregationLevel!!,
                            secondAggregationLevel!!, dateAggregationLevel)
                    }
                }
            }
            
            Spacer(modifier = Modifier.padding(horizontal = 20.dp))

            Column(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)) {
                DatePickerField(toDate, "Bis") { date ->
                    reportingViewModel.setToDate(date)
                    if (firstAggregationLevel != null && secondAggregationLevel != null) {
                        onFilterChange(firstAggregationLevel!!,
                            secondAggregationLevel!!, dateAggregationLevel)
                    }
                }
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
                    checkAggregationLevels(
                        firstAggregationLevel,
                        secondAggregationLevel,
                        AggregationLevel.TRAINING_TYPE
                    )
                },
                disableCheck = {
                    checkEnableAggregationLevelChip(
                        firstAggregationLevel,
                        secondAggregationLevel,
                        AggregationLevel.TRAINING_TYPE
                    )
                }
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
                    checkAggregationLevels(
                        firstAggregationLevel,
                        secondAggregationLevel,
                        AggregationLevel.SPORT
                    )
                },
                disableCheck = {
                    checkEnableAggregationLevelChip(
                        firstAggregationLevel,
                        secondAggregationLevel,
                        AggregationLevel.SPORT
                    )
                }
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
                DateLevelChip(
                    text = "täglich",
                    isChecked = { dateAggregationLevel == DateAggregationLevel.DAILY }
                ) {
                    dateAggregationLevel = DateAggregationLevel.DAILY
                    if (secondAggregationLevel != null) {
                        onFilterChange.invoke(firstAggregationLevel!!,
                            secondAggregationLevel!!, dateAggregationLevel)
                    }
                }
                
                DateLevelChip(
                    text = "wöchentlich",
                    isChecked = { dateAggregationLevel == DateAggregationLevel.WEEKLY }
                ) {
                    dateAggregationLevel = DateAggregationLevel.WEEKLY
                    if (secondAggregationLevel != null) {
                        onFilterChange.invoke(firstAggregationLevel!!,
                            secondAggregationLevel!!, dateAggregationLevel)
                    }
                }
                
                DateLevelChip(
                    text = "monatlich",
                    isChecked = { dateAggregationLevel == DateAggregationLevel.MONTHLY }
                ) {
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

private fun checkAggregationLevels(
    first: AggregationLevel?,
    second: AggregationLevel?,
    toBeChecked: AggregationLevel
): Int {
    return if (first == toBeChecked) 1 else if (second == toBeChecked) 2 else 0
}

private fun checkEnableAggregationLevelChip(
    first: AggregationLevel?,
    second: AggregationLevel?,
    toBeChecked: AggregationLevel
): Boolean {
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
        leadingIcon = {
            if (numberDetermination() == 1) ChipIcon()
            else if (numberDetermination() == 2) ChipIcon(false) }
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
        Icon(painterResource(id = R.drawable.looks_one), contentDescription = "first")
    } else {
        Icon(painterResource(id = R.drawable.looks_two), contentDescription = "second")
    }
}

@Composable
fun ChipGroup(label: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(top = 10.dp)) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 10.dp),
            color = Color.Gray,
            fontSize = 11.sp
        )

        Row(modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            content()
        }
    }
}
