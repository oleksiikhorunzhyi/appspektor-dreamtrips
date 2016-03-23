package com.worldventures.dreamtrips.modules.common.view.custom.tagview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.tripsimages.model.PhotoTag;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ExistsTagView extends TagView implements View.OnClickListener {

    @InjectView(R.id.tagged_user_name)
    protected TextView taggedUserName;
    @InjectView(R.id.tagged_user_delete_tag_divider)
    protected View divider;
    @InjectView(R.id.tagged_user_delete_tag)
    protected View btnDeleteTag;

    public ExistsTagView(Context context) {
        super(context);
    }

    public ExistsTagView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExistsTagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initialize() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_tag_view_exist, this, true);
        ButterKnife.inject(this);
        setClickable(true);
        setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        boolean isAccountOnPhoto = account.getId() == photoTag.getUser().getId();
        boolean isCreationState = photo == null || photo.getOwner() == null;
        if (isCreationState || isAccountOnPhoto || account.getId() == photo.getOwner().getId()) {
            if (btnDeleteTag.getVisibility() == VISIBLE) {
                hideDeleteButton();
            } else {
                showDeleteButton();
            }
        }
    }

    private void showDeleteButton() {
        btnDeleteTag.setVisibility(View.VISIBLE);
        divider.setVisibility(View.VISIBLE);
    }

    private void hideDeleteButton() {
        btnDeleteTag.setVisibility(View.GONE);
        divider.setVisibility(View.GONE);
    }

    @Override
    public void setPhotoTag(PhotoTag photoTag) {
        super.setPhotoTag(photoTag);
        taggedUserName.setText(photoTag.getUser().getFullName());
    }

    @OnClick({R.id.tagged_user_delete_tag})
    public void onDeleteTag() {
        deleteTag();
        tagListener.onTagDeleted(photoTag);
    }
}