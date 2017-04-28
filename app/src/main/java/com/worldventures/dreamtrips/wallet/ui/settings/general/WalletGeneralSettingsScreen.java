package com.worldventures.dreamtrips.wallet.ui.settings.general;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.custom.BadgeView;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;

import butterknife.InjectView;
import butterknife.OnClick;

public class WalletGeneralSettingsScreen extends WalletLinearLayout<WalletGeneralSettingsPresenter.Screen, WalletGeneralSettingsPresenter, WalletGeneralSettingsPath> implements WalletGeneralSettingsPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;

   @InjectView(R.id.profile_name) TextView profileName;
   @InjectView(R.id.profile_photo) SimpleDraweeView profilePhoto;

   @InjectView(R.id.badgeFirmwareUpdates) BadgeView badgeFirmwareUpdates;

   private MaterialDialog confirmFactoryResetDialog = null;
   private MaterialDialog noConnectionDialog = null;
   private MaterialDialog confirmRestartSmartCardDialog = null;

   public WalletGeneralSettingsScreen(Context context) {
      this(context, null);
   }

   public WalletGeneralSettingsScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      if (isInEditMode()) return;
      toolbar.setNavigationOnClickListener(v -> onNavigationClick());
      badgeFirmwareUpdates.hide();
   }

   @NonNull
   @Override
   public WalletGeneralSettingsPresenter createPresenter() {
      return new WalletGeneralSettingsPresenter(getContext(), getInjector());
   }

   protected void onNavigationClick() {
      presenter.goBack();
   }

   @OnClick(R.id.item_smartcard_profile)
   void onClickProfile() {
      presenter.openProfileScreen();
   }

   @OnClick(R.id.item_about)
   void onClickAbout() {
      presenter.openAboutScreen();
   }

   @OnClick(R.id.item_firmware_updates)
   void onClickSoftwareUpdate() {
      presenter.openSoftwareUpdateScreen();
   }

   @OnClick(R.id.item_factory_reset)
   void onClickReset() {
      presenter.onClickFactoryResetSmartCard();
   }

   @OnClick(R.id.item_setup_new_sc)
   void onClickSetupNewSmartCard() {
      presenter.openSetupNewSmartCardScreen();
   }

   @OnClick(R.id.item_restart_sc)
   void onClickRestart() {
      presenter.onClickRestartSmartCard();
   }

   @Override
   public void firmwareUpdateCount(int count) {
      badgeFirmwareUpdates.setText(String.valueOf(count));
   }

   @Override
   public void showFirmwareVersion() {
      badgeFirmwareUpdates.hide(true);
   }

   @Override
   public void showFirmwareBadge() {
      badgeFirmwareUpdates.show(true);
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return new DialogOperationScreen(this);
   }

   @Override
   protected boolean hasToolbar() {
      return true;
   }

   @Override
   public void setPreviewPhoto(String photoUrl) {
      profilePhoto.setImageURI(photoUrl);
   }

   @Override
   public void setUserName(String firstName, String middleName, String lastName) {
      String fullName
            = (TextUtils.isEmpty(firstName) ? "" : firstName + " ")
            + (TextUtils.isEmpty(middleName) ? "" : middleName + " ")
            + (TextUtils.isEmpty(lastName) ? "" : lastName + " ");

      profileName.setText(fullName);
   }

   @Override
   public void showSCNonConnectionDialog() {
      if (noConnectionDialog == null) {
         noConnectionDialog = new MaterialDialog.Builder(getContext())
               .title(R.string.wallet_card_settings_cant_connected)
               .content(R.string.wallet_card_settings_message_cant_connected)
               .positiveText(R.string.ok)
               .build();
      }
      if (!noConnectionDialog.isShowing()) noConnectionDialog.show();
   }

   @Override
   public void showConfirmRestartSCDialog() {
      if (confirmRestartSmartCardDialog == null) {
         confirmRestartSmartCardDialog = new MaterialDialog.Builder(getContext())
               .title(R.string.wallet_card_settings_restart_your_sc)
               .content(R.string.wallet_card_settings_are_you_sure)
               .positiveText(R.string.wallet_card_settings_restart)
               .negativeText(R.string.cancel)
               .onPositive(((dialog, which) -> presenter.onConfirmedRestartSmartCard()))
               .build();
      }
      if (!confirmRestartSmartCardDialog.isShowing()) confirmRestartSmartCardDialog.show();
   }

   @Override
   public void showConfirmFactoryResetDialog() {
      if (confirmFactoryResetDialog == null) {
         confirmFactoryResetDialog = new MaterialDialog.Builder(getContext())
               .content(R.string.wallet_confirm_factory_reset_msg)
               .positiveText(R.string.wallet_continue_label)
               .negativeText(R.string.cancel)
               .onPositive((dialog, which) -> presenter.openFactoryResetScreen())
               .build();
      }
      if (!confirmFactoryResetDialog.isShowing()) confirmFactoryResetDialog.show();
   }

   @Override
   protected void onDetachedFromWindow() {
      if (noConnectionDialog != null) noConnectionDialog.dismiss();
      if (confirmFactoryResetDialog != null) confirmFactoryResetDialog.dismiss();
      if (confirmRestartSmartCardDialog != null) confirmRestartSmartCardDialog.dismiss();
      super.onDetachedFromWindow();
   }
}
