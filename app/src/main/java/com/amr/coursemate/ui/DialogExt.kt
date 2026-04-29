package com.amr.coursemate.ui

import android.view.WindowManager
import androidx.appcompat.app.AlertDialog

fun AlertDialog.adjustForKeyboard(): AlertDialog = apply {
    window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
}