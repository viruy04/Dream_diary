package com.example.dreamdiary.navigation

sealed class Destination(val route: String) {
    data object Welcome : Destination("welcome")
    data object Home : Destination("home")
    data object Dreams : Destination("dreams")
    data object Profile : Destination("profile")

    data object Calendar : Destination("calendar")
    data object Statistics : Destination("statistics")
    data object Constellation : Destination("constellation")
    data object Achievements : Destination("achievements")
}
