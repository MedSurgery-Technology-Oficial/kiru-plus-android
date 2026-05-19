package com.medsurgery.kiruplus.core.util

/**
 * Validador de email puro (sin android.util.Patterns) para que sea testeable
 * en unit tests JVM sin Robolectric.
 *
 * Reglas del regex:
 *  - local-part: caracteres alfanuméricos + subset RFC 5322 seguro.
 *  - dominio: uno o más labels (`label.`) seguidos de un TLD de ≥ 2 letras.
 *  - rechaza TLD de 1 caracter (e.g. `user@example.c`) y dominios sin punto.
 */
object EmailValidator {

    private val EMAIL_REGEX = Regex(
        "^[A-Za-z0-9!#\$%&'*+\\-/=?^_`{|}~.]+@" +
            "(?:[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?\\.)+" +
            "[A-Za-z]{2,}$",
    )

    fun isValid(email: String): Boolean = EMAIL_REGEX.matches(email.trim())
}
