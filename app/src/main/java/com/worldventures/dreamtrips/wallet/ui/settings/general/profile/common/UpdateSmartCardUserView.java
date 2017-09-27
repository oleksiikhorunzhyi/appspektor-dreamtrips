package com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common;

import android.view.View;

import com.worldventures.dreamtrips.wallet.service.command.profile.RetryHttpUploadUpdatingCommand;
import com.worldventures.dreamtrips.wallet.service.command.profile.UpdateSmartCardUserCommand;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface UpdateSmartCardUserView {

   View getView();

   OperationView<UpdateSmartCardUserCommand> provideUpdateSmartCardOperation(WalletProfileDelegate delegate);

   OperationView<RetryHttpUploadUpdatingCommand> provideHttpUploadOperation(WalletProfileDelegate delegate);

}