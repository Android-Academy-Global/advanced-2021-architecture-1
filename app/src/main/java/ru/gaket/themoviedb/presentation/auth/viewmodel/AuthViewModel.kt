package ru.gaket.themoviedb.presentation.auth.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.gaket.themoviedb.R
import ru.gaket.themoviedb.domain.auth.AuthInteractor
import ru.gaket.themoviedb.domain.auth.LogInError
import ru.gaket.themoviedb.domain.auth.User
import ru.gaket.themoviedb.domain.auth.isAuthorized
import ru.gaket.themoviedb.util.Result
import ru.gaket.themoviedb.util.VoidResult
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authInteractor: AuthInteractor,
) : ViewModel() {

    private val _authState = MutableStateFlow(
        AuthState(
            isAuthorized = authInteractor.isAuthorized(),
        )
    )

    val authState: StateFlow<AuthState> = _authState
        .asStateFlow()

    fun auth() {
        val authState = _authState.value
        val validatedEmail = User.Email.createIfValid(authState.emailInput)
        val validatedPassword = User.Password.createIfValid(authState.passwordInput)

        _authState.update { value ->
            value.copy(
                emailError = if (validatedEmail == null) R.string.email_input_error else null,
                isEmailErrorVisible = validatedEmail == null,
                passwordError = if (validatedPassword == null) R.string.password_input_error else
                    null,
                isPasswordErrorVisible = validatedPassword == null,
                isAuthBtnEnabled = true,
                logInError = null,
            )
        }

        if (validatedEmail != null && validatedPassword != null) {
            executeAuthRequest(validatedEmail, validatedPassword)
        }
    }

    private fun executeAuthRequest(email: User.Email, password: User.Password) {
        viewModelScope.launch {
            val result = authInteractor.auth(email, password)
            handleAuthResult(result)
        }
    }

    private fun handleAuthResult(result: VoidResult<LogInError>) {
        _authState.update { value ->
            when (result) {
                is Result.Success -> value.copy(
                    passwordError = null,
                    isPasswordErrorVisible = false,
                    emailError = null,
                    isEmailErrorVisible = false,
                    isAuthBtnEnabled = true,
                    isAuthorized = true,
                    logInError = null,
                )
                is Result.Error -> value.copy(
                    passwordError = null,
                    isPasswordErrorVisible = false,
                    emailError = null,
                    isEmailErrorVisible = false,
                    isAuthBtnEnabled = true,
                    isAuthorized = false,
                    logInError = result.result.messageResId,
                )
            }
        }
    }

    fun onEmailInput(email: String) {
        _authState.update { value ->
            value.copy(
                emailInput = email,
                emailError = null,
                isEmailErrorVisible = false,
                isAuthBtnEnabled = true,
            )
        }
    }

    fun onPasswordInput(password: String) {
        _authState.update { value ->
            value.copy(
                passwordInput = password,
                passwordError = null,
                isPasswordErrorVisible = false,
                isAuthBtnEnabled = true,
            )
        }
    }
}

@get:StringRes
internal val LogInError.messageResId: Int
    get() = when (this) {
        LogInError.InvalidUserCredentials -> R.string.invalid_user_credentials
        LogInError.Unknown -> R.string.unknown_error
    }
