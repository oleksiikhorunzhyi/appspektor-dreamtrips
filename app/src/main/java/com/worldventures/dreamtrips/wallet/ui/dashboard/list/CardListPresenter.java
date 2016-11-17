package com.worldventures.dreamtrips.wallet.ui.dashboard.list;

import android.content.Context;
import android.net.NetworkInfo;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.util.Pair;

import com.github.pwittchen.reactivenetwork.library.ReactiveNetwork;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.modules.navdrawer.NavigationDrawerPresenter;
import com.worldventures.dreamtrips.wallet.analytics.AddPaymentCardAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.WalletHomeAction;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.CardStacksCommand;
import com.worldventures.dreamtrips.wallet.service.command.GetActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardModifier;
import com.worldventures.dreamtrips.wallet.service.command.firmware.FirmwareUpdateCacheCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.FetchFirmwareInfoCommand;
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

import io.techery.janet.Command;
import io.techery.janet.smartcard.exception.NotConnectedException;
import rx.Observable;
import timber.log.Timber;

import static com.worldventures.dreamtrips.wallet.domain.entity.SmartCard.ConnectionStatus.CONNECTED;
import static com.worldventures.dreamtrips.wallet.service.command.CardStacksCommand.CardStackModel;
import static com.worldventures.dreamtrips.wallet.util.WalletFilesUtils.getAppropriateFirmwareFile;
import static io.techery.janet.ActionState.Status.SUCCESS;

public class CardListPresenter extends WalletPresenter<CardListPresenter.Screen, Parcelable> {

   private static final int MAX_CARD_LIMIT = 10;

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject FirmwareInteractor firmwareInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject NavigationDrawerPresenter navigationDrawerPresenter;

   private final CardListStackConverter cardListStackConverter;

   private int cardLoaded = 0;

   private CardStackHeaderHolder cardStackHeaderHolder;

   public CardListPresenter(Context context, Injector injector) {
      super(context, injector);
      cardListStackConverter = new CardListStackConverter(context);
      cardStackHeaderHolder = ImmutableCardStackHeaderHolder.builder().build();
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      observeSmartCard();
      observeChanges();
      observeFirmwareInfo();

      fetchFirmwareInfo();
      fetchCards();
      trackScreen();
   }

   private void observeSmartCard() {
      smartCardInteractor.smartCardModifierPipe().observeSuccessWithReplay().map(SmartCardModifier::getResult)
            .startWith(smartCardInteractor.activeSmartCardPipe()
                  .createObservableResult(new GetActiveSmartCardCommand())
                  .map(Command::getResult)
            )
            .compose(bindViewIoToMainComposer())
            .subscribe(it -> setSmartCard(it));
   }

   private void fetchCardsOnce() {
      smartCardInteractor.cardStacksPipe().send(CardStacksCommand.get(false));
   }

   private void fetchFirmwareInfo(){
      smartCardInteractor
            .activeSmartCardPipe()
            .observeSuccessWithReplay()
            .take(1)
            .map(Command::getResult)
            .compose(bindView())
            .subscribe(smartCard -> firmwareInteractor.firmwareInfoPipe()
                  .send(new FetchFirmwareInfoCommand(smartCard.sdkVersion(), smartCard.firmWareVersion())), throwable -> Timber.e(throwable, "Error while loading smartcard"));
   }

   private void fetchCards() {
      smartCardInteractor.cardStacksPipe().send(CardStacksCommand.get(false));
   }

   private void observeFirmwareInfo() {
      firmwareInteractor.firmwareInfoPipe()
            .observeSuccessWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(command -> firmwareLoaded(command.getResult()));
   }

   private void firmwareLoaded(FirmwareUpdateData firmwareUpdateData) {
      this.cardStackHeaderHolder = ImmutableCardStackHeaderHolder.builder()
            .from(cardStackHeaderHolder)
            .firmware(firmwareUpdateData)
            .build();
      getView().notifySmartCardChanged(cardStackHeaderHolder);

      if (firmwareUpdateData.updateAvailable()) {
         getView().showFirmwareUpdateBtn();
      } else {
         getView().hideFirmwareUpdateBtn();
      }
   }

   private void trackScreen() {
      smartCardInteractor.cardsListPipe()
            .observeSuccessWithReplay()
            .take(1)
            .map(c -> c.getResult().size())
            .subscribe(
                  cards -> {
                     WalletAnalyticsCommand analyticsCommand = new WalletAnalyticsCommand(new WalletHomeAction(cards));
                     analyticsInteractor.walletAnalyticsCommandPipe().send(analyticsCommand);
                  }, throwable -> Timber.e(throwable, "")
            );
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
      navigator.go(new CardDetailsPath(bankCard));
   }

   void navigationClick() {
      navigationDrawerPresenter.openDrawer();
   }

   void onSettingsChosen() {
      navigator.go(new WalletSettingsPath());
   }

   void navigateToInstallFirmware() {
      firmwareInteractor.firmwareCachePipe().createObservable(new FirmwareUpdateCacheCommand())
            .flatMap(c -> {
               if (c.status == SUCCESS && c.action.getResult() == null) {
                  return Observable.error(new IllegalStateException("Firmware Update is not cached to retry it"));
               }
               return Observable.just(c);
            })
            .compose(bindViewIoToMainComposer())
            .subscribe(ErrorActionStateSubscriberWrapper.<FirmwareUpdateCacheCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(c -> navigator.go(new WalletInstallFirmwarePath(c.getResult())))
                  .wrap()
            );
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
               } else if (connectionStatusPair.second != CONNECTED) {
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
