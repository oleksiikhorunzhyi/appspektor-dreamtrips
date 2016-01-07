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
import com.messenger.messengerservers.entities.Participant;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.entities.User$Table;
import com.messenger.ui.adapter.holder.BaseViewHolder;
import com.messenger.ui.adapter.holder.ContactViewHolder;
import com.messenger.util.SwipeClickListener;
import com.worldventures.dreamtrips.R;
import java.util.List;

import butterknife.InjectView;

public class ActionButtonsContactsCursorAdapter
        extends ContactCursorAdapter implements SwipeItemMangerInterface, SwipeAdapterInterface {

    public final SwipeItemRecyclerMangerImpl mItemManger = new SwipeItemRecyclerMangerImpl(this);
    public final String userId;
    private final boolean owner;

    private DeleteRequestListener deleteRequestListener;
    private UserClickListener userClickListener;

    public ActionButtonsContactsCursorAdapter(Context context, User user, boolean owner) {
        super(context, null);
        this.userId = user.getId();
        this.owner = owner;
    }

    @Override
    public BaseViewHolder createContactViewHolder(ViewGroup parent, int viewType) {
        View itemRow = LayoutInflater
                .from(parent.getContext()).inflate(R.layout.list_item_contact_swipe, parent, false);
        return new ActionButtonsViewHolder(itemRow);
    }

    @Override
    public void onBindViewHolderCursor(BaseViewHolder h, Cursor cursor) {
        ContactViewHolder holder = (ContactViewHolder) h;
        Participant participant = Participant.from(cursor);

        onBindUserHolder(holder, cursor, participant.getUser());
        onBindUserHolder(holder, cursor, participant);
    }

    protected void onBindUserHolder(ContactViewHolder h, Cursor cursor, final Participant participant) {
        final User user = participant.getUser();
        ActionButtonsViewHolder holder = (ActionButtonsViewHolder)h;
        holder.getTickImageView().setVisibility(View.GONE);
        holder.swipeLayout.setSwipeEnabled(owner && !userId.equals(cursor.getString(cursor.getColumnIndex(User$Table._ID))));
        mItemManger.bindView(holder.itemView, cursor.getPosition());
        View.OnClickListener clickListener = view -> {
            if (userClickListener != null) {
                userClickListener.onUserClicked(user);
            }
        };
        holder.swipeLayout.addSwipeListener(new SwipeClickListener(holder.itemView, clickListener));
        holder.itemView.setOnClickListener(clickListener);

        holder.deleteButton.setOnClickListener(view -> {
            if (deleteRequestListener != null) {
                deleteRequestListener.onDeleteUserRequired(user);
            }
        });
    }

    public void setDeleteRequestListener(DeleteRequestListener deleteRequestListener) {
        this.deleteRequestListener = deleteRequestListener;
    }

    public void setUserClickListener(UserClickListener userClickListener) {
        this.userClickListener = userClickListener;
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

    public interface DeleteRequestListener {
        void onDeleteUserRequired(User user);
    }

    public interface UserClickListener {
        void onUserClicked(User user);
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
}
