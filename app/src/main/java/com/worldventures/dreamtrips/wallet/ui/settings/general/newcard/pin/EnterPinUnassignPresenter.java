package com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.pin;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.service.FactoryResetInteractor;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.FactoryResetAction;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.FactoryResetDelegate;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.FactoryResetView;

import javax.inject.Inject;

public class EnterPinUnassignPresenter extends WalletPresenter<EnterPinUnassignPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject FactoryResetInteractor factoryResetInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
   private final FactoryResetDelegate factoryResetDelegate;

   public EnterPinUnassignPresenter(Context context, Injector injector) {
      super(context, injector);
      factoryResetDelegate = FactoryResetDelegate.create(factoryResetInteractor, analyticsInteractor,
            navigator, FactoryResetAction.NEW_CARD);
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      factoryResetDelegate.bindView(getView());
   }

   @Override
   public void detachView(boolean retainInstance) {
      factoryResetDelegate.cancelFactoryReset();
      super.detachView(retainInstance);
   }

   void goBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen, FactoryResetView {
   }
}
