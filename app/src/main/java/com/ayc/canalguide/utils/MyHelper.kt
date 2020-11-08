package com.ayc.canalguide.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.TypedValue
import kotlin.math.roundToInt

class MyHelper {
    companion object {

        fun makeCall(context: Context?, number: String) {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$number")
            }
            context?.startActivity(intent)
        }

        fun openUrl(context: Context?, urls: String) {
            val uris = Uri.parse(urls)
            val intents = Intent(Intent.ACTION_VIEW, uris).apply {
                putExtra("new_window", true)
            }
            context?.startActivity(intents)
        }

    }
}


internal fun Int.dpToPx(context: Context): Int {
    return dpToPx(context, this.toFloat())
}

private fun dpToPx(context: Context, dp: Float): Int =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics).roundToInt()