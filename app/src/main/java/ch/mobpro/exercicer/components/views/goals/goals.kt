package ch.mobpro.exercicer.components.views.goals

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import ch.mobpro.exercicer.data.util.*
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

    val context = LocalContext.current

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
                ) { goalValidation ->
                    if (!goalValidation.validate()) {
                        Toast.makeText(context, goalValidation.getFirstEntryMessage(), Toast.LENGTH_LONG).show()
                    } else {
                        goalViewModel.insert(editableGoal)
                        showDialog = false
                        editableGoal = Goal()
                    }
                }
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

    val context = LocalContext.current

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
        val sumSeconds = sum.observeAsState().value?.sumTime ?: 0
        val sumDistance = sum.observeAsState().value?.sumDistance ?: 0f
        val sumTimes = sum.observeAsState().value?.sumTimes ?: 0
        val maxWeight = sum.observeAsState().value?.maxWeight ?: 0f

        // distance goal
        if (goal.distanceGoalInMetres != 0f) {
            GoalStatusBar(
                goalLabel = "Distanz",
                targetText = getFormattedDistance(goal.distanceGoalInMetres, goal.distanceUnit)!!,
                target = goal.distanceGoalInMetres,
                effectiveText = getFormattedDistance(sumDistance, goal.distanceUnit)!!,
                effective = sumDistance
            )
        }

        // time goal
        val hasTimeGoal = goal.trainingTimeGoalHours != 0 ||
                goal.trainingTimeGoalMinutes != 0 ||
                goal.trainingTimeGoalSeconds != 0

        if (hasTimeGoal) {
            val target = getTimeSum(
                goal.trainingTimeGoalHours,
                goal.trainingTimeGoalMinutes,
                goal.trainingTimeGoalSeconds
            ).toFloat()

            GoalStatusBar(
                goalLabel = "Zeit",
                targetText = getFormattedTime(
                    goal.trainingTimeGoalHours,
                    goal.trainingTimeGoalMinutes,
                    goal.trainingTimeGoalMinutes
                ),
                target = target,
                effectiveText = getFormattedTime(sumSeconds),
                effective = sumSeconds.toFloat()
            )
        }

        // times goal
        if (goal.numberOfTimesGoal != 0) {
            GoalStatusBar(
                goalLabel = "Anzahl",
                targetText = getFormattedTimes(goal.numberOfTimesGoal),
                target = goal.numberOfTimesGoal.toFloat(),
                effectiveText = getFormattedTimes(sumTimes),
                effective = sumTimes.toFloat()
            )
        }

        // weight goal
        if (goal.weightGoal != 0f) {
            GoalStatusBar(
                goalLabel = "Gewicht",
                targetText = getFormattedWeight(goal.weightGoal),
                target = goal.weightGoal,
                effectiveText = getFormattedWeight(maxWeight),
                effective = maxWeight
            )
        }

    }

    if (showDialog) {
        FullScreenGoalDialog(
            title = "Ziel bearbeiten",
            goal = goal,
            sportViewModel = sportViewModel,
            visibilityChange = { visibility -> showDialog = visibility }
        ) { goalValidation ->
            if (!goalValidation.validate()) {
                Toast.makeText(context, goalValidation.getFirstEntryMessage(), Toast.LENGTH_LONG).show()
            } else {
                goalViewModel.update(goal)
                showDialog = false
            }
        }
    }
}

@Composable
fun GoalStatusBar(
    goalLabel: String,
    targetText: String,
    target: Float,
    effectiveText: String,
    effective: Float
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = goalLabel,
            modifier = Modifier
                .padding(end = 5.dp)
                .weight(0.5f),
            textAlign = TextAlign.Left,
            fontSize = 10.sp
        )

        Text(
            modifier = Modifier
                .padding(end = 5.dp)
                .weight(1f),
            text = effectiveText,
            textAlign = TextAlign.Left
        )

        StatusBar(Modifier.weight(2f), effective, target)

        Text(
            modifier = Modifier
                .padding(start = 5.dp)
                .weight(1f),
            text = targetText,
            textAlign = TextAlign.Right
        )
    }
}

@Composable
fun GoalsList(
    goalViewModel: GoalViewModel,
    sportViewModel: SportViewModel
) {
    val goalList = goalViewModel.goalList.collectAsState().value
    val achievementViewModel: AchievementViewModel = hiltViewModel()

    val expiredGoals = goalList.filter { it.goal.end > LocalDate.now() }
    val activeGoals = goalList.filter { it.goal.end <= LocalDate.now() }

    var showArchivedGoals by remember {
        mutableStateOf(false)
    }

    SideLabeledSwitch(
        initialState = showArchivedGoals,
        labelLeft = "Aktive Ziele",
        labelRight = "Archivierte Ziele",
        onCheckedChange = {
            showArchivedGoals = it
        }
    )

    LazyColumn {
        items(if (showArchivedGoals) expiredGoals else activeGoals,
            key = {item -> item.goal.id!!},
            itemContent = { item ->
                ItemDeleteAction(
                    item = item.goal,
                    dismissAction = { goalToDismiss ->
                        goalViewModel.delete(goalToDismiss as Goal)
                    }
                ) {
                    GoalCard(item, achievementViewModel, goalViewModel, sportViewModel)
                }
            }
        )
    }
}

