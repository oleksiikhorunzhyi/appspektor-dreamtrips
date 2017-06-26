package com.worldventures.dreamtrips.wallet.ui.common.picker.dialog;


import android.view.KeyEvent;

import com.worldventures.dreamtrips.wallet.ui.common.picker.base.BasePickerViewModel;

import java.util.List;

public interface WalletPickerDialogPresenter<V extends WalletPickerDialogView> {

   List<BasePickerViewModel> providePickerResult();

   boolean handleKeyPress(int keyCode, KeyEvent event);

   void performCleanUp();

   void attachView(V view);

   void detachView(boolean b);
}
