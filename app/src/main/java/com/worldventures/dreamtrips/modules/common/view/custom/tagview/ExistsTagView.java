package com.worldventures.dreamtrips.modules.common.view.custom.tagview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.tripsimages.model.PhotoTag;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ExistsTagView extends TagView implements View.OnClickListener {

    private static final long HIDE_DELETE_BUTTON_DELAY = 2000;
    private int padding;
    private ValueAnimator anim = new ValueAnimator();
    private static final long PADDING_ANIMATION_DURATION = 100;

    @InjectView(R.id.tagged_user_name)
    public TextView taggedUserName;
    @InjectView(R.id.tagged_user_delete_tag_divider)
    public View divider;
    @InjectView(R.id.tagged_user_delete_tag)
    public View btnDeleteTag;

    private Runnable hideDeleteBtnRunnable = this::hideDeleteButton;

    public ExistsTagView(Context context) {
        super(context);
    }

    public ExistsTagView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExistsTagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ExistsTagView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void initialize() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_tag_view_exist, this, true);
        ButterKnife.inject(this);
        padding = getResources().getDimensionPixelSize(R.dimen.tag_exist_text_padding);
        setClickable(true);
        setOnClickListener(this);

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                taggedUserName.setWidth(taggedUserName.getWidth()); //Override WRAP_CONTENT
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(hideDeleteBtnRunnable);
        anim.removeAllListeners();
    }

    @Override
    public void onClick(View v) {
        tagListener.onTagClicked(photoTag.getUser().getId());
    }

    public void showDeleteButton(){
        if (taggedUserName.getPaddingLeft() != padding){
            return;
        }
        anim.setIntValues(padding, 0);
        anim.setEvaluator(new IntEvaluator());

        anim.addUpdateListener(animation -> {
            int val = (Integer) animation.getAnimatedValue();
            taggedUserName.setPadding(val, 0, (padding - val) * 2, 0);
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                btnDeleteTag.setVisibility(View.VISIBLE);
                divider.setVisibility(View.VISIBLE);
                postDelayed(hideDeleteBtnRunnable, HIDE_DELETE_BUTTON_DELAY);
                anim.removeListener(this);
            }
        });
        anim.setDuration(PADDING_ANIMATION_DURATION);
        anim.start();
    }

    private void hideDeleteButton(){
        removeCallbacks(hideDeleteBtnRunnable);
        btnDeleteTag.setVisibility(View.INVISIBLE);
        divider.setVisibility(View.INVISIBLE);

        anim.setIntValues(0, padding);
        anim.setEvaluator(new IntEvaluator());
        anim.addUpdateListener(animation -> {
            int val = (Integer) animation.getAnimatedValue();
            taggedUserName.setPadding(val, 0, (padding * 2) - val, 0);
        });
        anim.setDuration(PADDING_ANIMATION_DURATION);
        anim.start();
    }

    @Override
    public void setPhotoTag(PhotoTag photoTag) {
        super.setPhotoTag(photoTag);
        taggedUserName.setText(photoTag.getUser().getFullName());
    }

    @OnClick({R.id.tagged_user_delete_tag})
    public void onDeleteTag() {
        tagListener.onTagDeleted(photoTag);
        deleteTag();
    }
}