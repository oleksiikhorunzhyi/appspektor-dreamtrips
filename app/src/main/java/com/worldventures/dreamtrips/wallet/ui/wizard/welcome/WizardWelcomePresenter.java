package com.worldventures.dreamtrips.wallet.ui.wizard.welcome;

import android.content.Context;
import android.os.Parcelable;
import android.text.TextUtils;

import com.techery.spares.module.Injector;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.wallet.analytics.NewHeightsAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.service.SmartCardUserDataInteractor;
import com.worldventures.dreamtrips.wallet.service.command.LoadImageForSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardAvatarCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.profile.WizardEditProfilePath;

import java.io.File;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import timber.log.Timber;

public class WizardWelcomePresenter extends WalletPresenter<WizardWelcomePresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject SessionHolder<UserSession> appSessionHolder;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject SmartCardUserDataInteractor smartCardUserDataInteractor;

   private final String smartCardId;

   public WizardWelcomePresenter(Context context, Injector injector, String smartCardId) {
      super(context, injector);
      this.smartCardId = smartCardId;
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      analyticsInteractor.walletAnalyticsCommandPipe().send(new WalletAnalyticsCommand(new NewHeightsAction()));
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);

      User user = appSessionHolder.get().get().getUser();
      view.userName(user.getFullName());
      loadUserPhoto(user.getAvatar().getThumb());
      view.welcomeMessage(obtainMessage(user));
      view.showAnimation();
   }

   private void loadUserPhoto(String avatarPath) {
      if (!TextUtils.isEmpty(avatarPath)) {
         smartCardUserDataInteractor.smartCardAvatarPipe()
               .observe()
               .compose(bindViewIoToMainComposer())
               .subscribe(new ActionStateSubscriber<SmartCardAvatarCommand>()
                     .onSuccess(command -> getView().userPhoto(command.getResult().monochrome()))
                     .onFail((command, throwable) -> Timber.e("", throwable)));

         smartCardUserDataInteractor.smartCardAvatarPipe().send(new LoadImageForSmartCardCommand(avatarPath));
      }
   }

   private String obtainMessage(User user) {
      if (user.isGold()) return getContext().getString(R.string.wallet_wizard_welcome_gold_user);
      if (user.isPlatinum()) return getContext().getString(R.string.wallet_wizard_welcome_platinum_user);
      return getContext().getString(R.string.wallet_wizard_welcome_simple_user);
   }

   public void setupCardClicked() {
      navigator.withoutLast(new WizardEditProfilePath(smartCardId));
   }

   public void backButtonClicked() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {

      void userName(String userName);

      void welcomeMessage(String message);

      void userPhoto(File file);

      void showAnimation();

   }

}
