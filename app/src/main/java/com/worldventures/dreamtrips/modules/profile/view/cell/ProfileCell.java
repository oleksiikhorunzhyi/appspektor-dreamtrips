package com.worldventures.dreamtrips.modules.profile.view.cell;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.StringRes;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.andexert.expandablelayout.library.ExpandableLayout;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.techery.spares.annotations.Layout;
import com.techery.spares.session.SessionHolder;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.custom.DTEditText;
import com.worldventures.dreamtrips.modules.profile.event.profilecell.OnAcceptRequestEvent;
import com.worldventures.dreamtrips.modules.profile.event.profilecell.OnAddFriendEvent;
import com.worldventures.dreamtrips.modules.profile.event.profilecell.OnBucketListClickedEvent;
import com.worldventures.dreamtrips.modules.profile.event.profilecell.OnCoverClickEvent;
import com.worldventures.dreamtrips.modules.profile.event.profilecell.OnCreatePostClickEvent;
import com.worldventures.dreamtrips.modules.profile.event.profilecell.OnFriendsClickedEvent;
import com.worldventures.dreamtrips.modules.profile.event.profilecell.OnPhotoClickEvent;
import com.worldventures.dreamtrips.modules.profile.event.profilecell.OnRejectRequestEvent;
import com.worldventures.dreamtrips.modules.profile.event.profilecell.OnTripImageClickedEvent;
import com.worldventures.dreamtrips.modules.profile.view.ProfileViewUtil;

import java.text.DecimalFormat;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_profile)
public class ProfileCell extends AbstractCell<User> {

    private DecimalFormat df = new DecimalFormat("#0.00");

    private boolean isExpandEnabled = true;

    @InjectView(R.id.user_cover)
    protected SimpleDraweeView userCover;
    @InjectView(R.id.user_photo)
    protected SimpleDraweeView userPhoto;
    @InjectView(R.id.cover_camera)
    protected ImageView cover;
    @InjectView(R.id.avatar_camera)
    protected ImageView avatar;
    @InjectView(R.id.user_name)
    protected TextView userName;
    @InjectView(R.id.et_date_of_birth)
    protected DTEditText dateOfBirth;
    @InjectView(R.id.pb)
    protected ProgressBar progressBar;
    @InjectView(R.id.pb_cover)
    protected ProgressBar coverProgressBar;
    @InjectView(R.id.trip_images)
    protected TextView tripImages;
    @InjectView(R.id.dream_trips)
    protected TextView trips;
    @InjectView(R.id.update_info)
    protected TextView updateInfo;
    @InjectView(R.id.add_friend)
    protected TextView addFriend;
    @InjectView(R.id.user_status)
    protected TextView userStatus;
    @InjectView(R.id.bucket_list)
    protected TextView buckets;
    @InjectView(R.id.friends)
    protected TextView friends;
    @InjectView(R.id.post)
    protected TextView post;
    @InjectView(R.id.messages)
    protected TextView messages;
    @InjectView(R.id.control_more)
    protected TextView controllMore;
    @InjectView(R.id.et_user_id)
    protected DTEditText etUserId;
    @InjectView(R.id.et_from)
    protected DTEditText etFrom;
    @InjectView(R.id.et_enroll)
    protected DTEditText etEnroll;
    @InjectView(R.id.dt_points)
    protected TextView dtPoints;
    @InjectView(R.id.rovia_bucks)
    protected TextView roviaBucks;
    @InjectView(R.id.user_balance)
    protected ViewGroup userBalance;
    @InjectView(R.id.expandable_info)
    protected ExpandableLayout info;
    @InjectView(R.id.more)
    protected ViewGroup more;
    @InjectView(R.id.friend_request_caption)
    protected TextView friendRequestCaption;
    @InjectView(R.id.friend_request)
    protected ViewGroup friendRequest;
    @InjectView(R.id.accept)
    protected AppCompatTextView accept;
    @InjectView(R.id.reject)
    protected AppCompatTextView reject;
    @InjectView(R.id.control_panel)
    protected ViewGroup controlPanel;


    @Inject
    protected SessionHolder<UserSession> appSessionHolder;


    Context context;

    public ProfileCell(View view) {
        super(view);
        context = view.getContext();
    }

