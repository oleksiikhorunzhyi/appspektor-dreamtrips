package com.worldventures.dreamtrips.wallet.ui.settings.about;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.settings.AboutAnalyticsAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.domain.entity.card.Card;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.CardListCommand;
import com.worldventures.dreamtrips.wallet.service.command.device.SmartCardFirmwareCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.dashboard.CardListPresenter;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;

public class AboutPresenter extends WalletPresenter<AboutPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;

   public AboutPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      observeSmartCard();
      observePayCardsInfo();

      smartCardInteractor.activeSmartCardPipe().send(new ActiveSmartCardCommand());
      smartCardInteractor.smartCardFirmwarePipe().send(SmartCardFirmwareCommand.fetch());
      smartCardInteractor.cardsListPipe().send(CardListCommand.fetch());

      trackScreen();
   }

   private void observeSmartCard() {
      smartCardInteractor.activeSmartCardPipe()
            .observeSuccessWithReplay()
            .map(Command::getResult)
            .compose(bindViewIoToMainComposer())
            .subscribe(smartCard -> getView().setSmartCardId(smartCard.smartCardId()));
      smartCardInteractor.smartCardFirmwarePipe()
            .observeSuccessWithReplay()
            .map(Command::getResult)
            .compose(bindViewIoToMainComposer())
            .subscribe(getView()::setSmartCardFirmware);
      smartCardInteractor.smartCardUserPipe()
            .observeSuccessWithReplay()
            .map(Command::getResult)
            .compose(bindViewIoToMainComposer())
            .subscribe(getView()::setSmartCardUser);
   }

   private void observePayCardsInfo() {
      smartCardInteractor.cardsListPipe()
            .observeSuccessWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(command -> bindCardList(command.getResult()));
   }

   private void trackScreen() {
      final WalletAnalyticsCommand analyticsCommand = new WalletAnalyticsCommand(new AboutAnalyticsAction());
      analyticsInteractor.walletAnalyticsCommandPipe().send(analyticsCommand);
   }

   private void bindCardList(List<Card> cardList) {
      // TODO: 1/26/17 CardListPresenter.MAX_CARD_LIMIT should be a common constant
      //noinspection ConstantConditions
      getView().onProvidePayCardInfo(cardList.size(), CardListPresenter.MAX_CARD_LIMIT - cardList.size());
   }

   public void goBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {

      void onProvidePayCardInfo(final int cardStored, final int cardAvailable);

      void setSmartCardId(String smartCardId);

      void setSmartCardFirmware(SmartCardFirmware smartCardFirmware);

      void setSmartCardUser(SmartCardUser smartCardUser);
   }
}
