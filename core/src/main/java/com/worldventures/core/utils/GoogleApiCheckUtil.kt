package com.worldventures.core.utils

import android.app.Activity
import android.content.Context
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

fun checkAvailability(context: Context, successAction: () -> Unit, errorAction: (Int) -> Unit) {
   val code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context)
   if (code == ConnectionResult.SUCCESS) {
      successAction.invoke()
   } else {
      errorAction.invoke(code)
   }
}

/** For better implementation in java files*/
fun checkAvailability(context: Context, successAction: SuccessAction, errorAction: ErrorAction) {
   checkAvailability(context, successAction::onPlayServicesAvailable, errorAction::onPlayServicesError)
}

fun showErrorDialog(activity: Activity, errorCode: Int, requestCode: Int) {
   GoogleApiAvailability.getInstance().getErrorDialog(activity, errorCode, requestCode).show()
}

interface SuccessAction {
   fun onPlayServicesAvailable()
}

interface ErrorAction {
   fun onPlayServicesError(errorCode: Int)
}
