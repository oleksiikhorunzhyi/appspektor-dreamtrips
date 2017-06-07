package com.worldventures.dreamtrips.wallet.ui.dashboard;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.v4.util.Pair;
import android.view.View;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.modules.navdrawer.NavigationDrawerPresenter;
import com.worldventures.dreamtrips.wallet.analytics.AddPaymentCardAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.WalletHomeAction;
import com.worldventures.dreamtrips.wallet.analytics.firmware.WalletFirmwareAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.firmware.action.RetryInstallUpdateAction;
import com.worldventures.dreamtrips.wallet.domain.entity.ConnectionStatus;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardStatus;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.domain.entity.record.SyncRecordsStatus;
import com.worldventures.dreamtrips.wallet.service.FactoryResetInteractor;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.RecordInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.RecordListCommand;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.service.command.SyncSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.offline_mode.OfflineModeStatusCommand;
import com.worldventures.dreamtrips.wallet.service.command.record.DefaultRecordIdCommand;
import com.worldventures.dreamtrips.wallet.service.command.record.SyncRecordOnNewDeviceCommand;
import com.worldventures.dreamtrips.wallet.service.command.record.SyncRecordStatusCommand;
import com.worldventures.dreamtrips.wallet.service.firmware.command.FirmwareInfoCachedCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.adapter.BaseViewModel;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.model.TransitionModel;
import com.worldventures.dreamtrips.wallet.ui.records.detail.CardDetailsPath;
import com.worldventures.dreamtrips.wallet.ui.records.swiping.WizardChargingPath;
import com.worldventures.dreamtrips.wallet.ui.settings.WalletSettingsPath;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.install.WalletInstallFirmwarePath;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.start.StartFirmwareInstallPath;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.CheckPinDelegate;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.FactoryResetAction;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.FactoryResetView;
import com.worldventures.dreamtrips.wallet.util.CardListStackConverter;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import flow.Flow;
import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.helper.ActionStateToActionTransformer;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.operationsubscriber.view.OperationView;
import rx.Observable;
import timber.log.Timber;

import static com.worldventures.dreamtrips.wallet.util.WalletFilesUtils.getAppropriateFirmwareFile;

public class CardListPresenter extends WalletPresenter<CardListPresenter.Screen, Parcelable> {

   public static final int MAX_CARD_LIMIT = 10;

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject RecordInteractor recordInteractor;
   @Inject FirmwareInteractor firmwareInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject WalletNetworkService networkService;
   @Inject FactoryResetInteractor factoryResetInteractor;
   @Inject NavigationDrawerPresenter navigationDrawerPresenter;

   private final CardListStackConverter cardListStackConverter;
   private final CheckPinDelegate checkPinDelegate;
   private List<Record> records;

   public CardListPresenter(Context context, Injector injector) {
      super(context, injector);
      cardListStackConverter = new CardListStackConverter(context);
      checkPinDelegate = new CheckPinDelegate(smartCardInteractor, factoryResetInteractor, analyticsInteractor,
            navigator, FactoryResetAction.GENERAL);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      getView().setDefaultSmartCard();
      checkPinDelegate.observePinStatus(getView());
      observeSmartCard();
      observeConnectionStatus();
      observeChanges();
      observeFirmwareInfo();

      observeSyncRecordsStatus();
      fetchSyncRecordsStatus();

      recordInteractor.cardsListPipe().send(RecordListCommand.fetch());
      recordInteractor.defaultRecordIdPipe().send(DefaultRecordIdCommand.fetch());
      trackScreen();
   }

   private void fetchSyncRecordsStatus() {
      recordInteractor.syncRecordStatusPipe().send(SyncRecordStatusCommand.fetch());
   }

