package com.worldventures.wallet.ui.records.swiping;

import android.support.annotation.Nullable;

import com.worldventures.wallet.domain.entity.SmartCardUserPhoto;
import com.worldventures.wallet.service.command.http.CreateRecordCommand;
import com.worldventures.wallet.ui.common.base.screen.WalletScreen;

import io.techery.janet.operationsubscriber.view.OperationView;
import io.techery.janet.smartcard.action.charger.StartCardRecordingAction;

public interface WizardChargingScreen extends WalletScreen {

   void showSwipeError();

   void trySwipeAgain();

   void showSwipeSuccess();

   void userPhoto(@Nullable SmartCardUserPhoto photo);

   OperationView<CreateRecordCommand> provideOperationCreateRecord();

   OperationView<StartCardRecordingAction> provideOperationStartCardRecording();
}
