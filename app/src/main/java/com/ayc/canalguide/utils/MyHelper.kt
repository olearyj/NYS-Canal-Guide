package com.ayc.canalguide.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

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