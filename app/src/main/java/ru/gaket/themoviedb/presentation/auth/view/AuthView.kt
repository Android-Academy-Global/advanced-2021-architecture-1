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
import androidx.compose.runtime.livedata.observeAsState
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
import kotlinx.coroutines.flow.filterIsInstance
import ru.gaket.themoviedb.R
import ru.gaket.themoviedb.core.navigation.Navigator
import ru.gaket.themoviedb.presentation.auth.viewmodel.AuthState
import ru.gaket.themoviedb.presentation.auth.viewmodel.AuthViewModel

@Preview
@Composable
private fun AuthViewPreview() {
    AuthView(
        emailError = null,
        passwordError = null,
        onAuthClick = { _, _ -> },
    )
}

@Composable
internal fun AuthView(
    viewModel: AuthViewModel,
    navigator: Navigator,
) {
    val authState by viewModel.authState.observeAsState()
    val emailError = if (authState is AuthState.InputError.Email) {
        stringResource(id = R.string.email_input_error)
    } else {
        null
    }
    val passwordError = if (authState is AuthState.InputError.Password) {
        stringResource(id = R.string.password_input_error)
    } else {
        null
    }
    LaunchedEffect(Unit) {
        snapshotFlow { authState }
            .filterIsInstance<AuthState.Authorized>()
            .collect {
                navigator.back()
            }
    }
    AuthView(
        emailError = emailError,
        passwordError = passwordError,
        onAuthClick = viewModel::auth,
    )
}

@Composable
private fun AuthView(
    emailError: String?,
    passwordError: String?,
    onAuthClick: (email: String, password: String) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(id = R.dimen.space_normal)),
    ) {
        var emailInput by remember { mutableStateOf("") }
        var emailErrorVisible by remember(emailError) { mutableStateOf(!emailError.isNullOrEmpty()) }
        OutlinedTextField(
            value = emailInput,
            onValueChange = {
                emailErrorVisible = false
                emailInput = it
            },
            label = {
                Text(text = stringResource(id = R.string.email))
            },
            isError = emailErrorVisible,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
        )
        if (emailErrorVisible) {
            Text(
                text = emailError ?: "",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        var passwordInput by remember { mutableStateOf("") }
        var passwordVisible by remember { mutableStateOf(false) }
        var passwordErrorVisible by remember(passwordError) { mutableStateOf(!passwordError.isNullOrEmpty()) }
        OutlinedTextField(
            value = passwordInput,
            onValueChange = {
                passwordErrorVisible = false
                passwordInput = it
            },
            label = {
                Text(text = stringResource(id = R.string.password))
            },
            isError = passwordErrorVisible,
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
        if (passwordErrorVisible) {
            Text(
                text = passwordError ?: "",
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
        ) {
            Text(text = stringResource(id = R.string.auth))
        }
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