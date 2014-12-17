package com.worldventures.dreamtrips.view.fragment.navigationdrawer;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;

import java.util.List;

public class NavigationDrawerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private List<NavigationItem> mData;
    private NavigationDrawerCallbacks mNavigationDrawerCallbacks;
    private int mSelectedPosition;
    private int mTouchedPosition = -1;

    public NavigationDrawerAdapter(List<NavigationItem> data) {
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
            //inflate your layout and pass it to view holder
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.header_navigation_drawer, viewGroup, false);
            return new HeaderHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof ItemHolder) {
            ItemHolder holder = (ItemHolder) viewHolder;
            NavigationItem item = getItem(i);
            holder.title.setText(item.getText());
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
                    mNavigationDrawerCallbacks.onNavigationDrawerItemSelected(i);
            });
            if (mSelectedPosition == i || mTouchedPosition == i) {
                holder.itemView.setBackgroundColor(viewHolder.itemView.getContext().getResources().getColor(R.color.selected_gray));
            } else {
                holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            }

        } else if (viewHolder instanceof HeaderHolder) {
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

    private NavigationItem getItem(int position) {
        return mData.get(position - 1);
    }


    public static class ItemHolder extends RecyclerView.ViewHolder {
        public TextView title;

        public ItemHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.item_name);
        }
    }

    public static class HeaderHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public HeaderHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.item_name);
        }
    }
}