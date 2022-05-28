package ch.mobpro.exercicer.components.cards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ch.mobpro.exercicer.ui.theme.LightYellow


@Composable
fun SmallBadge(
    content: String,
    fontColor: Color = Color.Black,
    backgroundColor: Color = LightYellow
) {
    Card(shape = RoundedCornerShape(8.dp), backgroundColor = backgroundColor) {
        Text(
            content, 
            modifier = Modifier.padding(5.dp),
            color = fontColor,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun BaseCard(onClick: () -> Unit = {}, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.padding(vertical = 7.dp).fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(CornerSize(5.dp)),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
        ) {
            content()
        }
    }
}

@Composable
fun CardTitleRow(content: @Composable () -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {

        content()
    }
}

@Composable
fun CardContentRow(content: @Composable () -> Unit) {
    Row(modifier = Modifier
        .height(IntrinsicSize.Min)
        .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top) {

        content()
    }
}

@Composable
fun CardContentColumn(modifier: Modifier, content: @Composable () -> Unit) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = modifier
            .padding(5.dp,15.dp, 5.dp, 15.dp)
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Top
    ) {
        content()
    }
}
