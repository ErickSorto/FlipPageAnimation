package com.ballisticapps.pageanimation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults.iconButtonColors
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.ballisticapps.pageanimation.ui.theme.PageAnimationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PageAnimationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = Color.Black.copy(alpha = 0.3f)
                ) {
                    MobileFriendlyBook()
                }
            }
        }
    }
}


@Composable
fun MobileFriendlyBook(
    modifier: Modifier = Modifier
        .aspectRatio(5f / 9.5f)
        .fillMaxWidth(),
) {
    var currentPage by remember { mutableStateOf(0) }
    val maxPages = 10 // Total number of pages
    var swipeInProgress by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = modifier
                .padding(16.dp, 32.dp, 16.dp, 16.dp)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(onDragStart = {
                        swipeInProgress = true // Begin swipe
                    }, onDragEnd = {
                        swipeInProgress = false // End swipe
                    }, onHorizontalDrag = { change, dragAmount ->
                        if (!swipeInProgress) return@detectHorizontalDragGestures
                        if (dragAmount < -20f && currentPage < maxPages - 1) {
                            currentPage++
                            swipeInProgress = false // Prevent further page turns until next gesture
                            change.consume()
                        } else if (dragAmount > 20f && currentPage > 0) {
                            currentPage--
                            swipeInProgress = false // Prevent further page turns until next gesture
                            change.consume()
                        }
                    })
                }) {
            for (pageIndex in maxPages - 1 downTo 0) {
                val isFlipped = remember { mutableStateOf(pageIndex < currentPage) }

                LaunchedEffect(currentPage) {
                    isFlipped.value = pageIndex < currentPage
                }

                val rotationY by animateFloatAsState(
                    targetValue = if (isFlipped.value) -180f else 0f, animationSpec = tween(
                        durationMillis = if (pageIndex == currentPage - 1 || pageIndex == currentPage) 900 else 0,
                        easing = LinearOutSlowInEasing
                    )
                )
                val dynamicZIndex = when {
                    rotationY < -90f -> pageIndex.toFloat() // Page is flipped
                    rotationY > 90f -> maxPages - pageIndex.toFloat() // Page is flipping
                    else -> maxPages.toFloat() // Highest zIndex when in the middle of flipping
                }

                Box(modifier = Modifier
                    .fillMaxSize()
                    .zIndex(dynamicZIndex)
                    .graphicsLayer {
                        transformOrigin = TransformOrigin(0f, 0.5f)
                        this.rotationY = rotationY
                        cameraDistance = 12f * density

                    }
                    .background(Color.White)
                    .drawBehind {
                        // Draw the shadow for all pages except the first one
                        if (!isFlipped.value && pageIndex != 0) {
                            val gradient = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.1f), Color.Transparent
                                ), startX = 0f, endX = 15f
                            )
                            drawRect(
                                brush = gradient, size = Size(width = 15f, height = size.height)
                            )
                        }
                    }
                    .align(Alignment.Center)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.purple_sky),
                        contentDescription = "Page image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding()
                    )

                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(

                                Brush.horizontalGradient(
                                    colors = listOf(
                                        if (currentPage == 0) Color.Transparent else
                                        Color.Black.copy(alpha = 0.5f),
                                        Color.Transparent,
                                    ),
                                    startX = 0f,
                                    endX = 24f
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp, 8.dp, 16.dp, 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "Page ${pageIndex+1}",
                            style = TextStyle(
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 64.sp,
                                color = Color.Black
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            color = Color.White,
                        )

                        Spacer(modifier = Modifier.weight(1f))
                        if (currentPage == pageIndex) {
                            Row(
                                modifier = Modifier
                                    .padding(8.dp, 8.dp, 8.dp, 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                IconButton(
                                    enabled = currentPage > 0,
                                    onClick = { if (currentPage > 0) currentPage-- },
                                    colors = iconButtonColors(
                                        containerColor = Color.White.copy(alpha = 0.2f),
                                    )
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_keyboard_arrow_left_24),
                                        contentDescription = "Back",
                                        tint = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                IconButton(
                                    enabled = currentPage < maxPages - 1,
                                    onClick = { if (currentPage < maxPages - 1) currentPage++ },
                                    colors = iconButtonColors(
                                        containerColor = Color.White.copy(alpha = 0.2f),
                                    )
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_keyboard_arrow_right_24),
                                        contentDescription = "Next",
                                        tint = Color.White,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMobileFriendlyBook() {
    MobileFriendlyBook()
}