   private void observeSyncRecordsStatus() {
      recordInteractor.syncRecordStatusPipe()
            .observeSuccessWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(command -> handleSyncRecordStatus(command.getResult()));

      //noinspection ConstantConditions
      recordInteractor.syncRecordOnNewDevicePipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<SyncRecordOnNewDeviceCommand>()
                  .onFail((command, throwable) -> getView().showSyncFailedOptionsDialog())
            );
      //noinspection ConstantConditions
      recordInteractor.syncRecordOnNewDevicePipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().provideReSyncOperationView()).create());
   }

   private void handleSyncRecordStatus(SyncRecordsStatus status) {
      if (status.isFailAfterProvision()) {
         //noinspection ConstantConditions
         getView().modeSyncPaymentsFab();
      } else {
         //noinspection ConstantConditions
         getView().modeAddCard();
      }
   }

   private void observeSmartCard() {
      smartCardInteractor.deviceStatePipe().observeSuccessWithReplay()
            .map(Command::getResult)
            .throttleLast(300, TimeUnit.MILLISECONDS)
            .compose(bindViewIoToMainComposer())
            .subscribe(this::handleSmartCardStatus, throwable -> Timber.e("", throwable));

      smartCardInteractor.smartCardUserPipe().observeSuccessWithReplay()
            .map(Command::getResult)
            .compose(bindViewIoToMainComposer())
            .subscribe(this::handleSmartCardUser, throwable -> Timber.e("", throwable));

      smartCardInteractor.smartCardUserPipe().send(SmartCardUserCommand.fetch());
      smartCardInteractor.activeSmartCardPipe().send(new ActiveSmartCardCommand());
      smartCardInteractor.deviceStatePipe().send(DeviceStateCommand.fetch());
   }

   private void handleSmartCardStatus(SmartCardStatus cardStatus) {
      final boolean connected = cardStatus.connectionStatus().isConnected();
      getView().setSmartCardStatusAttrs(cardStatus.batteryLevel(), connected,
            cardStatus.lock(), cardStatus.stealthMode());
   }

   private void handleSmartCardUser(SmartCardUser smartCardUser) {
      final String photoFileUrl = smartCardUser.userPhoto() != null
            ? smartCardUser.userPhoto().photoUrl()
            : "";
      getView().setSmartCardUserAttrs(smartCardUser.fullName(), photoFileUrl);
   }

   private void observeConnectionStatus() {
      smartCardInteractor.deviceStatePipe().observeSuccessWithReplay()
            .map(command -> command.getResult().connectionStatus())
            .distinctUntilChanged()
            .compose(bindViewIoToMainComposer())
            .subscribe(connectionStatus -> {
               if (connectionStatus == ConnectionStatus.DFU) {
                  File firmwareFile = getAppropriateFirmwareFile(getContext());
                  if (firmwareFile.exists()) {
                     getView().showFirmwareUpdateError();
                  }
               }
            });
   }

   private void observeFirmwareInfo() {
      firmwareInteractor.firmwareInfoCachedPipe()
            .observeSuccessWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(command -> firmwareLoaded(command.getResult()), throwable -> Timber.e(throwable, ""));

      firmwareInteractor.firmwareInfoCachedPipe().send(FirmwareInfoCachedCommand.fetch());
   }

   private void firmwareLoaded(FirmwareUpdateData firmwareUpdateData) {
      getView().setFirmwareUpdateAvailable(firmwareUpdateData.updateAvailable());
      if (firmwareUpdateData.updateAvailable()) {
         if (firmwareUpdateData.updateCritical()) getView().showForceFirmwareUpdateDialog();
         getView().showFirmwareUpdateBtn();
      } else {
         getView().hideFirmwareUpdateBtn();
      }
   }

   private void trackScreen() {
      recordInteractor.cardsListPipe()
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

   /**
    * Create transition animation model which contains coordinates of\
    * transition view, its overlap rate and background
    *
    * @param view                transition view itself
    * @param overlap             overlap rate (used in ItemDecorator {@see OverlapDecoration}
    * @param cardBackGroundResId
    * @param defaultCard         true if card is default
    * @return {@see TransitionModel}
    */
   public TransitionModel getCardPosition(View view, int overlap, @DrawableRes int cardBackGroundResId,
         boolean defaultCard) {
      int[] position = new int[2];
      view.getLocationOnScreen(position);
      return new TransitionModel(defaultCard, position[0], position[1], view.getWidth(), view.getHeight(), overlap,
            cardBackGroundResId);
   }

   void cardClicked(String recId, TransitionModel transitionModel) {
      if (this.records != null && !this.records.isEmpty()) {
         Record record = Queryable.from(this.records).first(card -> card.id() != null && card.id().equals(recId));
         navigator.go(new CardDetailsPath(record, transitionModel));
      }
   }

   void navigationClick() {
      navigationDrawerPresenter.openDrawer();
   }

   void onSettingsChosen() {
      navigator.go(new WalletSettingsPath());
   }

   void navigateBack() {
      navigator.goBack();
   }

   void addCardRequired(int cardLoadedCount) {
      if (cardLoadedCount >= MAX_CARD_LIMIT) {
         getView().showAddCardErrorDialog(Screen.ERROR_DIALOG_FULL_SMARTCARD);
         return;
      }

      Observable.zip(
            smartCardInteractor.offlineModeStatusPipe().createObservableResult(OfflineModeStatusCommand.fetch()),
            smartCardInteractor.deviceStatePipe().createObservableResult(DeviceStateCommand.fetch()),
            (offlineModeState, smartCardModifier) -> {
               boolean needNetworkConnection = offlineModeState.getResult() || networkService.isAvailable();
               boolean needSmartCardConnection = smartCardModifier.getResult().connectionStatus().isConnected();
               return new Pair<>(needNetworkConnection, needSmartCardConnection);
            })
            .compose(bindViewIoToMainComposer())
            .subscribe(connectionStatusPair -> {
               if (!connectionStatusPair.first) {
                  getView().showAddCardErrorDialog(Screen.ERROR_DIALOG_NO_INTERNET_CONNECTION);
               } else if (!connectionStatusPair.second) {
                  getView().showSCNonConnectionDialog();
               } else {
                  trackAddCard();
                  navigator.go(new WizardChargingPath());
               }
            }, e -> Timber.e(e, "Could not subscribe to network and SmartCard events"));
   }

   private void trackAddCard() {
      analyticsInteractor.walletAnalyticsCommandPipe().send(new WalletAnalyticsCommand(new AddPaymentCardAction()));
   }

   private void observeChanges() {
      Observable.combineLatest(
            recordInteractor.cardsListPipe()
                  .observeWithReplay()
                  .compose(new ActionPipeCacheWiper<>(recordInteractor.cardsListPipe()))
                  .compose(new ActionStateToActionTransformer<>()),
            recordInteractor.defaultRecordIdPipe()
                  .observeWithReplay()
                  .compose(new ActionPipeCacheWiper<>(recordInteractor.defaultRecordIdPipe()))
                  .compose(new ActionStateToActionTransformer<>()),
            (cardListCommand, defaultCardIdCommand) -> Pair.create(cardListCommand.getResult(), defaultCardIdCommand.getResult()))
            .compose(bindViewIoToMainComposer())
            .subscribe(pair -> cardsLoaded(pair.first, pair.second), throwable -> { /*ignore here*/ });

      //noinspection ConstantConditions
      smartCardInteractor.smartCardSyncPipe()
            .observeWithReplay()
            .compose(new ActionPipeCacheWiper<>(smartCardInteractor.smartCardSyncPipe()))
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationSyncSmartCard()).create());
   }

   private void cardsLoaded(List<Record> loadedRecords, String defaultRecordId) {
      this.records = loadedRecords;
      getView().setCardsCount(null != loadedRecords ? loadedRecords.size() : 0);
      List<BaseViewModel> cardModels = cardListStackConverter.mapToViewModel(loadedRecords, defaultRecordId);

      getView().showRecordsInfo(cardModels);
   }

   void retryFWU() {
      sendRetryAnalyticAction(true);
      navigator.go(new WalletInstallFirmwarePath());
   }

   void retryFWUCanceled() {
      sendRetryAnalyticAction(false);
      navigateBack();
   }

   private void sendRetryAnalyticAction(boolean retry) {
      analyticsInteractor.walletFirmwareAnalyticsPipe()
            .send(new WalletFirmwareAnalyticsCommand(new RetryInstallUpdateAction(retry)));
   }

   public void navigateToFirmwareUpdate() {
      navigator.single(new StartFirmwareInstallPath(), Flow.Direction.REPLACE);
   }

   void confirmForceFirmwareUpdate() {
      // TODO: 3/6/17 can be better
      firmwareInteractor.firmwareInfoCachedPipe()
            .createObservable(FirmwareInfoCachedCommand.fetch())
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<FirmwareInfoCachedCommand>()
                  .onSuccess(command -> {
                     if (command.getResult().factoryResetRequired()) {
                        getView().showFactoryResetConfirmationDialog();
                     } else {
                        navigateToFirmwareUpdate();
                     }
                  })
            );
   }

   void syncPayments() {
      recordInteractor.syncRecordOnNewDevicePipe().send(new SyncRecordOnNewDeviceCommand());
   }

   void goToFactoryReset() {
      checkPinDelegate.getFactoryResetDelegate().setupDelegate(getView());
   }

   public interface Screen extends WalletScreen, FactoryResetView {

      int ERROR_DIALOG_FULL_SMARTCARD = 1;
      int ERROR_DIALOG_NO_INTERNET_CONNECTION = 2;
      int ERROR_DIALOG_NO_SMARTCARD_CONNECTION = 3;

      void showRecordsInfo(List<BaseViewModel> result);

      void setDefaultSmartCard();

      void setSmartCardStatusAttrs(int batteryLevel, boolean connected, boolean lock, boolean stealthMode);

      void setSmartCardUserAttrs(String fullname, String photoFileUrl);

      void setFirmwareUpdateAvailable(boolean firmwareUpdateAvailable);

      void setCardsCount(int count);

      void showAddCardErrorDialog(@ErrorDialogType int errorDialogType);

      void hideFirmwareUpdateBtn();

      void showFirmwareUpdateBtn();

      void showFirmwareUpdateError();

      void showForceFirmwareUpdateDialog();

      void showFactoryResetConfirmationDialog();

      @IntDef({ERROR_DIALOG_FULL_SMARTCARD, ERROR_DIALOG_NO_INTERNET_CONNECTION, ERROR_DIALOG_NO_SMARTCARD_CONNECTION})
      @interface ErrorDialogType {}

      void showSCNonConnectionDialog();

      void modeAddCard();

      void modeSyncPaymentsFab();

      void showSyncFailedOptionsDialog();

      OperationView<SyncSmartCardCommand> provideOperationSyncSmartCard();

      OperationView<SyncRecordOnNewDeviceCommand> provideReSyncOperationView();
   }
}
