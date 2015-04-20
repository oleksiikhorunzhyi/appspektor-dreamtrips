package com.worldventures.dreamtrips.modules.common.view.fragment.navigationdrawer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.navigation.NavigationDrawerListener;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

public class NavigationDrawerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private int headerSize = 0;

    private List<ComponentDescription> componentDescriptions;
    private NavigationDrawerListener mNavigationDrawerListener;
    private int mSelectedPosition;
    private int mTouchedPosition = -1;
    private NavigationHeader navigationHeader;

    public NavigationDrawerAdapter(List<ComponentDescription> data, Injector injector) {
        injector.inject(this);
        componentDescriptions = data;
    }

    public void setNavigationDrawerCallbacks(NavigationDrawerListener navigationDrawerListener) {
        mNavigationDrawerListener = navigationDrawerListener;
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
            bindItemViewHolder((ItemHolder) viewHolder, i);
        } else if (viewHolder instanceof HeaderHolder) {
            bindHeaderViewHolder((HeaderHolder) viewHolder, i);
        }
    }

    private void bindHeaderViewHolder(HeaderHolder holder, int i) {
        holder.userPhoto.setImageURI(navigationHeader.getUserPhoto());
        holder.userCover.setImageURI(navigationHeader.getUserCover());
        holder.userName.setText(navigationHeader.getUserName());
        holder.userEmail.setText(navigationHeader.getUserEmail());
    }

    private void bindItemViewHolder(ItemHolder holder, int i) {
        ComponentDescription item = getItem(i);

        holder.sectionIcon.setImageResource(item.getIcon());
        holder.itemView.setOnTouchListener((v, event) -> {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            touchPosition(i);
                            return false;
                        case MotionEvent.ACTION_CANCEL:
                        case MotionEvent.ACTION_UP:
                            touchPosition(-1);
                            return false;
                        case MotionEvent.ACTION_MOVE:
                            return false;
                    }
                    return true;
                }
        );

        holder.itemView.setOnClickListener(v -> {
            selectPosition(i);
            if (mNavigationDrawerListener != null) {
                mNavigationDrawerListener.onNavigationDrawerItemSelected(componentDescriptions.get(i - headerSize));
            }

        });

        final boolean isSelected = mSelectedPosition == i
                || mTouchedPosition == i;

        if (holder.itemName != null) {
            holder.itemName.setText(item.getTitle());
            holder.itemName.setSelected(isSelected);
        }

        holder.sectionIcon.setSelected(isSelected);
    }

    private void touchPosition(int position) {
        int lastPosition = mTouchedPosition;
        mTouchedPosition = position;
        if (lastPosition >= 0) {
            notifyItemChanged(lastPosition);
        }

        if (position >= 0) {
            notifyItemChanged(position);
        }
    }

    public void selectPosition(int position) {
        int lastPosition = mSelectedPosition;
        mSelectedPosition = position;
        notifyItemChanged(lastPosition);
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        return navigationHeader != null ? componentDescriptions.size() + 1 : componentDescriptions.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return TYPE_HEADER;
        }

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return navigationHeader != null && position == 0;
    }

    public ComponentDescription getItem(int position) {
        return componentDescriptions.get(navigationHeader != null ? position - 1 : position);
    }

    public void setHeader(NavigationHeader navigationHeader) {
        this.navigationHeader = navigationHeader;
        headerSize = 1;
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {

        @Optional
        @InjectView(R.id.item_name)
        protected TextView itemName;

        @InjectView(R.id.section_icon)
        protected ImageView sectionIcon;

        public ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    public static class HeaderHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.user_cover)
        protected ImageView userCover;
        @InjectView(R.id.user_photo)
        protected ImageView userPhoto;
        @InjectView(R.id.user_name)
        protected TextView userName;
        @InjectView(R.id.user_email)
        protected TextView userEmail;

        public HeaderHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}