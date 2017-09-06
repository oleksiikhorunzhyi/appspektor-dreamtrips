package com.worldventures.dreamtrips.wallet.ui.dashboard.impl;

import android.support.annotation.DrawableRes;
import android.support.v4.util.Pair;
import android.view.View;

import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.core.utils.ProjectTextUtils;
import com.worldventures.dreamtrips.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.dreamtrips.modules.navdrawer.NavigationDrawerPresenter;
import com.worldventures.dreamtrips.wallet.analytics.AddPaymentCardAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.WalletHomeAction;
import com.worldventures.dreamtrips.wallet.analytics.firmware.WalletFirmwareAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.firmware.action.RetryInstallUpdateAction;
import com.worldventures.dreamtrips.wallet.domain.WalletConstants;
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
import com.worldventures.dreamtrips.wallet.service.command.device.SmartCardFirmwareCommand;
import com.worldventures.dreamtrips.wallet.service.command.offline_mode.OfflineModeStatusCommand;
import com.worldventures.dreamtrips.wallet.service.command.record.DefaultRecordIdCommand;
import com.worldventures.dreamtrips.wallet.service.command.record.SyncRecordOnNewDeviceCommand;
import com.worldventures.dreamtrips.wallet.service.command.record.SyncRecordStatusCommand;
import com.worldventures.dreamtrips.wallet.service.command.settings.general.display.GetDisplayTypeCommand;
import com.worldventures.dreamtrips.wallet.service.firmware.command.FirmwareInfoCachedCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.LocationTrackingManager;
import com.worldventures.dreamtrips.wallet.ui.common.adapter.BaseViewModel;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.dashboard.CardListPresenter;
import com.worldventures.dreamtrips.wallet.ui.dashboard.CardListScreen;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.model.CommonCardViewModel;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.model.TransitionModel;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.CheckPinDelegate;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.FactoryResetAction;
import com.worldventures.dreamtrips.wallet.util.CardListStackConverter;
import com.worldventures.dreamtrips.wallet.util.WalletFeatureHelper;
import com.worldventures.dreamtrips.wallet.util.WalletRecordUtil;

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
import rx.functions.Action0;
import timber.log.Timber;

import static com.worldventures.dreamtrips.wallet.util.WalletFilesUtils.getAppropriateFirmwareFile;

public class CardListPresenterImpl extends WalletPresenterImpl<CardListScreen> implements CardListPresenter {

   private final RecordInteractor recordInteractor;
   private final FirmwareInteractor firmwareInteractor;
   private final WalletAnalyticsInteractor analyticsInteractor;
   private final NavigationDrawerPresenter navigationDrawerPresenter;
   private final WalletFeatureHelper featureHelper;
   private final CheckPinDelegate checkPinDelegate;
   private final CardListStackConverter cardListStackConverter;
   private final LocationTrackingManager locationTrackingManager;

   private List<Record> records;

   public CardListPresenterImpl(Navigator navigator, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService, RecordInteractor recordInteractor, FirmwareInteractor firmwareInteractor,
         WalletAnalyticsInteractor analyticsInteractor, FactoryResetInteractor factoryResetInteractor,
         NavigationDrawerPresenter navigationDrawerPresenter, WalletFeatureHelper walletFeatureHelper,
         LocationTrackingManager locationTrackingManager) {
      super(navigator, smartCardInteractor, networkService);
      this.recordInteractor = recordInteractor;
      this.firmwareInteractor = firmwareInteractor;
      this.analyticsInteractor = analyticsInteractor;
      this.navigationDrawerPresenter = navigationDrawerPresenter;
      this.featureHelper = walletFeatureHelper;
      this.checkPinDelegate = new CheckPinDelegate(smartCardInteractor, factoryResetInteractor, analyticsInteractor,
            navigator, FactoryResetAction.GENERAL);
      this.cardListStackConverter = new CardListStackConverter(new WalletRecordUtil(), walletFeatureHelper);
      this.locationTrackingManager = locationTrackingManager;
   }

