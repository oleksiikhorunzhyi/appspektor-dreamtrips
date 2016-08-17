package com.messenger.ui.module;

import android.os.Parcelable;

public interface ModuleStatefulPresenter<V extends ModuleView, S extends Parcelable> extends ModulePresenter<V> {

   void applyState(S state);

   S getState();

   void onSaveInstanceState(Parcelable parcelable);

   void onRestoreInstanceState(Parcelable parcelable);
}
