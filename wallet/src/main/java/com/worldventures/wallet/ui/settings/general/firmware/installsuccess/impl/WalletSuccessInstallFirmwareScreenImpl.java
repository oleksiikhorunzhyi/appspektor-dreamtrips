package com.worldventures.wallet.ui.settings.general.firmware.installsuccess.impl;


import android.animation.AnimatorSet;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldventures.wallet.R;
import com.worldventures.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.wallet.ui.common.base.WalletBaseController;
import com.worldventures.wallet.ui.settings.general.firmware.installsuccess.WalletSuccessInstallFirmwarePresenter;
import com.worldventures.wallet.ui.settings.general.firmware.installsuccess.WalletSuccessInstallFirmwareScreen;

import java.util.Arrays;

import javax.inject.Inject;

import static android.animation.ObjectAnimator.ofFloat;

public class WalletSuccessInstallFirmwareScreenImpl
      extends WalletBaseController<WalletSuccessInstallFirmwareScreen, WalletSuccessInstallFirmwarePresenter>
      implements WalletSuccessInstallFirmwareScreen {

   private static final String KEY_FIRMWARE_UPDATE_DATA = "key_firmware_update_data";
   private static final int ANIM_DURATION_MAIN_IMAGE = 300;
   private static final int ANIM_DURATION_SUB_IMAGE = 300;
   private static final int ANIM_DURATION_TITLE = 400;
   private static final int ANIM_DURATION_SUB_TITLE = 400;
   private static final int ANIM_DURATION_DONE_BTN = 500;
   private static final int ANIM_DELAY = 700;

   private ImageView image;
   private ImageView subImage;
   private TextView title;
   private TextView subTitle;
   private Button done;

   @Inject WalletSuccessInstallFirmwarePresenter presenter;

   public static WalletSuccessInstallFirmwareScreenImpl create(FirmwareUpdateData firmwareUpdateData) {
      final Bundle args = new Bundle();
      args.putSerializable(KEY_FIRMWARE_UPDATE_DATA, firmwareUpdateData);
      return new WalletSuccessInstallFirmwareScreenImpl(args);
   }

   public WalletSuccessInstallFirmwareScreenImpl() {
      super();
   }

   public WalletSuccessInstallFirmwareScreenImpl(Bundle args) {
      super(args);
   }

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      final Toolbar toolbar = view.findViewById(R.id.toolbar);
      toolbar.setNavigationIcon(new ColorDrawable(Color.TRANSPARENT));
      image = view.findViewById(R.id.success_firmware_install_image);
      subImage = view.findViewById(R.id.success_firmware_install_sub_image);
      title = view.findViewById(R.id.success_firmware_install_title);
      subTitle = view.findViewById(R.id.success_firmware_install_sub_title);
      done = view.findViewById(R.id.success_firmware_install_done);
      done.setOnClickListener(btnDone -> getPresenter().finishUpdateFlow());
      hideAllView();
   }

   @Override
   protected void onAttach(@NonNull View view) {
      super.onAttach(view);
      getView().postDelayed(this::startAnim, ANIM_DELAY);
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_success_install_firmware, viewGroup, false);
   }

   @Override
   public boolean supportConnectionStatusLabel() {
      return false;
   }

   @Override
   public boolean supportHttpConnectionStatusLabel() {
      return false;
   }

   private void hideAllView() {
      for (View view : Arrays.asList(image, subTitle, title, subImage, done)) {
         view.setAlpha(0);
      }
   }

   @Override
   public void setSubTitle(String subTitle) {
      this.subTitle.setText(getResources().getString(R.string.wallet_firmware_install_success_sub_title, subTitle));
   }

   @Override
   public FirmwareUpdateData getFirmwareUpdateData() {
      return (getArgs() != null && !getArgs().isEmpty() && getArgs().containsKey(KEY_FIRMWARE_UPDATE_DATA))
            ? (FirmwareUpdateData) getArgs().getSerializable(KEY_FIRMWARE_UPDATE_DATA)
            : null;
   }

   private void startAnim() {
      AnimatorSet mainAnimation = new AnimatorSet();
      mainAnimation
            .play(ofFloat(image, View.ALPHA, 1).setDuration(ANIM_DURATION_MAIN_IMAGE))
            .with(ofFloat(subImage, View.ALPHA, 1).setDuration(ANIM_DURATION_SUB_IMAGE))
            .with(ofFloat(title, View.ALPHA, 1).setDuration(ANIM_DURATION_TITLE))
            .with(ofFloat(subTitle, View.ALPHA, 1).setDuration(ANIM_DURATION_SUB_TITLE))
            .before(ofFloat(done, View.ALPHA, 1).setDuration(ANIM_DURATION_DONE_BTN));
      mainAnimation.start();
   }

   @Override
   public WalletSuccessInstallFirmwarePresenter getPresenter() {
      return presenter;
   }
}
