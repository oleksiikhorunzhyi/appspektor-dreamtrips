package com.worldventures.dreamtrips.wallet.ui.wizard.setup_smartcard;


import android.content.Context;
import android.os.Parcelable;
import android.text.TextUtils;

import com.techery.spares.module.Injector;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.wallet.service.SmartCardAvatarInteractor;
import com.worldventures.dreamtrips.wallet.service.command.LoadImageForSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardAvatarCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.wizard.profile.WizardEditProfilePath;

import java.io.File;

import javax.inject.Inject;

import flow.Flow;
import io.techery.janet.helper.ActionStateSubscriber;
import timber.log.Timber;

public class WizardSetupSmartCardPresenter extends WalletPresenter<WizardSetupSmartCardPresenter.Screen, Parcelable> {

   @Inject SessionHolder<UserSession> appSessionHolder;
   @Inject SmartCardAvatarInteractor smartCardAvatarInteractor;

   private final String smartCardId;

   public WizardSetupSmartCardPresenter(Context context, Injector injector, String smartCardId) {
      super(context, injector);
      this.smartCardId = smartCardId;
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);

      User userProfile = appSessionHolder.get().get().getUser();
      view.setUserName(userProfile.getFullName());
      loadUserPhoto(userProfile.getAvatar().getThumb());
      view.showAnimation();
   }

   private void loadUserPhoto(String avatarPath) {
      if (!TextUtils.isEmpty(avatarPath)) {
         smartCardAvatarInteractor.smartCardAvatarPipe()
               .observe()
               .compose(bindViewIoToMainComposer())
               .subscribe(new ActionStateSubscriber<SmartCardAvatarCommand>()
                     .onSuccess(command -> getView().setUserPhoto(command.getResult()))
                     .onFail((command, throwable) -> Timber.e("", throwable)));

         smartCardAvatarInteractor.smartCardAvatarPipe().send(new LoadImageForSmartCardCommand(avatarPath));
      }
   }

   public void setupCardClicked() {
      Flow.get(getContext()).set(new WizardEditProfilePath(smartCardId));
   }

   public void backButtonClicked() {
      Flow.get(getContext()).goBack();
   }

   public interface Screen extends WalletScreen {

      void setUserName(String userName);

      void setUserPhoto(File file);

      void showAnimation();

   }

}
