package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.installsuccess.impl;


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

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.installsuccess.WalletSuccessInstallFirmwarePresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.installsuccess.WalletSuccessInstallFirmwareScreen;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

import static android.animation.ObjectAnimator.ofFloat;
import static butterknife.ButterKnife.apply;
import static java.util.Arrays.asList;

public class WalletSuccessInstallFirmwareScreenImpl extends WalletBaseController<WalletSuccessInstallFirmwareScreen, WalletSuccessInstallFirmwarePresenter> implements WalletSuccessInstallFirmwareScreen {

   private static final String KEY_FIRMWARE_UPDATE_DATA = "key_firmware_update_data";
   private static final int ANIM_DURATION_MAIN_IMAGE = 300;
   private static final int ANIM_DURATION_SUB_IMAGE = 300;
   private static final int ANIM_DURATION_TITLE = 400;
   private static final int ANIM_DURATION_SUB_TITLE = 400;
   private static final int ANIM_DURATION_DONE_BTN = 500;
   private static final int ANIM_DELAY = 700;

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.success_firmware_install_image) ImageView image;
   @InjectView(R.id.success_firmware_install_sub_image) ImageView subImage;
   @InjectView(R.id.success_firmware_install_title) TextView title;
   @InjectView(R.id.success_firmware_install_sub_title) TextView subTitle;
   @InjectView(R.id.success_firmware_install_done) Button done;

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
      toolbar.setNavigationIcon(new ColorDrawable(Color.TRANSPARENT));
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
      apply(
            asList(image, subTitle, title, subImage, done),
            (view, index) -> view.setAlpha(0)
      );
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

   @OnClick(R.id.success_firmware_install_done)
   void onDoneClick() {
      getPresenter().finishUpdateFlow();
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
