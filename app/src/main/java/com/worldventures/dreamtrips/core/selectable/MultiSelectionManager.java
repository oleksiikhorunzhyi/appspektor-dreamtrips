package com.worldventures.dreamtrips.core.selectable;

import android.support.v7.widget.RecyclerView;

import com.innahema.collections.query.queriables.Queryable;

import java.util.List;

public class MultiSelectionManager extends SimpleSelectionManager {

    public MultiSelectionManager(RecyclerView recyclerView) {
        super(recyclerView);
    }

    @Override
    protected void toggle(int position) {
        selectableWrapperAdapter.toggleSelection(position);
    }

    @Override
    public void toggleSelection(int position) {
        super.toggleSelection(position);
        selectableWrapperAdapter.notifyDataSetChanged();
    }

    public List<Integer> getSelectedPositions() {
        return selectableWrapperAdapter.getSelectedItems();
    }

    public void setSelectedPositions(List<Integer> selectionPositions) {
        for (Integer position : selectionPositions) {
            toggle(position);
        }
        selectableWrapperAdapter.notifyDataSetChanged();
    }

    public void toggleSelectionForAll(boolean selectAll) {
        selectableWrapperAdapter.clearSelections();
        if (!selectAll) return;
        //
        for (int i = 0; i < selectableWrapperAdapter.getItemCount(); i++)
            selectableWrapperAdapter.toggleSelection(i);
        //
        selectableWrapperAdapter.notifyDataSetChanged();
    }
}
