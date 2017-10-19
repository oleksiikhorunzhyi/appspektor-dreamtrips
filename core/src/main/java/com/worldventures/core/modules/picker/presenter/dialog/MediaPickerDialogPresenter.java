package com.worldventures.core.modules.picker.presenter.dialog;

import android.view.KeyEvent;

import com.worldventures.core.modules.picker.model.MediaPickerAttachment;
import com.worldventures.core.modules.picker.view.dialog.MediaPickerDialogView;

public interface MediaPickerDialogPresenter<V extends MediaPickerDialogView> {

   MediaPickerAttachment providePickerResult();

   boolean handleKeyPress(int keyCode, KeyEvent event);

   void attachView(V view);

   void detachView(boolean b);
}
