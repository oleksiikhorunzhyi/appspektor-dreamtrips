package com.worldventures.dreamtrips.modules.feed.view.custom.collage;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.net.Uri;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.feed.view.util.blur.BlurPostprocessor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;

public class CollageView extends FrameLayout {

    private static final int BLUR_RADIUS = 30;
    private static final int BLUR_SAMPLING = 1; //scale canvas before blur

    private int halfPadding;

    private float textSize;
    private int iconResId;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LANDSCAPE, PORTRAIT, SQUARE})
    private @interface PhotoType {
    }

    private static final int LANDSCAPE = 0;
    private static final int PORTRAIT = 1;
    private static final int SQUARE = 2;

    private List<CollageItem> items = Collections.EMPTY_LIST;
    private ItemClickListener itemClickListener;

    private int side; //usually this layout is square. side = width; width = MATCH_PARENT;

    public CollageView(Context context) {
        this(context, null);
    }

    public CollageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CollageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(
                attrs, R.styleable.CollageView, 0, 0);

        try {
            int padding = (int) typedArray.getDimension(R.styleable.CollageView_padding, 0);
            halfPadding = padding / 2;
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
    }

    private void fillLayout() {
        switch (items.size()) {
            case 1:
                place1();
                break;
            case 2:
                place2();
                break;
            case 3:
                place3();
                break;
            case 4:
                place4();
                break;
            default:
                placeMany();
                break;
        }
    }

    private void place1() {
        addImage(0, new FrameLayout.LayoutParams(side, items.get(0).height));
        resize(side, items.get(0).height);
    }

    private void place2() {
        int firstType = getType(items.get(0));
        int secondType = getType(items.get(1));
        if (firstType == LANDSCAPE && secondType == LANDSCAPE) {
            addImage(0, getLayoutParams(side, side / 2), getPaddings(0, 0, 0, halfPadding));
            addImage(1, getLayoutParams(side, side / 2, Gravity.BOTTOM), getPaddings(0, halfPadding, 0, 0));
            resize(side, side);
        } else if (firstType == PORTRAIT && secondType == PORTRAIT) {
            addImage(0, getLayoutParams(side / 2, side), getPaddings(0, 0, halfPadding, 0));
            addImage(1, getLayoutParams(side / 2, side, Gravity.RIGHT), getPaddings(halfPadding, 0, 0, 0));
            resize(side, side);
        } else {
            addImage(0, getLayoutParams(side / 2, side / 2), getPaddings(0, 0, halfPadding, 0));
            addImage(1, getLayoutParams(side / 2, side / 2, Gravity.RIGHT), getPaddings(halfPadding, 0, 0, 0));
            resize(side, side / 2);
        }
    }

    private void place3() {
        int firstType = getType(items.get(0));
        int secondType = getType(items.get(1));
        int thirdType = getType(items.get(2));
        if (firstType == LANDSCAPE && secondType == LANDSCAPE && thirdType == LANDSCAPE) {
            addImage(0, getLayoutParams(side, side * 2 / 3), getPaddings(0, 0, 0, halfPadding));
            addImage(1, getLayoutParams(side / 2, side / 3, Gravity.BOTTOM), getPaddings(0, halfPadding, halfPadding, 0));
            addImage(2, getLayoutParams(side / 2, side / 3, Gravity.RIGHT | Gravity.BOTTOM), getPaddings(halfPadding, halfPadding, 0, 0));
            resize(side, side);
        } else if (firstType == PORTRAIT && secondType == PORTRAIT && thirdType == PORTRAIT) {
            addImage(0, getLayoutParams(side * 2 / 3, side), getPaddings(0, 0, halfPadding, 0));
            addImage(1, getLayoutParams(side / 3, side / 2, Gravity.RIGHT), getPaddings(halfPadding, 0, 0, halfPadding));
            addImage(2, getLayoutParams(side / 3, side / 2, Gravity.RIGHT | Gravity.BOTTOM), getPaddings(halfPadding, halfPadding, 0, 0));
            resize(side, side);
        } else if (firstType == LANDSCAPE) {
            addImage(0, getLayoutParams(side, side / 2), getPaddings(0, 0, 0, halfPadding));
            addImage(1, getLayoutParams(side / 2, side / 2, Gravity.BOTTOM), getPaddings(0, halfPadding, halfPadding, 0));
            addImage(2, getLayoutParams(side / 2, side / 2, Gravity.RIGHT | Gravity.BOTTOM), getPaddings(halfPadding, halfPadding, 0, 0));
            resize(side, side);
        } else if (firstType == PORTRAIT) {
            addImage(0, getLayoutParams(side / 2, side), getPaddings(0, 0, halfPadding, 0));
            addImage(1, getLayoutParams(side / 2, side / 2, Gravity.RIGHT), getPaddings(halfPadding, 0, 0, halfPadding));
            addImage(2, getLayoutParams(side / 2, side / 2, Gravity.RIGHT | Gravity.BOTTOM), getPaddings(halfPadding, halfPadding, 0, 0));
            resize(side, side);
        } else {
            addImage(0, getLayoutParams(side * 2 / 3, side * 2 / 3), getPaddings(0, 0, halfPadding, 0));
            addImage(1, getLayoutParams(side / 3, side / 3, Gravity.RIGHT), getPaddings(halfPadding, 0, 0, halfPadding));
            addImage(2, getLayoutParams(side / 3, side / 3, Gravity.RIGHT | Gravity.BOTTOM), getPaddings(halfPadding, halfPadding, 0, 0));
            resize(side, side * 2 / 3);
        }
    }

    private void place4() {
        int firstType = getType(items.get(0));
        if (firstType == LANDSCAPE) {
            addImage(0, getLayoutParams(side, side * 2 / 3), getPaddings(0, 0, 0, halfPadding));
            addImage(1, getLayoutParams(side / 3, side / 3, Gravity.BOTTOM), getPaddings(0, halfPadding, halfPadding, 0));
            addImage(2, getLayoutParams(side / 3, side / 3, Gravity.BOTTOM | Gravity.CENTER), getPaddings(halfPadding, halfPadding, halfPadding, 0));
            addImage(3, getLayoutParams(side / 3, side / 3, Gravity.BOTTOM | Gravity.RIGHT), getPaddings(halfPadding, halfPadding, 0, 0));
            resize(side, side);
        } else if (firstType == PORTRAIT) {
            addImage(0, getLayoutParams(side * 2 / 3, side), getPaddings(0, 0, halfPadding, 0));
            addImage(1, getLayoutParams(side / 3, side / 3, Gravity.RIGHT), getPaddings(halfPadding, 0, 0, halfPadding));
            addImage(2, getLayoutParams(side / 3, side / 3, Gravity.RIGHT | Gravity.CENTER), getPaddings(halfPadding, halfPadding, 0, halfPadding));
            addImage(3, getLayoutParams(side / 3, side / 3, Gravity.RIGHT | Gravity.BOTTOM), getPaddings(halfPadding, halfPadding, 0, 0));
            resize(side, side);
        } else {
            addImage(0, getLayoutParams(side / 2, side / 2), getPaddings(0, 0, halfPadding, halfPadding));
            addImage(1, getLayoutParams(side / 2, side / 2, Gravity.RIGHT), getPaddings(halfPadding, 0, 0, halfPadding));
            addImage(2, getLayoutParams(side / 2, side / 2, Gravity.BOTTOM), getPaddings(0, halfPadding, halfPadding, 0));
            addImage(3, getLayoutParams(side / 2, side / 2, Gravity.RIGHT | Gravity.BOTTOM), getPaddings(halfPadding, halfPadding, 0, 0));
            resize(side, side);
        }
    }

    private void placeMany() {
        int firstType = getType(items.get(0));
        if (firstType == LANDSCAPE) {
            addImage(0, getLayoutParams(side, side * 2 / 3), getPaddings(0, 0, 0, halfPadding));
            addImage(1, getLayoutParams(side / 3, side / 3, Gravity.BOTTOM), getPaddings(0, halfPadding, halfPadding, 0));
            addImage(2, getLayoutParams(side / 3, side / 3, Gravity.BOTTOM | Gravity.CENTER), getPaddings(halfPadding, halfPadding, halfPadding, 0));
            addMoreButton(3, getLayoutParams(side / 3, side / 3, Gravity.BOTTOM | Gravity.RIGHT), getPaddings(halfPadding, halfPadding, 0, 0));
            resize(side, side);
        } else if (firstType == PORTRAIT) {
            addImage(0, getLayoutParams(side * 2 / 3, side), getPaddings(0, 0, halfPadding, 0));
            addImage(1, getLayoutParams(side / 3, side / 3, Gravity.RIGHT), getPaddings(halfPadding, 0, 0, halfPadding));
            addImage(2, getLayoutParams(side / 3, side / 3, Gravity.RIGHT | Gravity.CENTER), getPaddings(halfPadding, halfPadding, 0, halfPadding));
            addMoreButton(3, getLayoutParams(side / 3, side / 3, Gravity.RIGHT | Gravity.BOTTOM), getPaddings(halfPadding, halfPadding, 0, 0));
            resize(side, side);
        } else {
            addImage(0, getLayoutParams(side * 2 / 3, side / 2), getPaddings(0, 0, halfPadding, halfPadding));
            addImage(1, getLayoutParams(side * 2 / 3, side / 2, Gravity.BOTTOM), getPaddings(0, halfPadding, halfPadding, 0));
            addImage(2, getLayoutParams(side / 3, side / 3, Gravity.RIGHT), getPaddings(halfPadding, 0, 0, halfPadding));
            addImage(3, getLayoutParams(side / 3, side / 3, Gravity.RIGHT | Gravity.CENTER), getPaddings(halfPadding, halfPadding, 0, halfPadding));
            resize(side, side);
            if (items.size() <= 5) {
                addImage(4, getLayoutParams(side / 3, side / 3, Gravity.RIGHT | Gravity.BOTTOM), getPaddings(halfPadding, halfPadding, 0, 0));
            } else {
                addMoreButton(3, getLayoutParams(side / 3, side / 3, Gravity.RIGHT | Gravity.BOTTOM), getPaddings(halfPadding, halfPadding, 0, 0));
            }
            resize(side, side);
        }
    }

    @PhotoType
    private int getType(CollageItem item) {
        if (item.width > item.height) {
            return LANDSCAPE;
        } else if (item.width < item.height) {
            return PORTRAIT;
        } else {
            return SQUARE;
        }
    }

    private FrameLayout.LayoutParams getLayoutParams(int width, int height) {
        return getLayoutParams(width, height, Gravity.NO_GRAVITY);
    }

    private FrameLayout.LayoutParams getLayoutParams(int width, int height, int gravity) {
        return new LayoutParams(width, height, gravity);
    }

    private Rect getPaddings(int left, int top, int right, int bottom) {
        return new Rect(left, top, right, bottom);
    }

    private void addImage(final int position, FrameLayout.LayoutParams params) {
        addImage(position, params, new Rect());
    }

    private void addImage(final int position, FrameLayout.LayoutParams params, Rect paddings) {
        SimpleDraweeView view = new SimpleDraweeView(getContext());
        view.setPadding(paddings.left, paddings.top, paddings.right, paddings.bottom);
        view.setImageURI(Uri.parse(items.get(position).url));
        addView(view, params);
        view.setOnClickListener(v -> {
            if (itemClickListener != null) itemClickListener.itemClicked(position);
        });
    }

    private void addMoreButton(int position, FrameLayout.LayoutParams params, Rect paddings) {
        //more image button root
        FrameLayout moreViewRoot = new FrameLayout(getContext());
        LayoutParams moreViewRootParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        moreViewRootParams.gravity = params.gravity;
        addView(moreViewRoot, moreViewRootParams);
        moreViewRoot.setOnClickListener(v -> {
            if (itemClickListener != null) itemClickListener.moreClicked();
        });

        //blur view
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(items.get(position).url))
                .setPostprocessor(new BlurPostprocessor(getContext(), BLUR_RADIUS, BLUR_SAMPLING))
                .build();
        PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .build();
        SimpleDraweeView view = new SimpleDraweeView(getContext());
        view.setController(controller);
        view.setPadding(paddings.left, paddings.top, paddings.right, paddings.bottom);
        params.gravity = Gravity.NO_GRAVITY;
        moreViewRoot.addView(view, params);

        //text
        TextView textView = new TextView(getContext());
        textView.setTextColor(getResources().getColor(android.R.color.white, null));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        textView.setText(String.format("+%d", items.size() - position));
        textView.setGravity(Gravity.CENTER);
        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, iconResId, 0);
        LayoutParams textViewParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textViewParams.gravity = Gravity.CENTER;
        moreViewRoot.addView(textView, textViewParams);
    }

    private void resize(int width, int height) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        setLayoutParams(layoutParams);
    }

    interface ItemClickListener {
        void itemClicked(int position);

        void moreClicked();
    }
}
