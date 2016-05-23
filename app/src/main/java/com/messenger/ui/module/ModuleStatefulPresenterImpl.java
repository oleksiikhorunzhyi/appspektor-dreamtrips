package com.messenger.ui.module;

import android.os.Bundle;
import android.os.Parcelable;

import icepick.Icepick;
import icepick.State;

public abstract class ModuleStatefulPresenterImpl<V extends ModuleView, S extends Parcelable>
        extends ModulePresenterImpl<V> implements ModuleStatefulPresenter<V, S> {

    @State S state;

    public ModuleStatefulPresenterImpl(V view) {
        super(view);
        this.state = createNewState();
    }

    @Override
    public void onSaveInstanceState(Parcelable parcelable) {
        checkParcelableType(parcelable);
        Icepick.saveInstanceState(this, (Bundle) parcelable);
    }

    @Override
    public void onRestoreInstanceState(Parcelable parcelable) {
        checkParcelableType(parcelable);
        Icepick.restoreInstanceState(this, (Bundle) parcelable);
        applyState(state);
    }

    private void checkParcelableType(Parcelable parcelable) {
        if (! (parcelable instanceof Bundle)) {
            throw new IllegalStateException("State Parcelable must be instance of Bundle");
        }
    }

    @Override
    public S getState() {
        return state;
    }

    protected void setState(S state) {
        this.state = state;
    }

    protected void resetState() {
        setState(createNewState());
    }

    protected abstract S createNewState();
}
