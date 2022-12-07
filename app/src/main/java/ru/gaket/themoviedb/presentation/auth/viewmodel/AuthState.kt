package ru.gaket.themoviedb.presentation.auth.viewmodel

data class AuthState(
    val emailError: Int? = null,
    val isEmailErrorVisible: Boolean = false,
    val passwordError: Int? = null,
    val isPasswordErrorVisible: Boolean = false,
    val isBtnEnabled: Boolean = true,
    val isAuthorized: Boolean = false,
    val logInError: Int? = null,
)