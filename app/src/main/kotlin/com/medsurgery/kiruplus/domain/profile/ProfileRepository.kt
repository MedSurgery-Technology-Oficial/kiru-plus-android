package com.medsurgery.kiruplus.domain.profile

interface ProfileRepository {
    /**
     * Trae el perfil del usuario autenticado actual.
     * RLS server-side filtra automáticamente al row con id = auth.uid().
     * Devuelve null si el usuario no tiene perfil aún (caso raro: el trigger
     * `handle_new_user` debería crearlo al signUp).
     */
    suspend fun fetchCurrentProfile(): Result<Profile?>
}
