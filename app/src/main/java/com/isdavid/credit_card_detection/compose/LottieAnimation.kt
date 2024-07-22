package com.isdavid.credit_card_detection.compose


import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.isdavid.R


@Composable
fun LottieRawView(
    modifier: Modifier = Modifier,
    progress: Float = 0f,
    resource: Int
) {
    val comp by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(resource))

    LottieAnimation(
        modifier = modifier,
        composition = comp,
        progress = progress
    )
}