package com.example.nmbcompose

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.compose.*
import com.example.nmbcompose.navigate.NavType
import com.example.nmbcompose.ui.screen.HomeScreen
import com.example.nmbcompose.ui.screen.LauncherScreen
import com.example.nmbcompose.ui.theme.NmbComposeTheme
import com.example.nmbcompose.viewmodel.HomeViewModel
import com.example.nmbcompose.viewmodel.MainViewModel
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @ExperimentalMaterialApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            App()
        }
    }
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun App() {
    MainScreen(viewModel = viewModel())
}

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun MainScreen(viewModel: MainViewModel) {
    NmbComposeTheme {
//        ProvideWindowInsets {
        val uiController = rememberSystemUiController()
        uiController.setSystemBarsColor(
            MaterialTheme.colors.primary,
            darkIcons = false
        )

        // A surface container using the 'background' color from the theme
        Surface(color = MaterialTheme.colors.background) {
            val navController = rememberNavController()
            //导航回调
            val navTo: (String) -> Unit = {
                navController.navigate(it)
            }
            NavHost(navController = navController, startDestination = LAUNCHER) {
                composable(LAUNCHER) {
                    LauncherScreen(
                        createViewModel(navController = navController, LAUNCHER),
                        navTo
                    )
                }
                composable(HOME) {
                    HomeScreen(
                        hiltViewModel<HomeViewModel>(
                            navController.getBackStackEntry(HOME)
                        )
                    )
                }
            }
        }
//        }
    }
}


@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    App()
}

//在 NavGraph 全局范围使用 Hilt 创建 ViewModel
@Composable
inline fun <reified VM : ViewModel> createViewModel(
    navController: NavController,
    graphId: String = ""
): VM = hiltViewModel(viewModelStoreOwner = navController.getBackStackEntry(graphId))
//
//@Composable
//inline fun <reified VM : ViewModel> createViewModel(): VM {
//    return viewModel(factory = ViewModelProvider.NewInstanceFactory())
//}
