package com.worldventures.wallet.ui.settings.general.about.impl;


import com.worldventures.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.wallet.analytics.settings.AboutAnalyticsAction;
import com.worldventures.wallet.domain.WalletConstants;
import com.worldventures.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.wallet.domain.entity.record.Record;
import com.worldventures.wallet.service.RecordInteractor;
import com.worldventures.wallet.service.SmartCardInteractor;
import com.worldventures.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.wallet.service.command.AboutSmartCardDataCommand;
import com.worldventures.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.wallet.service.command.RecordListCommand;
import com.worldventures.wallet.service.command.device.SmartCardFirmwareCommand;
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.settings.general.about.AboutPresenter;
import com.worldventures.wallet.ui.settings.general.about.AboutScreen;

import java.util.List;

import io.techery.janet.Command;
import rx.android.schedulers.AndroidSchedulers;

public class AboutPresenterImpl extends WalletPresenterImpl<AboutScreen> implements AboutPresenter {

   private final SmartCardInteractor smartCardInteractor;
   private final RecordInteractor recordInteractor;
   private final WalletAnalyticsInteractor analyticsInteractor;

   public AboutPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         SmartCardInteractor smartCardInteractor, RecordInteractor recordInteractor,
         WalletAnalyticsInteractor analyticsInteractor) {
      super(navigator, deviceConnectionDelegate);
      this.smartCardInteractor = smartCardInteractor;
      this.recordInteractor = recordInteractor;
      this.analyticsInteractor = analyticsInteractor;
   }

   @Override
   public void attachView(AboutScreen view) {
      super.attachView(view);
      observeSmartCard();
      observePayCardsInfo();

      smartCardInteractor.activeSmartCardPipe().send(new ActiveSmartCardCommand());
      smartCardInteractor.smartCardFirmwarePipe().send(SmartCardFirmwareCommand.Companion.fetch());
      recordInteractor.cardsListPipe().send(RecordListCommand.Companion.fetch());

      trackScreen();
   }

   private void observeSmartCard() {
      smartCardInteractor.activeSmartCardPipe()
            .observeSuccessWithReplay()
            .map(Command::getResult)
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(smartCard -> getView().setSmartCardId(smartCard.getSmartCardId()));
      smartCardInteractor.smartCardFirmwarePipe()
            .observeSuccessWithReplay()
            .map(Command::getResult)
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::restoreCachedFWInfo);
      smartCardInteractor.smartCardUserPipe()
            .observeSuccessWithReplay()
            .map(Command::getResult)
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(getView()::setSmartCardUser);
      smartCardInteractor.aboutSmartCardDataCommandPipe()
            .observeSuccessWithReplay()
            .map(Command::getResult)
            .filter(aboutSmartCardData -> aboutSmartCardData != null)
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(aboutSmartCardData -> getView().setSmartCardFirmware(aboutSmartCardData.getSmartCardFirmware()));
   }

   private void restoreCachedFWInfo(SmartCardFirmware smartCardFirmware) {
      if (smartCardFirmware.isEmpty()) {
         smartCardInteractor.aboutSmartCardDataCommandPipe().send(AboutSmartCardDataCommand.fetch());
      } else {
         getView().setSmartCardFirmware(smartCardFirmware);
      }
   }

   private void observePayCardsInfo() {
      recordInteractor.cardsListPipe()
            .observeSuccessWithReplay()
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(command -> bindCardList(command.getResult()));
   }

   private void trackScreen() {
      final WalletAnalyticsCommand analyticsCommand = new WalletAnalyticsCommand(new AboutAnalyticsAction());
      analyticsInteractor.walletAnalyticsPipe().send(analyticsCommand);
   }

   @SuppressWarnings("ConstantConditions")
   private void bindCardList(List<? extends Record> records) {
      getView().onProvidePayCardInfo(records.size(), WalletConstants.MAX_CARD_LIMIT - records.size());
   }

   @Override
   public void goBack() {
      getNavigator().goBack();
   }
}
