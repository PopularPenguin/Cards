package com.popularpenguin.cards

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.popularpenguin.cards.ui.theme.CardsTheme

/* Android card animation example
   This program shows a quick animation of cards being dealt and then reversed back to the deck
   using Compose AnimatedVisibility
   Card graphics from Wikimedia Commons <commons.wikimedia.org>
 */

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CardsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Table()
                }
            }
        }
    }
}