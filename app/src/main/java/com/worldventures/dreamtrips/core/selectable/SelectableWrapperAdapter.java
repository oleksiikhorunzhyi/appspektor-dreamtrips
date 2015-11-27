package com.worldventures.dreamtrips.core.selectable;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.ViewGroup;

import com.h6ah4i.android.widget.advrecyclerview.utils.BaseWrapperAdapter;

import java.util.ArrayList;
import java.util.List;

public class SelectableWrapperAdapter<VH extends RecyclerView.ViewHolder> extends BaseWrapperAdapter<VH> {

    private SparseBooleanArray selectedItems;
    private SelectableDelegateWrapper selectableDelegateWrapper;

    public SelectableWrapperAdapter(RecyclerView.Adapter<VH> adapter, SelectionManager selectionManager) {
        super(adapter);
        selectedItems = new SparseBooleanArray();
        this.selectableDelegateWrapper = new SelectableDelegateWrapper(selectionManager);
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        VH holder = super.onCreateViewHolder(parent, viewType);
        if (holder instanceof SelectableCell) {
            ((SelectableCell) holder).setSelectableDelegate(selectableDelegateWrapper);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        super.onBindViewHolder(holder, position);
    }

    void toggleSelection(int pos) {
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
        } else {
            selectedItems.put(pos, true);
        }
    }

    void clearSelections() {
        selectedItems.clear();
    }

    int getSelectedItemCount() {
        return selectedItems.size();
    }

    List<Integer> getSelectedItems() {
        List<Integer> items =
                new ArrayList<Integer>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    private static class SelectableDelegateWrapper implements SelectableDelegate {

        private SelectionManager selectionManager;

        public SelectableDelegateWrapper(SelectionManager selectionManager) {
            this.selectionManager = selectionManager;
        }

        @Override
        public void toggleSelection(int position) {
            selectionManager.toggleSelection(position);
        }

        @Override
        public boolean isSelected(int position) {
            return selectionManager.isSelected(position);
        }
    }

}
