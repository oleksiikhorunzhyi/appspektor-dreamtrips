package com.worldventures.dreamtrips.wallet.ui.settings.general.impl;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.custom.BadgeView;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhoto;
import com.worldventures.dreamtrips.wallet.service.command.reset.ResetSmartCardCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.settings.general.WalletGeneralSettingsPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.WalletGeneralSettingsScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.FactoryResetDelegate;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.FactoryResetOperationView;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.view.OperationView;

public class WalletGeneralSettingsScreenImpl extends WalletBaseController<WalletGeneralSettingsScreen, WalletGeneralSettingsPresenter> implements WalletGeneralSettingsScreen {

   @Inject WalletGeneralSettingsPresenter presenter;

   private TextView profileName;
   private SimpleDraweeView profilePhoto;
   private BadgeView badgeFirmwareUpdates;
   private List<View> toggleableItems;
   private MaterialDialog confirmFactoryResetDialog = null;
   private MaterialDialog noConnectionDialog = null;
   private MaterialDialog confirmRestartSmartCardDialog = null;

   public WalletGeneralSettingsScreenImpl() {
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_settings_general, viewGroup, false);
   }

   @Override
   public boolean supportConnectionStatusLabel() {
      return false;
   }

   @Override
   public boolean supportHttpConnectionStatusLabel() {
      return false;
   }

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      final Toolbar toolbar = view.findViewById(R.id.toolbar);
      toolbar.setNavigationOnClickListener(v -> onNavigationClick());
      profileName = view.findViewById(R.id.profile_name);
      profilePhoto = view.findViewById(R.id.profile_photo);
      badgeFirmwareUpdates = view.findViewById(R.id.badgeFirmwareUpdates);
      badgeFirmwareUpdates.hide();
      final View smartCardProfile = view.findViewById(R.id.item_smartcard_profile);
      smartCardProfile.setOnClickListener(btnSmartCardProfile -> getPresenter().openProfileScreen());
      final View aboutView = view.findViewById(R.id.item_about);
      aboutView.setOnClickListener(btnAbout -> getPresenter().openAboutScreen());
      final View firmwareUpdatesView = view.findViewById(R.id.item_firmware_updates);
      firmwareUpdatesView.setOnClickListener(btnFirmwareUpdates -> getPresenter().openSoftwareUpdateScreen());
      final View factoryResetView = view.findViewById(R.id.item_factory_reset);
      factoryResetView.setOnClickListener(btnFactoryReset -> getPresenter().onClickFactoryResetSmartCard());
      final View setupNewScView = view.findViewById(R.id.item_setup_new_sc);
      setupNewScView.setOnClickListener(btnSetupNewSc -> getPresenter().openSetupNewSmartCardScreen());
      final View restartScView = view.findViewById(R.id.item_restart_sc);
      restartScView.setOnClickListener(btnRestartSc -> getPresenter().onClickRestartSmartCard());
      final View displayOptionsView = view.findViewById(R.id.item_display_options);
      displayOptionsView.setOnClickListener(btnDisplayOptions -> getPresenter().openDisplayOptionsScreen());
      toggleableItems = Arrays.asList(setupNewScView, restartScView, displayOptionsView);
   }

   protected void onNavigationClick() {
      getPresenter().goBack();
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
   public void setPreviewPhoto(@Nullable SmartCardUserPhoto photo) {
      if (photo != null) {
         profilePhoto.setImageURI(photo.uri());
      } //// TODO: 5/23/17 add placeholder
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
               .title(R.string.wallet_card_settings_turn_off_your_sc)
               .content(R.string.wallet_card_settings_are_you_sure)
               .positiveText(R.string.wallet_card_settings_power_off_dialog_ok)
               .negativeText(R.string.cancel)
               .onPositive(((dialog, which) -> getPresenter().onConfirmedRestartSmartCard()))
               .build();
      }
      if (!confirmRestartSmartCardDialog.isShowing()) confirmRestartSmartCardDialog.show();
   }

   @Override
   public Context getViewContext() {
      return getContext();
   }

   @Override
   public List<View> getToggleableItems() {
      return toggleableItems;
   }

   @Override
   public void showConfirmFactoryResetDialog() {
      if (confirmFactoryResetDialog == null) {
         confirmFactoryResetDialog = new MaterialDialog.Builder(getContext())
               .content(R.string.wallet_confirm_factory_reset_msg)
               .positiveText(R.string.wallet_continue_label)
               .negativeText(R.string.cancel)
               .onPositive((dialog, which) -> getPresenter().openFactoryResetScreen())
               .build();
      }
      if (!confirmFactoryResetDialog.isShowing()) confirmFactoryResetDialog.show();
   }

   @Override
   protected void onDetach(@NonNull View view) {
      if (noConnectionDialog != null) noConnectionDialog.dismiss();
      if (confirmFactoryResetDialog != null) confirmFactoryResetDialog.dismiss();
      if (confirmRestartSmartCardDialog != null) confirmRestartSmartCardDialog.dismiss();
      super.onDetach(view);
   }

   @Override
   public OperationView<ResetSmartCardCommand> provideResetOperationView(FactoryResetDelegate factoryResetDelegate) {
      return FactoryResetOperationView.create(getContext(),
            factoryResetDelegate::factoryReset,
            () -> {
            },
            R.string.wallet_error_enter_pin_title,
            R.string.wallet_error_enter_pin_msg,
            R.string.retry,
            R.string.cancel,
            R.string.loading,
            false);
   }

   @Override
   public WalletGeneralSettingsPresenter getPresenter() {
      return presenter;
   }
}
