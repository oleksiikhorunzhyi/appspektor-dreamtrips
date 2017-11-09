package com.worldventures.dreamtrips.modules.common.presenter.delegate;

import android.app.Activity;
import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.core.utils.NetworkUtils;
import com.worldventures.dreamtrips.R;

/**
 * Display an offline warning once a session.
 */
public class OfflineWarningDelegate {

   private boolean offlineHintShown;

   public boolean needToShowOfflineAlert(Context context) {
      if (offlineHintShown) {
         return false;
      }
      return !NetworkUtils.isConnected(context);
   }

   public void showOfflineWarning(Activity activity) {
      new MaterialDialog.Builder(activity)
            .title(R.string.offline_warning_dialog_title)
            .content(R.string.offline_warning_dialog_message)
            .positiveText(R.string.ok)
            .show();
      offlineHintShown = true;
   }

   public void resetState() {
      offlineHintShown = false;
   }
}
