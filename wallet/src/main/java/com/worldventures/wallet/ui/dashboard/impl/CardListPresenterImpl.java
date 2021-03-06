package com.worldventures.wallet.ui.dashboard.impl;

import android.support.annotation.DrawableRes;
import android.support.v4.util.Pair;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.worldventures.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.core.utils.ProjectTextUtils;
import com.worldventures.wallet.analytics.AddPaymentCardAction;
import com.worldventures.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.wallet.analytics.WalletHomeAction;
import com.worldventures.wallet.analytics.firmware.WalletFirmwareAnalyticsCommand;
import com.worldventures.wallet.analytics.firmware.action.RetryInstallUpdateAction;
import com.worldventures.wallet.domain.WalletConstants;
import com.worldventures.wallet.domain.entity.ConnectionStatus;
import com.worldventures.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.wallet.domain.entity.SmartCardStatus;
import com.worldventures.wallet.domain.entity.SmartCardUser;
import com.worldventures.wallet.domain.entity.record.Record;
import com.worldventures.wallet.domain.entity.record.SyncRecordsStatus;
import com.worldventures.wallet.service.FirmwareInteractor;
import com.worldventures.wallet.service.RecordInteractor;
import com.worldventures.wallet.service.SmartCardInteractor;
import com.worldventures.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.wallet.service.command.RecordListCommand;
import com.worldventures.wallet.service.command.SmartCardUserCommand;
import com.worldventures.wallet.service.command.SyncSmartCardCommand;
import com.worldventures.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.wallet.service.command.device.SmartCardFirmwareCommand;
import com.worldventures.wallet.service.command.offline_mode.OfflineModeStatusCommand;
import com.worldventures.wallet.service.command.record.DefaultRecordIdCommand;
import com.worldventures.wallet.service.command.record.SyncRecordOnNewDeviceCommand;
import com.worldventures.wallet.service.command.record.SyncRecordStatusCommand;
import com.worldventures.wallet.service.command.settings.general.display.GetDisplayTypeCommand;
import com.worldventures.wallet.service.firmware.command.FirmwareInfoCachedCommand;
import com.worldventures.wallet.service.lostcard.LocationTrackingManager;
import com.worldventures.wallet.ui.common.WalletNavigationDelegate;
import com.worldventures.wallet.ui.common.adapter.BaseViewModel;
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.wallet.ui.common.base.WalletNetworkDelegate;
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.dashboard.CardListPresenter;
import com.worldventures.wallet.ui.dashboard.CardListScreen;
import com.worldventures.wallet.ui.dashboard.util.model.CommonCardViewModel;
import com.worldventures.wallet.ui.dashboard.util.model.TransitionModel;
import com.worldventures.wallet.ui.settings.general.reset.delegate.FactoryResetDelegate;
import com.worldventures.wallet.util.CardListStackConverter;
import com.worldventures.wallet.util.WalletFeatureHelper;
import com.worldventures.wallet.util.WalletRecordUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.helper.ActionStateToActionTransformer;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.operationsubscriber.view.OperationView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import timber.log.Timber;

import static com.worldventures.wallet.util.WalletFilesUtils.getAppropriateFirmwareFile;

@SuppressWarnings("PMD.GodClass") //TODO: Resolve this PMD error
public class CardListPresenterImpl extends WalletPresenterImpl<CardListScreen> implements CardListPresenter {

   private final SmartCardInteractor smartCardInteractor;
   private final WalletNetworkDelegate networkDelegate;
   private final RecordInteractor recordInteractor;
   private final FirmwareInteractor firmwareInteractor;
   private final WalletAnalyticsInteractor analyticsInteractor;
   private final WalletNavigationDelegate navigationDelegate;
   private final WalletFeatureHelper featureHelper;
   private final FactoryResetDelegate factoryResetDelegate;
   private final CardListStackConverter cardListStackConverter;
   private final LocationTrackingManager locationTrackingManager;

   private List<Record> records;

