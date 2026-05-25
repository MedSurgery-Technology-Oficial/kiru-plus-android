package com.medsurgery.kiruplus.nav

import com.medsurgery.kiruplus.app.nav.KiruRoute
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class KiruRouteTest {

    // --- Singleton object routes ---

    @Test
    fun `singleton routes are instances of KiruRoute`() {
        assertTrue(KiruRoute.Splash is KiruRoute)
        assertTrue(KiruRoute.Login is KiruRoute)
        assertTrue(KiruRoute.Home is KiruRoute)
        assertTrue(KiruRoute.Library is KiruRoute)
        assertTrue(KiruRoute.Pearls is KiruRoute)
        assertTrue(KiruRoute.KTools is KiruRoute)
        assertTrue(KiruRoute.KapibayaChat is KiruRoute)
        assertTrue(KiruRoute.Settings is KiruRoute)
        assertTrue(KiruRoute.Paywall is KiruRoute)
        assertTrue(KiruRoute.AccountDeletion is KiruRoute)
        assertTrue(KiruRoute.DataExport is KiruRoute)
    }

    @Test
    fun `singleton routes are referentially equal to themselves`() {
        val a = KiruRoute.Library
        val b = KiruRoute.Library
        assertTrue(a === b)
    }

    // --- Parametric routes carry their values ---

    @Test
    fun `LibraryModuleDetail carries moduleId`() {
        val route = KiruRoute.LibraryModuleDetail("surgery_principles")
        assertEquals("surgery_principles", route.moduleId)
    }

    @Test
    fun `ProductDetail carries productId`() {
        val route = KiruRoute.ProductDetail("prod_kiru_atlas")
        assertEquals("prod_kiru_atlas", route.productId)
    }

    @Test
    fun `PearlDetail carries pearlId`() {
        val route = KiruRoute.PearlDetail(42)
        assertEquals(42, route.pearlId)
    }

    @Test
    fun `QuizPlayer carries specialty`() {
        val route = KiruRoute.QuizPlayer("hepatobiliary")
        assertEquals("hepatobiliary", route.specialty)
    }

    @Test
    fun `KToolsCalculator carries calculator id`() {
        val route = KiruRoute.KToolsCalculator("apache_ii")
        assertEquals("apache_ii", route.id)
    }

    @Test
    fun `WebView carries title and url`() {
        val route = KiruRoute.WebView(
            title = "Política de privacidad",
            url = "https://www.medsurgery.academy/politica-de-privacidad-kiru-app",
        )
        assertEquals("Política de privacidad", route.title)
        assertEquals("https://www.medsurgery.academy/politica-de-privacidad-kiru-app", route.url)
    }

    @Test
    fun `LessonDetail carries contentId`() {
        val route = KiruRoute.LessonDetail("lesson_001")
        assertEquals("lesson_001", route.contentId)
    }

    // --- Equality semantics ---

    @Test
    fun `same parametric routes are equal`() {
        assertEquals(
            KiruRoute.LibraryModuleDetail("m1"),
            KiruRoute.LibraryModuleDetail("m1"),
        )
        assertEquals(
            KiruRoute.PearlDetail(7),
            KiruRoute.PearlDetail(7),
        )
        assertEquals(
            KiruRoute.QuizPlayer("emergency"),
            KiruRoute.QuizPlayer("emergency"),
        )
    }

    @Test
    fun `different parameter values produce unequal routes`() {
        assertNotEquals(
            KiruRoute.LibraryModuleDetail("m1"),
            KiruRoute.LibraryModuleDetail("m2"),
        )
        assertNotEquals(
            KiruRoute.PearlDetail(1),
            KiruRoute.PearlDetail(2),
        )
        assertNotEquals(
            KiruRoute.KToolsCalculator("apache_ii"),
            KiruRoute.KToolsCalculator("sofa"),
        )
    }

    @Test
    fun `different route types are not equal`() {
        val library: Any = KiruRoute.Library
        val pearls: Any = KiruRoute.Pearls
        assertNotEquals(library, pearls)
        val login: Any = KiruRoute.Login
        val register: Any = KiruRoute.Register
        assertNotEquals(login, register)
    }

    // --- Parametric routes are distinct from object routes ---

    @Test
    fun `LibraryModuleDetail is distinct from Library`() {
        val detail: KiruRoute = KiruRoute.LibraryModuleDetail("m1")
        val list: KiruRoute = KiruRoute.Library
        assertNotEquals(detail, list)
    }

    @Test
    fun `ChapterExam carries examId`() {
        val route = KiruRoute.ChapterExam("Chapter-042")
        assertEquals("Chapter-042", route.examId)
    }

    @Test
    fun `ChapterExam routes with same id are equal`() {
        assertEquals(KiruRoute.ChapterExam("Chapter-001"), KiruRoute.ChapterExam("Chapter-001"))
    }

    @Test
    fun `ChapterExam routes with different ids are not equal`() {
        assertNotEquals(KiruRoute.ChapterExam("Chapter-001"), KiruRoute.ChapterExam("Chapter-002"))
    }
}
