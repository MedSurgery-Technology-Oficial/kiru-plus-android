package com.medsurgery.kiruplus.feature.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.medsurgery.kiruplus.feature.academy.AcademyScreen
import com.medsurgery.kiruplus.feature.home.HomeScreen
import com.medsurgery.kiruplus.feature.logbook.LogbookScreen
import com.medsurgery.kiruplus.feature.profile.ProfileScreen
import com.medsurgery.kiruplus.feature.store.StoreScreen

/**
 * Root scaffold con Material 3 NavigationBar.
 *
 * Tabs viven como `MainTab` enum + estado local (rememberSaveable) para que sobreviva
 * a configuración. Los destinos OUTSIDE del bottom nav (Settings, AccountDeletion,
 * Privacy/Terms WebViews, Paywall) se reciben como callbacks y delegan al
 * `KiruNavHost` (outer NavController).
 */
@Composable
fun MainScreen(
    onOpenSettings: () -> Unit,
    onOpenPaywall: () -> Unit,
    onOpenAccountDeletion: () -> Unit,
    onOpenPrivacyPolicy: () -> Unit,
    onOpenTerms: () -> Unit,
    onOpenSubscriptions: () -> Unit,
    onOpenProductDetail: (String) -> Unit,
    onOpenPearls: () -> Unit,
    onOpenLesson: (String) -> Unit,
    onOpenQuiz: (String) -> Unit,
    onOpenNewSurgicalLog: () -> Unit,
    onOpenKapibaya: () -> Unit,
    onOpenKTools: () -> Unit,
) {
    var current by rememberSaveable { mutableStateOf(MainTab.Home) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                MainTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = current == tab,
                        onClick = { current = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = stringResource(tab.labelRes),
                            )
                        },
                        label = { Text(stringResource(tab.labelRes)) },
                    )
                }
            }
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            when (current) {
                MainTab.Home -> HomeScreen(
                    onOpenSettings = onOpenSettings,
                    onOpenPaywall = onOpenPaywall,
                    onOpenStore = { current = MainTab.Store },
                    onOpenPearls = onOpenPearls,
                    onOpenKapibaya = onOpenKapibaya,
                    onOpenKTools = onOpenKTools,
                )
                MainTab.Academy -> AcademyScreen(onOpenLesson = onOpenLesson, onOpenQuiz = onOpenQuiz)
                MainTab.Logbook -> LogbookScreen(onAddLog = onOpenNewSurgicalLog)
                MainTab.Store -> StoreScreen(onOpenProduct = onOpenProductDetail)
                MainTab.Profile -> ProfileScreen(
                    onDeleteAccount = onOpenAccountDeletion,
                    onPrivacyPolicy = onOpenPrivacyPolicy,
                    onTerms = onOpenTerms,
                    onSubscriptions = onOpenSubscriptions,
                    onOpenSettings = onOpenSettings,
                )
            }
        }
    }
}
