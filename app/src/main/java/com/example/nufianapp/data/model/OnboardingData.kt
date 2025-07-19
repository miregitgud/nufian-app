package com.example.nufianapp.data.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.nufianapp.R
import com.example.nufianapp.ui.theme.ClearBlue
import com.example.nufianapp.ui.theme.DarkBlueGradient
import com.example.nufianapp.ui.theme.NeonWhite
import com.example.nufianapp.ui.theme.OrangeGradient
import com.example.nufianapp.ui.theme.RedGradient

data class OnboardingData(
    val animations: List<Int>,
    val titles: List<String>,
    val subtitles: List<String>,
    val indicatorColors: List<List<Color>>,
    val backgroundGradients: List<Brush>
)

@Composable
fun getOnboardingData(): OnboardingData {
    return OnboardingData(
        animations = listOf(
            R.drawable.onboardpage1,
            R.drawable.onboardpage2,
            R.drawable.onboardpage3
        ),
        titles = listOf(
            stringResource(R.string.onboarding_title1),
            stringResource(R.string.onboarding_title2),
            stringResource(R.string.onboarding_title3)
        ),
        subtitles = listOf(
            stringResource(R.string.onboarding_body1),
            stringResource(R.string.onboarding_body2),
            stringResource(R.string.onboarding_body3)
        ),
        indicatorColors = listOf(
            listOf(ClearBlue, NeonWhite),
            listOf(ClearBlue, NeonWhite),
            listOf(ClearBlue, NeonWhite),
        ),
        backgroundGradients = listOf(
            DarkBlueGradient,
            OrangeGradient,
            RedGradient
        )
    )
}