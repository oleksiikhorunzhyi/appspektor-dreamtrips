package com.worldventures.dreamtrips.view.fragment.navigationdrawer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.State;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NavigationDrawerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int HEADER_SIZE = 1;
    private List<State> mData;
    private NavigationDrawerCallbacks mNavigationDrawerCallbacks;
    private int mSelectedPosition;
    private int mTouchedPosition = -1;
    private NavigationHeader navigationHeader;

    public NavigationDrawerAdapter(List<State> data) {
        mData = data;
    }

    public void setNavigationDrawerCallbacks(NavigationDrawerCallbacks navigationDrawerCallbacks) {
        mNavigationDrawerCallbacks = navigationDrawerCallbacks;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_item_navigation_drawer, viewGroup, false);
            return new ItemHolder(v);
        } else if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.header_navigation_drawer, viewGroup, false);
            return new HeaderHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof ItemHolder) {
            ItemHolder holder = (ItemHolder) viewHolder;
            State item = getItem(i);
            holder.itemName.setText(item.getTitle());
            holder.sectionIcon.setImageResource(item.getDrawableId());
            holder.itemView.setOnTouchListener((v, event) -> {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                touchPosition(i);
                                return false;
                            case MotionEvent.ACTION_CANCEL:
                                touchPosition(-1);
                                return false;
                            case MotionEvent.ACTION_MOVE:
                                return false;
                            case MotionEvent.ACTION_UP:
                                touchPosition(-1);
                                return false;
                        }
                        return true;
                    }
            );
            holder.itemView.setOnClickListener(v -> {
                if (mNavigationDrawerCallbacks != null)
                    mNavigationDrawerCallbacks.onNavigationDrawerItemSelected(i - HEADER_SIZE);
            });
            if (mSelectedPosition == i || mTouchedPosition == i) {
                holder.itemName.setSelected(true);
                holder.sectionIcon.setSelected(true);
            } else {
                holder.itemName.setSelected(false);
                holder.sectionIcon.setSelected(false);

            }

        } else if (viewHolder instanceof HeaderHolder) {
            HeaderHolder holder = (HeaderHolder) viewHolder;
            holder.userCover.setImageDrawable(navigationHeader.getUserCover());
            holder.userPhoto.setImageDrawable(navigationHeader.getUserPhoto());
            holder.userNome.setText(navigationHeader.getUserNome());
            holder.userEmail.setText(navigationHeader.getUserEmail());
        }

    }

    private void touchPosition(int position) {
        int lastPosition = mTouchedPosition;
        mTouchedPosition = position;
        if (lastPosition >= 0)
            notifyItemChanged(lastPosition);
        if (position >= 0)
            notifyItemChanged(position);
    }

    public void selectPosition(int position) {
        position = position + HEADER_SIZE;
        int lastPosition = mSelectedPosition;
        mSelectedPosition = position;
        notifyItemChanged(lastPosition);
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        return mData.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    public State getItem(int position) {
        return mData.get(position - 1);
    }

    public void setHeader(NavigationHeader navigationHeader) {
        this.navigationHeader = navigationHeader;
    }


    public static class ItemHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.item_name)
        TextView itemName;
        @InjectView(R.id.section_icon)
        ImageView sectionIcon;

        public ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    public static class HeaderHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.user_cover)
        ImageView userCover;
        @InjectView(R.id.user_photo)
        ImageView userPhoto;
        @InjectView(R.id.user_photo_2)
        ImageView userPhoto2;
        @InjectView(R.id.user_photo_3)
        ImageView userPhoto3;
        @InjectView(R.id.user_nome)
        TextView userNome;
        @InjectView(R.id.user_email)
        TextView userEmail;

        public HeaderHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}