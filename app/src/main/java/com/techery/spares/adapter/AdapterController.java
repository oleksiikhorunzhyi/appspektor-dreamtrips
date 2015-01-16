package com.techery.spares.adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.techery.spares.ui.view.cell.BaseCell;

import de.greenrobot.event.EventBus;

public class AdapterController {
    private final SparseArray<Bundle> states;
    private final Context context;

    private EventBus eventBus;
    private BaseCell.CellBuilder cellBuilder;

    public AdapterController(Context context, BaseCell.CellBuilder cellBuilder) {
        this(context, cellBuilder, null);
    }

    public AdapterController(Context context, BaseCell.CellBuilder cellBuilder, EventBus bus) {
        this.cellBuilder = cellBuilder;
        this.context = context;
        this.states = new SparseArray<>();
        this.eventBus = bus;
    }

    public void reset() {
        this.states.clear();
    }

    public android.view.View bindView(DataListAdapter adapter, int position, View view) {

        BaseCell cell = (BaseCell) view;

        Object item = adapter.getItem(position);

        Bundle b = new Bundle();

        cell.saveState(b);

        saveState(b, cell.getLastPosition());

        cell.setLastPosition(position);
        cell.setEventBus(getEventBus());
        cell.prepareForReuse();

        cell.fillWithItem(item);

        Bundle state = getState(position);

        if (state != null) {
            cell.restoreState(getState(position));
        }

        return (android.view.View) cell;
    }

    public View newView(DataListAdapter adapter, int position, ViewGroup parent) {
        return (View) this.cellBuilder.build(getContext(), adapter.getItem(position));
    }

    private Bundle getState(int position) {
        return this.states.get(position);
    }

    private void saveState(Bundle state, int position) {
        this.states.put(position, state);
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public void setEventBus(EventBus bus) {
        this.eventBus = bus;
    }

    public Context getContext() {
        return context;
    }

    public BaseCell.CellBuilder getCellBuilder() {
        return cellBuilder;
    }

    public void setCellBuilder(BaseCell.CellBuilder cellBuilder) {
        this.cellBuilder = cellBuilder;
    }

}