   public CardListPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         WalletNetworkDelegate networkDelegate, SmartCardInteractor smartCardInteractor, RecordInteractor recordInteractor,
         FirmwareInteractor firmwareInteractor, WalletAnalyticsInteractor analyticsInteractor,
         FactoryResetDelegate factoryResetDelegate, WalletNavigationDelegate navigationDelegate,
         WalletFeatureHelper walletFeatureHelper, LocationTrackingManager locationTrackingManager) {
      super(navigator, deviceConnectionDelegate);
      this.networkDelegate = networkDelegate;
      this.smartCardInteractor = smartCardInteractor;
      this.recordInteractor = recordInteractor;
      this.firmwareInteractor = firmwareInteractor;
      this.analyticsInteractor = analyticsInteractor;
      this.navigationDelegate = navigationDelegate;
      this.featureHelper = walletFeatureHelper;
      this.factoryResetDelegate = factoryResetDelegate;
      this.cardListStackConverter = new CardListStackConverter(new WalletRecordUtil(), walletFeatureHelper);
      this.locationTrackingManager = locationTrackingManager;
   }

   @Override
   public void attachView(CardListScreen view) {
      super.attachView(view);
      networkDelegate.setup(getView());
      featureHelper.prepareDashboardScreen(getView());
      getView().setDefaultSmartCard();
      factoryResetDelegate.bindView(getView());
      observeSmartCard();
      observeDisplayType();
      observeConnectionStatus();
      observeSmartCardSync();
      observeRecordsChanges();
      observeFirmwareInfo();

      observeSyncRecordsStatus();
      fetchSyncRecordsStatus();

      locationTrackingManager.track();

      recordInteractor.cardsListPipe().send(RecordListCommand.Companion.fetch());
      recordInteractor.defaultRecordIdPipe().send(DefaultRecordIdCommand.fetch());
      trackScreen();
   }

   private void fetchSyncRecordsStatus() {
      recordInteractor.syncRecordStatusPipe().send(SyncRecordStatusCommand.fetch());
   }

   @SuppressWarnings("ConstantConditions")
   private void observeSyncRecordsStatus() {
      recordInteractor.syncRecordStatusPipe()
            .observeSuccessWithReplay()
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnUnsubscribe(() -> Timber.d("observeSyncRecordsStatus doOnUnsubscribe"))
            .doOnSubscribe(() -> Timber.d("observeSyncRecordsStatus doOnSubscribe"))
            .subscribe(command -> handleSyncRecordStatus(command.getResult()), Timber::e);

      recordInteractor.syncRecordOnNewDevicePipe()
            .observe()
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new ActionStateSubscriber<SyncRecordOnNewDeviceCommand>()
                  .onFail((command, throwable) -> getView().showSyncFailedOptionsDialog())
            );
      recordInteractor.syncRecordOnNewDevicePipe()
            .observe()
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(getView().provideReSyncOperationView()).create());
   }

   @SuppressWarnings("ConstantConditions")
   private void handleSyncRecordStatus(SyncRecordsStatus status) {
      if (featureHelper.addingCardIsNotSupported()) {
         return;
      }
      if (status.isFailAfterProvision()) {
         getView().modeSyncPaymentsFab();
      } else {
         getView().modeAddCard();
      }
   }

   private void observeSmartCard() {
      smartCardInteractor.deviceStatePipe().observeSuccessWithReplay()
            .map(Command::getResult)
            .throttleLast(300, TimeUnit.MILLISECONDS)
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::handleSmartCardStatus, Timber::e);

      smartCardInteractor.smartCardUserPipe().observeSuccessWithReplay()
            .map(Command::getResult)
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::handleSmartCardUser, Timber::e);

      smartCardInteractor.smartCardUserPipe().send(SmartCardUserCommand.fetch());
      smartCardInteractor.activeSmartCardPipe().send(new ActiveSmartCardCommand());
      smartCardInteractor.deviceStatePipe().send(DeviceStateCommand.Companion.fetch());
   }

   private void observeDisplayType() {
      smartCardInteractor.getDisplayTypePipe().observeSuccessWithReplay()
            .map(Command::getResult)
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(getView()::setDisplayType, Timber::e);
      smartCardInteractor.getDisplayTypePipe().send(new GetDisplayTypeCommand(false));
   }

   private void handleSmartCardStatus(SmartCardStatus cardStatus) {
      final boolean connected = cardStatus.getConnectionStatus().isConnected();
      getView().setSmartCardStatusAttrs(cardStatus.getBatteryLevel(), connected,
            cardStatus.getLock(), cardStatus.getStealthMode());
   }

   private void handleSmartCardUser(SmartCardUser smartCardUser) {
      if (smartCardUser == null) {
         String message = String.format("User is null in SmartCardUserCommand storage in %s screen",
               getClass().getSimpleName());
         Timber.e(message);
         Crashlytics.log(message);
         return;
      }
      getView().setSmartCardUser(smartCardUser);
   }

   private void observeConnectionStatus() {
      smartCardInteractor.deviceStatePipe().observeSuccessWithReplay()
            .map(command -> command.getResult().getConnectionStatus())
            .distinctUntilChanged()
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(connectionStatus -> {
               if (connectionStatus == ConnectionStatus.DFU) {
                  File firmwareFile = getAppropriateFirmwareFile(getView().getViewContext());
                  if (firmwareFile.exists()) {
                     getView().showFirmwareUpdateError();
                  }
               }
            });
   }

   private void observeFirmwareInfo() {
      Observable.combineLatest(
            firmwareInteractor.firmwareInfoCachedPipe().observeSuccess().map(Command::getResult),
            smartCardInteractor.smartCardFirmwarePipe().observeSuccess().map(Command::getResult),
            (firmwareUpdate, scFirmware) -> {
               if (!ProjectTextUtils.isEmpty(scFirmware.getNordicAppVersion()) || scFirmware.getFirmwareBundleVersion() != null) {
                  return firmwareUpdate;
               }
               return null;
            })
            .filter(firmwareUpdateData -> firmwareUpdateData != null)
            .distinct()
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::firmwareLoaded, Timber::e);

      smartCardInteractor.smartCardFirmwarePipe().send(SmartCardFirmwareCommand.Companion.fetch());
      firmwareInteractor.firmwareInfoCachedPipe().send(FirmwareInfoCachedCommand.fetch());
   }

   private void firmwareLoaded(FirmwareUpdateData firmwareUpdateData) {
      if (firmwareUpdateData.isUpdateAvailable()) {
         if (firmwareUpdateData.isUpdateCritical()) {
            getView().showForceFirmwareUpdateDialog();
         }
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
                     analyticsInteractor.walletAnalyticsPipe().send(analyticsCommand);
                  }, Timber::e
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
   @Override
   public TransitionModel getCardPosition(View view, int overlap, @DrawableRes int cardBackGroundResId,
         boolean defaultCard) {
      int[] position = new int[2];
      view.getLocationOnScreen(position);
      return new TransitionModel(defaultCard, position[0], position[1], view.getWidth(), view.getHeight(), overlap,
            cardBackGroundResId);
   }

   @Override
   public void cardClicked(CommonCardViewModel record, TransitionModel transitionModel) {
      getNavigator().goCardDetails(record, transitionModel);
   }

   @Override
   public boolean isCardDetailSupported() {
      return !featureHelper.isSampleCardMode();
   }

   @Override
   public void navigationClick() {
      navigationDelegate.openDrawer();
   }

   @Override
   public void onSettingsChosen() {
      getNavigator().goWalletSettings();
   }

   @Override
   @SuppressWarnings("ConstantConditions")
   public void onProfileChosen() {
      featureHelper.openEditProfile(getView().getViewContext(),
            () -> assertSmartCardConnected(() -> getNavigator().goSettingsProfile()));
   }

   @Override
   public void navigateBack() {
      getNavigator().goBack();
   }

   @Override
   @SuppressWarnings("ConstantConditions")
   public void addCardRequired(int cardLoadedCount) {
      if (cardLoadedCount >= WalletConstants.MAX_CARD_LIMIT) {
         getView().showAddCardErrorDialog(CardListScreen.ERROR_DIALOG_FULL_SMARTCARD);
         return;
      }

      Observable.zip(
            smartCardInteractor.offlineModeStatusPipe().createObservableResult(OfflineModeStatusCommand.Companion.fetch()),
            smartCardInteractor.deviceStatePipe().createObservableResult(DeviceStateCommand.Companion.fetch()),
            (offlineModeState, smartCardModifier) -> {
               boolean needNetworkConnection = offlineModeState.getResult() || networkDelegate.isAvailable();
               boolean needSmartCardConnection = smartCardModifier.getResult().getConnectionStatus().isConnected();
               return new Pair<>(needNetworkConnection, needSmartCardConnection);
            })
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(connectionStatusPair -> {
               if (!connectionStatusPair.first) {
                  getView().showAddCardErrorDialog(CardListScreen.ERROR_DIALOG_NO_INTERNET_CONNECTION);
               } else if (!connectionStatusPair.second) {
                  getView().showSCNonConnectionDialog();
               } else {
                  trackAddCard();
                  getNavigator().goWizardCharging();
               }
            }, e -> Timber.e(e, "Could not subscribe to network and SmartCard events"));
   }

   private void trackAddCard() {
      analyticsInteractor.walletAnalyticsPipe().send(new WalletAnalyticsCommand(new AddPaymentCardAction()));
   }

   private void observeRecordsChanges() {
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
            .map(loadedRecords -> {
               records = (List<Record>) loadedRecords.first;
               return cardListStackConverter.mapToViewModel(getView().getViewContext(), (List<Record>) loadedRecords.first, loadedRecords.second);
            })
            .distinctUntilChanged()
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::cardsLoaded, Timber::e);
   }

   @SuppressWarnings("ConstantConditions")
   private void cardsLoaded(ArrayList<BaseViewModel<?>> cardModels) {
      getView().setCardsCount(null != this.records ? this.records.size() : 0);
      getView().showRecordsInfo(cardModels);
   }

   @SuppressWarnings("ConstantConditions")
   private void observeSmartCardSync() {
      final OperationView<SyncSmartCardCommand> operationView = getView().provideOperationSyncSmartCard();
      smartCardInteractor.smartCardSyncPipe()
            .observeWithReplay()
            .compose(new ActionPipeCacheWiper<>(smartCardInteractor.smartCardSyncPipe()))
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnCompleted(() -> {
               if (operationView.isProgressVisible()) {
                  operationView.hideProgress();
               }
            })
            .subscribe(OperationActionSubscriber.forView(operationView).create());
   }

   @Override
   public void retryFWU() {
      sendRetryAnalyticAction(true);
      getNavigator().goInstallFirmware();
   }

   @Override
   public void retryFWUCanceled() {
      sendRetryAnalyticAction(false);
      navigateBack();
   }

   @Override
   public void navigateToFirmwareUpdate() {
      getNavigator().goStartFirmwareInstall();
   }

   @SuppressWarnings("ConstantConditions")
   private void assertSmartCardConnected(Action0 onConnected) {
      smartCardInteractor.deviceStatePipe()
            .createObservable(DeviceStateCommand.Companion.fetch())
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new ActionStateSubscriber<DeviceStateCommand>()
                  .onSuccess(command -> {
                     if (command.getResult().getConnectionStatus().isConnected()) {
                        onConnected.call();
                     } else {
                        getView().showSCNonConnectionDialog();
                     }
                  })
            );
   }

   private void sendRetryAnalyticAction(boolean retry) {
      analyticsInteractor.walletFirmwareAnalyticsPipe()
            .send(new WalletFirmwareAnalyticsCommand(new RetryInstallUpdateAction(retry)));
   }

   @Override
   public void confirmForceFirmwareUpdate() {
      // TODO: 3/6/17 can be better
      firmwareInteractor.firmwareInfoCachedPipe()
            .createObservable(FirmwareInfoCachedCommand.fetch())
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new ActionStateSubscriber<FirmwareInfoCachedCommand>()
                  .onSuccess(command -> {
                     if (command.getResult().isFactoryResetRequired()) {
                        getView().showFactoryResetConfirmationDialog();
                     } else {
                        navigateToFirmwareUpdate();
                     }
                  })
            );
   }

   @Override
   public void syncPayments() {
      recordInteractor.syncRecordOnNewDevicePipe().send(new SyncRecordOnNewDeviceCommand());
   }

   @Override
   public void goToFactoryReset() {
      factoryResetDelegate.startRegularFactoryReset();
   }
}
