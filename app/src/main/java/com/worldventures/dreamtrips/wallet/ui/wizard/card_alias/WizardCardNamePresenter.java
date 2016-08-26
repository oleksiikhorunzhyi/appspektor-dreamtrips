package com.worldventures.dreamtrips.wallet.ui.wizard.card_alias;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.SetupSmartCardNameCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationSubscriberWrapper.MessageActionHolder;
import com.worldventures.dreamtrips.wallet.ui.wizard.profile.WizardEditProfilePath;
import com.worldventures.dreamtrips.wallet.ui.wizard.splash.WizardSplashPath;
import com.worldventures.dreamtrips.wallet.util.FormatException;

import javax.inject.Inject;

import flow.Flow;
import flow.History;

public class WizardCardNamePresenter extends WalletPresenter<WizardCardNamePresenter.Screen, Parcelable> {
   @Inject WizardInteractor wizardInteractor;

   private final String smartCardId;

   public WizardCardNamePresenter(Context context, Injector injector, String smartCardId) {
      super(context, injector);
      this.smartCardId = smartCardId;
   }

   public void goToBack() {
      Flow.get(getContext()).setHistory(History.single(new WizardSplashPath()), Flow.Direction.BACKWARD);
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      observeSmartCardNamePipe();
   }

   private void observeSmartCardNamePipe() {
      wizardInteractor.setupSmartCardNamePipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .compose(new ActionPipeCacheWiper<>(wizardInteractor.setupSmartCardNamePipe()))
            .subscribe(OperationSubscriberWrapper.<SetupSmartCardNameCommand>forView(getView().provideOperationDelegate())
                  .onStart(getContext().getString(R.string.wallet_wizard_card_alias_setup))
                  .onSuccess(getContext().getString(R.string.wallet_wizard_card_alias_was_setup), command -> Flow.get(getContext())
                        .set(new WizardEditProfilePath(command.getCardId())))
                  .onFail(throwable -> {
                     Context context = getContext();
                     String msg = throwable.getCause() instanceof FormatException ? context.getString(R.string.wallet_wizard_card_alias_format_error) : context
                           .getString(R.string.error_something_went_wrong);

                     return new MessageActionHolder<>(msg, null);
                  })
                  .wrap());
   }

   public void setupCardName() {
      wizardInteractor.setupSmartCardNamePipe()
            .send(new SetupSmartCardNameCommand(getView().getCardName().trim(), smartCardId));
   }

   public interface Screen extends WalletScreen {
      @NonNull
      String getCardName();
   }
}
