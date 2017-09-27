package com.worldventures.dreamtrips.wallet.ui.records.swiping;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhoto;
import com.worldventures.dreamtrips.wallet.service.command.http.CreateRecordCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;

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
