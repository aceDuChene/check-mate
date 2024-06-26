package com.oukschub.checkmate

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.oukschub.checkmate.ui.navigation.CheckMateNavHost
import com.oukschub.checkmate.ui.navigation.Screen

/**
 * The main app, comprised of the navHost and bottomNavBar.
 */
@Composable
fun CheckMateApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val navBackStack by navController.currentBackStackEntryAsState()

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar(
                navBackStack = navBackStack,
                onDestinationClick = {
                    navController.navigate(it) {
                        launchSingleTop = true
                        popUpTo(Screen.Home.route)
                    }
                },
                floatingActionButton = {
                    CreateChecklistFab(
                        onClick = { navController.navigate(Screen.AddChecklist.route) }
                    )
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(MaterialTheme.colorScheme.primary)
        )

        CheckMateNavHost(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues),
            navController = navController
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .background(MaterialTheme.colorScheme.primary)
        )
    }
}

@Composable
private fun NavigationBar(
    navBackStack: NavBackStackEntry?,
    onDestinationClick: (String) -> Unit,
    floatingActionButton: @Composable () -> Unit = {}
) {
    val navBarItems = listOf(Screen.Checklists, Screen.Home, Screen.Profile)
    val showNavBar = navBackStack.destinationEqualsTo(Screen.Checklists.route) ||
        navBackStack.destinationEqualsTo(Screen.Home.route) ||
        navBackStack.destinationEqualsTo(Screen.Profile.route)

    AnimatedVisibility(
        visible = showNavBar,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        BottomAppBar(
            actions = {
                for (screen in navBarItems) {
                    NavigationBarItem(
                        selected = navBackStack.destinationEqualsTo(screen.route),
                        onClick = {
                            if (!navBackStack.destinationEqualsTo(screen.route)) {
                                onDestinationClick(screen.route)
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = screen.icon!!,
                                contentDescription = stringResource(screen.resourceId)
                            )
                        },
                        label = {
                            Text(
                                stringResource(screen.resourceId),
                                softWrap = false
                            )
                        }
                    )
                }
            },
            floatingActionButton = floatingActionButton
        )
    }
}

@Composable
private fun CreateChecklistFab(onClick: () -> Unit) {
    FloatingActionButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = stringResource(R.string.desc_add_checklist)
        )
    }
}

private fun NavBackStackEntry?.destinationEqualsTo(destination: String): Boolean {
    return this?.destination?.hierarchy?.any { it.route == destination } ?: false
}
