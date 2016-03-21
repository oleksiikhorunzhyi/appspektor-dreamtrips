package com.worldventures.dreamtrips.modules.profile.view.cell;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.session.SessionHolder;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.core.session.acl.FeatureManager;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.core.utils.QuantityHelper;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.custom.BadgeView;
import com.worldventures.dreamtrips.modules.common.view.custom.DTEditText;
import com.worldventures.dreamtrips.modules.common.view.custom.SmartAvatarView;
import com.worldventures.dreamtrips.modules.profile.adapters.Expandable;
import com.worldventures.dreamtrips.modules.profile.adapters.OnExpandedListener;
import com.worldventures.dreamtrips.modules.profile.event.profilecell.OnAcceptRequestEvent;
import com.worldventures.dreamtrips.modules.profile.event.profilecell.OnAddFriendEvent;
import com.worldventures.dreamtrips.modules.profile.event.profilecell.OnBucketListClickedEvent;
import com.worldventures.dreamtrips.modules.profile.event.profilecell.OnCoverClickEvent;
import com.worldventures.dreamtrips.modules.profile.event.profilecell.OnCreatePostClickEvent;
import com.worldventures.dreamtrips.modules.profile.event.profilecell.OnFriendsClickedEvent;
import com.worldventures.dreamtrips.modules.profile.event.profilecell.OnPhotoClickEvent;
import com.worldventures.dreamtrips.modules.profile.event.profilecell.OnRejectRequestEvent;
import com.worldventures.dreamtrips.modules.profile.event.profilecell.OnTripImageClickedEvent;
import com.worldventures.dreamtrips.modules.profile.view.ProfileViewUtils;
import com.worldventures.dreamtrips.modules.profile.view.widgets.ExpandableLayout;

import java.text.DecimalFormat;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_profile)
public class ProfileCell extends AbstractCell<User> implements Expandable {

    private DecimalFormat df = new DecimalFormat("#0.00");

    private boolean isExpandEnabled = true;

    @InjectView(R.id.user_cover)
    protected SimpleDraweeView userCover;
    @InjectView(R.id.user_photo)
    protected SmartAvatarView userPhoto;
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
    @InjectView(R.id.add_friend)
    protected TextView addFriend;
    @InjectView(R.id.company_name)
    protected TextView companyName;
    @InjectView(R.id.user_status)
    protected TextView userStatus;
    @InjectView(R.id.bucket_list)
    protected TextView buckets;
    @InjectView(R.id.friends)
    protected TextView friends;
    @InjectView(R.id.post)
    protected TextView post;
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
    @InjectView(R.id.badge)
    BadgeView badge;
    @InjectView(R.id.fl_friends_container)
    View friendsContainer;
    @InjectView(R.id.divider1)
    View divider1;
    @InjectView(R.id.divider3)
    View divider3;

    @Inject
    protected SessionHolder<UserSession> appSessionHolder;
    @Inject
    SnappyRepository snapper;
    @Inject
    FeatureManager featureManager;

    @Inject
    @ForActivity
    Provider<Injector> injectorProvider;

    Context context;
    OnExpandedListener onExpandedListener;

    public ProfileCell(View view) {
        super(view);
        context = view.getContext();
    }

    @Override
    public void fillWithItem(User item) {
        super.fillWithItem(item);
        injectorProvider.get().inject(userPhoto);
    }

