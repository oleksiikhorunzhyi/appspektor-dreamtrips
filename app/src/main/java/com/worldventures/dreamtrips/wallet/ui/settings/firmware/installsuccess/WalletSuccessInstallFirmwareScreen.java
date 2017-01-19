package com.worldventures.dreamtrips.wallet.ui.settings.firmware.installsuccess;

import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;

import butterknife.InjectView;
import butterknife.OnClick;

import static android.animation.ObjectAnimator.ofFloat;
import static butterknife.ButterKnife.apply;
import static java.util.Arrays.asList;

public class WalletSuccessInstallFirmwareScreen
      extends WalletLinearLayout<WalletSuccessInstallFirmwarePresenter.Screen, WalletSuccessInstallFirmwarePresenter, WalletSuccessInstallFirmwarePath>
      implements WalletSuccessInstallFirmwarePresenter.Screen {

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

   public WalletSuccessInstallFirmwareScreen(Context context) {
      super(context);
   }

   public WalletSuccessInstallFirmwareScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   protected void onFinishInflate() {
      supportConnectionStatusLabel(false);
      super.onFinishInflate();
      toolbar.setNavigationIcon(new ColorDrawable(Color.TRANSPARENT));
      hideAllView();
      postDelayed(this::startAnim, ANIM_DELAY);
   }

   private void hideAllView() {
      if (isInEditMode()) return;
      apply(
            asList(image, subTitle, title, subImage, done),
            (view, index) -> view.setAlpha(0)
      );
   }

   @NonNull
   @Override
   public WalletSuccessInstallFirmwarePresenter createPresenter() {
      return new WalletSuccessInstallFirmwarePresenter(getContext(), getInjector(), getPath().firmwareUpdateData);
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return new DialogOperationScreen(this);
   }

   @Override
   public void setSubTitle(String subTitle) {
      this.subTitle.setText(getResources().getString(R.string.wallet_firmware_install_success_sub_title, subTitle));
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
   protected boolean hasToolbar() {
      return true;
   }
}