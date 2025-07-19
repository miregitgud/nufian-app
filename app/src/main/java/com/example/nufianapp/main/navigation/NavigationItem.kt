package com.example.nufianapp.main.navigation

data class NavigationItem(
    val title: String,
    val selectedIcon: Int,
    val unselectedIcon: Int,
    val screen: ScreenCustom,
)