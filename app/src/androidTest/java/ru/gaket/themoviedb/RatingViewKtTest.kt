package ru.gaket.themoviedb

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import ru.gaket.themoviedb.presentation.review.common.RatingView

@ExperimentalComposeUiApi
class RatingViewKtTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @ExperimentalComposeUiApi
    @Composable
    fun RatingViewTest(
        initialRating: Int = 0, maxRating: Int = 5, onRatingChange: (newRating: Int) -> Unit
    ) {
        var rating by remember { mutableStateOf(initialRating) }

        RatingView(rating = rating, maxRating = maxRating, onRatingChange = { newRating ->
            rating = newRating
            onRatingChange(newRating)
        })
    }

    @Test
    fun testRatingViewWithStateHoistingWithComposable() {
        var rating = 0
        composeTestRule.setContent {
            RatingViewTest(initialRating = rating, maxRating = 5) { newRating ->
                rating = newRating
            }
        }

        val stars = composeTestRule.onAllNodesWithTag("star")

        stars[3].performClick()
        assertEquals(4, rating)

        stars[0].performClick()
        composeTestRule.waitForIdle()
        assertEquals(1, rating)
    }


    @Test
    fun testRatingViewWithStateHoisting() {
        var rating by mutableStateOf(0)
        val maxRating = 5
        val starFour = 4
        val starOne = 1

        composeTestRule.setContent {
            RatingView(rating = rating, maxRating = maxRating) {
                rating = it
            }
        }

        val stars = composeTestRule.onAllNodesWithTag("star")

        stars[starFour - 1].performClick()
        assertEquals(starFour, rating)

        stars[starOne - 1].performClick()
        composeTestRule.waitForIdle()
        assertEquals(starOne, rating)
    }

    @Test
    fun testRatingViewWithoutStateHoisting() {
        val startRate = Icons.Filled.StarRate.name
        val starBorder = Icons.Filled.StarBorder.name

        composeTestRule.setContent {
            RatingView(maxRating = 5)
        }

        val stars =
            composeTestRule.onAllNodesWithTag("star").assertAll(hasContentDescription(starBorder))

        stars[3].performClick()

        stars[0].assertContentDescriptionContains(startRate)
        stars[1].assertContentDescriptionContains(startRate)
        stars[2].assertContentDescriptionContains(startRate)
        stars[3].assertContentDescriptionContains(startRate)
        stars[4].assertContentDescriptionContains(starBorder)

        stars[0].performClick()

        stars[0].assertContentDescriptionContains(startRate)
        stars[1].assertContentDescriptionContains(starBorder)
        stars[2].assertContentDescriptionContains(starBorder)
        stars[3].assertContentDescriptionContains(starBorder)
        stars[4].assertContentDescriptionContains(starBorder)
    }
}