package com.worldventures.wallet.ui.wizard.welcome.impl;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.wallet.R;
import com.worldventures.wallet.service.provisioning.ProvisioningMode;
import com.worldventures.wallet.ui.common.base.WalletBaseController;
import com.worldventures.wallet.ui.wizard.welcome.WizardWelcomePresenter;
import com.worldventures.wallet.ui.wizard.welcome.WizardWelcomeScreen;
import com.worldventures.wallet.util.SmartCardAvatarHelper;

import javax.inject.Inject;

import io.techery.janet.smartcard.model.User;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class WizardWelcomeScreenImpl extends WalletBaseController<WizardWelcomeScreen, WizardWelcomePresenter> implements WizardWelcomeScreen {

   private static final String KEY_PROVISION_MODE = "key_provision_mode";
   private static final long ANIMATION_DURATION = 1000;
   private static final long GREETING_ANIMATION_DELAY = 1000;
   private static final long ANIMATION_DELAY = 3000;

   private TextView greeting;
   private TextView content;
   private SimpleDraweeView userPhoto;
   private Button setupButton;

   @Inject WizardWelcomePresenter presenter;

   public static WizardWelcomeScreenImpl create(ProvisioningMode provisioningMode) {
      final Bundle args = new Bundle();
      args.putSerializable(KEY_PROVISION_MODE, provisioningMode);
      return new WizardWelcomeScreenImpl(args);
   }

   public WizardWelcomeScreenImpl() {
      super();
   }

   public WizardWelcomeScreenImpl(Bundle args) {
      super(args);
   }

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      final Toolbar toolbar = view.findViewById(R.id.toolbar);
      toolbar.setNavigationOnClickListener(v -> getPresenter().backButtonClicked());
      userPhoto = view.findViewById(R.id.user_photo);
      SmartCardAvatarHelper.applyGrayScaleColorFilter(userPhoto);
      greeting = view.findViewById(R.id.greeting_label);
      content = view.findViewById(R.id.content_label);
      content.setVisibility(INVISIBLE);
      setupButton = view.findViewById(R.id.setup_button);
      setupButton.setOnClickListener(setupBtn -> getPresenter().setupCardClicked());

   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_wizard_welcome, viewGroup, false);
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
   public void userName(String userName) {
      greeting.setText(getResources().getString(R.string.wallet_wizard_welcome_greeting, userName));
   }

   @Override
   public void welcomeMessage(User.MemberStatus memberStatus) {
      final String welcomeText;
      if (memberStatus == User.MemberStatus.GOLD) {
         welcomeText = getString(R.string.wallet_wizard_welcome_gold_user);
      } else if (memberStatus == User.MemberStatus.ACTIVE) {
         welcomeText = getString(R.string.wallet_wizard_welcome_platinum_user);
      } else {
         welcomeText = getString(R.string.wallet_wizard_welcome_simple_user);
      }
      content.setText(welcomeText);
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

   @Override
   public ProvisioningMode getProvisionMode() {
      return (getArgs() != null && !getArgs().isEmpty() && getArgs().containsKey(KEY_PROVISION_MODE))
            ? (ProvisioningMode) getArgs().getSerializable(KEY_PROVISION_MODE)
            : null;
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

   @Override
   public WizardWelcomePresenter getPresenter() {
      return presenter;
   }
}