   @Override
   public void attachView(CardListScreen view) {
      super.attachView(view);
      featureHelper.prepareDashboardScreen(getView());
      getView().setDefaultSmartCard();
      checkPinDelegate.observePinStatus(getView());
      observeSmartCard();
      observeDisplayType();
      observeConnectionStatus();
      observeSmartCardSync();
      observeRecordsChanges();
      observeFirmwareInfo();

      observeSyncRecordsStatus();
      fetchSyncRecordsStatus();

      locationTrackingManager.track();

      recordInteractor.cardsListPipe().send(RecordListCommand.fetch());
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
            .compose(bindViewIoToMainComposer())
            .subscribe(command -> handleSyncRecordStatus(command.getResult()));

      recordInteractor.syncRecordOnNewDevicePipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<SyncRecordOnNewDeviceCommand>()
                  .onFail((command, throwable) -> getView().showSyncFailedOptionsDialog())
            );
      recordInteractor.syncRecordOnNewDevicePipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().provideReSyncOperationView()).create());
   }

   @SuppressWarnings("ConstantConditions")
   private void handleSyncRecordStatus(SyncRecordsStatus status) {
      if (featureHelper.addingCardIsNotSupported()) return;
      if (status.isFailAfterProvision()) {
         getView().modeSyncPaymentsFab();
      } else {
         getView().modeAddCard();
      }
   }

   private void observeSmartCard() {
      getSmartCardInteractor().deviceStatePipe().observeSuccessWithReplay()
            .map(Command::getResult)
            .throttleLast(300, TimeUnit.MILLISECONDS)
            .compose(bindViewIoToMainComposer())
            .subscribe(this::handleSmartCardStatus, throwable -> Timber.e(throwable, ""));

      getSmartCardInteractor().smartCardUserPipe().observeSuccessWithReplay()
            .map(Command::getResult)
            .compose(bindViewIoToMainComposer())
            .subscribe(this::handleSmartCardUser, throwable -> Timber.e(throwable, ""));

      getSmartCardInteractor().smartCardUserPipe().send(SmartCardUserCommand.fetch());
      getSmartCardInteractor().activeSmartCardPipe().send(new ActiveSmartCardCommand());
      getSmartCardInteractor().deviceStatePipe().send(DeviceStateCommand.fetch());
   }

   private void observeDisplayType() {
      getSmartCardInteractor().getDisplayTypePipe().observeSuccessWithReplay()
            .map(Command::getResult)
            .compose(bindViewIoToMainComposer())
            .subscribe(getView()::setDisplayType, throwable -> Timber.e(throwable, ""));
      getSmartCardInteractor().getDisplayTypePipe().send(new GetDisplayTypeCommand(false));
   }

   private void handleSmartCardStatus(SmartCardStatus cardStatus) {
      final boolean connected = cardStatus.connectionStatus().isConnected();
      getView().setSmartCardStatusAttrs(cardStatus.batteryLevel(), connected,
            cardStatus.lock(), cardStatus.stealthMode());
   }

   private void handleSmartCardUser(SmartCardUser smartCardUser) {
      getView().setSmartCardUser(smartCardUser);
   }

   private void observeConnectionStatus() {
      getSmartCardInteractor().deviceStatePipe().observeSuccessWithReplay()
            .map(command -> command.getResult().connectionStatus())
            .distinctUntilChanged()
            .compose(bindViewIoToMainComposer())
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
            getSmartCardInteractor().smartCardFirmwarePipe().observeSuccess().map(Command::getResult),
            (firmwareUpdate, scFirmware) -> {
               if (!ProjectTextUtils.isEmpty(scFirmware.nordicAppVersion()) || scFirmware.firmwareBundleVersion() != null) {
                  return firmwareUpdate;
               }
               return null;
            })
            .filter(firmwareUpdateData -> firmwareUpdateData != null)
            .distinct()
            .compose(bindViewIoToMainComposer())
            .subscribe(this::firmwareLoaded, throwable -> Timber.e(throwable, ""));

      getSmartCardInteractor().smartCardFirmwarePipe().send(SmartCardFirmwareCommand.fetch());
      firmwareInteractor.firmwareInfoCachedPipe().send(FirmwareInfoCachedCommand.fetch());
   }

   private void firmwareLoaded(FirmwareUpdateData firmwareUpdateData) {
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
                     analyticsInteractor.walletAnalyticsPipe().send(analyticsCommand);
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
      navigationDrawerPresenter.openDrawer();
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
            getSmartCardInteractor().offlineModeStatusPipe().createObservableResult(OfflineModeStatusCommand.fetch()),
            getSmartCardInteractor().deviceStatePipe().createObservableResult(DeviceStateCommand.fetch()),
            (offlineModeState, smartCardModifier) -> {
               boolean needNetworkConnection = offlineModeState.getResult() || getNetworkService().isAvailable();
               boolean needSmartCardConnection = smartCardModifier.getResult().connectionStatus().isConnected();
               return new Pair<>(needNetworkConnection, needSmartCardConnection);
            })
            .compose(bindViewIoToMainComposer())
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
               records = loadedRecords.first;
               return cardListStackConverter.mapToViewModel(getView().getViewContext(), loadedRecords.first, loadedRecords.second);
            })
            .distinctUntilChanged()
            .compose(bindViewIoToMainComposer())
            .subscribe(this::cardsLoaded, throwable -> Timber.e(throwable, ""));
   }

   @SuppressWarnings("ConstantConditions")
   private void cardsLoaded(ArrayList<BaseViewModel> cardModels) {
      getView().setCardsCount(null != this.records ? this.records.size() : 0);
      getView().showRecordsInfo(cardModels);
   }

   @SuppressWarnings("ConstantConditions")
   private void observeSmartCardSync() {
      final OperationView<SyncSmartCardCommand> operationView = getView().provideOperationSyncSmartCard();
      getSmartCardInteractor().smartCardSyncPipe()
            .observeWithReplay()
            .compose(new ActionPipeCacheWiper<>(getSmartCardInteractor().smartCardSyncPipe()))
            .compose(bindViewIoToMainComposer())
            .doOnCompleted(() -> {
               if (operationView.isProgressVisible()) operationView.hideProgress();
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
      getNavigator().goStartFirmwareInstallCardList();
   }

   @SuppressWarnings("ConstantConditions")
   private void assertSmartCardConnected(Action0 onConnected) {
      getSmartCardInteractor().deviceStatePipe()
            .createObservable(DeviceStateCommand.fetch())
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<DeviceStateCommand>()
                  .onSuccess(command -> {
                     if (command.getResult().connectionStatus().isConnected()) onConnected.call();
                     else getView().showSCNonConnectionDialog();
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

   @Override
   public void syncPayments() {
      recordInteractor.syncRecordOnNewDevicePipe().send(new SyncRecordOnNewDeviceCommand());
   }

   @Override
   public void goToFactoryReset() {
      checkPinDelegate.getFactoryResetDelegate().setupDelegate(getView());
   }
}
