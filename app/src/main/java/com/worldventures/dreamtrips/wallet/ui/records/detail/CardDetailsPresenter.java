package com.worldventures.dreamtrips.wallet.ui.records.detail;

import android.content.Context;
import android.os.Parcelable;
import android.text.TextUtils;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.CardDetailsAction;
import com.worldventures.dreamtrips.wallet.analytics.ChangeDefaultCardAction;
import com.worldventures.dreamtrips.wallet.analytics.PaycardAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfoWithLocale;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableAddressInfoWithLocale;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.FetchDefaultRecordCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetDefaultCardOnDeviceCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetPaymentCardAction;
import com.worldventures.dreamtrips.wallet.service.command.UpdateRecordCommand;
import com.worldventures.dreamtrips.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.records.address.EditBillingAddressPath;
import com.worldventures.dreamtrips.wallet.util.WalletRecordUtil;
import com.worldventures.dreamtrips.wallet.util.WalletValidateHelper;

import java.util.Objects;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.smartcard.action.records.DeleteRecordAction;
import rx.Observable;
import timber.log.Timber;

import static java.lang.Integer.valueOf;

public class CardDetailsPresenter extends WalletPresenter<CardDetailsPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;

   private final Record record;
   private Record defaultRecord;
   private boolean cardDeleted = false;

   public CardDetailsPresenter(Context context, Injector injector, Record record) {
      super(context, injector);
      this.record = record;
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      trackScreen();
      Screen view = getView();

      view.showWalletRecord(record);
      view.showDefaultAddress(obtainAddressWithCountry());

      connectToDefaultCardPipe();
      connectToDeleteCardPipe();
      connectToSetDefaultCardIdPipe();
      connectSetPaymentCardPipe();
      observeCardNickName();
      observeDefaultCard();
   }

   @Override
   public void detachView(boolean retainInstance) {
      if (!cardDeleted && !TextUtils.equals(getView().getUpdateNickname(), record.nickName())) {
         nicknameUpdated(getView().getUpdateNickname());
      }

      super.detachView(retainInstance);
   }


   private void trackScreen() {
      analyticsInteractor.paycardAnalyticsCommandPipe()
            .send(new PaycardAnalyticsCommand(new CardDetailsAction(record.nickName()), record));
   }

   private void connectToDefaultCardPipe() {
      smartCardInteractor.fetchDefaultCardCommandPipe()
            .createObservableResult(new FetchDefaultRecordCommand())
            .map(Command::getResult)
            .compose(bindViewIoToMainComposer())
            .subscribe(defaultBankCard -> {
               this.defaultRecord = defaultBankCard;
               getView().setDefaultCardCondition(Objects.equals(defaultBankCard, record));
            }, throwable -> Timber.e(throwable, ""));
   }

   private void connectToDeleteCardPipe() {
      smartCardInteractor.deleteRecordPipe()
            .observeWithReplay()
            .filter(state -> valueOf(record.id()).equals(state.action.recordId))
            .compose(bindViewIoToMainComposer())
            .compose(new ActionPipeCacheWiper<>(smartCardInteractor.deleteRecordPipe()))
            .subscribe(OperationActionStateSubscriberWrapper.<DeleteRecordAction>forView(getView().provideOperationDelegate())
                  .onSuccess(deleteRecordAction -> {
                     cardDeleted = true;
                     navigator.goBack();
                  })
                  .onFail(getContext().getString(R.string.error_something_went_wrong))
                  .wrap());
   }

   private void connectToSetDefaultCardIdPipe() {
      smartCardInteractor.setDefaultCardOnDeviceCommandPipe().clearReplays();
      smartCardInteractor.setDefaultCardOnDeviceCommandPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .compose(new ActionPipeCacheWiper<>(smartCardInteractor.setDefaultCardOnDeviceCommandPipe()))
            .subscribe(OperationActionStateSubscriberWrapper.<SetDefaultCardOnDeviceCommand>forView(getView().provideOperationDelegate())
                  .onFail(getContext().getString(R.string.error_something_went_wrong))
                  .wrap());
   }

   private void connectSetPaymentCardPipe() {
      smartCardInteractor.setPaymentCardActionActionPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .compose(new ActionPipeCacheWiper<>(smartCardInteractor.setPaymentCardActionActionPipe()))
            .subscribe(OperationActionStateSubscriberWrapper.<SetPaymentCardAction>forView(getView().provideOperationDelegate())
                  .onFail(getContext().getString(R.string.error_something_went_wrong))
                  //TODO: use card name for this message
                  .onSuccess(action -> getView().showCardIsReadyDialog(record.nickName()))
                  .wrap());
   }

   private void observeCardNickName() {
      final Screen view = getView();
      view.getCardNicknameObservable()
            .compose(bindView())
            .filter(cardName -> !TextUtils.isEmpty(cardName))
            .subscribe(cardName -> {
               if (WalletValidateHelper.validateCardName(cardName)) {
                  getView().hideCardNameError();
                  getView().setCardNickname(cardName);
               } else {
                  getView().showCardNameError();
               }
            });
   }

   private void observeDefaultCard() {
      getView().setAsDefaultPaymentCardCondition()
            .compose(bindView())
            .subscribe(this::onSetAsDefaultCard);
   }

   private AddressInfoWithLocale obtainAddressWithCountry() {
      return ImmutableAddressInfoWithLocale.builder()
            .addressInfo(record.addressInfo())
            .locale(LocaleHelper.getDefaultLocale())
            .build();
   }

   void onDeleteCardClick() {
      smartCardInteractor.deviceStatePipe()
            .createObservableResult(DeviceStateCommand.fetch())
            .compose(bindViewIoToMainComposer())
            .subscribe(command -> {
               if (command.getResult().connectionStatus().isConnected()) {
                  getView().showDeleteCardDialog();
               } else {
                  getView().showSCNonConnectionDialog();
               }
            }, throwable -> Timber.e(throwable, ""));
   }

   void editAddress() {
      smartCardInteractor.deviceStatePipe()
            .createObservableResult(DeviceStateCommand.fetch())
            .compose(bindViewIoToMainComposer())
            .subscribe(command -> {
               if (command.getResult().connectionStatus().isConnected()) {
                  navigator.go(new EditBillingAddressPath(record));
               } else {
                  getView().showConnectionErrorDialog();
               }
            }, throwable -> Timber.e(throwable, ""));
   }

   void payThisCard() {
      smartCardInteractor.deviceStatePipe()
            .createObservableResult(DeviceStateCommand.fetch())
            .map(Command::getResult)
            .compose(bindViewIoToMainComposer())
            .subscribe(smartCard -> {
               if (smartCard.connectionStatus().isConnected()) {
                  smartCardInteractor.setPaymentCardActionActionPipe().send(new SetPaymentCardAction(record));
               } else {
                  getView().showSCNonConnectionDialog();
               }
            }, throwable -> Timber.e(throwable, ""));
   }

   void onCardIsReadyDialogShown() {
      navigator.goBack();
   }

   void onDeleteCardConfirmed() {
      smartCardInteractor.deleteRecordPipe().send(new DeleteRecordAction(valueOf(record.id())));
   }

   private void executeSetDefaultCard(boolean setDefaultCard) {
      if (setDefaultCard) {
         if (WalletRecordUtil.isRealRecord(defaultRecord)) {
            getView().showDefaultCardDialog(defaultRecord);
         } else {
            trackSetAsDefault();
            smartCardInteractor.setDefaultCardOnDeviceCommandPipe()
                  .send(SetDefaultCardOnDeviceCommand.setAsDefault(record.id()));
         }
      } else {
         if (Objects.equals(defaultRecord, record)) {
            smartCardInteractor.setDefaultCardOnDeviceCommandPipe()
                  .send(SetDefaultCardOnDeviceCommand.unsetDefaultCard());
         }
      }
   }

   void defaultCardDialogConfirmed(boolean confirmed) {
      if (!confirmed) {
         getView().setDefaultCardCondition(Objects.equals(defaultRecord, record));
      } else {
         trackSetAsDefault();
         smartCardInteractor.setDefaultCardOnDeviceCommandPipe()
               .send(SetDefaultCardOnDeviceCommand.setAsDefault(record.id()));
      }
   }

   private void trackSetAsDefault() {
      analyticsInteractor.walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(ChangeDefaultCardAction.forBankCard(record)));
   }

   public void goBack() {
      navigator.goBack();
   }

   private void nicknameUpdated(String nickName) {
      smartCardInteractor.updateRecordPipe().send(UpdateRecordCommand.updateNickName(record, nickName));
   }

   private void onSetAsDefaultCard(boolean setDefaultCard) {
      smartCardInteractor.deviceStatePipe()
            .createObservableResult(DeviceStateCommand.fetch())
            .compose(bindViewIoToMainComposer())
            .subscribe(command -> {
               if (command.getResult().connectionStatus().isConnected()) {
                  executeSetDefaultCard(setDefaultCard);
               } else {
                  getView().setDefaultCardCondition(Objects.equals(defaultRecord, record));
                  getView().showSCNonConnectionDialog();
               }
            }, throwable -> Timber.e(throwable, ""));
   }

   public interface Screen extends WalletScreen {
      void showWalletRecord(Record record);

      void showDefaultAddress(AddressInfoWithLocale addressInfoWithLocale);

      void showDefaultCardDialog(Record defaultRecord);

      void showDeleteCardDialog();

      void showConnectionErrorDialog();

      void setDefaultCardCondition(boolean defaultCard);

      void showCardIsReadyDialog(String cardName);

      void setCardNickname(String cardNickname);

      Observable<Boolean> setAsDefaultPaymentCardCondition();

      Observable<String> getCardNicknameObservable();

      String getUpdateNickname();

      void showSCNonConnectionDialog();

      void showCardNameError();

      void hideCardNameError();
   }
}
