package com.worldventures.wallet.ui.wizard.input.helper;

import com.worldventures.wallet.service.command.SmartCardUserCommand;
import com.worldventures.wallet.service.command.http.GetSmartCardStatusCommand;
import com.worldventures.wallet.ui.common.base.screen.RxLifecycleView;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface InputDelegateView extends RxLifecycleView {

   OperationView<GetSmartCardStatusCommand> provideOperationFetchCardStatus();

   OperationView<SmartCardUserCommand> provideOperationFetchSmartCardUser();

   void showErrorCardIsAssignedDialog();
}
