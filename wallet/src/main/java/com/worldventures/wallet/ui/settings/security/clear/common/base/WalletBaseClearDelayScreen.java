package com.worldventures.wallet.ui.settings.security.clear.common.base;

import com.worldventures.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.wallet.ui.settings.security.clear.common.items.SettingsRadioModel;

import java.util.List;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface WalletBaseClearDelayScreen extends WalletScreen {

   void setItems(List<SettingsRadioModel> items);

   void setSelectedPosition(int position);

   int getSelectedPosition();

   void notifyDataIsSaved();

   <T> OperationView<T> provideOperationView();
}
