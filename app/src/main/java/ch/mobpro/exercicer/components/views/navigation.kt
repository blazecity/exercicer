package ch.mobpro.exercicer.components.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ch.mobpro.exercicer.components.views.Screens.Items.items
import ch.mobpro.exercicer.ui.theme.Blue500

@Composable
fun NavigationController() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            Card(modifier = Modifier.padding(5.dp), shape = RoundedCornerShape(7.dp), elevation = 16.dp) {
                BottomNavigation(backgroundColor = MaterialTheme.colors.background) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    items.forEach {
                        BottomNavigationItem(
                            icon = {
                                Icon(
                                    painterResource(id = it.icon),
                                    contentDescription = it.route.route,
                                    tint = if (currentRoute == it.route.route) Blue500 else Color.LightGray
                                )
                            },
                            selected = currentRoute == it.route.route,
                            label = {
                                Text(
                                    text = it.label,
                                    color = if (currentRoute == it.route.route) Blue500
                                        else Color.LightGray,
                                    textAlign = TextAlign.Center,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            onClick = {
                                if (currentRoute != it.route.route) {
                                    navController.graph.startDestinationRoute?.let { item ->
                                        navController.popBackStack(item, false)
                                    }
                                }

                                if (currentRoute != it.route.route) {
                                    navController.navigate(it.route.route)
                                }
                            },
                            alwaysShowLabel = true,
                            selectedContentColor = Color.DarkGray
                        )
                    }
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            ScreenController(navController = navController)
        }
    }
}