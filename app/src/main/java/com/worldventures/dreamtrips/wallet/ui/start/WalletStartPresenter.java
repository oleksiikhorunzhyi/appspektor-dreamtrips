package com.worldventures.dreamtrips.wallet.ui.start;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.core.session.acl.FeatureManager;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.GetActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.CardListPath;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.WalletProvisioningBlockedPath;
import com.worldventures.dreamtrips.wallet.ui.wizard.power_on.WizardPowerOnPath;

import javax.inject.Inject;

import flow.Flow;
import io.techery.janet.helper.ActionStateSubscriber;

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
            () -> smartCardInteractor.activeSmartCardPipe()
                  .createObservable(new GetActiveSmartCardCommand())
                  .compose(bindViewIoToMainComposer())
                  .subscribe(new ActionStateSubscriber<GetActiveSmartCardCommand>()
                        .onSuccess(command -> navigator.single(new CardListPath(), Flow.Direction.REPLACE))
                        .onFail((command, throwable) -> navigator.single(new WizardPowerOnPath(), Flow.Direction.REPLACE))
                  ),
            () -> navigator.single(new WalletProvisioningBlockedPath(), Flow.Direction.REPLACE)
      );
   }

   public interface Screen extends WalletScreen {

   }
}
