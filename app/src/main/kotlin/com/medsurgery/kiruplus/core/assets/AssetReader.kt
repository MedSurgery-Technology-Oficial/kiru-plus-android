package com.medsurgery.kiruplus.core.assets

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Injectable wrapper around [Context.assets].
 *
 * Returns null instead of throwing when an asset is missing, allowing
 * callers to emit a clean error state rather than crash.
 */
@Singleton
class AssetReader @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun open(path: String): InputStream? = try {
        context.assets.open(path)
    } catch (e: IOException) {
        Timber.w(e, "Asset not found: $path")
        null
    }
}
