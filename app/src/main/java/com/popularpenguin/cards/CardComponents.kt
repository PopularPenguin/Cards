package com.popularpenguin.cards

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import kotlin.math.roundToInt

const val CARD_WIDTH = 50
const val CARD_HEIGHT = 80
const val HAND_SIZE = 5

// The entire screen
@Composable
fun Table() {
    var isVisible by remember { mutableStateOf(false) } // toggle enter/exit card animations
    var isClickable by remember { mutableStateOf(true) } // is deck clickable right now?
    val coroutineScope = rememberCoroutineScope()
    val onClick: () -> Unit = { // deal some cards on click
        if (isClickable) {
            isVisible = !isVisible
            isClickable = false

            coroutineScope.launch {
                delay(2000L)
                isClickable = true
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient( // gray/white background
                    0.1f to Color.DarkGray,
                    0.35f to Color.White,
                    0.65f to Color.White,
                    0.9f to Color.DarkGray
                )
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Graphical representation of the card deck
        // also where the played card animations start
        Deck(
            modifier = Modifier
                .padding(top = 128.dp, start = 8.dp, end = CARD_WIDTH.dp),
            onClick = onClick
        )

        // Add cards to the table
        repeat(HAND_SIZE) { cardIndex ->
            // Start card animation at (initialX, initialY) at deck's location
            // These values are based on dp then converted to pixels
            // due to varying screen sizes on different devices
            val initialX = with (LocalDensity.current) {
                val individualCardOffsetX = (CARD_WIDTH * 0.66f)
                    .roundToInt() * (HAND_SIZE - 1)

                -(2 * CARD_WIDTH).dp.toPx().roundToInt() - individualCardOffsetX
            }
            val initialY = with (LocalDensity.current) {
                (CARD_HEIGHT - 16).dp.toPx().roundToInt()
            }
            val offset = with (LocalDensity.current) {
                (CARD_WIDTH / 2).dp.toPx().roundToInt() * cardIndex
            }

            AnimatedCard(
                isVisible = isVisible, // toggle whether the cards are in a dealt state or not
                elevation = cardIndex.dp, // overlap each card in the hand dealt
                rotation = -20f + cardIndex * 10, // tilt hand -20 to 20 degrees
                initialX = initialX - offset, // offset cards so their bounds overlap during animation
                initialY = initialY,
                duration = 1500 - 300 * cardIndex, // cards on the right move faster
                imageRes = when (cardIndex) { // give a different graphic for each card dealt
                    0 -> R.drawable.jack
                    1 -> R.drawable.seven
                    2 -> R.drawable.four
                    3 -> R.drawable.queen
                    4 -> R.drawable.ace

                    else -> throw IndexOutOfBoundsException()
                }
            )
        }
    }
}

// Basic card composable used for both the Deck and AnimatedCard
@Composable
fun BaseCard(
    modifier: Modifier = Modifier,
    elevation: Dp = 0.dp,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(10.dp)

    Card(
        modifier = modifier
            .size(width = CARD_WIDTH.dp, height = CARD_HEIGHT.dp)
            .clip(shape)
            .border(2.dp, Color.Black, shape),
        elevation = elevation,
        content = content
    )
}

// Composable that deals a hand of cards when clicked
@Composable
fun Deck(modifier: Modifier = Modifier, onClick: () -> Unit) {
    BaseCard(
        modifier = modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        0.0f to Color.Green,
                        0.75f to Color.Blue
                    )
                )
                .border(4.dp, Color.White, RoundedCornerShape(10.dp))
        )
    }
}

// Card that is dealt in five at a time when the deck is clicked
// isVisible: toggle if the cards are shown
// elevation: set a different elevation for each card so they can overlap each other
// rotation: set a slight rotation offet for the cards dealt
// initialX: starting x position of the animation (x position of the deck)
// initialY: starting y position of the animation (y position of the deck)
// duration: total duration of the animation (slightly staggered for each card)
// imageRes: the card's image resource
@Composable
fun AnimatedCard(
    isVisible: Boolean,
    elevation: Dp = 0.dp,
    rotation: Float = 0f,
    initialX: Int = 0,
    initialY: Int = 0,
    duration: Int = 0,
    imageRes: Int = 0
) {
    // Slide right and up to the card's resting position on enter
    // Slight left and down back to the deck's position on exit
    AnimatedVisibility(
        visible = isVisible,
        enter = slideIn(
            initialOffset = { IntOffset(initialX, initialY) },

            animationSpec = tween(duration)
        ),
        exit = slideOut(
            targetOffset = { IntOffset(initialX, initialY) },
            animationSpec = tween((duration * 1.3f).roundToInt()),
        )
    ) {
        // the card
        BaseCard(
            modifier = Modifier
                .background(Color.Transparent)
                .rotate(rotation),
            elevation = elevation
        ) {
            // PNG image of the card
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(id = imageRes),
                contentDescription = "",
                contentScale = ContentScale.FillBounds
            )
        }
    }
}

@Preview
@Composable
fun PlayAreaPreview() {
    Table()
}