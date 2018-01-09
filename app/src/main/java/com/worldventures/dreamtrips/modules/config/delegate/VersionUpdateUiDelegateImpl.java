package com.worldventures.dreamtrips.modules.config.delegate;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.format.DateUtils;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.qa.QaAppConfig;

import timber.log.Timber;

public class VersionUpdateUiDelegateImpl implements VersionUpdateUiDelegate {

   private final Activity activity;
   private final SnappyRepository snappyRepository;
   private final QaAppConfig qaAppConfig;

   public VersionUpdateUiDelegateImpl(Activity activity, SnappyRepository snappyRepository, QaAppConfig qaAppConfig) {
      this.activity = activity;
      this.snappyRepository = snappyRepository;
      this.qaAppConfig = qaAppConfig;
   }

   @Override
   public void showOptionalUpdateDialog(long timestamp) {
      if (!qaAppConfig.getEnableBlockingInteractions()) {
         return;
      }
      String dateFormatted = DateUtils.formatDateTime(activity, timestamp,
            DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR);
      String message = activity.getString(R.string.app_update_alert_optional_update_message, dateFormatted);
      new MaterialDialog.Builder(activity)
            .title(R.string.app_update_alert_title)
            .neutralText(R.string.app_update_alert_neutral_button)
            .positiveText(R.string.app_update_alert_pos_button)
            .onPositive((dialog, which) -> openGooglePlayAppScreen())
            .dismissListener(dialogInterface -> saveConfirmedOptionalDialogShown())
            .content(message)
            .show();
   }

   private void saveConfirmedOptionalDialogShown() {
      snappyRepository.saveAppUpdateOptionalDialogConfirmedTimestamp(System.currentTimeMillis());
   }

   @Override
   public void showForceUpdateDialog() {
      if (!qaAppConfig.getEnableBlockingInteractions()) {
         return;
      }
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
