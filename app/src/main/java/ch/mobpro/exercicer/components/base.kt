package ch.mobpro.exercicer.components

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import ch.mobpro.exercicer.components.views.ScreenTitle
import ch.mobpro.exercicer.data.entity.DistanceUnit
import ch.mobpro.exercicer.ui.theme.LightRed
import kotlinx.coroutines.delay

@Composable
fun Page(modifier: Modifier = Modifier, title: String, content: @Composable () -> Unit) {
    Column(modifier = modifier.padding(10.dp)) {
        ScreenTitle(title = title)
        content()
    }
}

@Composable
fun StatusBar(modifier: Modifier = Modifier, effective: Float, target: Float) {
    var progress by remember { mutableStateOf(0.0f) }
    val animatedProgress by animateFloatAsState(
        targetValue = progress
    )

    var enabled by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(enabled, effective, target) {
        while ((progress < effective / target)) {
            progress += 0.01f
            delay(1)
        }
    }

    if (progress >= 1f) {
        enabled = false
    }

    LinearProgressIndicator(
        progress = animatedProgress,
        modifier = modifier
            .height(13.dp)
            .clip(RoundedCornerShape(8.dp)),
        color = if (progress >= 1f) MaterialTheme.colors.primary else LightRed
    )

}

@Composable
fun LabeledText(content: String, label: String) {
    Column {
        Text(
            label,
            color = Color.Gray,
            fontSize = 11.sp
        )
        Text(content)
    }
}

@Composable
fun SideLabeledSwitch(
    initialState: Boolean,
    labelLeft: String,
    labelRight: String,
    onCheckedChange: (Boolean) -> Unit
) {
    var isChecked by remember {
        mutableStateOf(initialState)
    }

    Row(modifier = Modifier.padding(top = 15.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(
            labelLeft,
            color = Color.Gray,
            fontSize = 11.sp
        )

        Switch(
            checked = isChecked,
            onCheckedChange = {
                onCheckedChange(it)
                isChecked = it
            }
        )

        Text(
            labelRight,
            color = Color.Gray,
            fontSize = 11.sp
        )

    }
}

@Composable
fun LabeledSwitch(initialState: Boolean, label: String, onCheckedChange: (Boolean) -> Unit) {
    var isChecked by remember {
        mutableStateOf(initialState)
    }

    Column(modifier = Modifier.padding(top = 15.dp)) {
        Text(
            label,
            color = Color.Gray,
            fontSize = 11.sp
        )
        Switch(
            checked = isChecked,
            onCheckedChange = {
                onCheckedChange(it)
                isChecked = it
            }
        )
    }
}

@Composable
fun ListItem(
    title: String,
    description: String? = null,
    iconId: Int? = null,
    onClick: () -> Unit
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(15.dp)
        .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically) {

        if (iconId != null) {
            Icon(painterResource(id = iconId), contentDescription = "icon")
        }

        Column(modifier = Modifier.padding(start = 7.dp)) {
            Text(title, fontSize = 18.sp, modifier = Modifier.padding(bottom = 5.dp))
            if (description != null) {
                Text(description, fontSize = 14.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun ListDivider() {
    Divider(modifier = Modifier.padding(horizontal = 10.dp))
}

@Composable
fun ListSection(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerSize(10.dp)),
        elevation = 10.dp
    ) {
        Column {
            content()
        }
    }
}

interface Listable {
    val id: Long?
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ListDeleteAction(list: MutableList<Listable>, dismissAction: (Listable) -> Unit, onClick: (Listable) -> Unit) {
    ListSection {
        LazyColumn(Modifier.scrollable(rememberScrollState(), orientation = Orientation.Vertical)) {
            itemsIndexed(list, key = {_, listItem -> listItem.id!!}, itemContent = {index, item ->
                val dismissState = rememberDismissState()
                if (dismissState.isDismissed(DismissDirection.EndToStart)) {
                    dismissAction(item)
                    list.remove(item)
                }

                SwipeToDismiss(
                    state = dismissState,
                    directions = setOf(DismissDirection.EndToStart),
                    dismissThresholds = {
                        FractionalThreshold(if (it == DismissDirection.EndToStart) 0.1f else 0.05f)
                    },
                    background = {
                        val color by animateColorAsState(
                            when (dismissState.targetValue) {
                                DismissValue.Default -> Color.White
                                else -> LightRed
                            }
                        )

                        val scale by animateFloatAsState(
                            if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "delete icon",
                                modifier = Modifier
                                    .scale(scale)
                                    .padding(end = 7.dp)
                            )
                        }
                    }
                ) {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)) {
                        ListItem(title = item.toString(), onClick = { onClick(item) })

                        if (index < list.size - 1) {
                            ListDivider()
                        }
                    }
                }
            })
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ItemDeleteAction(
    item: Listable, dismissAction: (Listable) -> Unit,
    content: @Composable () -> Unit
) {
    val dismissState = rememberDismissState()
    if (dismissState.isDismissed(DismissDirection.EndToStart)) {
        dismissAction(item)
    }

    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.EndToStart),
        dismissThresholds = {
            FractionalThreshold(if (it == DismissDirection.EndToStart) 0.1f else 0.05f)
        },
        background = {
            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    DismissValue.Default -> Color.White
                    else -> LightRed
                }
            )

            val scale by animateFloatAsState(
                if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "delete icon",
                    modifier = Modifier
                        .scale(scale)
                        .padding(end = 7.dp)
                )
            }
        }
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
        ) {

            content()
        }
    }
}

