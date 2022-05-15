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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ch.mobpro.exercicer.components.ItemDeleteAction
import ch.mobpro.exercicer.components.LabeledText
import ch.mobpro.exercicer.components.Page
import ch.mobpro.exercicer.components.StatusBar
import ch.mobpro.exercicer.components.cards.BaseCard
import ch.mobpro.exercicer.components.cards.CardContentRow
import ch.mobpro.exercicer.components.cards.SmallBadge
import ch.mobpro.exercicer.data.entity.Goal
import ch.mobpro.exercicer.data.entity.mapping.GoalSportTrainingTypeMapping
import ch.mobpro.exercicer.data.entity.mapping.SummingWrapper
import ch.mobpro.exercicer.data.util.getFormattedDistance
import ch.mobpro.exercicer.data.util.getFormattedString
import ch.mobpro.exercicer.data.util.getFormattedTime
import ch.mobpro.exercicer.data.util.getTimeSum
import ch.mobpro.exercicer.viewmodel.AchievementViewModel
import ch.mobpro.exercicer.viewmodel.GoalViewModel

@Composable
fun GoalsPage() {
    val goalViewModel: GoalViewModel = hiltViewModel()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { }) {
                Icon(Icons.Default.Add, "add button")
            }
        }
    ) { paddingValues ->
        Page(modifier = Modifier.padding(paddingValues), title = "Ziele") {
            GoalsList(goalViewModel = goalViewModel)
        }
    }
}

@Composable
fun GoalCard(goalMapping: GoalSportTrainingTypeMapping, achievementViewModel: AchievementViewModel) {
    val goal = goalMapping.goal
    val isSport = goalMapping.sport != null

    BaseCard {
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
                StatusBar(sumDistance.toFloat(), goal.distanceGoalInMetres?.toFloat() ?: 0f)
                Text(
                    modifier = Modifier.padding(start = 5.dp),
                    text = getFormattedDistance(goal.distanceGoalInMetres, goal.distanceUnit)!!
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
                StatusBar(sumSeconds.toFloat(), target)
                Text(
                    modifier = Modifier.padding(start = 5.dp),
                    text = getFormattedTime(goal.trainingTimeGoalHours, goal.trainingTimeGoalMinutes, goal.trainingTimeGoalMinutes)!!
                )
            }
        }
    }
}

@Composable
fun GoalsList(goalViewModel: GoalViewModel) {
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
                GoalCard(item, achievementViewModel)
            }
        })
    }
}

@Composable
fun GoalDialog() {

}