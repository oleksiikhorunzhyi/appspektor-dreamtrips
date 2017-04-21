package com.worldventures.dreamtrips.wallet.ui.settings.about;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.domain.entity.card.Card;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.CardListCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.dashboard.CardListPresenter;

import java.util.List;

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
      observeSmartCardFirmware();
      observePayCardsInfo();

      smartCardInteractor.activeSmartCardPipe().send(new ActiveSmartCardCommand());
      smartCardInteractor.cardsListPipe().send(CardListCommand.fetch());
   }

   private void observeSmartCardFirmware() {
      smartCardInteractor.activeSmartCardPipe()
            .observeSuccessWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(command -> bindSmartCard(command.getResult()),
                  throwable -> Timber.e(throwable, ""));
   }

   private void observePayCardsInfo() {
      smartCardInteractor.cardsListPipe()
            .observeSuccessWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(command -> bindCardList(command.getResult()));
   }

   private void bindSmartCard(SmartCard smartCard) {
      //noinspection ConstantConditions
      getView().onProvideSmartCard(smartCard.firmwareVersion(), smartCard.smartCardId(), smartCard.user());
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

      void onProvideSmartCard(final SmartCardFirmware smartCardFirmware, final String smartCardId, final SmartCardUser user);

      void onProvidePayCardInfo(final int cardStored, final int cardAvailable);
   }
}
