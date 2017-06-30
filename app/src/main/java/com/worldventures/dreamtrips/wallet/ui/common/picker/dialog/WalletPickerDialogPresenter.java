package com.worldventures.dreamtrips.wallet.ui.common.picker.dialog;


import android.view.KeyEvent;

import com.worldventures.dreamtrips.modules.common.model.MediaPickerAttachment;

public interface WalletPickerDialogPresenter<V extends WalletPickerDialogView> {

   MediaPickerAttachment providePickerResult();

   boolean handleKeyPress(int keyCode, KeyEvent event);

   void performCleanUp();

   void attachView(V view);

   void detachView(boolean b);
}
