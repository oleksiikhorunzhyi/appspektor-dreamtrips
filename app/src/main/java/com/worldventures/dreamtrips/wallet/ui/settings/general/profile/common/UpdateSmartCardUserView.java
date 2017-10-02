package com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common;

import com.worldventures.dreamtrips.wallet.service.command.profile.RetryHttpUploadUpdatingCommand;
import com.worldventures.dreamtrips.wallet.service.command.profile.UpdateSmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.RxLifecycleView;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface UpdateSmartCardUserView extends RxLifecycleView {

   OperationView<UpdateSmartCardUserCommand> provideUpdateSmartCardOperation(WalletProfileDelegate delegate);

   OperationView<RetryHttpUploadUpdatingCommand> provideHttpUploadOperation(WalletProfileDelegate delegate);

}