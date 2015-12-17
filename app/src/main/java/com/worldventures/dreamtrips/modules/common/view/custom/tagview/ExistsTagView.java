package com.worldventures.dreamtrips.modules.common.view.custom.tagview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.tripsimages.model.PhotoTag;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ExistsTagView extends TagView implements View.OnClickListener {

    private static final long HIDE_DELETE_BUTTON_DELAY = 1000;

    @InjectView(R.id.tagged_user_name)
    public TextView taggedUserName;
    @InjectView(R.id.tagged_user_delete_tag)
    public View btnDeleteTag;

    private Runnable hideDeleteBtnRunnable = () -> btnDeleteTag.setVisibility(View.GONE);

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
        setClickable(true);
        setOnClickListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(hideDeleteBtnRunnable);
    }

    @Override
    public void onClick(View v) {
        tagListener.onTagClicked(photoTag.getTargetUserId());
    }

    public void showDeleteButton(){
        btnDeleteTag.setVisibility(View.VISIBLE);
        postDelayed(hideDeleteBtnRunnable, HIDE_DELETE_BUTTON_DELAY);
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