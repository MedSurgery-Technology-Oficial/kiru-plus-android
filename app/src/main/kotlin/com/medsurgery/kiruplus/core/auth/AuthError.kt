package com.medsurgery.kiruplus.core.auth

import androidx.annotation.StringRes
import com.medsurgery.kiruplus.R
import io.github.jan.supabase.exceptions.HttpRequestException

sealed class AuthError(
    @StringRes val messageRes: Int,
    cause: Throwable? = null,
) : Exception(cause) {
    data object InvalidCredentials : AuthError(R.string.auth_error_invalid_credentials)
    data object EmailAlreadyInUse : AuthError(R.string.auth_error_email_in_use)
    data object EmailNotConfirmed : AuthError(R.string.auth_error_email_not_confirmed)
    data object RateLimited : AuthError(R.string.auth_error_rate_limited)
    data object WeakPassword : AuthError(R.string.auth_error_weak_password)
    data object InvalidEmail : AuthError(R.string.auth_error_invalid_email)
    data object PasswordsDontMatch : AuthError(R.string.auth_error_passwords_dont_match)
    data object Network : AuthError(R.string.auth_error_network)
    data class Unknown(val original: Throwable) : AuthError(R.string.error_generic, original)
}

fun Throwable.toAuthError(): AuthError {
    if (this is AuthError) return this

    val msg = (message ?: "").lowercase()
    val isNetwork = this is HttpRequestException ||
        cause is HttpRequestException ||
        msg.contains("unable to resolve host") ||
        msg.contains("failed to connect") ||
        msg.contains("network is unreachable")

    return when {
        isNetwork -> AuthError.Network
        msg.contains("invalid login credentials") ||
            msg.contains("invalid_credentials") ||
            msg.contains("invalid grant") -> AuthError.InvalidCredentials
        msg.contains("user already registered") ||
            msg.contains("user_already_exists") ||
            msg.contains("email_exists") -> AuthError.EmailAlreadyInUse
        msg.contains("email not confirmed") ||
            msg.contains("email_not_confirmed") -> AuthError.EmailNotConfirmed
        msg.contains("rate limit") ||
            msg.contains("over_request_rate_limit") ||
            msg.contains("too many requests") -> AuthError.RateLimited
        msg.contains("password should be") ||
            msg.contains("weak_password") -> AuthError.WeakPassword
        msg.contains("unable to validate email") ||
            msg.contains("email_address_invalid") ||
            msg.contains("invalid email") -> AuthError.InvalidEmail
        else -> AuthError.Unknown(this)
    }
}
