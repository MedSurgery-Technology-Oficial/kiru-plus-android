package com.medsurgery.kiruplus.core.ui

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.medsurgery.kiruplus.R

/**
 * Pantalla genérica WebView para mostrar las 3 URLs legales de medsurgery.academy:
 * privacy policy, terms, subscriptions policy.
 *
 * Espejo del patrón iOS donde estas URLs viven en `Info.plist` y se abren con WebView.
 */
@Composable
fun WebViewScreen(
    title: String,
    url: String,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back),
                        )
                    }
                },
            )
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    WebView(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                        )
                        webViewClient = WebViewClient()
                        // Por defecto: no JS habilitado. Las URLs legales son estáticas.
                        // Si alguna requiere JS, habilitar puntualmente y revisar seguridad.
                        settings.javaScriptEnabled = false
                        settings.domStorageEnabled = false
                        loadUrl(url)
                    }
                },
            )
        }
    }
}
