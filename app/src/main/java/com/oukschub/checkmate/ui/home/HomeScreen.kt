package com.oukschub.checkmate.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.common.collect.ImmutableList
import com.oukschub.checkmate.R
import com.oukschub.checkmate.data.model.Checklist
import com.oukschub.checkmate.ui.component.Checklist

/**
 * The screen displayed after Splash screen completes.
 * Displays the user's favorite checklists.
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        viewModel.isContentVisible = true
    }

    if (viewModel.checklists.none { it.isFavorite }) {
        SadCheckmate(modifier = modifier)
    } else {
        Content(
            checklists = viewModel.checklists,
            isContentVisible = viewModel.isContentVisible,
            onTitleFocus = { title -> viewModel.focusTitle(title) },
            onTitleSet = { checklistIndex, title -> viewModel.setTitle(checklistIndex, title) },
            onChecklistUnfavorite = { checklistIndex -> viewModel.unfavoriteChecklist(checklistIndex) },
            onChecklistDelete = { checklistIndex -> viewModel.deleteChecklist(checklistIndex) },
            onItemCheck = { checklistIndex, itemIndex, isChecked -> viewModel.setItemChecked(checklistIndex, itemIndex, isChecked) },
            onItemNameFocus = { itemName -> viewModel.focusItem(itemName) },
            onItemNameSet = { checklistIndex, itemIndex, itemName -> viewModel.setItemName(checklistIndex, itemIndex, itemName) },
            onItemAdd = { checklistIndex, itemName -> viewModel.addItem(checklistIndex, itemName) },
            onItemLongClick = { checklistIndex, itemIndex -> viewModel.showDeleteItemDialog(checklistIndex, itemIndex) },
            modifier = modifier
        )

        if (viewModel.isDeleteItemDialogVisible) {
            DeleteItemDialog(
                itemName = viewModel.itemToBeDeleted,
                onDismiss = { viewModel.hideDeleteItemDialog() },
                onConfirm = { viewModel.deleteItem() }
            )
        }
    }
}

@Composable
private fun Content(
    checklists: ImmutableList<Checklist>,
    isContentVisible: Boolean,
    onTitleFocus: (String) -> Unit,
    onTitleSet: (Int, String) -> Unit,
    onChecklistUnfavorite: (Int) -> Unit,
    onChecklistDelete: (Int) -> Unit,
    onItemCheck: (Int, Int, Boolean) -> Unit,
    onItemNameFocus: (String) -> Unit,
    onItemNameSet: (Int, Int, String) -> Unit,
    onItemAdd: (Int, String) -> Unit,
    onItemLongClick: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        itemsIndexed(
            items = checklists,
            key = { _, checklist -> checklist.id }
        ) { checklistIndex, checklist ->
            if (!checklist.isFavorite) {
                return@itemsIndexed
            }

            AnimatedVisibility(
                visible = isContentVisible,
                enter = fadeIn(tween(200, 80 * checklistIndex)) +
                    slideInVertically(
                        tween(200, 80 * checklistIndex),
                        initialOffsetY = { it / 2 }
                    )
            ) {
                Checklist(
                    header = {
                        Header(
                            title = checklist.title,
                            onTitleFocus = { title -> onTitleFocus(title) },
                            onTitleSet = { title -> onTitleSet(checklistIndex, title) },
                            onChecklistUnfavorite = { onChecklistUnfavorite(checklistIndex) },
                            onChecklistDelete = { onChecklistDelete(checklistIndex) }
                        )
                    },
                    items = ImmutableList.copyOf(checklist.items),
                    onItemCheck = { itemIndex, isChecked -> onItemCheck(checklistIndex, itemIndex, isChecked) },
                    onItemNameFocus = { itemName -> onItemNameFocus(itemName) },
                    onItemNameSet = { itemIndex, itemName -> onItemNameSet(checklistIndex, itemIndex, itemName) },
                    onItemAdd = { itemName -> onItemAdd(checklistIndex, itemName) },
                    onItemLongClick = { itemIndex -> onItemLongClick(checklistIndex, itemIndex) }
                )
            }
        }
    }
}

@Composable
private fun Header(
    title: String,
    onTitleFocus: (String) -> Unit,
    onTitleSet: (String) -> Unit,
    onChecklistUnfavorite: () -> Unit,
    onChecklistDelete: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(start = 20.dp, top = 5.dp, end = 5.dp)
    ) {
        var checklistTitle by remember { mutableStateOf(title) }
        TextField(
            value = checklistTitle,
            onValueChange = { checklistTitle = it },
            textStyle = TextStyle(fontSize = 18.sp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier.onFocusChanged { focusState ->
                if (focusState.isFocused) {
                    onTitleFocus(checklistTitle)
                } else {
                    onTitleSet(checklistTitle)
                }
            }
        )

        Box {
            var isDropdownVisible by remember { mutableStateOf(false) }

            IconButton(onClick = { isDropdownVisible = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(R.string.desc_checklist_options)
                )
            }

            DropdownMenu(
                expanded = isDropdownVisible,
                onDismissRequest = { isDropdownVisible = false }
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.checklist_unfavorite)) },
                    onClick = {
                        isDropdownVisible = false
                        onChecklistUnfavorite()
                    }
                )

                DropdownMenuItem(
                    text = { Text(stringResource(R.string.checklist_delete)) },
                    onClick = {
                        isDropdownVisible = false
                        onChecklistDelete()
                    }
                )
            }
        }
    }
}

@Composable
private fun DeleteItemDialog(
    itemName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(text = stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(text = stringResource(R.string.cancel))
            }
        },
        title = { Text(text = stringResource(R.string.home_delete_dialog_title)) },
        text = { Text(text = stringResource(R.string.home_delete_dialog_prompt, itemName)) }
    )
}

@Composable
private fun SadCheckmate(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.ic_checkmate_sad),
            contentDescription = null,
            modifier = Modifier.scale(0.8F),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
        )

        Text(text = stringResource(R.string.home_no_favorites))
    }
}
