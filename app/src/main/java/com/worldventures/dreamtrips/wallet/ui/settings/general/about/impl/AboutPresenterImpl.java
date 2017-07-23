package com.worldventures.dreamtrips.wallet.ui.settings.general.about.impl;


import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.settings.AboutAnalyticsAction;
import com.worldventures.dreamtrips.wallet.domain.WalletConstants;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.service.RecordInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.RecordListCommand;
import com.worldventures.dreamtrips.wallet.service.command.device.SmartCardFirmwareCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.general.about.AboutPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.about.AboutScreen;

import java.util.List;

import io.techery.janet.Command;

public class AboutPresenterImpl extends WalletPresenterImpl<AboutScreen> implements AboutPresenter {

   private final RecordInteractor recordInteractor;
   private final AnalyticsInteractor analyticsInteractor;

   public AboutPresenterImpl(Navigator navigator, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService, RecordInteractor recordInteractor, AnalyticsInteractor analyticsInteractor) {
      super(navigator, smartCardInteractor, networkService);
      this.recordInteractor = recordInteractor;
      this.analyticsInteractor = analyticsInteractor;
   }

   @Override
   public void attachView(AboutScreen view) {
      super.attachView(view);
      observeSmartCard();
      observePayCardsInfo();

      getSmartCardInteractor().activeSmartCardPipe().send(new ActiveSmartCardCommand());
      getSmartCardInteractor().smartCardFirmwarePipe().send(SmartCardFirmwareCommand.fetch());
      recordInteractor.cardsListPipe().send(RecordListCommand.fetch());

      trackScreen();
   }

   private void observeSmartCard() {
      getSmartCardInteractor().activeSmartCardPipe()
            .observeSuccessWithReplay()
            .map(Command::getResult)
            .compose(bindViewIoToMainComposer())
            .subscribe(smartCard -> getView().setSmartCardId(smartCard.smartCardId()));
      getSmartCardInteractor().smartCardFirmwarePipe()
            .observeSuccessWithReplay()
            .map(Command::getResult)
            .compose(bindViewIoToMainComposer())
            .subscribe(getView()::setSmartCardFirmware);
      getSmartCardInteractor().smartCardUserPipe()
            .observeSuccessWithReplay()
            .map(Command::getResult)
            .compose(bindViewIoToMainComposer())
            .subscribe(getView()::setSmartCardUser);
   }

   private void observePayCardsInfo() {
      recordInteractor.cardsListPipe()
            .observeSuccessWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(command -> bindCardList(command.getResult()));
   }

   private void trackScreen() {
      final WalletAnalyticsCommand analyticsCommand = new WalletAnalyticsCommand(new AboutAnalyticsAction());
      analyticsInteractor.walletAnalyticsCommandPipe().send(analyticsCommand);
   }

   @SuppressWarnings("ConstantConditions")
   private void bindCardList(List<Record> records) {
      getView().onProvidePayCardInfo(records.size(), WalletConstants.MAX_CARD_LIMIT - records.size());
   }

   @Override
   public void goBack() {
      getNavigator().goBack();
   }
}