    @Override
    protected void syncUIStateWithModel() {
        User user = getModelObject();
        if (isAccount()) {
            controlPanel.setVisibility(View.VISIBLE);
            cover.setVisibility(View.VISIBLE);
            avatar.setVisibility(View.VISIBLE);
            addFriend.setVisibility(View.GONE);
            updateInfo.setVisibility(View.VISIBLE);
            userBalance.setVisibility(View.VISIBLE);
        } else {
            controlPanel.setVisibility(View.GONE);
            cover.setVisibility(View.GONE);
            avatar.setVisibility(View.GONE);
            updateInfo.setVisibility(View.GONE);
            userBalance.setVisibility(View.GONE);
            addFriend.setVisibility(View.VISIBLE);

            buckets.setClickable(false);
            tripImages.setClickable(false);

            buckets.setText(R.string.coming_soon);
            tripImages.setText(R.string.coming_soon);

            itemView.findViewById(R.id.wrapper_enroll).setVisibility(View.GONE);
            itemView.findViewById(R.id.wrapper_from).setVisibility(View.GONE);
            itemView.findViewById(R.id.wrapper_date_of_birth).setVisibility(View.GONE);
            more.setVisibility(View.INVISIBLE);

            setIsExpandEnabled(false);
            info.show();
        }


        setUserName(user.getFullName());
        setDateOfBirth(DateTimeUtils.convertDateToString(user.getBirthDate(),
                DateFormat.getMediumDateFormat(context)));
        setEnrollDate(DateTimeUtils.convertDateToString(user.getEnrollDate(),
                DateFormat.getMediumDateFormat(context)));
        setUserId(user.getUsername());
        setFrom(user.getLocation());

        ProfileViewUtil.setUserStatus(user, userStatus, context.getResources());

        setAvatarImage(Uri.parse(user.getAvatar().getMedium()));
        setCoverImage(Uri.parse(user.getBackgroundPhotoUrl()));
        setFriendButtonText(R.string.profile_friends);

        if (isAccount()) {
            setTripImagesCount(user.getTripImagesCount());
            setBucketItemsCount(user.getBucketListItemsCount());
            setRoviaBucks(df.format(user.getRoviaBucks()));
            setDreamTripPoints(df.format(user.getDreamTripsPoints()));
        } else {
            setTripImagesCount(user.getTripImagesCount());
            setBucketItemsCount(user.getBucketListItemsCount());
            setSocial(user.isSocialEnabled());
            setIsFriend(false);
            if (user.getRelationship() != null) {
                switch (user.getRelationship()) {
                    case FRIEND:
                        setIsFriend(true);
                        hideFriendRequest();
                        break;
                    case OUTGOING_REQUEST:
                        setWaiting();
                        hideFriendRequest();
                        break;
                    case INCOMING_REQUEST:
                        setRespond();
                        showFriendRequest(user.getFirstName());
                        break;
                    default:
                        hideFriendRequest();
                        break;
                }
            }
        }

        progressBar.setVisibility(user.isAvatarUploadInProgress() ? View.VISIBLE : View.GONE);
        coverProgressBar.setVisibility(user.isCoverUploadInProgress() ? View.VISIBLE : View.GONE);

    }

    private boolean isAccount() {
        return appSessionHolder.get().get().getUser().getId() == getModelObject().getId();
    }


    @Override
    public void prepareForReuse() {

    }


    public void setAvatarImage(Uri uri) {
        if (uri != null) {
            setImage(uri, userPhoto);
        }
    }

    public void setCoverImage(Uri uri) {
        if (uri != null) {
            setImage(uri, userCover);
        }
    }

    private void setImage(Uri uri, SimpleDraweeView draweeView) {
        PipelineDraweeControllerBuilder builder = Fresco.newDraweeControllerBuilder();
        if (draweeView.getTag() != null) {
            builder.setLowResImageRequest(ImageRequest.fromUri((Uri) draweeView.getTag()));
        }
        builder.setImageRequest(ImageRequest.fromUri(uri));
        DraweeController dc = builder.build();
        draweeView.setController(dc);
        draweeView.setTag(uri);
    }


    public void setDateOfBirth(String format) {
        dateOfBirth.setText(format);
    }

    public void setFrom(String location) {
        etFrom.setText(location);
    }

