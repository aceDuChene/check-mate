package com.oukschub.checkmate.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.oukschub.checkmate.ui.component.BottomNavBar

@Composable
fun Home(
    onNavigateToChecklists: () -> Unit,
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    BottomNavBar(
        onClickChecklists = { onNavigateToChecklists() },
        onClickHome = { /*TO-DO*/ },
        onClickProfile = { onNavigateToProfile() }

    ) {
        Column {
            Text(text = "Home")
        }
    }
}