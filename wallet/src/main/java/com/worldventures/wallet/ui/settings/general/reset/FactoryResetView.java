package com.worldventures.wallet.ui.settings.general.reset;


import android.view.View;

import com.worldventures.wallet.service.command.reset.ResetSmartCardCommand;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface FactoryResetView {

   OperationView<ResetSmartCardCommand> provideResetOperationView(FactoryResetDelegate factoryResetDelegate);

   View getView();

}
