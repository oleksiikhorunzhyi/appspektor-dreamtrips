package com.worldventures.dreamtrips.wallet.ui.settings.general.reset;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.service.FactoryResetInteractor;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;

import javax.inject.Inject;

public class FactoryResetPresenter extends WalletPresenter<FactoryResetPresenter.Screen, Parcelable> {

   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject FactoryResetInteractor factoryResetInteractor;
   @Inject Navigator navigator;
   private final FactoryResetDelegate factoryResetDelegate;

   public FactoryResetPresenter(Context context, Injector injector) {
      super(context, injector);
      factoryResetDelegate = FactoryResetDelegate.create(factoryResetInteractor, analyticsInteractor,
            navigator, FactoryResetAction.GENERAL);
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

   public void goBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen, FactoryResetView {
   }
}
