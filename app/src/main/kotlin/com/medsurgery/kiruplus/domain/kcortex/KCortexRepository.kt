package com.medsurgery.kiruplus.domain.kcortex

/**
 * Repository contract for K-CORTEX async surgical analysis pipeline.
 *
 * Flow:
 *  1. [submitAnalysis] → POST /v1/kcortex/analyze → synchronous response (backend
 *     may block or return immediately with a partial result; treated as one-shot here).
 *  2. Result is returned inline (the Render backend /v1/kcortex/analyze is synchronous
 *     on the HTTP level even though it uses Gemini Vision internally).
 *
 * Note: the iOS async pipeline (Supabase Storage + kcortex_jobs + Realtime) is the
 * NEW flow that hasn't fully shipped to production yet. Android v1 targets the
 * current production endpoint: POST https://kiru-backend.onrender.com/v1/kcortex/analyze
 * with multipart body (image + modality + prompt). Since Android v1 doesn't do
 * on-device image capture, we send a text-only body as "text_input" field instead
 * of a binary image. The backend falls back to text-only processing when no image is
 * present.
 */
interface KCortexRepository {
    /**
     * Submit a clinical analysis request and return the structured result.
     * This is a suspend function that performs the network call on IO dispatcher.
     */
    suspend fun analyze(request: KCortexRequest): Result<KCortexAnalysis>
}
