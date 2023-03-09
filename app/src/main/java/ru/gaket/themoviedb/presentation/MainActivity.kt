package ru.gaket.themoviedb.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.gaket.themoviedb.R
import ru.gaket.themoviedb.core.navigation.AppNavGraph
import ru.gaket.themoviedb.core.navigation.MoviesScreen
import ru.gaket.themoviedb.core.navigation.Navigator
import ru.gaket.themoviedb.core.navigation.WebNavigator
import javax.inject.Inject

/* Temporary measure to quickly switch the navigation mechanism */
private const val useClearComposeNavigation = true

@OptIn(ExperimentalAnimationApi::class)
@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.main_activity) {

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var webNavigator: WebNavigator

    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (useClearComposeNavigation) {
            setContent {
                // TODO: to be sets compose-theme and background color and insets
                val navController = rememberAnimatedNavController()
                this.navController = navController

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

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navController?.handleDeepLink(intent)
    }
}
