package com.worldventures.dreamtrips.modules.feed.view.cell.notification;

import android.net.Uri;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.module.RouteCreatorModule;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.bucketlist.manager.BucketItemManager;
import com.worldventures.dreamtrips.modules.bucketlist.manager.ForeignBucketItemManager;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.bundle.BucketBundle;
import com.worldventures.dreamtrips.modules.feed.bundle.CommentsBundle;
import com.worldventures.dreamtrips.modules.feed.model.BaseEventModel;
import com.worldventures.dreamtrips.modules.feed.model.IFeedObject;
import com.worldventures.dreamtrips.modules.feed.model.feed.item.Links;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.trips.view.bundle.TripDetailsBundle;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.FullScreenImagesBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.InjectView;
import butterknife.Optional;
import timber.log.Timber;

import static com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem.BucketType;
import static com.worldventures.dreamtrips.modules.feed.model.BaseEventModel.Type.UNDEFINED;

@Layout(R.layout.adapter_item_notification)
public class NotificationCell extends AbstractCell<BaseEventModel> {

    @Optional
    @InjectView(R.id.notification_avatar)
    SimpleDraweeView notificationAvatar;
    @Optional
    @InjectView(R.id.notification_owner)
    TextView notificationOwner;
    @Optional
    @InjectView(R.id.notification_text)
    TextView notificationText;
    @Optional
    @InjectView(R.id.notification_time)
    TextView notificationTime;
    @Optional
    @InjectView(R.id.notification_header_image)
    SimpleDraweeView notificationImage;

    @Inject
    ActivityRouter activityRouter;
    @Inject
    @Named(RouteCreatorModule.PROFILE)
    RouteCreator<Integer> profileRouteCreator;
    @Inject
    @Named(RouteCreatorModule.BUCKET_DETAILS)
    RouteCreator<Integer> bucketRouteCreator;

    @Inject
    BucketItemManager bucketItemManager;
    @Inject
    ForeignBucketItemManager foreignBucketItemManager;

    public NotificationCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        User user = getModelObject().getLinks().getUsers().get(0);
        String thumb = user.getAvatar().getThumb();

        notificationAvatar.setImageURI(Uri.parse(thumb));
        notificationOwner.setText(user.getFullName());
        notificationText.setText(Html.fromHtml(getModelObject().infoText(itemView.getResources())));
        CharSequence relativeTimeSpanString = DateTimeUtils.getRelativeTimeSpanString(itemView.getResources(),
                getModelObject().getCreatedAt().getTime());
        notificationTime.setText(relativeTimeSpanString);

        if (getModelObject().getType() == null) {
            notificationImage.setVisibility(View.GONE);
        } else {
            notificationImage.setVisibility(View.VISIBLE);
            String url = getModelObject().previewImage(itemView.getResources());

            if (url != null)
                notificationImage.setImageURI(Uri.parse(url));
        }

        itemView.setOnClickListener(v -> open(getModelObject()));
        notificationAvatar.setOnClickListener(v -> openProfile(getModelObject().getLinks().getUsers().get(0)));
    }

    private void open(BaseEventModel item) {
        if (item.getType() != UNDEFINED) openByType(getModelObject().getItem(), item.getType());
        else if (item.getAction() != null)
            openByAction(getModelObject().getLinks(), item.getAction());
        else Timber.w("Can't open event model by type or action");
    }

    private void openByType(IFeedObject item, BaseEventModel.Type type) {
        switch (type) {
            case TRIP:
                openTrip((TripModel) item);
                break;
            case PHOTO:
                openPhoto(((IFullScreenObject) getModelObject().getItem()));
                break;
            case BUCKET_LIST_ITEM:
            case POST:
                openComments();
                break;
        }
    }

    private void openByAction(Links links, BaseEventModel.Action action) {
        switch (action) {
            case REJECT_REQUEST:
            case SEND_REQUEST:
            case ACCEPT_REQUEST:
                openProfile(links.getUsers().get(0));
        }
    }

    private void openProfile(User user) {
        NavigationBuilder.create()
                .with(activityRouter)
                .data(new UserBundle(user))
                .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                .move(profileRouteCreator.createRoute(user.getId()));
    }

    private void openTrip(TripModel tripModel) {
        NavigationBuilder.create()
                .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                .with(activityRouter)
                .data(new TripDetailsBundle(tripModel))
                .attach(Route.DETAILED_TRIP);
    }

    private void openPhoto(IFullScreenObject item) {
        ArrayList<IFullScreenObject> items = new ArrayList<>();
        items.add(item);

        FullScreenImagesBundle data = new FullScreenImagesBundle.Builder()
                .position(0)
                .type(TripImagesListFragment.Type.FIXED_LIST)
                .fixedList(items)
                .build();

        NavigationBuilder.create()
                .with(activityRouter)
                .data(data)
                .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                .move(Route.FULLSCREEN_PHOTO_LIST);
    }

    private void openComments() {
        NavigationBuilder.create()
                .with(activityRouter)
                .data(new CommentsBundle(getModelObject()))
                .move(Route.COMMENTS);
    }

    private void openBucketDetails(BucketItem bucketItem) {
        BucketBundle bundle = new BucketBundle();

        BucketType bucketType = BucketType.valueOf(bucketItem.getType().toUpperCase());
        bundle.setType(bucketType);
        bundle.setBucketItemId(getModelObject().getItem().getUid());
        bucketItemManager.saveSingleBucketItem(bucketItem, bucketType);
        User user = getModelObject().getLinks().getUsers().get(0);
        Route route = bucketRouteCreator.createRoute(user.getId());
        if (route == Route.DETAIL_BUCKET) {
            bucketItemManager.saveSingleBucketItem(bucketItem, bucketType);
        } else {
            foreignBucketItemManager.saveSingleBucketItem(bucketItem, bucketType);
        }
        NavigationBuilder.create()
                .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                .data(bundle)
                .with(activityRouter)
                .attach(route);
    }

    @Override
    public void prepareForReuse() {

    }
}
