package com.worldventures.dreamtrips.wallet.ui.dashboard.list;

import android.content.Context;
import android.os.Bundle;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.modules.navdrawer.NavigationDrawerPresenter;
import com.worldventures.dreamtrips.wallet.analytics.AddPaymentCardAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.WalletHomeAction;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.Firmware;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.CardListCommand;
import com.worldventures.dreamtrips.wallet.service.command.CardStacksCommand;
import com.worldventures.dreamtrips.wallet.service.command.ConnectSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.GetActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.FetchFirmwareInfoCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.dashboard.detail.CardDetailsPath;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.CardStackHeaderHolder;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.CardStackViewModel;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.ImmutableCardStackHeaderHolder;
import com.worldventures.dreamtrips.wallet.ui.settings.firmware.newavailable.WalletNewFirmwareAvailablePath;
import com.worldventures.dreamtrips.wallet.ui.settings.general.WalletSettingsPath;
import com.worldventures.dreamtrips.wallet.ui.wizard.charging.WizardChargingPath;
import com.worldventures.dreamtrips.wallet.util.CardListStackConverter;
import com.worldventures.dreamtrips.wallet.util.CardUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import timber.log.Timber;

import static com.worldventures.dreamtrips.wallet.service.command.CardStacksCommand.CardStackModel;

public class CardListPresenter extends WalletPresenter<CardListPresenter.Screen, CardListViewState> {

   private static final int MAX_CARD_LIMIT = 10;

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject FirmwareInteractor firmwareInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject NavigationDrawerPresenter navigationDrawerPresenter;

   private final CardListStackConverter cardListStackConverter;
   private Firmware firmware;

   private int cardLoaded = 0;

   private CardStackHeaderHolder cardStackHeaderHolder = ImmutableCardStackHeaderHolder.builder().build();

   public CardListPresenter(Context context, Injector injector) {
      super(context, injector);
      cardListStackConverter = new CardListStackConverter(context);
   }

   // BEGIN view state
   @Override
   public void applyViewState() {
      this.firmware = state.firmware;
      this.cardStackHeaderHolder = ImmutableCardStackHeaderHolder.builder()
            .from(cardStackHeaderHolder).firmware(firmware).build();
   }

   @Override
   public void onNewViewState() {
      state = new CardListViewState();
   }

   @Override
   public void onSaveInstanceState(Bundle bundle) {
      state.firmware = firmware;
      super.onSaveInstanceState(bundle);
   }
   // END view state

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      observeChanges();

      Observable.concat(smartCardInteractor.cardStacksPipe()
            .createObservable(CardStacksCommand.get(false)), smartCardInteractor.cardStacksPipe()
            .createObservable(CardStacksCommand.get(true))).debounce(100, TimeUnit.MILLISECONDS).subscribe();

      trackScreen();

      smartCardInteractor.smartCardModifierPipe()
            .observeSuccessWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(it -> setSmartCard(it.getResult()));

      firmwareInteractor.firmwareInfoPipe()
            .createObservableResult(new FetchFirmwareInfoCommand())
            .compose(bindViewIoToMainComposer())
            .map(it -> it.getResult())
            .subscribe(it -> {
               this.firmware = it;
               if (it.updateAvailable()) {
                  getView().showFirmwareUpdateBtn();
                  this.cardStackHeaderHolder = ImmutableCardStackHeaderHolder.builder()
                        .from(cardStackHeaderHolder)
                        .firmware(it)
                        .build();
                  getView().notifySmartCardChanged(cardStackHeaderHolder);
               } else {
                  getView().hideFirmwareUpdateBtn();
               }
            }, e -> {
            });
   }

   private void trackScreen() {
      smartCardInteractor.cardsListPipe()
            .createObservableResult(CardListCommand.get(false))
            .take(1)
            .subscribe(cardStacksCommand ->
                  analyticsInteractor.walletAnalyticsCommandPipe()
                        .send(new WalletAnalyticsCommand(new WalletHomeAction(cardStacksCommand.getResult().size()))));
   }

   private void setSmartCard(SmartCard smartCard) {
      this.cardStackHeaderHolder = ImmutableCardStackHeaderHolder.builder()
            .from(cardStackHeaderHolder)
            .smartCard(smartCard)
            .build();
      getView().notifySmartCardChanged(cardStackHeaderHolder);
   }

   public void showBankCardDetails(BankCard bankCard) {
      navigator.go(new CardDetailsPath(bankCard));
   }

   public void navigationClick() {
      navigationDrawerPresenter.openDrawer();
   }

   public void onSettingsChosen() {
      navigator.go(new WalletSettingsPath());
   }

   public void addCardRequired() {
      if (cardLoaded < MAX_CARD_LIMIT) {
         navigator.go(new WizardChargingPath());
         trackAddCard();
      } else {
         getView().showAddCardErrorDialog();
      }
   }

   private void trackAddCard() {
      analyticsInteractor.walletAnalyticsCommandPipe().send(new WalletAnalyticsCommand(new AddPaymentCardAction()));
   }

   void firmwareAvailable() {
      navigator.go(new WalletNewFirmwareAvailablePath());
   }

   private void observeChanges() {
      smartCardInteractor.cardStacksPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<CardStacksCommand>() //TODO check for progress, f.e. swipe refresh
                  .onSuccess(command -> cardsLoaded(command.getResult()))
                  .onFail((command, throwable) -> Timber.e(throwable, "")));
   }

   private void cardsLoaded(List<CardStackModel> loadedModels) {
      List<CardStackViewModel> cards = cardListStackConverter.convertToModelViews(loadedModels);
      cardLoaded = CardUtils.stacksToItemsCount(cards);
      cardStackHeaderHolder = ImmutableCardStackHeaderHolder.builder()
            .from(cardStackHeaderHolder)
            .cardCount(cardLoaded)
            .build();

      getView().notifySmartCardChanged(cardStackHeaderHolder);
      getView().showRecordsInfo(cards);
      getView().enableAddCardButton(cardLoaded != MAX_CARD_LIMIT);
   }

   private Observable<GetActiveSmartCardCommand> activeSmartCard() {
      return smartCardInteractor.activeSmartCardPipe()
            .createObservableResult(new GetActiveSmartCardCommand());
   }

   private Observable<ConnectSmartCardCommand> connectCard(SmartCard smartCard) {
      return smartCardInteractor.connectActionPipe()
            .createObservableResult(new ConnectSmartCardCommand(smartCard));
   }

   public interface Screen extends WalletScreen {
      void enableAddCardButton(boolean enabled);

      void showRecordsInfo(List<CardStackViewModel> result);

      void notifySmartCardChanged(CardStackHeaderHolder smartCard);

      void showAddCardErrorDialog();

      void hideFirmwareUpdateBtn();

      void showFirmwareUpdateBtn();
   }
}
