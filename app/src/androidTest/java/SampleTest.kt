package ru.gaket.themoviedb.tests

import androidx.test.rule.ActivityTestRule
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import javax.inject.Inject
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ru.gaket.themoviedb.R
import ru.gaket.themoviedb.domain.auth.AuthInteractor
import ru.gaket.themoviedb.presentation.MainActivity

@HiltAndroidTest
class SampleTest : TestCase() {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val activityTestRule = ActivityTestRule(MainActivity::class.java, true, false)

    @Inject
    lateinit var authInteractor: AuthInteractor

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun test() =
        before { runBlocking { authInteractor.logOut() } }
            .after { }
            .run {
                step("Type Duna, press Find") {
                    activityTestRule.launchActivity(null)
                    MainScreen {
                        searchInput {
                            isVisible()
                            typeText("Duna")
                        }
                        searchIcon {
                            isVisible()
                            click()
                        }
                        pressBack()
                    }
                }
                step("Open Duna film details") {
                    MainScreen {
                        moviesList {
                            isVisible()
                            firstChild<MainScreen.MovieItem> {
                                isVisible()
                                title { hasText("Duna") }
                                click()
                            }
                        }
                    }
                }
                // Workshop part 1 - writing PageObject
                // To complete you need
                // 1. Uncomment code inside this step - it will be red at first
                // 2. Create a `MovieScreen` class - page object for `MovieDetailsFragment` - it should make test passing
                step("Check film details screen") {
                    /*MovieScreen {
                        title {
                            isVisible()
                            hasText("Duna")
                        }
                        description {
                            isVisible()
                        }
                        image {
                            isVisible()
                        }
                    }*/
                }
                // 3. Uncomment code inside this step - it will be red at first
                // 4. Add `loginButton` to MovieScreen - it should make test passing
                step("Open Login") {
                    /*MovieScreen {
                        loginButton {
                            isVisible()
                            click()
                        }
                    }*/
                }
                // Workshop part 2 - writing Test
                // To complete you need
                // 1. Uncomment existing code inside this step.
                // There was intentionally left spaces to fill them and make test passing.
                step("Login") {
                    /*AuthScreen {
                        // TODO
                        authButton.click()
                        emailInput.hasError(R.string.email_input_error)
                        // TODO
                        authButton.click()
                        passwordInput.hasError(R.string.password_input_error)
                        // TODO
                        authButton.click()
                        // the login here should be completed, however we will check it in the next step
                    }*/
                }
                // 2. Uncomment existing code inside this step.
                // 3. Add `addReview` to MovieScreen - it should make test passing
                step("Check film details screen contains `Add review`") {
                    /*MovieScreen {
                        addReview {
                            isVisible()
                            containsText("Add review")
                        }
                    }*/
                }
            }
}

