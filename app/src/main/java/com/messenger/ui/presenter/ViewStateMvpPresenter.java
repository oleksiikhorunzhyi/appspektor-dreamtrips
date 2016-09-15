package com.messenger.ui.presenter;

import android.os.Bundle;
import android.os.Parcelable;

import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;

public interface ViewStateMvpPresenter<V extends MvpView, S extends Parcelable> extends MvpPresenter<V> {
   void onSaveInstanceState(Bundle bundle);
   void onRestoreInstanceState(Bundle instanceState);
   void onAttachedToWindow();
   void onDetachedFromWindow();
   void onVisibilityChanged(int visibility);
   S getViewState();
   void onNewViewState();
   void applyViewState();
}
