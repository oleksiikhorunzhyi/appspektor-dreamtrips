package com.worldventures.dreamtrips.wallet.ui.wizard.setup_smartcard;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletFrameLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;

import java.io.File;

import butterknife.InjectView;
import butterknife.OnClick;

public class WizardSetupSmartCardScreen extends WalletFrameLayout<WizardSetupSmartCardPresenter.Screen, WizardSetupSmartCardPresenter, WizardSetupSmartCardPath> implements WizardSetupSmartCardPresenter.Screen {

   private static final long ANIMATION_DURATION = 1000;
   private static final long GREETING_ANIMATION_DELAY = 1000;
   private static final long ANIMATION_DELAY = 3000;

   @InjectView(R.id.toolbar) Toolbar toolbar;

   @InjectView(R.id.greeting_label) TextView greeting;
   @InjectView(R.id.content_label) View content;
   @InjectView(R.id.user_photo) SimpleDraweeView userPhoto;
   @InjectView(R.id.setup_button) Button setupButton;

   public WizardSetupSmartCardScreen(Context context) {
      super(context);
   }

   public WizardSetupSmartCardScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   public WizardSetupSmartCardPresenter createPresenter() {
      return new WizardSetupSmartCardPresenter(getContext(), getInjector(), getPath().smartCardId);
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      toolbar.setNavigationOnClickListener(v -> getPresenter().backButtonClicked());
   }


   @Override
   public void setUserName(String userName) {
      greeting.setText(getResources().getString(R.string.wallet_setup_smart_card_greeting, userName));
   }

   @Override
   public void setUserPhoto(File photo) {
      userPhoto.setImageURI(Uri.fromFile(photo));
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
