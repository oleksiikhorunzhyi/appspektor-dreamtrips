package com.worldventures.dreamtrips.wallet.ui.settings.about;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.CardListCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.dashboard.CardListPresenter;

import javax.inject.Inject;

import timber.log.Timber;

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
            .createObservableResult(new ActiveSmartCardCommand())
            .compose(bindViewIoToMainComposer())
            .map(command -> command.getResult())
            .subscribe(smartCard -> getView().onProvideSmartCard(
                  smartCard.firmwareVersion(), smartCard.smartCardId(), smartCard.user()),
                  throwable -> Timber.e(throwable, ""));

      smartCardInteractor.activeSmartCardPipe().send(new ActiveSmartCardCommand());
   }

   private void providePayCardsInfo() {
      smartCardInteractor.cardsListPipe()
            .createObservableResult(new CardListCommand())
            .compose(bindViewIoToMainComposer())
            .subscribe(cardListCommand -> getView().onProvidePayCardInfo(
                  cardListCommand.getResult().size(),
                  CardListPresenter.MAX_CARD_LIMIT - cardListCommand.getResult().size()));

      smartCardInteractor.cardsListPipe().send(CardListCommand.fetch());
   }

   public void goBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {
      void onProvideSmartCard(final SmartCardFirmware smartCardFirmware, final String smartCardId, final SmartCardUser user);
      void onProvidePayCardInfo(final int cardStored, final int cardAvailable);
   }
}
