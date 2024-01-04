package com.oukschub.checkmate.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.oukschub.checkmate.R

@Composable
fun InputFields(
    email: String,
    password: String,
    emailError: String,
    passwordError: String,
    onChangeEmail: (String) -> Unit,
    onChangePassword: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        val focusManager = LocalFocusManager.current

        EmailTextField(
            email = email,
            errorMessage = emailError,
            focusManager = focusManager,
            onChangeEmail = onChangeEmail
        )

        Spacer(modifier = Modifier.height(10.dp))

        PasswordTextField(
            password = password,
            errorMessage = passwordError,
            focusManager = focusManager,
            onChangePassword = onChangePassword
        )
    }
}

@Composable
private fun EmailTextField(
    email: String,
    errorMessage: String,
    focusManager: FocusManager,
    onChangeEmail: (String) -> Unit
) {
    OutlinedTextField(
        value = email,
        onValueChange = { onChangeEmail(it) },
        placeholder = { Text(text = stringResource(R.string.email)) },
        supportingText = {
            if (errorMessage.isNotBlank()) {
                Text(text = errorMessage)
            }
        },
        isError = errorMessage.isNotBlank(),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        ),
        singleLine = true
    )
}

@Composable
private fun PasswordTextField(
    password: String,
    errorMessage: String,
    focusManager: FocusManager,
    onChangePassword: (String) -> Unit
) {
    val passwordVisualTransformation = PasswordVisualTransformation()

    var isPasswordVisible by remember {
        mutableStateOf(false)
    }

    OutlinedTextField(
        value = password,
        onValueChange = { onChangePassword(it) },
        placeholder = { Text(text = stringResource(R.string.password)) },
        trailingIcon = {
            IconButton(
                onClick = { isPasswordVisible = !isPasswordVisible },
                modifier = Modifier.focusProperties { canFocus = false }
            ) {
                Icon(
                    imageVector = if (isPasswordVisible) Icons.Default.Search else Icons.Default.Lock,
                    contentDescription = null
                )
            }
        },
        supportingText = {
            if (errorMessage.isNotBlank()) {
                Text(text = errorMessage)
            }
        },
        isError = errorMessage.isNotBlank(),
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else passwordVisualTransformation,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
        keyboardActions = KeyboardActions(
            // Calls next button down onClick on press enter while focused on password text field.
            onSend = { focusManager.moveFocus(FocusDirection.Down) }
        ),
        singleLine = true
    )
}
