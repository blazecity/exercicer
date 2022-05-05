package ch.mobpro.exercicer.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.mobpro.exercicer.R
import ch.mobpro.exercicer.data.entity.Sport
import ch.mobpro.exercicer.data.entity.Training


@Composable
fun SmallBadge(
    content: String,
    fontColor: Color = Color.Black,
    backgroundColor: Color = Color.LightGray
) {
    Card(backgroundColor = backgroundColor) {
        Text(
            content, 
            modifier = Modifier.padding(3.dp),
            color = fontColor,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TrainingCard(
    modifier: Modifier = Modifier,
    sport: Sport,
    fontColorBadge: Color,
    backgroundColorBadge: Color,
    training: Training) {
    Card(
        modifier = modifier
            .padding(7.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(CornerSize(5.dp)),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
               ) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text(sport.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                SmallBadge(sport.trainingType.name, fontColorBadge, backgroundColorBadge)
            }
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text(training.date.toString())
                }

                Column(horizontalAlignment = Alignment.Start) {

                    Row {
                       val trainingTime = training.getFormattedTrainingTime()
                       val trainingDistance = training.getFormattedTrainingDistance()
                       val trainingRepeats = training.getFormattedRepeatsAndSets()
                       Column(modifier = Modifier.padding(end = 10.dp)) {
                           if (trainingTime != null) Text(stringResource(id = R.string.time))
                           if (trainingDistance != null) Text(stringResource(id = R.string.distance))
                           if (trainingRepeats != null) Text(stringResource(id = R.string.repeats))
                       }
                        Column(horizontalAlignment = Alignment.End) {
                            if (trainingTime != null) Text(trainingTime)
                            if (trainingDistance != null) Text(trainingDistance)
                            if (trainingRepeats != null) Text(trainingRepeats)
                        }
                    }
                }
            }
        }

    }
}
