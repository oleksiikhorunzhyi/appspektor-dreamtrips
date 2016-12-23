package com.worldventures.dreamtrips.wallet.ui.start;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.core.session.acl.FeatureManager;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.http.FetchAssociatedSmartCardCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.dashboard.CardListPath;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.WalletProvisioningBlockedPath;
import com.worldventures.dreamtrips.wallet.ui.wizard.welcome.WizardWelcomePath;

import javax.inject.Inject;

import flow.Flow;

public class WalletStartPresenter extends WalletPresenter<WalletStartPresenter.Screen, Parcelable> {

   @Inject SmartCardInteractor smartCardInteractor;
   @Inject Navigator navigator;
   @Inject FeatureManager featureManager;

   public WalletStartPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);

      featureManager.with(Feature.WALLET_PROVISIONING,
            this::onWalletAvailable,
            () -> navigator.single(new WalletProvisioningBlockedPath(), Flow.Direction.REPLACE)
      );
   }

   private void onWalletAvailable() {
      smartCardInteractor.fetchAssociatedSmartCard()
            .createObservable(new FetchAssociatedSmartCardCommand())
            .compose(bindViewIoToMainComposer())
            .subscribe(
                  OperationActionStateSubscriberWrapper.<FetchAssociatedSmartCardCommand>forView(getView().provideOperationDelegate())
                        .onSuccess(command -> handleResult(command.getResult()))
                        .onFail(ErrorHandler.create(getContext(), command -> navigator.goBack()))
                        .wrap()
            );
   }

   private void handleResult(FetchAssociatedSmartCardCommand.AssociatedCard associatedCard) {
      if (associatedCard.exist()) {
         navigator.single(new CardListPath(), Flow.Direction.REPLACE);
      } else {
         navigator.single(new WizardWelcomePath(), Flow.Direction.REPLACE);
      }
   }

   public interface Screen extends WalletScreen {

   }
}
