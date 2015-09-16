package com.worldventures.dreamtrips.modules.feed.view.cell.notification;

import android.net.Uri;
import android.os.Bundle;
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
import com.worldventures.dreamtrips.modules.feed.model.BaseEventModel;
import com.worldventures.dreamtrips.modules.feed.view.fragment.CommentsFragment;
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

import static com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem.BucketType;

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
    @Named(RouteCreatorModule.BUCKET_DETAILS)
    RouteCreator<Integer> routeCreator;

    @Inject
    BucketItemManager bucketItemManager;
    @Inject
    ForeignBucketItemManager foreignBucketItemManager;

    public NotificationCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        User user = getModelObject().getItem().getUser();
        String thumb = user.getAvatar().getThumb();

        notificationAvatar.setImageURI(Uri.parse(thumb));
        notificationOwner.setText(user.getFullName());
        notificationText.setText(Html.fromHtml(getModelObject().infoText(itemView.getResources())));
        CharSequence relativeTimeSpanString = DateTimeUtils.getRelativeTimeSpanString(itemView.getResources(),
                getModelObject().getCreatedAt().getTime());
        notificationTime.setText(relativeTimeSpanString);

        notificationImage.setImageURI(Uri.parse(getModelObject().previewImage(itemView.getResources())));
        itemView.setOnClickListener(v -> {
            switch (getModelObject().getType()) {
                case TRIP:
                    openTrip((TripModel) getModelObject().getItem());
                    break;
                case PHOTO:
                    openPhoto(((IFullScreenObject) getModelObject().getItem()));
                    break;
                case BUCKET_LIST_ITEM:
                    openBucketDetails(((BucketItem) getModelObject().getItem()));
                    break;
                case POST:
                    openComments();
                    break;
                case UNDEFINED:
                    break;
            }
        });
    }

    private void openComments() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(CommentsFragment.EXTRA_FEED_ITEM, getModelObject());
        NavigationBuilder.create()
                .with(activityRouter)
                .args(bundle)
                .move(Route.COMMENTS);
    }

    private void openBucketDetails(BucketItem bucketItem) {
        BucketBundle bundle = new BucketBundle();

        BucketType bucketType = BucketType.valueOf(bucketItem.getType().toUpperCase());
        bundle.setType(bucketType);
        bundle.setBucketItemId(getModelObject().getItem().getUid());
        bucketItemManager.saveSingleBucketItem(bucketItem, bucketType);
        User user = getModelObject().getItem().getUser();
        Route route = routeCreator.createRoute(user.getId());
        if(route==Route.DETAIL_BUCKET){
            bucketItemManager.saveSingleBucketItem(bucketItem, bucketType);
        }else{
            foreignBucketItemManager.saveSingleBucketItem(bucketItem, bucketType);
        }
        NavigationBuilder.create()
                .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                .data(bundle)
                .with(activityRouter)
                .attach(route);
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

    @Override
    public void prepareForReuse() {

    }
}
