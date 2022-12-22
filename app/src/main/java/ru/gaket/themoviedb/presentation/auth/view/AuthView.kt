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
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.rememberScaffoldState
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
import ru.gaket.themoviedb.presentation.auth.viewmodel.AuthViewModel

@Preview
@Composable
private fun AuthViewPreview() {
    AuthView(
        emailError = "",
        emailInput = "",
        passwordError = "",
        passwordInput = "",
        loginError = null,
        isEmailErrorVisible = false,
        isPasswordErrorVisible = false,
        isAuthBtnEnabled = true,
        onEmailChange = { _ -> },
        onPasswordChange = { _ -> },
        onAuthClick = { },
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
        emailInput = authState.emailInput,
        emailError = if (emailError != null) stringResource(emailError) else "",
        passwordError = if (passwordError != null) stringResource(passwordError) else "",
        loginError = authState.logInError,
        passwordInput = authState.passwordInput,
        isEmailErrorVisible = authState.isEmailErrorVisible,
        isPasswordErrorVisible = authState.isPasswordErrorVisible,
        isAuthBtnEnabled = authState.isAuthBtnEnabled,
        onEmailChange = viewModel::onEmailInput,
        onPasswordChange = viewModel::onPasswordInput,
        onAuthClick = viewModel::auth,
    )
}

@Composable
private fun AuthView(
    emailInput: String,
    emailError: String,
    passwordError: String,
    onEmailChange: (String) -> Unit,
    isEmailErrorVisible: Boolean,
    passwordInput: String,
    isPasswordErrorVisible: Boolean,
    onPasswordChange: (String) -> Unit,
    loginError: Int?,
    isAuthBtnEnabled: Boolean,
    onAuthClick: () -> Unit,
) {
    ShowToastOnLoginErrorIfNeeded(loginError)

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(id = R.dimen.space_normal)),
    ) {
        OutlinedTextField(
            value = emailInput,
            onValueChange = onEmailChange,
            label = { Text(text = stringResource(id = R.string.email)) },
            isError = isEmailErrorVisible,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
        )
        if (isEmailErrorVisible) {
            Text(
                text = emailError,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        var passwordVisible by remember { mutableStateOf(false) }
        OutlinedTextField(
            value = passwordInput,
            onValueChange = onPasswordChange,
            label = { Text(text = stringResource(id = R.string.password)) },
            isError = isPasswordErrorVisible,
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
        if (isPasswordErrorVisible) {
            Text(
                text = passwordError,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Button(
            onClick = onAuthClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = isAuthBtnEnabled,
        ) {
            Text(text = stringResource(id = R.string.auth))
        }
    }
}

@Composable
private fun ShowToastOnLoginErrorIfNeeded(
    loginError: Int?,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
) {
    if (loginError == null) return

    val errorMessage = stringResource(loginError)

    LaunchedEffect(scaffoldState.snackbarHostState) {
        scaffoldState.snackbarHostState.showSnackbar(
            message = errorMessage,
        )
    }
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