    @Override
    protected void syncUIStateWithModel() {
        User user = getModelObject();
        if (isAccount()) {
            cover.setVisibility(View.VISIBLE);
            avatar.setVisibility(View.VISIBLE);
            addFriend.setVisibility(View.GONE);
            userBalance.setVisibility(View.VISIBLE);
        } else {
            cover.setVisibility(View.GONE);
            avatar.setVisibility(View.GONE);
            userBalance.setVisibility(View.GONE);
            addFriend.setVisibility(View.VISIBLE);

            itemView.findViewById(R.id.wrapper_enroll).setVisibility(View.GONE);
            itemView.findViewById(R.id.wrapper_from).setVisibility(View.GONE);
            itemView.findViewById(R.id.wrapper_date_of_birth).setVisibility(View.GONE);
            itemView.findViewById(R.id.wrapper_user_id).setVisibility(View.GONE);
            more.setVisibility(View.INVISIBLE);

            setIsExpandEnabled(false);
            info.show();
        }

        if (isAccount() && featureManager.available(Feature.SOCIAL)) {
            post.setVisibility(View.VISIBLE);
            friendsContainer.setVisibility(View.VISIBLE);
            divider3.setVisibility(View.VISIBLE);
        } else {
            post.setVisibility(View.GONE);
            divider3.setVisibility(View.GONE);
        }
        friends.setEnabled(isAccount());

        divider1.setVisibility(isAccount() && !featureManager.available(Feature.SOCIAL) ? View.GONE : View.VISIBLE);

        if (!TextUtils.isEmpty(user.getCompany())) {
            companyName.setVisibility(View.VISIBLE);
            companyName.setText(user.getCompany());
        } else
            companyName.setVisibility(View.GONE);

        setUserName(user.getFullName());
        setDateOfBirth(DateTimeUtils.convertDateToString(user.getBirthDate(),
                DateFormat.getMediumDateFormat(context)));
        setEnrollDate(DateTimeUtils.convertDateToString(user.getEnrollDate(),
                DateFormat.getMediumDateFormat(context)));
        setUserId(user.getUsername());
        setFrom(user.getLocation());

        ProfileViewUtils.setUserStatus(user, userStatus, context.getResources());

        setAvatarImage(Uri.parse(user.getAvatar() == null ? "" : user.getAvatar().getMedium()));
        setUserPresence(user);
        setCoverImage(Uri.parse(user.getBackgroundPhotoUrl()));

        setTripImagesCount(user.getTripImagesCount());
        setBucketItemsCount(user.getBucketListItemsCount());
        setFriendsCount(user.getFriendsCount());
        if (isAccount()) {
            setRoviaBucks(df.format(user.getRoviaBucks()));
            setDreamTripPoints(df.format(user.getDreamTripsPoints()));
        } else {
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

        setBadgeValue();
    }

    private boolean isAccount() {
        return appSessionHolder.get().isPresent() && appSessionHolder.get().get().getUser().getId() == getModelObject().getId();
    }

    @Override
    public void prepareForReuse() {

    }

    private void setAvatarImage(Uri uri) {
        if (uri != null) {
            setImage(uri, userPhoto);
        }
    }

    private void setUserPresence(User user){
        userPhoto.setup(user, injectorProvider.get());
    }

    private void setCoverImage(Uri uri) {
        if (uri != null) {
            setImage(uri, userCover);
        }
    }

    private void setImage(Uri uri, SimpleDraweeView draweeView) {
        PipelineDraweeControllerBuilder builder = Fresco.newDraweeControllerBuilder();
        if (draweeView.getTag() != null) {
            if (uri.equals(draweeView.getTag())) {
                return;
            }
            builder.setLowResImageRequest(ImageRequest.fromUri((Uri) draweeView.getTag()));
        }
        builder.setOldController(draweeView.getController());
        builder.setImageRequest(ImageRequest.fromUri(uri));
        DraweeController dc = builder.build();
        draweeView.setController(dc);
        draweeView.setTag(uri);
    }

    private void setDateOfBirth(String format) {
        dateOfBirth.setText(format);
    }

    private void setFrom(String location) {
        etFrom.setText(location);
    }

    private void setUserName(String username) {
        userName.setText(username);
    }

    private void setUserId(String username) {
        etUserId.setText(username);
    }

    private void setEnrollDate(String date) {
        etEnroll.setText(date);
    }

    private void setTripImagesCount(int count) {
        int stringResource = QuantityHelper.chooseResource(count, R.string.profile_zero_trip_images,
                R.string.profile_trip_images, R.string.profile_trip_images);
        tripImages.setText(String.format(context.getString(stringResource), count));
    }

    private void setBucketItemsCount(int count) {
        int stringResource = QuantityHelper.chooseResource(count, R.string.profile_zero_bucket_list,
                R.string.profile_bucket_list, R.string.profile_bucket_list);
        buckets.setText(String.format(context.getString(stringResource), count));
    }

    private void setFriendsCount(int count) {
        int stringResource = QuantityHelper.chooseResource(count, R.string.empty,
                R.string.profile_friend_formatter, R.string.profile_friends_formatter);
        friends.setText(String.format(context.getString(stringResource), count));
    }

    private void setSocial(Boolean isEnabled) {
        addFriend.setEnabled(isEnabled);
        friendRequest.setEnabled(isEnabled);
        friendRequest.setEnabled(isEnabled);
    }

    private void setIsExpandEnabled(boolean isExpandEnabled) {
        this.isExpandEnabled = isExpandEnabled;
    }

    private void setRoviaBucks(String count) {
        roviaBucks.setText(Html.fromHtml(context.getString(R.string.profile_rovia_bucks, count)));
    }

    private void setDreamTripPoints(String count) {
        dtPoints.setText(Html.fromHtml(context.getString(R.string.profile_dt_points, count)));
    }

    private void setIsFriend(boolean isFriend) {
        addFriend.setText(isFriend ? R.string.profile_friends : R.string.profile_add_friend);
        addFriend.setCompoundDrawablesWithIntrinsicBounds(0,
                isFriend ? R.drawable.ic_profile_friend
                        : R.drawable.ic_profile_add_friend_selector,
                0, 0);
    }

    private void setWaiting() {
        addFriend.setText(R.string.profile_waiting);
        addFriend.setCompoundDrawablesWithIntrinsicBounds(0,
                R.drawable.ic_profile_friend_respond,
                0, 0);
    }

    private void setRespond() {
        addFriend.setText(R.string.profile_respond);
        addFriend.setCompoundDrawablesWithIntrinsicBounds(0,
                R.drawable.ic_profile_respond,
                0, 0);
    }

    private void showFriendRequest(String name) {
        friendRequestCaption.setText(String.format(context.getString(R.string.profile_friend_request), name));
        friendRequest.setVisibility(View.VISIBLE);
    }

    private void hideFriendRequest() {
        friendRequest.setVisibility(View.GONE);
    }

    @Override
    public void clearResources() {
        super.clearResources();
        if (getEventBus().isRegistered(this))
            getEventBus().unregister(this);
    }

    private void setBadgeValue() {
        if (isAccount()) {
            int badgeCount = snapper.getFriendsRequestsCount();
            if (badgeCount > 0) {
                badge.setVisibility(View.VISIBLE);
                badge.setText(String.valueOf(badgeCount));
            } else {
                badge.setVisibility(View.INVISIBLE);
            }
        } else {
            badge.setVisibility(View.INVISIBLE);
        }
    }

    /***
     * View Events
     *********************/

    @OnClick(R.id.bucket_list)
    protected void onBucketListClicked() {
        getEventBus().post(new OnBucketListClickedEvent(getModelObject().getId()));
        sendAnalyticIfNeed(TrackingHelper.ATTRIBUTE_SHOW_BUCKETLIST);
    }

    @OnClick(R.id.trip_images)
    protected void onTripImageClicked() {
        getEventBus().post(new OnTripImageClickedEvent(getModelObject().getId()));
        sendAnalyticIfNeed(TrackingHelper.ATTRIBUTE_SHOW_TRIPS);
    }

    @OnClick(R.id.friends)
    protected void onFriendsClick() {
        if (isAccount()) {
            getEventBus().post(new OnFriendsClickedEvent());
            sendAnalyticIfNeed(TrackingHelper.ATTRIBUTE_SHOW_FRIENDS);
        }
    }

    @OnClick(R.id.post)
    protected void onPostClick() {
        getEventBus().post(new OnCreatePostClickEvent());
        sendAnalyticIfNeed(TrackingHelper.ATTRIBUTE_NEW_POST);
    }

    @OnClick(R.id.user_photo)
    protected void onPhotoClick() {
        getEventBus().post(new OnPhotoClickEvent());

    }

    @OnClick(R.id.user_cover)
    protected void onCoverClick() {
        getEventBus().post(new OnCoverClickEvent());
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
                if (onExpandedListener != null) onExpandedListener.onItemExpanded(false);
            } else {
                info.show();
                more.setVisibility(View.INVISIBLE);
                if (onExpandedListener != null) onExpandedListener.onItemExpanded(true);
            }
        }
    }

    @Override
    public void setListener(OnExpandedListener expandedListener) {
        this.onExpandedListener = expandedListener;
    }

    @Override
    public void setExpanded(boolean expanded) {
        if (expanded) {
            info.showWithoutAnimation();
            more.setVisibility(View.INVISIBLE);
        } else {
            info.hideWithoutAnimation();
            more.setVisibility(View.VISIBLE);
        }
    }

    private void sendAnalyticIfNeed(String tapedButtonActionAttribute) {
        if (isAccount()) {
            TrackingHelper.tapMyProfileButton(tapedButtonActionAttribute);
        }
    }
}
