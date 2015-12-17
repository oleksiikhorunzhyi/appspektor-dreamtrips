package com.worldventures.dreamtrips.modules.tripsimages.view.util;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.custom.FlagView;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.ScaleImageView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class FullScreenPhotoActionPanelDelegate {

    @InjectView(R.id.iv_image)
    ScaleImageView ivImage;
    @InjectView(R.id.ll_global_content_wrapper)
    protected LinearLayout llContentWrapper;
    @InjectView(R.id.ll_more_info)
    protected LinearLayout llMoreInfo;
    @InjectView(R.id.tv_title)
    protected TextView tvTitle;
    @InjectView(R.id.tv_description)
    protected TextView tvDescription;
    @InjectView(R.id.tv_see_more)
    protected TextView tvSeeMore;
    @InjectView(R.id.tv_location)
    protected TextView tvLocation;
    @InjectView(R.id.textViewInspireMeTitle)
    protected TextView textViewInspireMeTitle;
    @InjectView(R.id.tv_date)
    protected TextView tvDate;
    @InjectView(R.id.tv_likes_count)
    protected TextView tvLikesCount;
    @InjectView(R.id.tv_comments_count)
    protected TextView tvCommentsCount;
    @InjectView(R.id.iv_like)
    protected ImageView ivLike;
    @InjectView(R.id.iv_share)
    protected ImageView ivShare;
    @InjectView(R.id.flag)
    protected FlagView flag;
    @InjectView(R.id.edit)
    protected ImageView edit;
    @InjectView(R.id.user_photo)
    protected SimpleDraweeView civUserPhoto;
    @InjectView(R.id.checkBox)
    protected CheckBox checkBox;
    @InjectView(R.id.iv_comment)
    protected ImageView ivComment;

    Context context;
    User account;

    public void setup(Activity activity, View rootView, User account) {
        ButterKnife.inject(this, rootView);
        this.context = activity;
        this.account = account;

        ivImage.setSingleTapListener(this::toggleContent);
        ivImage.setDoubleTapListener(this::hideContent);
    }

    public void setContent(Photo photo) {
        setTitleSpanned(photo.getUser().getUsernameWithCompany(context));
        setCommentCount(photo.getFSCommentCount());
        setDescription(photo.getFSDescription());
        setLikeCount(photo.getFSLikeCount());
        setLocation(photo.getFSLocation());
        setDate(photo.getFSDate());
        setUserPhoto(photo.getFSUserPhoto());
        setLiked(photo.isLiked());

        User owner = photo.getOwner();
        boolean isAccountsPhoto = owner != null && account.getId() == owner.getId();
        flag.setVisibility(isAccountsPhoto ? View.GONE : View.VISIBLE);
        edit.setVisibility(isAccountsPhoto ? View.VISIBLE : View.GONE);
    }


    public void setUserPhoto(String fsPhoto) {
        if (TextUtils.isEmpty(fsPhoto)) {
            civUserPhoto.setVisibility(View.GONE);
        } else {
            civUserPhoto.setImageURI(Uri.parse(fsPhoto));
        }
    }

    public void setDate(String date) {
        if (TextUtils.isEmpty(date)) {
            tvDate.setVisibility(View.GONE);
        } else {
            tvDate.setVisibility(View.VISIBLE);
            tvDate.setText(date);
        }
    }

    public void setLocation(String location) {
        if (TextUtils.isEmpty(location)) {
            tvLocation.setVisibility(View.GONE);
        } else {
            tvLocation.setVisibility(View.VISIBLE);
            tvLocation.setText(location);
        }
    }


    public void setCommentCount(int count) {
        if (count > 0) {
            tvCommentsCount.setText(context.getString(R.string.comments, count));
            tvCommentsCount.setVisibility(View.VISIBLE);
        } else {
            tvCommentsCount.setVisibility(View.GONE);
        }
    }

    public void setLikeCount(int count) {
        if (count > 0) {
            tvLikesCount.setText(context.getString(R.string.likes, count));
            tvLikesCount.setVisibility(View.VISIBLE);
        } else {
            tvLikesCount.setVisibility(View.GONE);
        }
    }

    public void setDescription(String desc) {
        tvDescription.setText(desc);
        actionSeeMore();
    }


    @OnClick(R.id.tv_see_more)
    public void actionSeeMore() {
        llMoreInfo.setVisibility(View.VISIBLE);
        tvDescription.setSingleLine(false);

        tvSeeMore.setVisibility(View.GONE);
        if (tvDescription.getText().length() == 0) {
            tvDescription.setVisibility(View.GONE);
        }
        if (tvDate.getText().length() == 0) {
            tvDate.setVisibility(View.GONE);
        }
        if (tvLocation.getText().length() == 0) {
            tvLocation.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.bottom_container, R.id.title_container})
    public void actionSeeLess() {
        llMoreInfo.setVisibility(View.GONE);
        tvDescription.setSingleLine(true);
        tvDescription.setVisibility(View.VISIBLE);
        tvSeeMore.setVisibility(View.VISIBLE);
    }

    public void setTitleSpanned(Spanned titleSpanned) {
        tvTitle.setText(titleSpanned);
    }

    public void setLiked(boolean isLiked) {
        ivLike.setSelected(isLiked);
    }


    private void hideContent() {
        llContentWrapper.setVisibility(View.GONE);
    }

    private void showContent() {
        llContentWrapper.setVisibility(View.VISIBLE);
    }

    public void toggleContent() {
        if (llContentWrapper.getVisibility() == View.VISIBLE) {
            hideContent();
        } else {
            showContent();
        }
    }

}
