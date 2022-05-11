package ch.mobpro.exercicer.components.date

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import ch.mobpro.exercicer.data.util.getFormattedString
import java.time.LocalDate

@Composable
fun DatePickerField(labelText: String, onValueChange: (LocalDate) -> Unit) {
    var dateState by remember {
        mutableStateOf(LocalDate.now())
    }

    val datePickerDialog = DatePickerDialog(
        LocalContext.current,
        { _, year, month, day ->
            dateState = LocalDate.of(year, month, day)
            onValueChange(dateState)
        },
        dateState.year,
        dateState.monthValue,
        dateState.dayOfMonth
    )

    Box {
        OutlinedTextField(value = dateState.getFormattedString(),
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.padding(3.dp).height(55.dp),
            label = { Text(labelText) },
            leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = "date range icon") }
        )

        // Box to handle to clickable action
        Box(modifier = Modifier.matchParentSize().clickable { datePickerDialog.show() })
    }
}