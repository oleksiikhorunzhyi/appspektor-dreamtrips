package com.worldventures.dreamtrips.modules.common.presenter.delegate;

import android.app.Activity;
import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.util.Utils;

/**
 * Display an offline warning once a session.
 */
public class OfflineWarningDelegate {

   private boolean offlineHintShown;

   public boolean needToShowOfflineAlert(Context context) {
      if (offlineHintShown) return false;
      return !Utils.isConnected(context);
   }

   public void showOfflineWarning(Activity activity) {
      new MaterialDialog.Builder(activity)
            .title(R.string.offline_warning_dialog_title)
            .content(R.string.offline_warning_dialog_title)
            .show();
      offlineHintShown = true;
   }

   public void resetState() {
      offlineHintShown = false;
   }
}
