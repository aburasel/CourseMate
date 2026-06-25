package com.amr.coursemate.ocr

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.core.content.FileProvider
import com.googlecode.tesseract.android.TessBaseAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * On-device OCR for Bangla + Arabic using Tesseract (tesseract4android).
 *
 * Language data (`ben.traineddata`, `ara.traineddata`) is bundled under
 * `assets/tessdata/` and copied to the app's files dir on first use, because
 * [TessBaseAPI.init] needs a real filesystem path containing a `tessdata` folder.
 */
object OcrHelper {

    private const val LANG = "ben+ara"
    private const val MAX_DIM = 2048

    /**
     * Creates a `content://` Uri (via FileProvider) for the camera app to write a
     * captured photo into. Reuses a single file in cacheDir/images so scans don't pile up.
     */
    fun createImageCaptureUri(context: Context): Uri {
        val dir = File(context.cacheDir, "images").apply { mkdirs() }
        val file = File(dir, "ocr_capture.jpg")
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    /** True when at least one bundled `*.traineddata` exists in assets. */
    fun hasLanguageData(context: Context): Boolean = try {
        (context.assets.list("tessdata") ?: emptyArray()).any { it.endsWith(".traineddata") }
    } catch (e: IOException) {
        false
    }

    /**
     * Recognizes text in [uri]. Runs on a background dispatcher; safe to call from
     * a UI coroutine scope. Returns the recognized text trimmed, or an empty string
     * if the image could not be read or no text was found.
     */
    suspend fun recognize(context: Context, uri: Uri): String = withContext(Dispatchers.Default) {
        val dataPath = prepareDataPath(context)
        val bitmap = decodeSampledBitmap(context, uri) ?: return@withContext ""
        val api = TessBaseAPI()
        try {
            if (!api.init(dataPath, LANG)) return@withContext ""
            api.setImage(bitmap)
            api.getUTF8Text()?.trim().orEmpty()
        } catch (e: Exception) {
            ""
        } finally {
            try { api.recycle() } catch (_: Exception) {}
            bitmap.recycle()
        }
    }

    /** Copies bundled traineddata into filesDir/tessdata once; returns the parent path. */
    private fun prepareDataPath(context: Context): String {
        val parent = context.filesDir
        val tessDir = File(parent, "tessdata").apply { mkdirs() }
        val names = try {
            context.assets.list("tessdata") ?: emptyArray()
        } catch (e: IOException) {
            emptyArray()
        }
        for (name in names) {
            if (!name.endsWith(".traineddata")) continue
            val out = File(tessDir, name)
            if (out.exists() && out.length() > 0L) continue
            try {
                context.assets.open("tessdata/$name").use { input ->
                    FileOutputStream(out).use { input.copyTo(it) }
                }
            } catch (e: IOException) {
                // leave it missing; init() will fail gracefully and recognize() returns ""
            }
        }
        return parent.absolutePath
    }

    /** Decodes [uri] with downscaling so large photos don't OOM or slow OCR to a crawl. */
    private fun decodeSampledBitmap(context: Context, uri: Uri): Bitmap? {
        val resolver = context.contentResolver
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        try {
            resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) } ?: return null
        } catch (e: IOException) {
            return null
        }
        var sample = 1
        while (bounds.outWidth / sample > MAX_DIM || bounds.outHeight / sample > MAX_DIM) sample *= 2
        val opts = BitmapFactory.Options().apply {
            inSampleSize = sample
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }
        val bitmap = try {
            resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opts) }
        } catch (e: IOException) {
            null
        } ?: return null
        return applyExifRotation(context, uri, bitmap)
    }

    /** Rotates [bitmap] upright per the image's EXIF orientation (camera photos are often sideways). */
    private fun applyExifRotation(context: Context, uri: Uri, bitmap: Bitmap): Bitmap {
        val degrees = try {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                when (ExifInterface(stream).getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL
                )) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                    else -> 0f
                }
            } ?: 0f
        } catch (e: IOException) {
            0f
        }
        if (degrees == 0f) return bitmap
        val matrix = Matrix().apply { postRotate(degrees) }
        val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        if (rotated != bitmap) bitmap.recycle()
        return rotated
    }
}
