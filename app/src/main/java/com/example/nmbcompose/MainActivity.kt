package com.example.nmbcompose

import android.os.Bundle
import android.util.Log
import android.view.WindowManager
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
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.*
import androidx.navigation.navigation
import com.example.nmbcompose.ui.screen.ArticleDetailScreen
import com.example.nmbcompose.ui.screen.HomeScreen
import com.example.nmbcompose.ui.screen.LauncherScreen
import com.example.nmbcompose.ui.screen.MainScreen
import com.example.nmbcompose.ui.theme.NmbComposeTheme
import com.example.nmbcompose.viewmodel.BaseViewModel
import com.example.nmbcompose.viewmodel.HomeViewModel
import com.example.nmbcompose.viewmodel.MainViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @ExperimentalMaterialApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.navigationBarColor = android.graphics.Color.parseColor("#fafafa") //#fafafa
//        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            App(viewModel())
        }
    }
}

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun App(viewModel: MainViewModel) {
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
            val dispatcher = object : RouteDispatcher() {
                override fun invoke(data: RouterData) {
                    val url = data.route
                    var param: String? = null
                    data.params?.let {
                        val type = Types.newParameterizedType(
                            Map::class.java,
                            String::class.java,
                            String::class.java
                        )
                        param = Moshi.Builder().build().adapter<Map<String, String>>(type)
                            .toJson(data.params)
                    }
                    val route = "${url}${param?.run { "/${this}" } ?: ""}"
                    Log.d(TAG, "invoke: $route")
                    navController.navigate(route = route) {
                    }
                }
            }
            NavHost(navController = navController, startDestination = LAUNCHER) {
                composable(LAUNCHER) {
                    createArgument(navBackStackEntry = it) {
                        LauncherScreen(
                            createViewModel(),
                            dispatcher
                        )
                    }
                }
                composable(MAIN) {
                    createArgument(it) {
                        MainScreen(
                            createViewModel(),
                            dispatcher
                        )
                    }
                }
//                composable(
//                    getRouteWithParam(THREAD_DETAIL),
//                ) {
//                    createArgument(it) { args ->
//                        ArticleDetailScreen(
//                            createViewModel(
//                                args = args
//                            ),
//                            dispatcher,
//                        ) {
//                            navController.popBackStack()
//                        }
//                    }
//                }
            }
        }
//        }
    }
}

//在 NavGraph 全局范围使用 Hilt 创建 ViewModel
@Composable
inline fun <reified VM : BaseViewModel<*>> createViewModel(
    args: Map<String, String> = hashMapOf()
): VM {
    val vm = hiltViewModel<VM>()
    vm.arguments = args
    return vm
}


//解析param生成map的args
@Composable
fun createArgument(
    navBackStackEntry: NavBackStackEntry,
    content: @Composable (Map<String, String>) -> Unit
) {
    val param = navBackStackEntry.arguments?.getString("param") ?: "{}"
    val type = Types.newParameterizedType(
        Map::class.java,
        String::class.java,
        String::class.java
    )
    val args = Moshi.Builder().build().adapter<Map<String, String>>(type).fromJson(param)
    content.invoke(args ?: hashMapOf())
}