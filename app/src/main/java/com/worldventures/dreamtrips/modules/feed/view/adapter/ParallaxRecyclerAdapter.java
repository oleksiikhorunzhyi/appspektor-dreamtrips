package com.worldventures.dreamtrips.modules.feed.view.adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

import com.techery.spares.adapter.LoaderRecycleAdapter;
import com.techery.spares.module.Injector;
import com.techery.spares.ui.view.cell.AbstractCell;

import java.util.List;

import javax.inject.Provider;


public class ParallaxRecyclerAdapter<T> extends LoaderRecycleAdapter<T> {
    private final float SCROLL_MULTIPLIER = 0.5f;

    public ParallaxRecyclerAdapter(Context context, Provider<Injector> injector) {
        super(context, injector);
    }

    public static class VIEW_TYPES {
        public static final int NORMAL = 1;
        public static final int HEADER = 700;
        public static int FIRST_VIEW = 999;
    }

    public interface RecyclerAdapterMethods {
        void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i);

        RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i);

        int getItemCount();
    }


    @Override
    public int getCount() {
        return super.getCount();
    }

    public interface OnClickEvent {
        /**
         * Event triggered when you click on a item of the adapter
         *
         * @param v        view
         * @param position position on the array
         */
        void onClick(View v, int position);
    }

    public interface OnParallaxScroll {
        /**
         * Event triggered when the parallax is being scrolled.
         *
         * @param percentage
         * @param offset
         * @param parallax
         */
        void onParallaxScroll(float percentage, float offset, View parallax);
    }

    private List<T> mData;
    private CustomRelativeWrapper mHeader;
    private OnParallaxScroll mParallaxScroll;
    private RecyclerView mRecyclerView;
    private int mTotalYScrolled;
    private boolean mShouldClipView = true;

    /**
     * Translates the adapter in Y
     *
     * @param of offset in px
     */
    public void translateHeader(float of) {
        float ofCalculated = of * SCROLL_MULTIPLIER;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mHeader.setTranslationY(ofCalculated);
        } else {
            TranslateAnimation anim = new TranslateAnimation(0, 0, ofCalculated, ofCalculated);
            anim.setFillAfter(true);
            anim.setDuration(0);
            mHeader.startAnimation(anim);
        }
        mHeader.setClipY(Math.round(ofCalculated));
        if (mParallaxScroll != null) {
            float left = Math.min(1, ((ofCalculated) / (mHeader.getHeight() * SCROLL_MULTIPLIER)));
            mParallaxScroll.onParallaxScroll(left, of, mHeader);
        }
    }

    /**
     * Set the view as header.
     *
     * @param header The inflated header
     * @param view   The RecyclerView to set scroll listeners
     */
    public void setParallaxHeader(View header, final RecyclerView view) {
        mRecyclerView = view;
        mHeader = new CustomRelativeWrapper(header.getContext(), mShouldClipView);
        mHeader.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mHeader.addView(header, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mHeader != null) {
                    mTotalYScrolled += dy;
                    translateHeader(mTotalYScrolled);
                }
            }
        });
    }

    @Override
    public void onBindViewHolder(AbstractCell cell, final int i) {
        if (i != 0 && mHeader != null) {
            super.onBindViewHolder(cell, i - 1);
        } else if (i != 0)
            super.onBindViewHolder(cell, i);
    }

    @Override
    public AbstractCell onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i == VIEW_TYPES.HEADER && mHeader != null)
            return new AbstractCell(mHeader) {

                @Override
                public void prepareForReuse() {

                }

                @Override
                protected void syncUIStateWithModel() {

                }
            };
        if (i == VIEW_TYPES.FIRST_VIEW && mHeader != null && mRecyclerView != null) {
            RecyclerView.ViewHolder holder = mRecyclerView.findViewHolderForPosition(0);
            if (holder != null) {
                translateHeader(-holder.itemView.getTop());
                mTotalYScrolled = -holder.itemView.getTop();
            }
        }
        return super.onCreateViewHolder(viewGroup, i);
    }

    /**
     * @return true if there is a header on this adapter, false otherwise
     */
    public boolean hasHeader() {
        return mHeader != null;
    }


    public boolean isShouldClipView() {
        return mShouldClipView;
    }

    /**
     * Defines if we will clip the layout or not. MUST BE CALLED BEFORE {@link #setParallaxHeader(android.view.View, android.support.v7.widget.RecyclerView)}
     *
     * @param shouldClickView
     */
    public void setShouldClipView(boolean shouldClickView) {
        mShouldClipView = shouldClickView;
    }

    public void setOnParallaxScroll(OnParallaxScroll parallaxScroll) {
        mParallaxScroll = parallaxScroll;
        mParallaxScroll.onParallaxScroll(0, 0, mHeader);
    }

    public List<T> getData() {
        return mData;
    }

    public void setData(List<T> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public void addItem(T item, int position) {
        mData.add(position, item);
        notifyItemInserted(position + (mHeader == null ? 0 : 1));
    }

    public void removeItem(T item) {
        int position = mData.indexOf(item);
        if (position < 0)
            return;
        mData.remove(item);
        notifyItemRemoved(position + (mHeader == null ? 0 : 1));
    }


    public int getItemCount() {
        return super.getItemCount() + (mHeader == null ? 0 : 1);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && mHeader != null) {
            return VIEW_TYPES.HEADER;
        }
        int itemViewType = super.getItemViewType(Math.max(0, position - 1));
        if (position == 1)
            return VIEW_TYPES.FIRST_VIEW = itemViewType;
        return itemViewType;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    static class CustomRelativeWrapper extends RelativeLayout {

        private int mOffset;
        private boolean mShouldClip;

        public CustomRelativeWrapper(Context context, boolean shouldClick) {
            super(context);
            mShouldClip = shouldClick;
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            if (mShouldClip) {
                canvas.clipRect(new Rect(getLeft(), getTop(), getRight(), getBottom() + mOffset));
            }
            super.dispatchDraw(canvas);
        }

        public void setClipY(int offset) {
            mOffset = offset;
            invalidate();
        }
    }
}