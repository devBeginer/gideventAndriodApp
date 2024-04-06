package ru.gidevent.androidapp.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object Utils {
    const val PASSWORD_PATTERN = "^" +
            "(?=.*[0-9])" +         //at least 1 digit
            "(?=.*[a-zA-Z])" +      //any letter
            "(?=\\S+$)" +           //no white spaces
            ".{8,}" +               //at least 8 characters
            "$"

    const val EMAIL_PATTERN = "^" +
            "([a-z0-9_\\.-]+)" +    //several letters a-z, digits, _, - and .
            "@" +                   //@
            "([a-z0-9_\\.-]+)" +    //several letters a-z, digits, _, - and .
            "\\." +                 //.
            "([a-z]{2,6}\\.?)" +    //domain zone (several letters a-z repeats 2-6 times and . )
            "$"

    const val URL_PATTERN = "^" +
            "(https?:\\/\\/)?" +    //http, then s or not, then ://, however it may be missed
            "([\\da-z\\.-]+)" +     //several letters a-z, digits, - and .
            "\\." +                 // .
            "([a-z]{2,6}\\.?)" +    //domain zone (several letters a-z repeats 2-6 times and . )
            "([\\/\\w\\.-]*)*\\/?" +//slash and any alphabet symbols with . and - repeats from 0 to infinity times, and / or not
            "$"

    const val IMAGE_URL = "http://10.0.2.2:8080/api/auth/photo/"

    fun calculateInSampleSize(
        options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int
    ): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            // Calculate ratios of height and width to requested height and width
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = if (heightRatio > widthRatio) heightRatio else widthRatio
        }
        return inSampleSize
    }

    fun decodeSampledBitmap(
        byteArray: ByteArray?,
        reqWidth: Int, reqHeight: Int
    ): Bitmap? {
        // First decode with inJustDecodeBounds=true to check dimensions
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size, options)

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false

        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, options)
    }

    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    fun dayOfWeekToString(date: Calendar): String {
        val day = date.get(Calendar.DAY_OF_WEEK)
        return when (day) {
            Calendar.SUNDAY -> "Вс"
            Calendar.MONDAY -> "Пн"
            Calendar.TUESDAY -> "Вт"
            Calendar.WEDNESDAY -> "Ср"
            Calendar.THURSDAY -> "Чт"
            Calendar.FRIDAY -> "Пт"
            Calendar.SATURDAY -> "Сб"
            else -> ""
        }
    }


}