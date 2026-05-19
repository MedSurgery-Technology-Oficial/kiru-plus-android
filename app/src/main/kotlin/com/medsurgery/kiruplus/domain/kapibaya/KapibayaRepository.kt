package com.medsurgery.kiruplus.domain.kapibaya

interface KapibayaRepository {
    /**
     * Envía un mensaje al Edge Function `ask_kapibaya`. La inserción del turn
     * (user + assistant) en `kapibaya_conversation_turns` la hace el EF con
     * service_role — el cliente sólo recibe el texto de la respuesta.
     *
     * @return el texto del assistant, o el error mapeado.
     */
    suspend fun sendMessage(
        conversationId: String,
        message: String,
    ): Result<String>
}
