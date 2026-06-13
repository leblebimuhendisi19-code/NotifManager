package com.kiraldev.notifmanager

import android.graphics.drawable.Drawable

data class AppItem(
    val name: String,
    val packageName: String,
    val icon: Drawable,
    var mode: String
)
