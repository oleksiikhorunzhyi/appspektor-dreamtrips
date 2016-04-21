package com.techery.spares.adapter.expandable;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.module.Injector;
import com.techery.spares.ui.view.cell.AbstractCell;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BaseExpandableAdapter<T> extends BaseArrayListAdapter<T> implements GroupCell.GroupExpandListener {

    protected WeakReference<RecyclerView> recyclerViewReference;
    protected Set<Integer> expandedSet = new HashSet<>(); // TODO : think about autoboxing

    public BaseExpandableAdapter(Context context, Injector injector) {
        super(context, injector);
    }

    @Override
    public AbstractCell<T> onCreateViewHolder(ViewGroup parent, int viewType) {
        AbstractCell cell = super.onCreateViewHolder(parent, viewType);
        if (cell instanceof GroupCell) ((GroupCell) cell).setGroupExpandListener(this);
        return cell;
    }

    @Override
    public void onBindViewHolder(AbstractCell cell, int position) {
        prepareBind(cell, position);
        super.onBindViewHolder(cell, position);
    }

    @Override
    public void onGroupExpanded(int position) {
        if (recyclerViewReference.get() == null) return;
        if (!expandedSet.contains(position)) expandedSet.add(position);
        //
        AbstractCell cell = (AbstractCell) recyclerViewReference.get().findViewHolderForAdapterPosition(position);

        if (cell instanceof GroupCell) expandView((GroupCell) cell, position);
    }

    @Override
    public void onGroupCollapsed(int position) {
        if (recyclerViewReference.get() == null) return;
        if (expandedSet.contains(position)) expandedSet.remove(position);
        //
        AbstractCell cell = (AbstractCell) recyclerViewReference.get().findViewHolderForAdapterPosition(position);

        if (cell instanceof GroupCell) collapseView((GroupCell) cell, position);
    }

    public void expandView(GroupCell cell, int position) {
        final List expanded = cell.getChildListCell();
        if (expanded.isEmpty()) return;
        //
        recalculateExpandedSet(position, expanded.size(), true);
        notifyGroupUpdate(cell, position, true);
        notifyRangeInserted(position, expanded);
    }

    public void collapseView(GroupCell cell, int position) {
        final List collapses = cell.getChildListCell();
        if (collapses.isEmpty()) return;
        //
        recalculateExpandedSet(position, collapses.size(), false);
        notifyGroupUpdate(cell, position, false);
        notifyRangeRemoved(position, collapses);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recyclerViewReference = new WeakReference<>(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        this.recyclerViewReference.clear();
        this.recyclerViewReference = null;
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public void setItems(List<T> items) {
        expandedSet.clear();
        super.setItems(items);
    }

    @Override
    public void updateItem(T changedItem) {
        super.updateItem(changedItem);
    }

    protected void recalculateExpandedSet(int position, int count, boolean expand) {
        Set<Integer> newSet = new HashSet<>();

        for (Integer item : expandedSet) {
            if (expand) newSet.add(item > position ? item + count : item);
            else newSet.add(item < position ? item : item - count);
        }

        expandedSet.clear();
        expandedSet.addAll(newSet);
    }

    protected void notifyRangeRemoved(int location, List<T> childList) {
        if (childList.isEmpty()) return;

        for (int index = 0; index < childList.size(); index++) {
            remove(location + 1);
        }

        notifyItemRangeRemoved(location + 1, childList.size());
    }

    protected void notifyRangeInserted(int location, List<T> childList) {
        if (childList.isEmpty()) return;

        for (int index = 0; index < childList.size(); index++) {
            addItem(location + index + 1, childList.get(index));
        }

        notifyItemRangeInserted(location + 1, childList.size());
    }

    protected void notifyGroupUpdate(GroupCell cell, int position, boolean expand) {
        notifyItemChanged(position); // can use notifyItemChanged(position) with animation
    }

    protected void prepareBind(AbstractCell cell, int position) {
        if (cell instanceof GroupCell) {
            boolean isExpanded = expandedSet.contains(position);
            ((GroupCell) cell).setExpanded(isExpanded);
        }
    }
}
