package com.worldventures.dreamtrips.wallet.ui.settings.general.about;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.settings.AboutAnalyticsAction;
import com.worldventures.dreamtrips.wallet.domain.WalletConstants;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.service.RecordInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.RecordListCommand;
import com.worldventures.dreamtrips.wallet.service.command.device.SmartCardFirmwareCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;

public class AboutPresenter extends WalletPresenter<AboutPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject RecordInteractor recordInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;

   public AboutPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      observeSmartCard();
      observePayCardsInfo();

      smartCardInteractor.activeSmartCardPipe().send(new ActiveSmartCardCommand());
      smartCardInteractor.smartCardFirmwarePipe().send(SmartCardFirmwareCommand.fetch());
      recordInteractor.cardsListPipe().send(RecordListCommand.fetch());

      trackScreen();
   }

   private void observeSmartCard() {
      smartCardInteractor.activeSmartCardPipe()
            .observeSuccessWithReplay()
            .map(Command::getResult)
            .compose(bindViewIoToMainComposer())
            .subscribe(smartCard -> getView().setSmartCardId(smartCard.smartCardId()));
      smartCardInteractor.smartCardFirmwarePipe()
            .observeSuccessWithReplay()
            .map(Command::getResult)
            .compose(bindViewIoToMainComposer())
            .subscribe(getView()::setSmartCardFirmware);
      smartCardInteractor.smartCardUserPipe()
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

   public void goBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {

      void onProvidePayCardInfo(final int cardStored, final int cardAvailable);

      void setSmartCardId(String smartCardId);

      void setSmartCardFirmware(SmartCardFirmware smartCardFirmware);

      void setSmartCardUser(SmartCardUser smartCardUser);
   }
}
