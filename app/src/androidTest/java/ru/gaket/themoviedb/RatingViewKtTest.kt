package ru.gaket.themoviedb

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import ru.gaket.themoviedb.presentation.review.common.RatingView

class RatingViewKtTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun ratingView_onClickChangesRatingAndCallsCallback() {
        var rating = 3
        val maxRating = 5
        var onRatingChangeCalled = false
        composeTestRule.setContent {
            RatingView(rating = rating, maxRating = maxRating) {
                onRatingChangeCalled = true
                rating = it
            }
        }

        val stars = composeTestRule.onAllNodes(hasTestTag("star"))
        stars[rating - 1].assertHasClickAction()
        // Click on the third star
        stars[rating - 1].performClick()
        // Verify that the rating and callback were updated
        assertEquals(3, rating)
        assertTrue(onRatingChangeCalled)
        // Click on the fourth star
        stars[rating].performClick()
        // Verify that the rating and callback were updated
        assertEquals(4, rating)
        assertTrue(onRatingChangeCalled)
    }

    @Test
    fun testRatingView() {
        val startRate = Icons.Filled.StarRate.name
        val starBorder = Icons.Filled.StarBorder.name
        composeTestRule.setContent {
            RatingView(maxRating = 5)
        }

        // Check that the initial rating is 0
        repeat(5) { index ->
            composeTestRule.onNodeWithTag("star-$index")
                .assertContentDescriptionContains(starBorder)
        }
        // Click on the third star
        composeTestRule.onNodeWithTag("star-3").performClick()

        // Check that the rating is updated to 3
        composeTestRule.onNodeWithTag("star-0").assertContentDescriptionContains(startRate)
        composeTestRule.onNodeWithTag("star-1").assertContentDescriptionContains(startRate)
        composeTestRule.onNodeWithTag("star-2").assertContentDescriptionContains(startRate)
        composeTestRule.onNodeWithTag("star-3").assertContentDescriptionContains(startRate)
        composeTestRule.onNodeWithTag("star-4").assertContentDescriptionContains(starBorder)

        // Click on the first star
        composeTestRule.onNodeWithTag("star-0").performClick()

        // Check that the rating is updated to 1
        composeTestRule.onNodeWithTag("star-0").assertContentDescriptionContains(startRate)
        composeTestRule.onNodeWithTag("star-1").assertContentDescriptionContains(starBorder)
        composeTestRule.onNodeWithTag("star-2").assertContentDescriptionContains(starBorder)
        composeTestRule.onNodeWithTag("star-3").assertContentDescriptionContains(starBorder)
        composeTestRule.onNodeWithTag("star-4").assertContentDescriptionContains(starBorder)
    }
}