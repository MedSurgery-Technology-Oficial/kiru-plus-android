package com.medsurgery.kiruplus.domain.kapibaya

import java.util.UUID

/**
 * Turn local del chat con Dr. Kapibaya. NO mapea 1:1 con la tabla
 * `kapibaya_conversation_turns` (que persiste el Edge Function server-side
 * con campos extra como `mode` y `auth_user_id`). Aquí solo modelamos lo que
 * el cliente necesita renderear.
 *
 * Roles posibles: "user" y "assistant" (paridad iOS).
 */
data class KapibayaTurn(
    val id: String = UUID.randomUUID().toString(),
    val role: Role,
    val content: String,
) {
    enum class Role { USER, ASSISTANT }
}
