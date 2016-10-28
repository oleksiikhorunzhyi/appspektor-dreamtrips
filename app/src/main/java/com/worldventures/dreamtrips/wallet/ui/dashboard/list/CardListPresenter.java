package com.worldventures.dreamtrips.wallet.ui.dashboard.list;

import android.content.Context;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.util.Pair;

import com.github.pwittchen.reactivenetwork.library.ReactiveNetwork;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.modules.navdrawer.NavigationDrawerPresenter;
import com.worldventures.dreamtrips.wallet.analytics.AddPaymentCardAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.WalletHomeAction;
import com.worldventures.dreamtrips.wallet.domain.entity.Firmware;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.Card;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.CardListCommand;
import com.worldventures.dreamtrips.wallet.service.command.CardStacksCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.dashboard.detail.CardDetailsPath;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.CardStackHeaderHolder;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.CardStackViewModel;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.ImmutableCardStackHeaderHolder;
import com.worldventures.dreamtrips.wallet.ui.settings.firmware.install.WalletInstallFirmwarePath;
import com.worldventures.dreamtrips.wallet.ui.settings.firmware.newavailable.WalletNewFirmwareAvailablePath;
import com.worldventures.dreamtrips.wallet.ui.settings.general.WalletSettingsPath;
import com.worldventures.dreamtrips.wallet.ui.wizard.charging.WizardChargingPath;
import com.worldventures.dreamtrips.wallet.util.CardListStackConverter;
import com.worldventures.dreamtrips.wallet.util.CardUtils;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.smartcard.exception.NotConnectedException;
import rx.Observable;
import timber.log.Timber;

import static com.worldventures.dreamtrips.wallet.service.command.CardStacksCommand.CardStackModel;
import static com.worldventures.dreamtrips.wallet.util.WalletFilesUtils.getAppropriateFirmwareFile;

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
      observeFirmwareInfo();

      //TODO For first release we should get info from cache cause SC device does't support operation
      smartCardInteractor.cardStacksPipe().send(CardStacksCommand.get(false));
      trackScreen();

      smartCardInteractor.smartCardModifierPipe()
            .observeSuccessWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(it -> setSmartCard(it.getResult()));
   }

   private void observeFirmwareInfo() {
      firmwareInteractor.firmwareInfoPipe()
            .observeSuccessWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(command -> firmwareLoaded(command.getResult()), throwable -> Timber.e(throwable, "Error while fetching firmware"));
   }

   private void firmwareLoaded(Firmware firmware) {
      this.firmware = firmware;
      this.cardStackHeaderHolder = ImmutableCardStackHeaderHolder.builder()
            .from(cardStackHeaderHolder)
            .firmware(firmware)
            .build();
      getView().notifySmartCardChanged(cardStackHeaderHolder);

      if (firmware.updateAvailable()) {
         getView().showFirmwareUpdateBtn();
      } else {
         getView().hideFirmwareUpdateBtn();
      }
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
      if (smartCard.connectionStatus() == SmartCard.ConnectionStatus.DFU) {
         File firmwareFile = getAppropriateFirmwareFile(getContext());
         if (firmwareFile.exists()) {
            getView().showFirmwareUpdateError();
         }
      }
   }

   void cardClicked(BankCard bankCard) {
      if (bankCard.category() != Card.Category.SAMPLE) {
         navigator.go(new CardDetailsPath(bankCard));
      }
   }

   void navigationClick() {
      navigationDrawerPresenter.openDrawer();
   }

   void onSettingsChosen() {
      navigator.go(new WalletSettingsPath());
   }

   void navigateToInstallFirmware() {
      File firmwareFile = getAppropriateFirmwareFile(getContext());
      navigator.go(new WalletInstallFirmwarePath(firmwareFile.getAbsolutePath(), firmware.firmwareInfo()));
   }

   void navigateBack() {
      navigator.goBack();
   }

   void addCardRequired() {
      if (cardLoaded >= MAX_CARD_LIMIT) {
         getView().showAddCardErrorDialog(Screen.ERROR_DIALOG_FULL_SMARTCARD);
         return;
      }

      Observable.combineLatest(
            ReactiveNetwork.observeNetworkConnectivity(getContext()).take(1),
            smartCardInteractor.smartCardModifierPipe().observeSuccessWithReplay().take(1),
            (connectivity, smartCardModifier) -> new Pair<>(connectivity.getState(), smartCardModifier.getResult()
                  .connectionStatus()))
            .compose(bindViewIoToMainComposer())
            .subscribe(connectionStatusPair -> {
               if (connectionStatusPair.first != NetworkInfo.State.CONNECTED) {
                  getView().showAddCardErrorDialog(Screen.ERROR_DIALOG_NO_INTERNET_CONNECTION);
               } else if (connectionStatusPair.second != SmartCard.ConnectionStatus.CONNECTED) {
                  getView().showAddCardErrorDialog(Screen.ERROR_DIALOG_NO_SMARTCARD_CONNECTION);
               } else {
                  trackAddCard();
                  navigator.go(new WizardChargingPath());
               }
            }, e -> Timber.e(e, "Could not subscribe to network and smartcard events"));
   }

   private void trackAddCard() {
      analyticsInteractor.walletAnalyticsCommandPipe().send(new WalletAnalyticsCommand(new AddPaymentCardAction()));
   }

   void firmwareAvailable() {
      navigator.go(new WalletNewFirmwareAvailablePath());
   }

   private void observeChanges() {
      ErrorHandler errorHandler = ErrorHandler.builder(getContext())
            .ignore(NotConnectedException.class)
            .build();

      smartCardInteractor.cardStacksPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(ErrorActionStateSubscriberWrapper.<CardStacksCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(command -> cardsLoaded(command.getResult()))
                  .onFail(errorHandler)
                  .wrap());
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
   }

   public interface Screen extends WalletScreen {

      int ERROR_DIALOG_FULL_SMARTCARD = 1;
      int ERROR_DIALOG_NO_INTERNET_CONNECTION = 2;
      int ERROR_DIALOG_NO_SMARTCARD_CONNECTION = 3;

      void showRecordsInfo(List<CardStackViewModel> result);

      void notifySmartCardChanged(CardStackHeaderHolder smartCard);

      void showAddCardErrorDialog(@ErrorDialogType int errorDialogType);

      void hideFirmwareUpdateBtn();

      void showFirmwareUpdateBtn();

      void showFirmwareUpdateError();

      @IntDef({ERROR_DIALOG_FULL_SMARTCARD, ERROR_DIALOG_NO_INTERNET_CONNECTION, ERROR_DIALOG_NO_SMARTCARD_CONNECTION})
      @interface ErrorDialogType {}
   }
}