@Composable
fun DetailPage(
    title: String,
    navController: NavController,
    onClickAdd: () -> Unit,
    content: @Composable () -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        topBar = {
            TopAppBar(backgroundColor = MaterialTheme.colors.background, elevation = 10.dp) {
                Row(modifier = Modifier.padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.Start) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "back arrow",
                            modifier = Modifier.clickable { navController.popBackStack() }
                        )

                        Text(
                            title,
                            modifier = Modifier.padding(start = 10.dp),
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontSize = 20.sp
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onClickAdd) {
                Icon(Icons.Default.Add, "add button")
            }
        }
    ) {
        Column(
            Modifier
                .padding(it)
                .padding(10.dp)) {
            content()
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FullScreenDialog(
    title: String,
    visible: Boolean,
    onClose: () -> Unit,
    onSave: () -> Unit,
    content: @Composable () -> Unit
) {

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically() + expandVertically(expandFrom = Alignment.Bottom) + fadeIn(initialAlpha = 0.3f),
        exit = slideOutVertically() + shrinkVertically(shrinkTowards = Alignment.Bottom) + fadeOut(targetAlpha = 0.3f)
    ) {
        Dialog(onDismissRequest = { }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            Surface(
                modifier = Modifier.padding(top = 10.dp, start = 5.dp, end = 5.dp),
                shape = RoundedCornerShape(10.dp),
                elevation = 10.dp
            ) {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.Start, modifier = Modifier.weight(1f)) {
                            Icon(
                                modifier = Modifier
                                    .clickable { onClose() },
                                imageVector = Icons.Default.Close,
                                contentDescription = "close dialog"
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(2f)) {
                            Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }

                        Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
                            TextButton(onClick = { onSave() }) {
                                Text("Speichern")
                            }
                        }
                    }

                    content()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Dropdown(
    title: String,
    list: List<Listable>,
    selectedItem: Listable? = null,
    onItemClick: (Listable) -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    var selectedOption by remember {
        mutableStateOf(selectedItem)
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {expanded = !expanded},
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            selectedOption.toString(),
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = title)},
            onValueChange = {},
            readOnly = true
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            list.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        selectedOption = item
                        expanded = false
                        onItemClick(item)
                    }
                ) {
                    Text(item.toString())
                }
            }
        }
    }
}

@Composable
fun DistancePicker(
    initialDistance: Float = 0f,
    initialDistanceUnit: DistanceUnit = DistanceUnit.KILOMETERS,
    onChange: (Float, DistanceUnit) -> Unit
) {
    var distance by remember {
        mutableStateOf(initialDistance)
    }

    var distanceUnit by remember {
        mutableStateOf(initialDistanceUnit)
    }

    Row(horizontalArrangement = Arrangement.SpaceBetween) {
        NumberInput(label = "Distanz", initialValue = distance) {
            distance = it
            onChange(distance, distanceUnit)
        }

        Dropdown(
            title = "Einheit",
            list = listOf(DistanceUnit.METERS, DistanceUnit.KILOMETERS),
            selectedItem = distanceUnit
        ) {
            distanceUnit = it as DistanceUnit
            onChange(distance, distanceUnit)
        }
    }
}

@Composable
fun SecondsTimePicker(
    hours: Int = 0,
    minutes: Int = 0,
    seconds: Int = 0,
    onChange: (Int, Int, Int) -> Unit
) {
    var timeHours by remember {
        mutableStateOf(hours)
    }

    var timeMinutes by remember {
        mutableStateOf(minutes)
    }

    var timeSeconds by remember {
        mutableStateOf(seconds)
    }

    Row(horizontalArrangement = Arrangement.SpaceBetween) {
        NumberInput(
            modifier = Modifier.weight(1f),
            label = "Stunden",
            initialValue = timeHours,
            onValueChange = {
                timeHours = it
                onChange(timeHours, timeMinutes, timeSeconds)
            }
        )

        NumberInput(
            modifier = Modifier.weight(1f),
            label = "Minuten",
            initialValue = timeMinutes,
            onValueChange = {
                timeMinutes = it
                onChange(timeHours, timeMinutes, timeSeconds)
            }
        )

        NumberInput(
            modifier = Modifier.weight(1f),
            label = "Sekunden",
            initialValue = timeSeconds,
            onValueChange = {
                timeSeconds = it
                onChange(timeHours, timeMinutes, timeSeconds)
            }
        )
    }
}

@Composable
inline fun <reified T: Number> NumberInput(
    modifier: Modifier = Modifier,
    label: String,
    initialValue: T,
    crossinline onValueChange: (T) -> Unit
) {
    var value by remember {
        mutableStateOf(initialValue)
    }

    var isError by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    OutlinedTextField(
        modifier = modifier.padding(end = 10.dp),
        textStyle = TextStyle(textAlign = TextAlign.Right),
        label = { Text(label) },
        value = value.toString(),
        isError = isError,
        onValueChange = {
            if (it.isEmpty()) {
                isError = true
            } else {
                isError = false
                try {
                    when (T::class) {
                        Float::class -> value = it.toFloat() as T
                        Int::class -> value = it.toInt() as T
                    }
                } catch (e: java.lang.NumberFormatException) {
                    Toast.makeText(context, "Es muss ein Zahlenwert eingegeben werden.", Toast.LENGTH_SHORT).show()
                }
                onValueChange(value)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}



