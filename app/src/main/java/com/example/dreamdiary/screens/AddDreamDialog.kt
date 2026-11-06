package com.example.dreamdiary.components

import android.content.Context
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.dreamdiary.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Список ID настроений из strings.xml
private val availableMoodResIds = listOf(
    R.string.mood_calm,
    R.string.mood_joy,
    R.string.mood_surprise,
    R.string.mood_scary,
    R.string.mood_sadness
)

/**
 * Отрисовка визуала ползунка скролла (Scrollbar)
 */
fun Modifier.drawScrollbar(
    scrollState: ScrollState,
    color: Color
): Modifier = drawWithContent {
    drawContent()
    if (scrollState.maxValue > 0) {
        val elementHeight = size.height / (scrollState.maxValue + size.height)
        val scrollbarHeight = (size.height * elementHeight).coerceAtLeast(36.dp.toPx())
        val scrollbarOffsetY = (scrollState.value.toFloat() / scrollState.maxValue.toFloat()) * (size.height - scrollbarHeight)

        drawRoundRect(
            color = color,
            topLeft = Offset(size.width - 2.dp.toPx(), scrollbarOffsetY),
            size = Size(4.dp.toPx(), scrollbarHeight),
            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
        )
    }
}

/**
 * Чтение списка тегов из файла res/raw/tags.txt через BufferedReader
 */
private fun loadTagsFromRaw(context: Context): List<String> {
    return try {
        context.resources.openRawResource(R.raw.tags)
            .bufferedReader()
            .use { reader ->
                reader.readLines()
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
            }
    } catch (e: Exception) {
        listOf("Вода", "Природа", "Город", "Путешествие", "Семья", "Работа")
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddDreamDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    // Поля ввода
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Настроение (null пока не выбрано пользователем)
    var selectedMoodResId by remember { mutableStateOf<Int?>(null) }

    // Чтение тегов из файла res/raw/tags.txt
    val availableTags = remember(context) { loadTagsFromRaw(context) }
    val selectedTags = remember { mutableStateListOf<String>() }

    // Состояние скролла
    val scrollState = rememberScrollState()
    val scrollbarColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)

    // Автоматическая дата
    val currentDate = remember {
        SimpleDateFormat("d MMMM yyyy, HH:mm", Locale("ru")).format(Date())
    }

    // Проверка заполнения всех 4 полей
    val isFormValid = title.isNotBlank() &&
            description.isNotBlank() &&
            selectedMoodResId != null &&
            selectedTags.isNotEmpty()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.button_add_dream),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 480.dp)
                    .drawScrollbar(scrollState, scrollbarColor) // Анимированный ползунок скролла
                    .padding(end = 16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Автоматическая дата
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = currentDate,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }

                // 2. Название сна
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Название сна *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // 3. Описание сна
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Описание сна *") },
                    placeholder = { Text("Опишите подробности вашего сновидения...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    minLines = 6,
                    shape = RoundedCornerShape(12.dp)
                )

                // 4. Настроение
                Text(
                    text = "Настроение:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    availableMoodResIds.forEach { moodResId ->
                        val isSelected = selectedMoodResId == moodResId
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedMoodResId = moodResId },
                            label = { Text(stringResource(moodResId)) },
                            shape = RoundedCornerShape(12.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }

                // 5. Теги из файла res/raw/tags.txt
                Text(
                    text = "Теги:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    availableTags.forEach { tagName ->
                        val isSelected = selectedTags.contains(tagName)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                if (isSelected) {
                                    selectedTags.remove(tagName)
                                } else {
                                    selectedTags.add(tagName)
                                }
                            },
                            label = { Text("#$tagName") },
                            leadingIcon = if (isSelected) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                            } else null,
                            shape = RoundedCornerShape(10.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondary,
                                selectedLabelColor = MaterialTheme.colorScheme.onSecondary
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                enabled = isFormValid // Заблокировано, пока НЕ заполнены все 4 блока
            ) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}