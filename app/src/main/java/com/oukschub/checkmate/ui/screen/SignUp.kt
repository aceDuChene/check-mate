package com.oukschub.checkmate.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oukschub.checkmate.R
import com.oukschub.checkmate.ui.component.Footer
import com.oukschub.checkmate.ui.component.InputFields
import com.oukschub.checkmate.ui.component.PasswordTextField
import com.oukschub.checkmate.viewmodel.SignUpViewModel

@Composable
fun SignUp(
    onSignUp: () -> Unit,
    onClickSignIn: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = viewModel()
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .weight(.85F),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val focusManager = LocalFocusManager.current

            InputFields(
                email = viewModel.email,
                password = viewModel.password,
                emailError = viewModel.emailError,
                passwordError = "",
                focusManager = focusManager,
                onChangeEmail = { viewModel.updateEmail(it) },
                onChangePassword = { viewModel.updatePassword(it) }
            )

            PasswordTextField(
                password = viewModel.passwordMatch,
                errorMessage = "",
                placeholder = stringResource(R.string.sign_up_repeat_password),
                focusManager = focusManager,
                onChangePassword = { viewModel.updatePasswordMatch(it) }
            )

            PasswordCheckText(passwordChecks = viewModel.passwordChecks)

            Spacer(modifier = Modifier.height(10.dp))

            Button(onClick = {
                viewModel.signUp(
                    onSuccess = { onSignUp() }
                )
            }) {
                Text(text = stringResource(R.string.sign_up))
            }
        }

        Footer(
            text = buildAnnotatedString {
                append(stringResource(R.string.sign_up_prompt_to_sign_in))
                append(" ")
                pushStyle(SpanStyle(color = Color.Blue, fontWeight = FontWeight.Bold))
                append(stringResource(R.string.sign_in))
            },
            onClickText = { onClickSignIn() },
            modifier = Modifier.weight(.15F)
        )
    }
}

@Composable
private fun PasswordCheckText(passwordChecks: List<Pair<Boolean, Int>>) {
    Text(
        text = buildAnnotatedString {
            for ((checkStatus, resId) in passwordChecks) {
                if (checkStatus) {
                    pushStyle(SpanStyle(color = Color.Gray))
                    append(stringResource(resId))
                    pop()
                } else {
                    append(stringResource(R.string.red_x))
                    append(" ")
                    append(stringResource(resId))
                }
            }
        },
        fontSize = 12.sp
    )
}