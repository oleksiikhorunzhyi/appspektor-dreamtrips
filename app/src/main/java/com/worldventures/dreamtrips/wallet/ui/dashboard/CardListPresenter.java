package com.worldventures.dreamtrips.wallet.ui.dashboard;

import android.content.Context;
import android.net.NetworkInfo;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.v4.util.Pair;

import com.github.pwittchen.reactivenetwork.library.ReactiveNetwork;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.modules.navdrawer.NavigationDrawerPresenter;
import com.worldventures.dreamtrips.wallet.analytics.AddPaymentCardAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.WalletHomeAction;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.Card;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.CardListCommand;
import com.worldventures.dreamtrips.wallet.service.command.DefaultCardIdCommand;
import com.worldventures.dreamtrips.wallet.service.command.SyncCardsCommand;
import com.worldventures.dreamtrips.wallet.service.firmware.SCFirmwareFacade;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.CardStackHeaderHolder;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.CardStackViewModel;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.ImmutableCardStackHeaderHolder;
import com.worldventures.dreamtrips.wallet.ui.records.detail.CardDetailsPath;
import com.worldventures.dreamtrips.wallet.ui.records.swiping.WizardChargingPath;
import com.worldventures.dreamtrips.wallet.ui.settings.firmware.force.factoryreset.ForceFactoryResetPath;
import com.worldventures.dreamtrips.wallet.ui.settings.firmware.install.WalletInstallFirmwarePath;
import com.worldventures.dreamtrips.wallet.ui.settings.firmware.newavailable.WalletNewFirmwareAvailablePath;
import com.worldventures.dreamtrips.wallet.ui.settings.firmware.force.factoryreset.ForceFactoryResetPath;
import com.worldventures.dreamtrips.wallet.ui.settings.general.WalletSettingsPath;
import com.worldventures.dreamtrips.wallet.util.CardListStackConverter;
import com.worldventures.dreamtrips.wallet.util.CardUtils;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import flow.Flow;
import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateToActionTransformer;
import io.techery.janet.smartcard.exception.NotConnectedException;
import rx.Observable;
import timber.log.Timber;

import static com.worldventures.dreamtrips.wallet.domain.entity.SmartCard.ConnectionStatus.CONNECTED;
import static com.worldventures.dreamtrips.wallet.util.WalletFilesUtils.getAppropriateFirmwareFile;

public class CardListPresenter extends WalletPresenter<CardListPresenter.Screen, Parcelable> {

   public static final int MAX_CARD_LIMIT = 10;

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject FirmwareInteractor firmwareInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject SCFirmwareFacade firmwareFacade;

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

      firmwareFacade.fetchFirmwareInfo();

