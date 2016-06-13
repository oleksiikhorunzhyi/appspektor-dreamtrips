package com.messenger.ui.module;

import android.os.Parcelable;

public interface ModuleStatefulView<P> extends ModuleView<P> {

    void onSaveInstanceState(Parcelable parcelable);

    void onRestoreInstanceState(Parcelable parcelable);
}
