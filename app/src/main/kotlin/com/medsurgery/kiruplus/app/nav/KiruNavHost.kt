package com.medsurgery.kiruplus.app.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.medsurgery.kiruplus.BuildConfig
import com.medsurgery.kiruplus.core.ui.WebViewScreen
import com.medsurgery.kiruplus.feature.auth.AccountDeletionScreen
import com.medsurgery.kiruplus.feature.auth.ForgotPasswordScreen
import com.medsurgery.kiruplus.feature.auth.LoginScreen
import com.medsurgery.kiruplus.feature.auth.RegisterScreen
import com.medsurgery.kiruplus.feature.main.MainScreen
import com.medsurgery.kiruplus.feature.onboarding.MedicalDisclaimerScreen
import com.medsurgery.kiruplus.feature.onboarding.SplashScreen
import com.medsurgery.kiruplus.feature.settings.SettingsScreen
import com.medsurgery.kiruplus.feature.store.ProductDetailScreen

@Composable
fun KiruNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = KiruRoute.Splash,
    ) {
        composable<KiruRoute.Splash> {
            SplashScreen(
                onContinueToDisclaimer = {
                    navController.navigate(KiruRoute.MedicalDisclaimer) {
                        popUpTo(KiruRoute.Splash) { inclusive = true }
                    }
                },
                onContinueToHome = {
                    navController.navigate(KiruRoute.Home) {
                        popUpTo(KiruRoute.Splash) { inclusive = true }
                    }
                },
            )
        }

        composable<KiruRoute.MedicalDisclaimer> {
            MedicalDisclaimerScreen(
                onAccepted = {
                    navController.navigate(KiruRoute.Login) {
                        popUpTo(KiruRoute.MedicalDisclaimer) { inclusive = true }
                    }
                },
                onPrivacyPolicy = {
                    navController.navigate(
                        KiruRoute.WebView(
                            title = "Privacidad",
                            url = BuildConfig.PRIVACY_POLICY_URL,
                        ),
                    )
                },
                onTerms = {
                    navController.navigate(
                        KiruRoute.WebView(
                            title = "Términos",
                            url = BuildConfig.TERMS_URL,
                        ),
                    )
                },
            )
        }

        composable<KiruRoute.Login> {
            LoginScreen(
                onAuthenticated = {
                    navController.navigate(KiruRoute.Home) {
                        popUpTo(KiruRoute.Login) { inclusive = true }
                    }
                },
                onForgotPassword = {
                    navController.navigate(KiruRoute.ForgotPassword)
                },
                onRegister = {
                    navController.navigate(KiruRoute.Register)
                },
                onPrivacyPolicy = {
                    navController.navigate(
                        KiruRoute.WebView(
                            title = "Privacidad",
                            url = BuildConfig.PRIVACY_POLICY_URL,
                        ),
                    )
                },
                onTerms = {
                    navController.navigate(
                        KiruRoute.WebView(
                            title = "Términos",
                            url = BuildConfig.TERMS_URL,
                        ),
                    )
                },
            )
        }

        composable<KiruRoute.Home> {
            MainScreen(
                onOpenSettings = { navController.navigate(KiruRoute.Settings) },
                onOpenPaywall = { navController.navigate(KiruRoute.Paywall) },
                onOpenAccountDeletion = { navController.navigate(KiruRoute.AccountDeletion) },
                onOpenPrivacyPolicy = {
                    navController.navigate(
                        KiruRoute.WebView(
                            title = "Privacidad",
                            url = BuildConfig.PRIVACY_POLICY_URL,
                        ),
                    )
                },
                onOpenTerms = {
                    navController.navigate(
                        KiruRoute.WebView(
                            title = "Términos",
                            url = BuildConfig.TERMS_URL,
                        ),
                    )
                },
                onOpenSubscriptions = {
                    navController.navigate(
                        KiruRoute.WebView(
                            title = "Suscripciones",
                            url = BuildConfig.SUBSCRIPTIONS_POLICY_URL,
                        ),
                    )
                },
                onOpenProductDetail = { productId ->
                    navController.navigate(KiruRoute.ProductDetail(productId))
                },
            )
        }

        composable<KiruRoute.ProductDetail> {
            ProductDetailScreen(onBack = { navController.popBackStack() })
        }

        composable<KiruRoute.Register> {
            RegisterScreen(
                onBackToLogin = { navController.popBackStack() },
            )
        }

        composable<KiruRoute.ForgotPassword> {
            ForgotPasswordScreen(
                onBack = { navController.popBackStack() },
            )
        }

        composable<KiruRoute.AccountDeletion> {
            AccountDeletionScreen(
                onBack = { navController.popBackStack() },
                onCompleted = {
                    navController.navigate(KiruRoute.Login) {
                        popUpTo(KiruRoute.Home) { inclusive = true }
                    }
                },
            )
        }

        composable<KiruRoute.Settings> {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onSignedOut = {
                    navController.navigate(KiruRoute.Login) {
                        popUpTo(KiruRoute.Home) { inclusive = true }
                    }
                },
                onDeleteAccount = { navController.navigate(KiruRoute.AccountDeletion) },
                onPrivacyPolicy = {
                    navController.navigate(
                        KiruRoute.WebView(
                            title = "Privacidad",
                            url = BuildConfig.PRIVACY_POLICY_URL,
                        ),
                    )
                },
                onTerms = {
                    navController.navigate(
                        KiruRoute.WebView(
                            title = "Términos",
                            url = BuildConfig.TERMS_URL,
                        ),
                    )
                },
                onSubscriptions = {
                    navController.navigate(
                        KiruRoute.WebView(
                            title = "Suscripciones",
                            url = BuildConfig.SUBSCRIPTIONS_POLICY_URL,
                        ),
                    )
                },
            )
        }

        composable<KiruRoute.WebView> { entry ->
            val args = entry.toRoute<KiruRoute.WebView>()
            WebViewScreen(
                title = args.title,
                url = args.url,
                onBack = { navController.popBackStack() },
            )
        }

        // TODO E7+: Paywall, Store
    }
}
