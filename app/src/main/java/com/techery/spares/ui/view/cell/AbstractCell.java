package com.techery.spares.ui.view.cell;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public abstract class AbstractCell<T> extends LinearLayout implements BaseCell<T> {
    private T modelObject;

    private EventBus eventBus;
    private int lastPosition;

    public AbstractCell(Context context) {
        super(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        android.view.View view = inflater.inflate(getLayoutResourceId(), this);

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

    @Override
    public int getLastPosition() {
        return this.lastPosition;
    }

    @Override
    public void setLastPosition(int position) {
        this.lastPosition = position;
    }

    protected abstract int getLayoutResourceId();
    protected abstract void syncUIStateWithModel();
}
