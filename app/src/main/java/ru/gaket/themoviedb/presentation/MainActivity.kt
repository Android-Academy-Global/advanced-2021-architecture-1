package ru.gaket.themoviedb.presentation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.gaket.themoviedb.R
import ru.gaket.themoviedb.core.navigation.AppNavGraph
import ru.gaket.themoviedb.core.navigation.MoviesScreen
import ru.gaket.themoviedb.core.navigation.Navigator
import ru.gaket.themoviedb.core.navigation.WebNavigator
import javax.inject.Inject

/* Временная мера для быстрого переключения механизма навигации */
private const val useClearComposeNavigation = true

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.main_activity) {

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var webNavigator: WebNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (useClearComposeNavigation) {
            setContent {
                // TODO: to be sets compose-theme and background color and insets
                val navController = rememberNavController()
                AppNavGraph(
                    navController = navController,
                    modifier = Modifier,
                    webNavigator = webNavigator
                )
            }
        } else {
            if (savedInstanceState == null) {
                navigator.navigateTo(
                    screen = MoviesScreen(),
                    addToBackStack = false,
                )
            }
        }
    }
}
