package com.worldventures.dreamtrips.modules.version_check.delegate;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.format.DateUtils;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;

import timber.log.Timber;

public class VersionUpdateUiDelegateImpl implements VersionUpdateUiDelegate {

   private Activity activity;
   private SnappyRepository snappyRepository;

   public VersionUpdateUiDelegateImpl(Activity activity, SnappyRepository snappyRepository) {
      this.activity = activity;
      this.snappyRepository = snappyRepository;
   }

   @Override
   public void showOptionalUpdateDialog(long timestamp) {
      String dateFormatted = DateUtils.formatDateTime(activity, timestamp,
            DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR);
      String message = activity.getString(R.string.app_update_alert_optional_update_message, dateFormatted);
      new MaterialDialog.Builder(activity)
            .title(R.string.app_update_alert_title)
            .neutralText(R.string.app_update_alert_neutral_button)
            .onNeutral((dialog, which) -> saveConfirmedOptionalDialogShown())
            .positiveText(R.string.app_update_alert_pos_button)
            .onPositive((dialog, which) -> {
               saveConfirmedOptionalDialogShown();
               openGooglePlayAppScreen();
            })
            .content(message)
            .show();
   }

   private void saveConfirmedOptionalDialogShown() {
      snappyRepository.saveAppUpdateOptionalDialogConfirmedTimestamp(System.currentTimeMillis());
   }

   @Override
   public void showForceUpdateDialog() {
      new MaterialDialog.Builder(activity)
            .title(R.string.app_update_alert_title)
            .positiveText(R.string.app_update_alert_pos_button)
            .onPositive((dialog, which) -> openGooglePlayAppScreen())
            .content(R.string.app_update_alert_force_update_message)
            .autoDismiss(false)
            .cancelable(false)
            .show();
   }

   @Override
   public void openGooglePlayAppScreen() {
      try {
         activity.startActivity(new Intent(Intent.ACTION_VIEW,
               Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_PACKAGE_PROD)));
      } catch (Exception e) {
         Timber.w(e, "Could not open app Google Play page");
      }
   }
}
