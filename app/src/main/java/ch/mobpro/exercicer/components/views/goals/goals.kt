package ch.mobpro.exercicer.components.views.goals

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ch.mobpro.exercicer.components.*
import ch.mobpro.exercicer.components.cards.BaseCard
import ch.mobpro.exercicer.components.cards.CardContentRow
import ch.mobpro.exercicer.components.cards.SmallBadge
import ch.mobpro.exercicer.components.date.DatePickerField
import ch.mobpro.exercicer.data.entity.Goal
import ch.mobpro.exercicer.data.entity.Sport
import ch.mobpro.exercicer.data.entity.TrainingType
import ch.mobpro.exercicer.data.entity.mapping.GoalSportTrainingTypeMapping
import ch.mobpro.exercicer.data.util.getFormattedDistance
import ch.mobpro.exercicer.data.util.getFormattedString
import ch.mobpro.exercicer.data.util.getFormattedTime
import ch.mobpro.exercicer.data.util.getTimeSum
import ch.mobpro.exercicer.viewmodel.AchievementViewModel
import ch.mobpro.exercicer.viewmodel.GoalViewModel
import ch.mobpro.exercicer.viewmodel.SportViewModel
import java.time.LocalDate

@Composable
fun GoalsPage() {
    val goalViewModel: GoalViewModel = hiltViewModel()
    val sportViewModel: SportViewModel = hiltViewModel()

    var showDialog by remember {
        mutableStateOf(false)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                showDialog = true
            }) {
                Icon(Icons.Default.Add, "add button")
            }
        }
    ) { paddingValues ->
        Page(modifier = Modifier.padding(paddingValues), title = "Ziele") {
            GoalsList(goalViewModel, sportViewModel)

            var editableGoal by remember {
                mutableStateOf(Goal())
            }

            if (showDialog) {
                FullScreenGoalDialog(
                    title = "Neues Ziel",
                    goal = editableGoal,
                    sportViewModel = sportViewModel,
                    visibilityChange = {visibility -> showDialog = visibility}
                ) {
                    goalViewModel.insert(editableGoal)
                    showDialog = false
                    editableGoal = Goal()
                }

//                FullScreenDialog(
//                    title = "Neues Ziel",
//                    visible = showDialog,
//                    onClose = { showDialog = false },
//                    onSave = {
//                        goalViewModel.insert(editableGoal)
//                        showDialog = false
//                        editableGoal = Goal()
//                    }
//                ) {
//                    val sportMap = sportViewModel.sportMap.collectAsState().value
//                    val sports = sportMap.keys
//                    val trainingTypes = sportMap.values.toSet()
//                    GoalDialog(
//                        editableGoal,
//                        sports.find { it.id == editableGoal.sportId } ?: sports.first(),
//                        sportMap.keys.toList(),
//                        sportMap.values.find { it.id == editableGoal.trainingTypeId } ?: trainingTypes.first(),
//                        trainingTypes.toList()
//                    )
//                }
            }
        }
    }
}

@Composable
fun GoalCard(
    goalMapping: GoalSportTrainingTypeMapping,
    achievementViewModel: AchievementViewModel,
    goalViewModel: GoalViewModel,
    sportViewModel: SportViewModel
) {
    val goal = goalMapping.goal
    val isSport = goalMapping.sport != null

    var showDialog by remember {
        mutableStateOf(false)
    }

    BaseCard(onClick = { showDialog = true }) {
        CardContentRow {
            SmallBadge(if (isSport) goalMapping.sport!!.name else goalMapping.trainingType!!.name)
            LabeledText(goal.start.getFormattedString(), "Start")
            LabeledText(goal.end.getFormattedString(), "Ende")
        }

        Spacer(modifier = Modifier.padding(vertical = 3.dp))

        val sum by remember {
            val flow = if (goal.sportId != null) achievementViewModel.getSportSumsByDate(goal.sportId!!, goal.start, goal.end)
            else achievementViewModel.getTrainingTypeSumsByDate(goal.trainingTypeId!!, goal.start, goal.end)
            mutableStateOf(flow)
        }
        val sumSeconds = sum.observeAsState().value?.sumSeconds ?: 0
        val sumDistance = sum.observeAsState().value?.sumMeters ?: 0

        if (goal.distanceGoalInMetres != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .padding(end = 5.dp)
                        .weight(1f),
                    text = getFormattedDistance(sumDistance, goal.distanceUnit)!!,
                    textAlign = TextAlign.Left
                )
                StatusBar(Modifier.weight(3f), sumDistance.toFloat(), goal.distanceGoalInMetres?.toFloat() ?: 0f)
                Text(
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .weight(1f),
                    text = getFormattedDistance(goal.distanceGoalInMetres, goal.distanceUnit)!!,
                    textAlign = TextAlign.Right
                )
            }
        }
        val hasTimeGoal = goal.trainingTimeGoalHours != null ||
                goal.trainingTimeGoalMinutes != null ||
                goal.trainingTimeGoalSeconds != null

        if (hasTimeGoal) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val target = getTimeSum(
                    goal.trainingTimeGoalHours,
                    goal.trainingTimeGoalMinutes,
                    goal.trainingTimeGoalSeconds
                )?.toFloat() ?: 0f

                Text(
                    modifier = Modifier
                        .padding(end = 5.dp)
                        .weight(1f),
                    text = getFormattedTime(sumSeconds),
                    textAlign = TextAlign.Left
                )
                StatusBar(Modifier.weight(3f), sumSeconds.toFloat(), target)
                Text(
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .weight(1f),
                    text = getFormattedTime(
                        goal.trainingTimeGoalHours,
                        goal.trainingTimeGoalMinutes,
                        goal.trainingTimeGoalMinutes
                    )!!,
                    textAlign = TextAlign.Right
                )
            }
        }
    }

    if (showDialog) {
        FullScreenGoalDialog(
            title = "Ziel bearbeiten",
            goal = goal,
            sportViewModel = sportViewModel,
            visibilityChange = { visibility -> showDialog = visibility }
        ) {
            goalViewModel.update(goal)
            showDialog = false
        }
    }
}

