package com.worldventures.dreamtrips.wallet.ui.records.detail;

import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.service.command.SetDefaultCardOnDeviceCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetPaymentCardAction;
import com.worldventures.dreamtrips.wallet.service.command.record.DeleteRecordCommand;
import com.worldventures.dreamtrips.wallet.service.command.record.UpdateRecordCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.model.TransitionModel;

import io.techery.janet.operationsubscriber.view.OperationView;
import rx.Observable;

public interface CardDetailsScreen extends WalletScreen {

   void showWalletRecord(Record record);

   void showDefaultCardDialog(Record defaultRecord);

   void showDeleteCardDialog();

   void showNetworkConnectionErrorDialog();

   void setDefaultCardCondition(boolean defaultCard);

   void showCardIsReadyDialog(String cardName);

   void setCardNickname(String cardNickname);

   Observable<Boolean> setAsDefaultPaymentCardCondition();

   Observable<String> getCardNicknameObservable();

   String getUpdateNickname();

   void showSCNonConnectionDialog();

   void showCardNameError();

   void hideCardNameError();

   OperationView<UpdateRecordCommand> provideOperationSaveCardData();

   void notifyCardDataIsSaved();

   OperationView<DeleteRecordCommand> provideOperationDeleteRecord();

   OperationView<SetDefaultCardOnDeviceCommand> provideOperationSetDefaultOnDevice();

   OperationView<SetPaymentCardAction> provideOperationSetPaymentCardAction();

   void animateCard(TransitionModel transitionModel);

   TransitionModel getTransitionModel();

   Record getRecord();
}
