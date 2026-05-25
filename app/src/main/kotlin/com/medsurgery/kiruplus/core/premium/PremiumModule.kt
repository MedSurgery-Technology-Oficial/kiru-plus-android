package com.medsurgery.kiruplus.core.premium

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Singleton

/**
 * Hilt module that provides the [EntitlementState] flow.
 *
 * SPRINT A STUB — emits [EntitlementState.Free] unconditionally until:
 *   1. A valid RevenueCat Android `goog_` API key is configured.
 *   2. `EntitlementsService` (task A7) is implemented and wired here.
 *
 * Replace the [MutableStateFlow] source with a real RC-backed implementation
 * in Sprint B. Until then this stub is intentionally conservative: the app
 * behaves as if every user is on the free tier — no content is ever
 * unlocked erroneously.
 */
@Module
@InstallIn(SingletonComponent::class)
object PremiumModule {

    @Provides
    @Singleton
    fun provideEntitlementStateFlow(): StateFlow<EntitlementState> =
        MutableStateFlow(EntitlementState.Free)
}