    public void setUserName(String username) {
        userName.setText(username);
    }

    public void setUserId(String username) {
        etUserId.setText(username);
    }

    public void setEnrollDate(String date) {
        etEnroll.setText(date);
    }

    public void setTripImagesCount(int count) {
        tripImages.setText(String.format(context.getString(R.string.profile_trip_images), count));
    }

    public void setTripsCount(int count) {
        trips.setText(String.format(context.getString(R.string.profile_dream_trips), count));
    }

    public void setBucketItemsCount(int count) {
        buckets.setText(String.format(context.getString(R.string.profile_bucket_list), count));
    }

    public void setSocial(Boolean isEnabled) {
        addFriend.setEnabled(isEnabled);
        friendRequest.setEnabled(isEnabled);
        friendRequest.setEnabled(isEnabled);
    }

    public void setFriendButtonText(@StringRes int res) {
        friends.setText(res);
    }


    public void setIsExpandEnabled(boolean isExpandEnabled) {
        this.isExpandEnabled = isExpandEnabled;
    }


    public void setRoviaBucks(String count) {
        roviaBucks.setText(Html.fromHtml(context.getString(R.string.profile_rovia_bucks, count)));
    }

    public void setDreamTripPoints(String count) {
        dtPoints.setText(Html.fromHtml(context.getString(R.string.profile_dt_points, count)));
    }

    public void setIsFriend(boolean isFriend) {
        addFriend.setText(isFriend ? R.string.profile_friends : R.string.profile_add_friend);
        addFriend.setCompoundDrawablesWithIntrinsicBounds(0,
                isFriend ? R.drawable.ic_profile_friend
                        : R.drawable.ic_profile_add_friend_selector,
                0, 0);
    }

    public void setWaiting() {
        addFriend.setText(R.string.profile_waiting);
        addFriend.setCompoundDrawablesWithIntrinsicBounds(0,
                R.drawable.ic_profile_friend_respond,
                0, 0);
    }

    public void setRespond() {
        addFriend.setText(R.string.profile_respond);
        addFriend.setCompoundDrawablesWithIntrinsicBounds(0,
                R.drawable.ic_profile_respond,
                0, 0);
    }

    public void showFriendRequest(String name) {
        friendRequestCaption.setText(String.format(context.getString(R.string.profile_friend_request), name));
        friendRequest.setVisibility(View.VISIBLE);
    }

    public void hideFriendRequest() {
        friendRequest.setVisibility(View.GONE);
    }


    /*** View Events *********************/

    @OnClick(R.id.bucket_list)
    protected void onBucketListClicked() {
        getEventBus().post(new OnBucketListClickedEvent());
    }

    @OnClick(R.id.trip_images)
    protected void onTripImageClicked() {
        getEventBus().post(new OnTripImageClickedEvent());

    }

    @OnClick(R.id.friends)
    protected void onFriendsClick() {
        getEventBus().post(new OnFriendsClickedEvent());

    }

    @OnClick(R.id.post)
    protected void onPostClick() {
        getEventBus().post(new OnCreatePostClickEvent());

    }

    @OnClick(R.id.user_photo)
    protected void onPhotoClick() {
        getEventBus().post(new OnPhotoClickEvent());

    }

    @OnClick(R.id.user_cover)
    protected void onCoverClick() {
        getEventBus().post(new OnCoverClickEvent());
    }


    @OnClick(R.id.update_info)
    void onUpdateInfo() {
    }

    @OnClick(R.id.accept)
    protected void onAcceptRequest() {
        getEventBus().post(new OnAcceptRequestEvent());

    }

    @OnClick(R.id.reject)
    protected void onRejectRequest() {
        getEventBus().post(new OnRejectRequestEvent());

    }

    @OnClick(R.id.add_friend)
    protected void onAddFriend() {
        getEventBus().post(new OnAddFriendEvent());

    }

    @OnClick({R.id.header, R.id.info, R.id.more, R.id.et_from, R.id.et_enroll, R.id.et_date_of_birth, R.id.et_user_id})
    public void onInfoClick() {
        if (isExpandEnabled) {
            if (info.isOpened()) {
                info.hide();
                more.setVisibility(View.VISIBLE);
            } else {
                info.show();
                more.setVisibility(View.INVISIBLE);
            }
        }
    }


}
