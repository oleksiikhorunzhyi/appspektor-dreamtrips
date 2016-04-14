package com.techery.spares.adapter.expandable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.ViewGroup;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.module.Injector;
import com.techery.spares.ui.view.cell.AbstractCell;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class BaseExpandableAdapter<T> extends BaseArrayListAdapter<T> implements GroupCell.GroupExpandListener {

    protected WeakReference<RecyclerView> recyclerViewReference;
    protected SparseBooleanArray expanded = new SparseBooleanArray();

    public BaseExpandableAdapter(Context context, Injector injector) {
        super(context, injector);
    }

    @Override
    public AbstractCell onCreateViewHolder(ViewGroup parent, int viewType) {
        AbstractCell cell = super.onCreateViewHolder(parent, viewType);
        if (cell instanceof GroupCell) ((GroupCell) cell).setGroupExpandListener(this);
        return cell;
    }

    @Override
    public void onBindViewHolder(AbstractCell cell, int position) {
        if (cell instanceof GroupCell) {
            boolean isExpanded = expanded.get(position, false);
            ((GroupCell) cell).setExpanded(isExpanded);
        }
        super.onBindViewHolder(cell, position);
    }

    @Override
    public void onGroupExpanded(int position) {
        if(recyclerViewReference.get() == null) return;
        expanded.put(position, true);

        AbstractCell cell = (AbstractCell) recyclerViewReference.get().findViewHolderForAdapterPosition(position);
        List expanded = getExpandedCollection(cell);
        addNotifyItems(position, expanded);
    }

    @Override
    public void onGroupCollapsed(int position) {
        if(recyclerViewReference.get() == null) return;
        expanded.put(position, false);

        AbstractCell cell = (AbstractCell) recyclerViewReference.get().findViewHolderForAdapterPosition(position);
        List collapses = getExpandedCollection(cell);
        removeNotifyItems(position, collapses);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recyclerViewReference = new WeakReference<>(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        recyclerViewReference.clear();
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @SuppressLint("Unchecked")
    private List getExpandedCollection(AbstractCell cell) {
        if (cell != null && cell instanceof GroupListCell) return ((GroupListCell) cell).getChildListCell();
        return new ArrayList<>();
    }

    protected void removeNotifyItems(int location, List<T> childList) {
        if (childList.isEmpty()) return;

        for (int index = 0; index < childList.size(); index++) {
            remove(location + 1);
        }
        notifyItemRangeRemoved(location, childList.size());
    }

    protected void addNotifyItems(int location, List<T> childList) {
        if (childList.isEmpty()) return;

        for (int index = 0; index < childList.size(); index++) {
            addItem(location + index + 1, childList.get(index));
        }
        notifyItemRangeInserted(location, childList.size());
    }
}
