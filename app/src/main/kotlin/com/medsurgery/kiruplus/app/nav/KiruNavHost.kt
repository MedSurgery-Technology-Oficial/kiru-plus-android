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
import com.medsurgery.kiruplus.feature.academy.ContentDetailScreen
import com.medsurgery.kiruplus.feature.quiz.QuizPlayerScreen
import com.medsurgery.kiruplus.feature.kapibaya.KapibayaChatScreen
import com.medsurgery.kiruplus.feature.ktools.CalculatorScreen
import com.medsurgery.kiruplus.feature.ktools.KToolsMenuScreen
import com.medsurgery.kiruplus.feature.chapterexam.ChapterExamScreen
import com.medsurgery.kiruplus.feature.library.LibraryScreen
import com.medsurgery.kiruplus.feature.library.ModuleDetailScreen
import com.medsurgery.kiruplus.feature.paywall.PaywallScreen
import com.medsurgery.kiruplus.feature.logbook.NewSurgicalLogScreen
import com.medsurgery.kiruplus.feature.main.MainScreen
import com.medsurgery.kiruplus.feature.onboarding.MedicalDisclaimerScreen
import com.medsurgery.kiruplus.feature.onboarding.SplashScreen
import com.medsurgery.kiruplus.feature.cases.CaseDetailScreen
import com.medsurgery.kiruplus.feature.cases.CasesScreen
import com.medsurgery.kiruplus.feature.drugs.DrugDetailScreen
import com.medsurgery.kiruplus.feature.drugs.DrugsScreen
import com.medsurgery.kiruplus.feature.kcortex.KCortexScreen
import com.medsurgery.kiruplus.feature.pearls.PearlDetailScreen
import com.medsurgery.kiruplus.feature.pearls.PearlsScreen
import com.medsurgery.kiruplus.feature.settings.DataExportScreen
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
                onContinueToLogin = {
                    navController.navigate(KiruRoute.Login) {
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
                onOpenLesson = { contentId ->
                    navController.navigate(KiruRoute.LessonDetail(contentId))
                },
                onOpenQuiz = { specialty ->
                    navController.navigate(KiruRoute.QuizPlayer(specialty))
                },
                onOpenNewSurgicalLog = { navController.navigate(KiruRoute.NewSurgicalLog) },
                onOpenKapibaya = { navController.navigate(KiruRoute.KapibayaChat) },
                onOpenKTools   = { navController.navigate(KiruRoute.KTools) },
                onOpenLibrary  = { navController.navigate(KiruRoute.Library) },
                onOpenDrugs    = { navController.navigate(KiruRoute.Drugs) },
                onOpenCases    = { navController.navigate(KiruRoute.Cases) },
                onOpenKCortex  = { navController.navigate(KiruRoute.KCortex) },
            )
        }

        composable<KiruRoute.NewSurgicalLog> {
            NewSurgicalLogScreen(
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() },
            )
        }

        composable<KiruRoute.KapibayaChat> {
            KapibayaChatScreen(onBack = { navController.popBackStack() })
        }

        composable<KiruRoute.KTools> {
            KToolsMenuScreen(
                onBack = { navController.popBackStack() },
                onOpenCalculator = { id -> navController.navigate(KiruRoute.KToolsCalculator(id)) },
            )
        }

        composable<KiruRoute.KToolsCalculator> { entry ->
            val args = entry.toRoute<KiruRoute.KToolsCalculator>()
            CalculatorScreen(calculatorId = args.id, onBack = { navController.popBackStack() })
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
                onDataExport = { navController.navigate(KiruRoute.DataExport) },
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

        composable<KiruRoute.DataExport> {
            DataExportScreen(onBack = { navController.popBackStack() })
        }

        composable<KiruRoute.Pearls> {
            PearlsScreen(
                onBack = { navController.popBackStack() },
                onOpenPearl = { pearlId ->
                    navController.navigate(KiruRoute.PearlDetail(pearlId))
                },
            )
        }

        composable<KiruRoute.PearlDetail> {
            PearlDetailScreen(onBack = { navController.popBackStack() })
        }

        composable<KiruRoute.Drugs> {
            DrugsScreen(
                onBack = { navController.popBackStack() },
                onOpenDrug = { drugId ->
                    navController.navigate(KiruRoute.DrugDetail(drugId))
                },
            )
        }

        composable<KiruRoute.DrugDetail> {
            DrugDetailScreen(onBack = { navController.popBackStack() })
        }

        composable<KiruRoute.Cases> {
            CasesScreen(
                onBack = { navController.popBackStack() },
                onOpenCase = { caseId ->
                    navController.navigate(KiruRoute.CaseDetail(caseId))
                },
            )
        }

        composable<KiruRoute.CaseDetail> {
            CaseDetailScreen(onBack = { navController.popBackStack() })
        }

        composable<KiruRoute.LessonDetail> {
            ContentDetailScreen(onBack = { navController.popBackStack() })
        }

        composable<KiruRoute.QuizPlayer> {
            QuizPlayerScreen(onBack = { navController.popBackStack() })
        }

        composable<KiruRoute.WebView> { entry ->
            val args = entry.toRoute<KiruRoute.WebView>()
            WebViewScreen(
                title = args.title,
                url = args.url,
                onBack = { navController.popBackStack() },
            )
        }

        composable<KiruRoute.Library> {
            LibraryScreen(
                onBack = { navController.popBackStack() },
                onOpenModule = { moduleId ->
                    navController.navigate(KiruRoute.LibraryModuleDetail(moduleId))
                },
                onStartChapterQuiz = { examId ->
                    navController.navigate(KiruRoute.ChapterExam(examId))
                },
            )
        }

        composable<KiruRoute.LibraryModuleDetail> {
            ModuleDetailScreen(onBack = { navController.popBackStack() })
        }

        composable<KiruRoute.ChapterExam> {
            ChapterExamScreen(onBack = { navController.popBackStack() })
        }

        composable<KiruRoute.Paywall> {
            PaywallScreen(onDismiss = { navController.popBackStack() })
        }

        composable<KiruRoute.KCortex> {
            KCortexScreen(onBack = { navController.popBackStack() })
        }
    }
}
