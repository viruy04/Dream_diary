package com.example.dreamdiary.screens

import androidx.compose.foundation.Image
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.dreamdiary.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.dreamdiary.navigation.Destination

@Composable
fun HomeScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(
            text = "Дневник сновидений",
            fontSize = 25.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Привет! Как прошла ночь?",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Цитата дня",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Здесь будет цитата о снах",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Моё пространство",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))


        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SpaceCard(
                title = "Календарь",
                imageRes = R.drawable.calendar_bg,
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    navController.navigate(Destination.Calendar.route)
                }
            )

            SpaceCard(
                title = "Статистика",
                imageRes = R.drawable.statistics_bg,
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    navController.navigate(Destination.Statistics.route)
                }
            )

            SpaceCard(
                title = "Созвездие снов",
                imageRes = R.drawable.constellation_bg,
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    navController.navigate(Destination.Constellation.route)
                }
            )

            SpaceCard(
                title = "Достижения",
                imageRes = R.drawable.achievements_bg,
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    navController.navigate(Destination.Achievements.route)
                }
            )
        }
    }
}

@Composable
private fun SpaceCard(
    title: String,
    imageRes: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(140.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.BottomStart
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Text(
            text = title,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    colorResource(R.color.home_card_overlay)
                )
                .padding(12.dp),
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = colorResource(R.color.home_card_text)
        )
    }
}

