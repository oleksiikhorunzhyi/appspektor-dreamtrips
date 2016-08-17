package com.worldventures.dreamtrips.modules.video.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import timber.log.Timber;

public class HeadphonesPlugReceiver extends BroadcastReceiver {

   /**
    * @param context
    * @param intent  The intent will have the following extra values:
    *                state - 0 for unplugged, 1 for plugged.
    *                name - Headset type, human readable string
    *                microphone - 1 if headset has a microphone, 0 otherwise
    */
   @Override
   public final void onReceive(Context context, Intent intent) {
      if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
         int state = intent.getIntExtra("state", -1);
         switch (state) {
            case 0:
               Timber.d("Headset is unplugged");
               onHeadphonesUnPlugged();
               break;
            case 1:
               Timber.d("Headset is plugged");
               onHeadphonesPluggedIn();
               break;
            default:
               Timber.d("Headset state is undefined");
         }
      }
   }

   protected void onHeadphonesPluggedIn() {
   }

   protected void onHeadphonesUnPlugged() {
   }
}