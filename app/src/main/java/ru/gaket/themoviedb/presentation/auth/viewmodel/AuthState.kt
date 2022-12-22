package ru.gaket.themoviedb.presentation.auth.viewmodel

data class AuthState(
    val emailInput: String = "",
    val emailError: Int? = null,
    val isEmailErrorVisible: Boolean = false,
    val passwordInput: String = "",
    val passwordError: Int? = null,
    val isPasswordErrorVisible: Boolean = false,
    val isAuthBtnEnabled: Boolean = true,
    val isAuthorized: Boolean = false,
    val logInError: Int? = null,
)