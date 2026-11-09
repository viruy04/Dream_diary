package com.example.dreamdiary.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dreamdiary.R
import com.example.dreamdiary.components.ScreenHeader
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private data class DreamStar(
    val title: String,
    val tags: List<String>,
    val x: Float,
    val y: Float
)

private fun DrawScope.drawStar(
    center: Offset,
    outerRadius: Float,
    color: Color
) {
    val innerRadius = outerRadius * 0.45f
    val path = Path()

    for (i in 0 until 16) {
        val angle = -PI / 2 + i * PI / 8
        val radius = if (i % 2 == 0) outerRadius else innerRadius

        val x = center.x + cos(angle).toFloat() * radius
        val y = center.y + sin(angle).toFloat() * radius

        if (i == 0) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
    }

    path.close()
    drawPath(path = path, color = color)
}

@Composable
fun ConstellationScreen(onBack: () -> Unit = {}) {
    var showInfo by remember { mutableStateOf(false) }
    var selectedDream by remember { mutableStateOf<DreamStar?>(null) }

    val colors = MaterialTheme.colorScheme

    val mapColor = colorResource(R.color.constellation_background)
    val nebulaColor1 = colorResource(R.color.constellation_nebula_primary)
    val nebulaColor2 = colorResource(R.color.constellation_nebula_secondary)

    val brightStar = colorResource(R.color.constellation_star_connected)
    val ordinaryStar = colorResource(R.color.constellation_star_ordinary)
    val backgroundStarColor = colorResource(R.color.constellation_star_background)

    val mapTextColor = colorResource(R.color.constellation_text)
    val labelColor = colorResource(R.color.constellation_label_background)
    val transparentColor = colorResource(R.color.constellation_transparent)

    val secondaryText = colors.onBackground.copy(alpha = 0.7f)

    val dreams = remember {
        listOf(
            DreamStar("Прогулка у моря", listOf("море", "свобода", "покой"), 0.25f, 0.19f),
            DreamStar("Полёт над облаками", listOf("полёт", "свобода", "небо"), 0.67f, 0.36f),
            DreamStar("Необычный город", listOf("город", "приключения"), 0.76f, 0.65f),
            DreamStar("Лесная тропинка", listOf("лес", "природа"), 0.18f, 0.55f),
            DreamStar("Старый дом", listOf("дом", "прошлое"), 0.40f, 0.79f)
        )
    }

    fun getConnectedDreams(dream: DreamStar): List<DreamStar> {
        return dreams.filter { other ->
            other != dream && dream.tags.any { tag -> tag in other.tags }
        }
    }

    // Считаем связи один раз, а не на каждой перерисовке Canvas
    val connectedFlags = remember(dreams) {
        dreams.map { dream -> getConnectedDreams(dream).isNotEmpty() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ScreenHeader(
            title = stringResource(R.string.constellation_title),
            onBack = onBack,
            trailing = {
                IconButton(onClick = { showInfo = true }) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Информация",
                        tint = colors.primary
                    )
                }
            }
        )

        Text(
            text = stringResource(R.string.constellation_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = secondaryText
        )

        Spacer(Modifier.height(12.dp))

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(
                    color = mapColor,
                    shape = RoundedCornerShape(18.dp)
                )
        ) {
            val mapWidth = maxWidth
            val mapHeight = maxHeight

            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(nebulaColor1, transparentColor),
                        center = Offset(size.width * 0.75f, size.height * 0.25f),
                        radius = size.width * 0.7f
                    )
                )

                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(nebulaColor2, transparentColor),
                        center = Offset(size.width * 0.15f, size.height * 0.8f),
                        radius = size.width * 0.6f
                    )
                )

                val backgroundStars = listOf(
                    0.05f to 0.12f, 0.17f to 0.08f, 0.82f to 0.10f, 0.94f to 0.25f,
                    0.08f to 0.37f, 0.91f to 0.49f, 0.05f to 0.75f, 0.88f to 0.84f,
                    0.55f to 0.93f, 0.43f to 0.06f, 0.73f to 0.91f
                )

                backgroundStars.forEachIndexed { index, point ->
                    drawCircle(
                        color = backgroundStarColor.copy(alpha = 0.3f + (index % 3) * 0.12f),
                        radius = if (index % 3 == 0) 2.dp.toPx() else 1.dp.toPx(),
                        center = Offset(size.width * point.first, size.height * point.second)
                    )
                }
            }

            Canvas(modifier = Modifier.fillMaxSize()) {
                val points = dreams.map { dream -> Offset(size.width * dream.x, size.height * dream.y) }

                for (i in dreams.indices) {
                    for (j in i + 1 until dreams.size) {
                        val commonTags = dreams[i].tags.any { tag -> tag in dreams[j].tags }
                        if (commonTags) {
                            drawLine(
                                color = brightStar.copy(alpha = 0.8f),
                                start = points[i],
                                end = points[j],
                                strokeWidth = 2.dp.toPx()
                            )
                        }
                    }
                }

                dreams.forEachIndexed { index, dream ->
                    val connected = connectedFlags[index]
                    val starColor = if (connected) brightStar else ordinaryStar
                    val radius = if (connected) 7.dp.toPx() else 5.dp.toPx()

                    if (connected) {
                        drawCircle(
                            color = starColor.copy(alpha = 0.16f),
                            radius = radius * 2,
                            center = points[index]
                        )
                    }

                    drawStar(
                        center = points[index],
                        outerRadius = radius,
                        color = starColor
                    )
                }
            }

            dreams.forEach { dream ->
                Box(
                    modifier = Modifier
                        .offset(x = mapWidth * dream.x - 22.dp, y = mapHeight * dream.y - 22.dp)
                        .size(44.dp)
                        .clickable { selectedDream = dream },
                    contentAlignment = Alignment.Center
                ){}

                Text(
                    text = dream.title,
                    modifier = Modifier
                        .offset(
                            x = (mapWidth * dream.x - 45.dp).coerceAtLeast(0.dp),
                            y = mapHeight * dream.y + 20.dp
                        )
                        .background(color = labelColor, shape = RoundedCornerShape(8.dp))
                        .clickable { selectedDream = dream }
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    fontSize = 11.sp,
                    color = mapTextColor
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.constellation_hint),
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.labelSmall,
            color = secondaryText
        )
    }

    if (showInfo) {
        AlertDialog(
            onDismissRequest = { showInfo = false },
            title = { Text(stringResource(R.string.constellation_info_title)) },
            text = {
                Column {
                    Text(stringResource(R.string.constellation_info_text1))
                    Spacer(Modifier.height(10.dp))
                    Text(stringResource(R.string.constellation_info_text2))
                    Spacer(Modifier.height(10.dp))
                    Text(stringResource(R.string.constellation_info_text3))
                }
            },
            confirmButton = {
                TextButton(onClick = { showInfo = false }) {
                    Text(stringResource(R.string.button_got_it))
                }
            }
        )
    }

    selectedDream?.let { dream ->
        val connectedDreams = getConnectedDreams(dream)

        AlertDialog(
            onDismissRequest = { selectedDream = null },
            title = { Text(dream.title) },
            text = {
                Column {
                    Text(
                        text = stringResource(R.string.tags_label),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(dream.tags.joinToString(" · "))

                    Spacer(Modifier.height(14.dp))

                    Text(
                        text = stringResource(R.string.connected_dreams_label),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))

                    if (connectedDreams.isEmpty()) {
                        Text(stringResource(R.string.no_connected_dreams))
                    } else {
                        connectedDreams.forEach { connectedDream ->
                            val commonTags = dream.tags.filter { tag -> tag in connectedDream.tags }
                            Text(text = "• ${connectedDream.title}")
                            Text(
                                text = "Общие теги: " + commonTags.joinToString(", "),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                fontSize = 13.sp
                            )
                            Spacer(Modifier.height(6.dp))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedDream = null }) {
                    Text(stringResource(R.string.button_close))
                }
            }
        )
    }
}