@Composable
fun GoalsList(
    goalViewModel: GoalViewModel,
    sportViewModel: SportViewModel
) {
    val goalList = goalViewModel.goalList.collectAsState().value
    val achievementViewModel: AchievementViewModel = hiltViewModel()
    LazyColumn {
        items(goalList, key = {item -> item.goal.id!!}, itemContent = { item ->
            ItemDeleteAction(
                item = item.goal,
                dismissAction = { goalToDismiss ->
                    goalViewModel.delete(goalToDismiss as Goal)
                }
            ) {
                GoalCard(item, achievementViewModel, goalViewModel, sportViewModel)
            }
        })
    }
}

@Composable
fun FullScreenGoalDialog(
    title: String,
    goal: Goal,
    sportViewModel: SportViewModel,
    visibilityChange: (Boolean) -> Unit,
    onSave: () -> Unit
) {

    FullScreenDialog(
        title = title,
        visible = true,
        onClose = { visibilityChange(false) },
        onSave = onSave
    ) {
        val sportMap = sportViewModel.sportMap.collectAsState().value
        val sports = sportMap.keys
        val trainingTypes = sportMap.values.toSet()
        GoalDialog(
            goal,
            sports.find { it.id == goal.sportId } ?: sports.first(),
            sportMap.keys.toList(),
            sportMap.values.find { it.id == goal.trainingTypeId } ?: trainingTypes.first(),
            trainingTypes.toList()
        )
    }
}

@Composable
fun GoalDialog(
    goal: Goal,
    sport: Sport?,
    sportList: List<Sport>,
    trainingType: TrainingType?,
    trainingTypeList: List<TrainingType>
) {
    val start by remember {
        mutableStateOf(LocalDate.now())
    }

    val end by remember {
        mutableStateOf(LocalDate.now())
    }

    var isTrainingTypeGoal by remember {
        mutableStateOf(false)
    }

    var hasTimeGoal by remember {
        mutableStateOf(false)
    }

    var hasDistanceGoal by remember {
        mutableStateOf(false)
    }

    goal.sportId = sport?.id
    goal.trainingTypeId = trainingType?.id

    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)) {
                DatePickerField(start, "Von") { date ->
                    goal.start = date
                }
            }

            Spacer(modifier = Modifier.padding(horizontal = 20.dp))

            Column(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)) {
                DatePickerField(end, "Bis") { date ->
                    goal.end = date
                }
            }
        }

        LabeledSwitch(
            label = "Trainingsart",
            onCheckedChange = {
                isTrainingTypeGoal = it
            }
        )

        if (isTrainingTypeGoal) {
            Dropdown(
                title = "Trainingsart",
                list = trainingTypeList,
                selectedItem = trainingType
            ) {
                goal.trainingTypeId = (it as TrainingType).id
            }
        }

        if (!isTrainingTypeGoal) {
            Dropdown(
                title = "Sportart",
                list = sportList,
                selectedItem = sport
            ) {
                goal.trainingTypeId = (it as TrainingType).id
            }
        }

        LabeledSwitch(
            label = "Zeitziel",
            onCheckedChange = {
                hasTimeGoal = it
            }
        )

        if (hasTimeGoal) {
            SecondsTimePicker { hours, minutes, seconds ->
                goal.trainingTimeGoalHours = hours
                goal.trainingTimeGoalMinutes = minutes
                goal.trainingTimeGoalSeconds = seconds
            }
        }

        LabeledSwitch(
            label = "Distanzziel",
            onCheckedChange = {
                hasDistanceGoal = it
            }
        )

        if (hasDistanceGoal) {
            val distance = (goal.distanceGoalInMetres ?: 0) / goal.distanceUnit.multiplicator

            DistancePicker(distance, goal.distanceUnit) { dist, distUnit ->
                goal.distanceGoalInMetres = dist * distUnit.multiplicator
                goal.distanceUnit = distUnit
            }
        }
    }
}