      smartCardInteractor.cardsListPipe().send(CardListCommand.fetch());
      smartCardInteractor.defaultCardIdPipe().send(new DefaultCardIdCommand());
      trackScreen();
   }

   private void observeSmartCard() {
      smartCardInteractor.activeSmartCardPipe().observeSuccessWithReplay()
            .map(Command::getResult)
            .compose(bindViewIoToMainComposer())
            .subscribe(this::setSmartCard, throwable -> {
            });

      smartCardInteractor.activeSmartCardPipe().observeSuccessWithReplay()
            .map(Command::getResult)
            .map(SmartCard::connectionStatus)
            .distinctUntilChanged()
            .compose(bindViewIoToMainComposer())
            .subscribe(connectionStatus -> {
               if (connectionStatus == SmartCard.ConnectionStatus.DFU) {
                  File firmwareFile = getAppropriateFirmwareFile(getContext());
                  if (firmwareFile.exists()) {
                     getView().showFirmwareUpdateError();
                  }
               }
            });
   }

   private void observeFirmwareInfo() {
      firmwareFacade.takeFirmwareInfo()
            .compose(bindViewIoToMainComposer())
            .subscribe(this::firmwareLoaded);
   }

   private void firmwareLoaded(FirmwareUpdateData firmwareUpdateData) {
      this.cardStackHeaderHolder = ImmutableCardStackHeaderHolder.builder()
            .from(cardStackHeaderHolder)
            .firmware(firmwareUpdateData)
            .build();
      getView().notifySmartCardChanged(cardStackHeaderHolder);

      if (firmwareUpdateData.updateAvailable()) {
         if (firmwareUpdateData.updateCritical()) getView().showForceFirmwareUpdateDialog();
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
      navigator.go(new WalletInstallFirmwarePath());
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
            smartCardInteractor.activeSmartCardPipe().observeSuccessWithReplay().take(1),
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
      navigator.go(new WalletNewFirmwareAvailablePath(cardStackHeaderHolder.smartCard(), cardStackHeaderHolder.firmware()));
   }

   private void observeChanges() {
      ErrorHandler errorHandler = ErrorHandler.builder(getContext())
            .ignore(NotConnectedException.class)
            .build();

      Observable.combineLatest(
            smartCardInteractor.cardsListPipe()
                  .observeWithReplay()
                  .compose(new ActionPipeCacheWiper<>(smartCardInteractor.cardsListPipe()))
                  .compose(new ActionStateToActionTransformer<>()),
            smartCardInteractor.defaultCardIdPipe()
                  .observeWithReplay()
                  .compose(new ActionPipeCacheWiper<>(smartCardInteractor.defaultCardIdPipe()))
                  .compose(new ActionStateToActionTransformer<>()),
            (cardListCommand, defaultCardIdCommand) -> Pair.create(cardListCommand.getResult(), defaultCardIdCommand.getResult()))
            .compose(bindViewIoToMainComposer())
            .subscribe(pair -> cardsLoaded(pair.first, pair.second), throwable -> {
            } /*ignore here*/);

      smartCardInteractor.cardsListPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(ErrorActionStateSubscriberWrapper.<CardListCommand>forView(getView().provideOperationDelegate())
                  .onFail(errorHandler)
                  .wrap());

      smartCardInteractor.defaultCardIdPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(ErrorActionStateSubscriberWrapper.<DefaultCardIdCommand>forView(getView().provideOperationDelegate())
                  .onFail(errorHandler)
                  .wrap());

      smartCardInteractor.cardSyncPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(ErrorActionStateSubscriberWrapper.<SyncCardsCommand>forView(getView().provideOperationDelegate())
                  .onStart(syncCardsCommand -> getView().showCardSynchronizationDialog(true))
                  .onSuccess(syncCardsCommand -> getView().showCardSynchronizationDialog(false))
                  .onFail(throwable -> {
                     getView().showCardSynchronizationDialog(false);
                     return errorHandler.call(throwable);
                  })
                  .wrap());

   }

   private void cardsLoaded(List<Card> loadedCards, String defaultCardId) {
      List<CardStackViewModel> cards = cardListStackConverter.convertToModelViews(loadedCards, defaultCardId);
      cardLoaded = CardUtils.stacksToItemsCount(cards);
      cardStackHeaderHolder = ImmutableCardStackHeaderHolder.builder()
            .from(cardStackHeaderHolder)
            .cardCount(cardLoaded)
            .build();

      getView().notifySmartCardChanged(cardStackHeaderHolder);
      getView().showRecordsInfo(cards);
   }

   public void navigateToForceUpdate() {
      navigator.single(new ForceFactoryResetPath(), Flow.Direction.REPLACE);
   }

   public void handleForceFirmwareUpdateConfirmation() {
      if (cardStackHeaderHolder.firmware().factoryResetRequired()) {
         getView().showFactoryResetConfirmationDialog();
      } else {
         navigateToForceUpdate();
      }
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

      void showForceFirmwareUpdateDialog();

      void showFactoryResetConfirmationDialog();

      void showCardSynchronizationDialog(boolean visible);

      @IntDef({ERROR_DIALOG_FULL_SMARTCARD, ERROR_DIALOG_NO_INTERNET_CONNECTION, ERROR_DIALOG_NO_SMARTCARD_CONNECTION})
      @interface ErrorDialogType {}
   }
}
