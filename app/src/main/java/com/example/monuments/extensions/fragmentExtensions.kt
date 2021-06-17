package com.example.monuments.extensions

import android.view.View

fun View.changeVisible(value: Boolean) {
    visibility = if (value) {
        View.VISIBLE

    } else {
        View.GONE
    }
}