package com.worldventures.dreamtrips.social.util

import android.os.Build
import android.support.annotation.ColorRes
import android.view.View

@Suppress("DEPRECATION")
fun View.getColor(@ColorRes colorRes: Int): Int {
   when (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
      true -> return this.resources.getColor(colorRes, null)
      false -> return this.resources.getColor(colorRes)
   }
}
