package com.messenger.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.implments.SwipeItemRecyclerMangerImpl;
import com.daimajia.swipe.interfaces.SwipeAdapterInterface;
import com.daimajia.swipe.interfaces.SwipeItemMangerInterface;
import com.daimajia.swipe.util.Attributes;
import com.messenger.messengerservers.entities.User;
import com.messenger.ui.adapter.holder.BaseViewHolder;
import com.messenger.ui.adapter.holder.ContactViewHolder;
import com.worldventures.dreamtrips.R;
import java.util.List;

import butterknife.InjectView;

public class ActionButtonsContactsCursorAdapter
        extends ContactCursorAdapter implements SwipeItemMangerInterface, SwipeAdapterInterface {

    public interface RowButtonsActionListener {
        void onDeleteUserButtonPressed(User user);
    }

    public static class ActionButtonsViewHolder extends ContactViewHolder {
        @InjectView(R.id.swipe)
        SwipeLayout swipeLayout;
        @InjectView(R.id.swipe_layout_button_delete)
        View deleteButton;

        public ActionButtonsViewHolder(View itemView) {
            super(itemView);
        }
    }

    public SwipeItemRecyclerMangerImpl mItemManger = new SwipeItemRecyclerMangerImpl(this);

    private RowButtonsActionListener rowButtonsActionListener;

    public ActionButtonsContactsCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    @Override
    public BaseViewHolder createContactViewHolder(ViewGroup parent, int viewType) {
        View itemRow = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_contact_swipe,
                parent, false);
        return new ActionButtonsViewHolder(itemRow);
    }

    @Override
    protected void onBindUserHolder(ContactViewHolder h, Cursor cursor, final User user) {
        super.onBindUserHolder(h, cursor, user);
        ActionButtonsViewHolder holder = (ActionButtonsViewHolder)h;
        holder.getTickImageView().setVisibility(View.GONE);
        mItemManger.bindView(holder.itemView, cursor.getPosition());
        holder.deleteButton.setOnClickListener(view -> {
            if (rowButtonsActionListener != null) {
                rowButtonsActionListener.onDeleteUserButtonPressed(user);
            }
        });
    }

    public void setRowButtonsActionListener(RowButtonsActionListener rowButtonsActionListener) {
        this.rowButtonsActionListener = rowButtonsActionListener;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public void openItem(int position) {
        mItemManger.openItem(position);
    }

    @Override
    public void closeItem(int position) {
        mItemManger.closeItem(position);
    }

    @Override
    public void closeAllExcept(SwipeLayout layout) {
        mItemManger.closeAllExcept(layout);
    }

    @Override
    public void closeAllItems() {
        mItemManger.closeAllItems();
    }

    @Override
    public List<Integer> getOpenItems() {
        return mItemManger.getOpenItems();
    }

    @Override
    public List<SwipeLayout> getOpenLayouts() {
        return mItemManger.getOpenLayouts();
    }

    @Override
    public void removeShownLayouts(SwipeLayout layout) {
        mItemManger.removeShownLayouts(layout);
    }

    @Override
    public boolean isOpen(int position) {
        return mItemManger.isOpen(position);
    }

    @Override
    public Attributes.Mode getMode() {
        return mItemManger.getMode();
    }

    @Override
    public void setMode(Attributes.Mode mode) {
        mItemManger.setMode(mode);
    }
}
