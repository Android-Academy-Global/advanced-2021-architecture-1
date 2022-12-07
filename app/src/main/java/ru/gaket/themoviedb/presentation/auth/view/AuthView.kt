package ru.gaket.themoviedb.presentation.auth.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.filter
import ru.gaket.themoviedb.R
import ru.gaket.themoviedb.core.navigation.Navigator
import ru.gaket.themoviedb.presentation.auth.viewmodel.AuthState
import ru.gaket.themoviedb.presentation.auth.viewmodel.AuthViewModel
import ru.gaket.themoviedb.util.showSystemMessage

@Preview
@Composable
private fun AuthViewPreview() {
    AuthView(
        emailError = "",
        passwordError = "",
        authState = AuthState(),
        onAuthClick = { _, _ -> },
    )
}

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
internal fun AuthView(
    viewModel: AuthViewModel,
    navigator: Navigator,
) {

    val authState by viewModel.authState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        snapshotFlow { authState }
            .filter { state -> state.isAuthorized }
            .collect {
                navigator.back()
            }
    }

    val emailError = authState.emailError
    val passwordError = authState.passwordError

    AuthView(
        emailError = if (emailError != null) stringResource(emailError) else "",
        passwordError = if (passwordError != null) stringResource(passwordError) else "",
        authState = authState,
        onAuthClick = viewModel::auth,
    )
}

@Composable
private fun AuthView(
    emailError: String,
    passwordError: String,
    authState: AuthState,
    onAuthClick: (email: String, password: String) -> Unit,
) {
    val loginError = authState.logInError

    ShowToastOnLoginErrorIfNeeded(loginError)

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(id = R.dimen.space_normal)),
    ) {
        var emailInput by remember { mutableStateOf("") }
        OutlinedTextField(
            value = emailInput,
            onValueChange = { input -> emailInput = input },
            label = { Text(text = stringResource(id = R.string.email)) },
            isError = authState.isEmailErrorVisible,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
        )
        if (authState.isEmailErrorVisible) {
            Text(
                text = emailError,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        var passwordInput by remember { mutableStateOf("") }
        var passwordVisible by remember { mutableStateOf(false) }
        OutlinedTextField(
            value = passwordInput,
            onValueChange = { input -> passwordInput = input },
            label = { Text(text = stringResource(id = R.string.password)) },
            isError = authState.isPasswordErrorVisible,
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                PasswordVisibilityToggleButton(isVisible = passwordVisible) {
                    passwordVisible = !passwordVisible
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
        )
        if (authState.isPasswordErrorVisible) {
            Text(
                text = passwordError,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Button(
            onClick = {
                onAuthClick(emailInput, passwordInput)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = authState.isBtnEnabled,
        ) {
            Text(text = stringResource(id = R.string.auth))
        }
    }
}

@Composable
private fun ShowToastOnLoginErrorIfNeeded(loginError: Int?) {
    if (loginError == null) return

    val context = LocalContext.current
    context.showSystemMessage(stringResource(loginError))
}

@Composable
private fun PasswordVisibilityToggleButton(isVisible: Boolean, onToggle: () -> Unit) {
    IconButton(onClick = onToggle) {
        Icon(
            imageVector = if (isVisible) {
                Icons.Filled.Visibility
            } else {
                Icons.Filled.VisibilityOff
            },
            contentDescription = if (isVisible) {
                stringResource(id = R.string.hide_password)
            } else {
                stringResource(id = R.string.show_password)
            }
        )
    }
}