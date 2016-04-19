package com.worldventures.dreamtrips.modules.feed.view.custom.collage;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.util.Size;
import com.worldventures.dreamtrips.modules.feed.view.custom.collage.layoutmanager.LayoutManager;
import com.worldventures.dreamtrips.modules.feed.view.custom.collage.layoutmanager.LayoutManagerFactory;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class CollageView extends FrameLayout {

    private List<CollageItem> items = new ArrayList<>();
    private ItemClickListener itemClickListener;
    private int side; //usually this layout is square. side = width; width = MATCH_PARENT;

    private int padding;
    private float textSize;
    private int iconResId;

    public CollageView(Context context) {
        this(context, null);
    }

    public CollageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CollageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.CollageView, 0, 0);

        try {
            padding = (int) typedArray.getDimension(R.styleable.CollageView_padding, 0);
            textSize = typedArray.getDimension(R.styleable.CollageView_moreTextSize, 25);
            iconResId = typedArray.getResourceId(R.styleable.CollageView_moreIcon, 0);
        } catch (Exception e) {
            Timber.e(e, "Can't parse custom attributes");
        } finally {
            typedArray.recycle();
        }
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setItems(List<CollageItem> items, int width) {
        clear();
        this.items = items;
        side = width;
        fillLayout();
    }

    public void clear() {
        removeAllViews();
        items.clear();
        resize(new Size(side, 0));
    }

    private void fillLayout() {
        if (items.isEmpty()) return;
        LayoutManager layoutManager = LayoutManagerFactory.getManager(items.size());
        layoutManager.initialize(getContext(), items);
        layoutManager.setAttributes(padding, textSize, iconResId);
        for (View view : layoutManager.getLocatedViews(side, itemClickListener)) {
            addView(view);
        }
        resize(layoutManager.getHolderSize());
    }

    private void resize(Size size) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = size.getWidth();
        layoutParams.height = size.getHeight();
        setLayoutParams(layoutParams);
    }

    public interface ItemClickListener {
        void itemClicked(int position);

        void moreClicked();
    }
}
