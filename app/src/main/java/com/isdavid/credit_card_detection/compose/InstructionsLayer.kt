package com.isdavid.credit_card_detection.compose

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.isdavid.R
import com.isdavid.credit_card_detection.view_model.CreditCardDetectionVMC
import com.isdavid.credit_card_detection.view_model.CreditCardDetectionVMC.Mode
import com.isdavid.credit_card_detection.view_model.CreditCardDetectionVMC.Side
import kotlin.math.sin

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun InstructionsLayer(
    modifier: Modifier = Modifier,
    viewModel: CreditCardDetectionVMC,
    viewLog: Boolean = false
) {
    val shouldShowOkAnimation = viewModel.capturingSide.value == Side.BACK &&
            viewModel.captureMode.value == Mode.BOTH

    val okAnimation: Float by animateFloatAsState(
        targetValue = if (shouldShowOkAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 3000),
        label = "captureInstructions"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .wrapContentSize()
        ) {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background.copy(alpha = .8f))
                    .fillMaxWidth()
                    .height(65.sp.value.dp)
            ) {

                AnimatedContent(
                    modifier = Modifier
                        .align(Alignment.Center),
                    targetState = stringResource(
                        id = viewModel.capturingSide.value.captureStepInstructions
                    ),
                    transitionSpec = {
                        fadeIn(animationSpec = tween(500)) with fadeOut(animationSpec = tween(500))
                    }
                ) { targetText ->
                    Text(
                        text = targetText,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            DrawableImage(
                resource = if (viewModel.capturingSide.value == Side.BACK)
                    R.drawable.back else R.drawable.front,

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp)
                    .aspectRatio(1f),
            )
        }

        if (viewLog) Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CreditCardImageField(
                imageBitmap = viewModel.candidateFrontImage.value,
                modifier = Modifier.weight(1f)
            )
            CreditCardImageField(
                imageBitmap = viewModel.candidateBackImage.value,
                modifier = Modifier.weight(1f)
            )
        }


        if (shouldShowOkAnimation) {
            val configuration = LocalConfiguration.current
            val screenWidth = configuration.screenWidthDp.dp

            LottieRawView(
                modifier = Modifier
                    .align(Alignment.Center)
                    .width(screenWidth / 2),
                resource = R.raw.ok_a,
                progress = sin(okAnimation * Math.PI).toFloat()
            )
        }
    }
}

@Composable
fun DrawableImage(
    modifier: Modifier = Modifier,
    resource: Int,
    contentDescription: String? = null
) {
    val painter: Painter = painterResource(id = resource)

    Image(
        modifier = modifier,
        painter = painter,
        contentDescription = contentDescription
    )
}