package com.worldventures.dreamtrips.core.selectable;

import android.support.v7.widget.RecyclerView;

public class SimpleSelectionManager implements SelectionManager {

    protected SelectableWrapperAdapter selectableWrapperAdapter;
    protected boolean enabled;

    protected RecyclerView recyclerView;

    public SimpleSelectionManager(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @Override
    public void toggleSelection(int position) {
        if (!enabled) return;
        //
        toggle(position);
    }

    protected void toggle(int position) {
        selectableWrapperAdapter.toggleSelection(position);
        selectableWrapperAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean isSelected(int position) {
        return enabled && selectableWrapperAdapter.getSelectedItems().contains(position);
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    @SuppressWarnings("unchecked")
    public RecyclerView.Adapter provideWrappedAdapter(RecyclerView.Adapter adapter) {
        selectableWrapperAdapter = new SelectableWrapperAdapter(adapter, this);
        return selectableWrapperAdapter;
    }

    @Override
    public void release() {
        selectableWrapperAdapter = null;
        recyclerView = null;
    }
}
