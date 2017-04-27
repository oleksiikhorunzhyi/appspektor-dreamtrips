package com.worldventures.dreamtrips.wallet.ui.wizard.welcome;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;

import butterknife.InjectView;
import butterknife.OnClick;

public class WizardWelcomeScreen extends WalletLinearLayout<WizardWelcomePresenter.Screen, WizardWelcomePresenter, WizardWelcomePath> implements WizardWelcomePresenter.Screen {

   private static final long ANIMATION_DURATION = 1000;
   private static final long GREETING_ANIMATION_DELAY = 1000;
   private static final long ANIMATION_DELAY = 3000;

   @InjectView(R.id.toolbar) Toolbar toolbar;

   @InjectView(R.id.greeting_label) TextView greeting;
   @InjectView(R.id.content_label) TextView content;
   @InjectView(R.id.user_photo) SimpleDraweeView userPhoto;
   @InjectView(R.id.setup_button) Button setupButton;

   public WizardWelcomeScreen(Context context) {
      super(context);
   }

   public WizardWelcomeScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @NonNull
   @Override
   public WizardWelcomePresenter createPresenter() {
      return new WizardWelcomePresenter(getContext(), getInjector());
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      supportConnectionStatusLabel(false);
      toolbar.setNavigationOnClickListener(v -> getPresenter().backButtonClicked());
      content.setVisibility(INVISIBLE);
      ImageUtils.applyGrayScaleColorFilter(userPhoto);
   }

   @Override
   public void userName(String userName) {
      greeting.setText(getResources().getString(R.string.wallet_wizard_welcome_greeting, userName));
   }

   @Override
   public void welcomeMessage(String message) {
      content.setText(message);
   }

   @Override
   public void userPhoto(String photoUrl) {
      userPhoto.setImageURI(photoUrl);
   }

   @Override
   public void showAnimation() {
      animateView(greeting, GREETING_ANIMATION_DELAY);
      animateView(content, ANIMATION_DELAY);
      animateView(setupButton, ANIMATION_DELAY);
   }

   private void animateView(View view, long timeDelay) {
      ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f).setDuration(ANIMATION_DURATION);
      animator.setStartDelay(timeDelay);
      animator.addListener(new AnimatorListenerAdapter() {
         @Override
         public void onAnimationStart(Animator animation) {
            super.onAnimationStart(animation);
            view.setVisibility(VISIBLE);
         }
      });
      animator.start();
   }

   @OnClick(R.id.setup_button)
   public void onSetSetupButtonClicked() {
      getPresenter().setupCardClicked();
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return null;
   }
}
