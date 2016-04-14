package com.techery.spares.adapter.expandable;

import android.view.View;

import com.techery.spares.ui.view.cell.AbstractCell;

import butterknife.ButterKnife;

public abstract class GroupCell<G, C> extends AbstractCell<G> implements View.OnAttachStateChangeListener, View.OnClickListener, GroupListCell<C> {

    private boolean expanded;
    private boolean enabled;
    private GroupExpandListener groupExpandListener;

    public GroupCell(View view) {
        super(view);
        setEnabled(true);
        view.addOnAttachStateChangeListener(this);
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setGroupExpandListener(GroupExpandListener groupExpandListener) {
        this.groupExpandListener = groupExpandListener;
    }

    protected View getExpandItemView() {
        return getExpandItemViewId() > 0 ? ButterKnife.findById(itemView, getExpandItemViewId()) : itemView;
    }

    protected int getExpandItemViewId() {
        return 0;
    }

    @Override
    public void onViewAttachedToWindow(View v) {
        getExpandItemView().setOnClickListener(this);
    }

    @Override
    public void onViewDetachedFromWindow(View v) {
        getExpandItemView().setOnClickListener(this);
    }

    protected boolean shouldExpandViewWhenClick(View v) {
        return true;
    }

    @Override
    public void onClick(View v) {
        if (!shouldExpandViewWhenClick(v) || !isEnabled()) return;

        if (isExpanded()) collapseView();
        else expandView();
    }

    /**
     * Triggers expansion of the parent.
     */
    protected void expandView() {
        setExpanded(true);

        if (groupExpandListener != null) {
            groupExpandListener.onGroupExpanded(getAdapterPosition());
        }
    }

    /**
     * Triggers collapse of the parent.
     */
    protected void collapseView() {
        setExpanded(false);

        if (groupExpandListener != null) {
            groupExpandListener.onGroupCollapsed(getAdapterPosition());
        }
    }


    public interface GroupExpandListener {

        /**
         * Called when a list item is expanded.
         *
         * @param position The index of the item in the list being expanded
         */
        void onGroupExpanded(int position);

        /**
         * Called when a list item is collapsed.
         *
         * @param position The index of the item in the list being collapsed
         */
        void onGroupCollapsed(int position);
    }
}
