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
import com.worldventures.dreamtrips.core.navigation.NavigationDrawerListener;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.utils.UniversalImageLoader;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

import static com.worldventures.dreamtrips.core.utils.UniversalImageLoader.OP_AVATAR;
import static com.worldventures.dreamtrips.core.utils.UniversalImageLoader.OP_COVER;

public class NavigationDrawerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    @Inject
    UniversalImageLoader universalImageLoader;

    private int headerSize = 0;

    private List<Route> mData;
    private NavigationDrawerListener mNavigationDrawerListener;
    private int mSelectedPosition;
    private int mTouchedPosition = -1;
    private NavigationHeader navigationHeader;

    public NavigationDrawerAdapter(List<Route> data, Injector injector) {
        injector.inject(this);
        mData = data;
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
            ItemHolder holder = (ItemHolder) viewHolder;
            Route item = getItem(i);
            if (holder.itemName != null)
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
                selectPosition(i);
                if (mNavigationDrawerListener != null) {
                    mNavigationDrawerListener.onNavigationDrawerItemSelected(mData.get(i - headerSize));
                }

            });

            final boolean isSelected = mSelectedPosition == i || mTouchedPosition == i;

            if (holder.itemName != null) {
                holder.itemName.setSelected(isSelected);
            }

            holder.sectionIcon.setSelected(isSelected);

        } else if (viewHolder instanceof HeaderHolder) {
            HeaderHolder holder = (HeaderHolder) viewHolder;
            universalImageLoader.loadImage(navigationHeader.getUserPhoto(), holder.userPhoto, OP_AVATAR);
            universalImageLoader.loadImage(navigationHeader.getUserCover(), holder.userCover, OP_COVER);
            holder.userNome.setText(navigationHeader.getUserNome());
            holder.userEmail.setText(navigationHeader.getUserEmail());
        }

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
        return navigationHeader != null ? mData.size() + 1 : mData.size();
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

    public Route getItem(int position) {
        return mData.get(navigationHeader != null ? position - 1 : position);
    }

    public void setHeader(NavigationHeader navigationHeader) {
        this.navigationHeader = navigationHeader;
        headerSize = 1;
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {

        @Optional
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
        @InjectView(R.id.user_name)
        TextView userNome;
        @InjectView(R.id.user_email)
        TextView userEmail;

        public HeaderHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}