@Composable
fun FullScreenGoalDialog(
    title: String,
    goal: Goal,
    sportViewModel: SportViewModel,
    visibilityChange: (Boolean) -> Unit,
    onSave: (Validation) -> Unit
) {
    val goalValidation by remember {
        val validation = Validation()
        validation.addValidationEntry(ValidationEntry.ANY_SET)
        mutableStateOf(validation)
    }

    FullScreenDialog(
        title = title,
        visible = true,
        onClose = { visibilityChange(false) },
        onSave = {
            onSave(goalValidation)
        }
    ) {
        val sportMap = sportViewModel.sportMap.collectAsState().value
        val sports = sportMap.keys.toList()
        val trainingTypes = sportMap.values.toSet().toList()

        if (goal.sportId == null && goal.trainingTypeId == null) {
            goal.sportId = sports.firstOrNull()?.id ?: -1
        }

        GoalDialog(
            goal,
            sports,
            trainingTypes,
            goalValidation
        )
    }
}

@Composable
fun GoalDialog(
    goal: Goal,
    sportList: List<Sport>,
    trainingTypeList: List<TrainingType>,
    validation: Validation
) {
    var start by remember {
        mutableStateOf(goal.start)
    }

    var end by remember {
        mutableStateOf(goal.end)
    }

    var isTrainingTypeGoal by remember {
        mutableStateOf(goal.trainingTypeId != null)
    }

    var selectedTrainingType by remember {
        mutableStateOf(trainingTypeList.find {
            it.id == goal.trainingTypeId
        } ?: trainingTypeList.firstOrNull() ?: TrainingType(name = ""))
    }

    var selectedSport by remember {
        mutableStateOf(sportList.find {
            it.id == goal.sportId
        } ?: sportList.firstOrNull() ?: Sport(name = ""))
    }

    var hasTimeGoal by remember {
        mutableStateOf(goal.trainingTimeGoalHours != 0 || goal.trainingTimeGoalMinutes != 0 || goal.trainingTimeGoalSeconds != 0)
    }

    var hourTimeGoal by remember {
        mutableStateOf(goal.trainingTimeGoalHours)
    }

    var minutesTimeGoal by remember {
        mutableStateOf(goal.trainingTimeGoalMinutes)
    }

    var secondsTimeGoal by remember {
        mutableStateOf(goal.trainingTimeGoalSeconds)
    }

    var hasDistanceGoal by remember {
        mutableStateOf(goal.distanceGoalInMetres != 0f)
    }

    var distanceGoal by remember {
        mutableStateOf(goal.distanceGoalInMetres)
    }

    var currentDistanceUnit by remember {
        mutableStateOf(goal.distanceUnit)
    }

    var hasTimesGoal by remember {
        mutableStateOf(goal.numberOfTimesGoal != 0)
    }

    var timesGoal by remember {
        mutableStateOf(goal.numberOfTimesGoal)
    }

    var hasWeightGoal by remember {
        mutableStateOf(goal.weightGoal != 0f)
    }

    var weightGoal by remember {
        mutableStateOf(goal.weightGoal)
    }

    Column(Modifier.verticalScroll(rememberScrollState())) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)) {
                DatePickerField(start, "Von") { date ->
                    start = date
                    goal.start = start
                    validateDatePair(start, end, validation)
                }
            }

            Spacer(modifier = Modifier.padding(horizontal = 20.dp))

            Column(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)) {
                DatePickerField(end, "Bis") { date ->
                    end = date
                    goal.end = end
                    validateDatePair(start, end, validation)
                }
            }
        }

        // what does the goal apply to?
        SideLabeledSwitch(
            initialState = isTrainingTypeGoal,
            labelLeft = "Sportart",
            labelRight = "Trainingsart",
            onCheckedChange = {
                isTrainingTypeGoal = it
                if (isTrainingTypeGoal) {
                    goal.sportId = null
                    goal.trainingTypeId = selectedTrainingType.id

                    validateTrainingType(selectedTrainingType, validation)
                    validation.removeValidationEntry(ValidationEntry.SPORT)
                } else {
                    goal.trainingTypeId = null
                    goal.sportId = selectedSport.id

                    validateSport(selectedSport, validation)
                    validation.removeValidationEntry(ValidationEntry.TRAINING_TYPE)
                }
            }
        )

        if (isTrainingTypeGoal) {
            Dropdown(
                title = "Trainingsart",
                list = trainingTypeList,
                selectedItem = selectedTrainingType
            ) {
                val newTrainingType = it as TrainingType
                selectedTrainingType = newTrainingType
                goal.trainingTypeId = selectedTrainingType.id

                validateTrainingType(selectedTrainingType, validation)
            }
        }

        if (!isTrainingTypeGoal) {
            Dropdown(
                title = "Sportart",
                list = sportList,
                selectedItem = selectedSport
            ) {
                val newSport = it as Sport
                selectedSport = newSport
                goal.sportId = selectedSport.id

                validateSport(selectedSport, validation)
            }
        }

        // time goal
        if (isTrainingTypeGoal || selectedSport.hasTime) {
            LabeledSwitch(
                initialState = hasTimeGoal,
                label = "Zeitziel",
                onCheckedChange = {
                    hasTimeGoal = it
                    if (!hasTimeGoal) {
                        goal.trainingTimeGoalHours = 0
                        goal.trainingTimeGoalMinutes = 0
                        goal.trainingTimeGoalSeconds = 0

                        validation.removeValidationEntry(ValidationEntry.TIME)
                    } else {
                        goal.trainingTimeGoalHours = hourTimeGoal
                        goal.trainingTimeGoalMinutes = minutesTimeGoal
                        goal.trainingTimeGoalSeconds = secondsTimeGoal

                        validateTime(hourTimeGoal, minutesTimeGoal, secondsTimeGoal, validation)
                    }

                    validateAnySet(hasTimeGoal, hasDistanceGoal, hasTimesGoal, hasWeightGoal, validation)
                }
            )

            if (hasTimeGoal) {
                SecondsTimePicker(hourTimeGoal, minutesTimeGoal, secondsTimeGoal) { hours, minutes, seconds ->
                    hourTimeGoal = hours
                    minutesTimeGoal = minutes
                    secondsTimeGoal = seconds
                    goal.trainingTimeGoalHours = hourTimeGoal
                    goal.trainingTimeGoalMinutes = minutesTimeGoal
                    goal.trainingTimeGoalSeconds = secondsTimeGoal

                    validateTime(hourTimeGoal, minutesTimeGoal, secondsTimeGoal, validation)
                }
            }
        }

        // distance goal
        if (isTrainingTypeGoal || selectedSport.hasDistance) {
            LabeledSwitch(
                initialState = hasDistanceGoal,
                label = "Distanzziel",
                onCheckedChange = {
                    hasDistanceGoal = it
                    if (!hasDistanceGoal) {
                        goal.distanceGoalInMetres = 0f
                        validation.removeValidationEntry(ValidationEntry.DISTANCE)
                    } else {
                        goal.distanceGoalInMetres = distanceGoal
                        validateDistance(distanceGoal, validation)
                    }

                    validateAnySet(hasTimeGoal, hasDistanceGoal, hasTimesGoal, hasWeightGoal, validation)
                }
            )

            if (hasDistanceGoal) {
                val distance = distanceGoal / currentDistanceUnit.multiplicator

                DistancePicker(distance, goal.distanceUnit) { dist, distUnit ->
                    distanceGoal = dist * distUnit.multiplicator
                    currentDistanceUnit = distUnit

                    goal.distanceGoalInMetres = distanceGoal
                    goal.distanceUnit = currentDistanceUnit

                    validateDistance(distanceGoal, validation)
                }
            }
        }

        // times goal
        if (isTrainingTypeGoal || selectedSport.hasNumberOfTimes) {
            LabeledSwitch(
                initialState = hasTimesGoal,
                label = "Ziele Anzahl Male",
                onCheckedChange = {
                    hasTimesGoal = it
                    if (!hasTimesGoal) {
                        goal.numberOfTimesGoal = 0
                        validation.removeValidationEntry(ValidationEntry.NUMBER_OF_TIMES)
                    } else {
                        goal.numberOfTimesGoal = timesGoal
                        validateTimes(timesGoal, validation)
                    }

                    validateAnySet(hasTimeGoal, hasDistanceGoal, hasTimesGoal, hasWeightGoal, validation)
                }
            )

            if (hasTimesGoal) {
                NumberInput(
                    label = "Anzahl Male",
                    initialValue = timesGoal,
                    onValueChange = {
                        timesGoal = it
                        goal.numberOfTimesGoal = timesGoal
                        validateTimes(timesGoal, validation)
                    })
            }
        }

        // weight goal
        if (isTrainingTypeGoal || selectedSport.hasWeight) {
            LabeledSwitch(
                initialState = hasWeightGoal,
                label = "Ziel Trainingsgewicht",
                onCheckedChange = {
                    hasWeightGoal = it
                    if (!hasWeightGoal) {
                        goal.weightGoal = 0f
                        validation.removeValidationEntry(ValidationEntry.WEIGHT)
                    } else {
                        goal.weightGoal = weightGoal
                        validateWeight(weightGoal, validation)
                    }

                    validateAnySet(hasTimeGoal, hasDistanceGoal, hasTimesGoal, hasWeightGoal, validation)
                }
            )

            if (hasWeightGoal) {
                NumberInput(
                    label = "Gewicht",
                    initialValue = weightGoal,
                    onValueChange = {
                        weightGoal = it
                        goal.weightGoal = it
                        validateWeight(weightGoal, validation)
                    })
            }
        }
    }
}
