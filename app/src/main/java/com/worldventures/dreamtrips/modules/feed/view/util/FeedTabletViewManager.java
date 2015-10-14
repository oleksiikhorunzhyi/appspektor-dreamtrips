package com.worldventures.dreamtrips.modules.feed.view.util;

import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.profile.view.ProfileViewUtils;

import java.text.DecimalFormat;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;

public class FeedTabletViewManager {

    DecimalFormat df = new DecimalFormat("#0.00");

    @Optional
    @InjectView(R.id.user_cover)
    SimpleDraweeView userCover;
    @Optional
    @InjectView(R.id.user_photo)
    SimpleDraweeView userPhoto;
    @Optional
    @InjectView(R.id.user_name)
    TextView userName;
    @Optional
    @InjectView(R.id.company_name)
    TextView companyName;
    @Optional
    @InjectView(R.id.account_type)
    TextView accountType;
    @Optional
    @InjectView(R.id.dt_points)
    TextView dtPoints;
    @Optional
    @InjectView(R.id.rovia_bucks)
    TextView roviaBucks;
    @Optional
    @InjectView(R.id.share_post)
    TextView sharePost;
    @Optional
    @InjectView(R.id.share_photo)
    TextView sharePhoto;
    @Optional
    @InjectView(R.id.details)
    ViewGroup details;
    @InjectView(R.id.view_profile)
    TextView viewProfile;

    ViewClickListener onUserClick;
    ViewClickListener onCreatePostClick;

    public FeedTabletViewManager(View view) {
        ButterKnife.inject(this, view);
        details.setVisibility(View.GONE);
        viewProfile.setVisibility(View.GONE);
    }

    public void setUser(User user, boolean withDetails) {
        if (user != null) {
            userPhoto.setImageURI(Uri.parse(user.getAvatar().getThumb()));
            userCover.setImageURI(Uri.parse(user.getBackgroundPhotoUrl()));
            userName.setText(user.getFullName());
            companyName.setText(user.getCompany());
            accountType.setText(user.getCompany());
            dtPoints.setText(df.format(user.getDreamTripsPoints()));
            roviaBucks.setText(df.format(user.getRoviaBucks()));
            ProfileViewUtils.setUserStatus(user, accountType, companyName.getResources());

            details.setVisibility(withDetails ? View.VISIBLE : View.GONE);
            viewProfile.setVisibility(!withDetails ? View.VISIBLE : View.GONE);
        }

    }


    @Optional
    @OnClick({R.id.user_cover, R.id.view_profile})
    void onUserClick() {
        if (onUserClick != null) {
            onUserClick.onClick();
        }
    }

    @Optional
    @OnClick(R.id.share_post)
    void onPostClicked() {
        if (onCreatePostClick != null) {
            onCreatePostClick.onClick();
        }
    }


    public void setOnUserClick(ViewClickListener onUserClick) {
        this.onUserClick = onUserClick;
    }

    public void setOnCreatePostClick(ViewClickListener onCreatePostClick) {
        this.onCreatePostClick = onCreatePostClick;
    }

    public interface ViewClickListener {
        void onClick();
    }

}
