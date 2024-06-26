package com.oukschub.checkmate.ui.signin

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.oukschub.checkmate.R
import com.oukschub.checkmate.ui.component.Footer
import com.oukschub.checkmate.ui.component.InputFields
import com.oukschub.checkmate.ui.component.Logo
import com.oukschub.checkmate.util.MessageUtil

/**
 * The screen for users to login to an existing account.
 */
@Composable
fun SignInScreen(
    onSignIn: () -> Unit,
    onForgotPassword: () -> Unit,
    onFooterClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignInViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .padding(40.dp)
                .weight(0.85F),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Logo(
                modifier = Modifier
                    .padding(30.dp)
                    .size(200.dp)
            )

            val context = LocalContext.current

            InputFields(
                email = viewModel.email,
                password = viewModel.password,
                passwordImeAction = ImeAction.Done,
                emailError = stringResource(viewModel.emailError),
                passwordError = stringResource(viewModel.passwordError),
                focusManager = focusManager,
                onEmailChange = { viewModel.changeEmail(it) },
                onPasswordChange = { viewModel.changePassword(it) },
                onImeAction = {
                    focusManager.clearFocus()
                    viewModel.signIn(
                        onSuccess = { onSignIn() },
                        onFailure = { MessageUtil.displayToast(context, it) }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            TextButton(
                modifier = Modifier.padding(bottom = 10.dp),
                onClick = {
                    focusManager.clearFocus()
                    onForgotPassword()
                }
            ) {
                Text(
                    text = buildAnnotatedString {
                        pushStyle(SpanStyle(color = Color.Black, fontWeight = FontWeight.Normal))
                        append(stringResource(R.string.sign_in_forgot_password))
                        append(" ")
                        pushStyle(SpanStyle(color = Color.Blue, fontWeight = FontWeight.Bold))
                        append(stringResource(R.string.sign_in_click_here))
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.signIn(
                        onSuccess = { onSignIn() },
                        onFailure = { MessageUtil.displayToast(context, it) }
                    )
                }
            ) {
                Text(stringResource(R.string.sign_in))
            }

            if (viewModel.isSigningIn) {
                CircularProgressIndicator(modifier = Modifier.padding(80.dp))
            }
        }

        Footer(
            text = buildAnnotatedString {
                append(stringResource(R.string.sign_in_prompt_to_sign_up))
                append(" ")
                pushStyle(SpanStyle(color = Color.Blue, fontWeight = FontWeight.Bold))
                append(stringResource(R.string.sign_up))
            },
            onClick = { onFooterClick() },
            modifier = Modifier.weight(0.15F)
        )
    }
}
