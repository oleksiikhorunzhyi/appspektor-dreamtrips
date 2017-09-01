package com.worldventures.dreamtrips.modules.picker.presenter.dialog;

import android.view.KeyEvent;

import com.worldventures.dreamtrips.modules.common.model.MediaPickerAttachment;
import com.worldventures.dreamtrips.modules.picker.view.dialog.MediaPickerDialogView;

public interface MediaPickerDialogPresenter<V extends MediaPickerDialogView> {

   MediaPickerAttachment providePickerResult();

   boolean handleKeyPress(int keyCode, KeyEvent event);

   void attachView(V view);

   void detachView(boolean b);
}
