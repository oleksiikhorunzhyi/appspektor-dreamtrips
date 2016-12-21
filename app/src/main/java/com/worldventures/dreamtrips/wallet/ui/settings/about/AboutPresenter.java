package com.worldventures.dreamtrips.wallet.ui.settings.about;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.CardStacksCommand;
import com.worldventures.dreamtrips.wallet.service.command.GetActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.dashboard.CardListPresenter;

import javax.inject.Inject;

public class AboutPresenter extends WalletPresenter<AboutPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;

   public AboutPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      provideSmartCardFirmware();
      providePayCardsInfo();
   }

   private void provideSmartCardFirmware() {
      smartCardInteractor.activeSmartCardPipe()
            .observeSuccess()
            .map(command -> command.getResult())
            .subscribe(smartCard -> getView().onProvideSmartCard(
                  smartCard.firmwareVersion(), smartCard.smartCardId(), smartCard.user()));

      smartCardInteractor.activeSmartCardPipe().send(new GetActiveSmartCardCommand());
   }

   private void providePayCardsInfo() {
      smartCardInteractor.cardStacksPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(ErrorActionStateSubscriberWrapper.<CardStacksCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(command -> getView().onProvidePayCardInfo(
                        command.getResult().size(),
                        CardListPresenter.MAX_CARD_LIMIT - command.getResult().size()))
                  .onFail(ErrorHandler.<CardStacksCommand>builder(getContext()).build())
                  .wrap());

      smartCardInteractor.cardStacksPipe().send(CardStacksCommand.get(false));
   }

   public void goBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {
      void onProvideSmartCard(final SmartCardFirmware smartCardFirmware, final String smartCardId, final SmartCardUser user);
      void onProvidePayCardInfo(final int cardStored, final int cardAvailable);
   }
}
