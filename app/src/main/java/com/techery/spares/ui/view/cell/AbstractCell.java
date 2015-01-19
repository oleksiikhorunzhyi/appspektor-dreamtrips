package com.techery.spares.ui.view.cell;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public abstract class AbstractCell<T> extends RecyclerView.ViewHolder implements BaseCell<T> {
    private T modelObject;

    private EventBus eventBus;

    public AbstractCell(View view) {
        super(view);
        ButterKnife.inject(this, view);

        initialUISetup();
    }

    protected void initialUISetup() {

    }

    @Override
    public void setEventBus(EventBus bus) {
        this.eventBus = bus;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public T getModelObject() {
        return modelObject;
    }

    public void setModelObject(T modelObject) {
        this.modelObject = modelObject;
    }

    @Override
    public void fillWithItem(T item) {
        setModelObject(item);
        syncUIStateWithModel();
    }

    public void saveState(Bundle b) {

    }

    public void restoreState(Bundle bundle) {

    }

    protected abstract void syncUIStateWithModel();
}
