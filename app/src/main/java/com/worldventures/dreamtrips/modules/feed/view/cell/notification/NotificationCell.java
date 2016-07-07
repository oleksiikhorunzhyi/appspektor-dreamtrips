package com.worldventures.dreamtrips.modules.feed.view.cell.notification;

import android.net.Uri;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.session.SessionHolder;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.module.RouteCreatorModule;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.custom.SmartAvatarView;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedDetailsBundle;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder.Type;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.feed.item.Links;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.FullScreenImagesBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import butterknife.InjectView;
import butterknife.Optional;
import timber.log.Timber;

@Layout(R.layout.adapter_item_notification)
public class NotificationCell extends AbstractCell<FeedItem> {

    @Optional
    @InjectView(R.id.notification_avatar)
    SmartAvatarView notificationAvatar;
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
    @Named(RouteCreatorModule.PROFILE)
    RouteCreator<Integer> profileRouteCreator;

    @Inject
    SessionHolder<UserSession> appSessionHolder;

    @Inject
    @ForActivity
    Provider<Injector> injectorProvider;

    public NotificationCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        User user = getModelObject().getLinks().getUsers().get(0);
        String thumb = user.getAvatar().getThumb();

        notificationAvatar.setImageURI(Uri.parse(thumb));
        notificationAvatar.setup(user, injectorProvider.get());
        notificationOwner.setText(user.getFullName());
        int accountId = appSessionHolder.get().get().getUser().getId();
        notificationText.setText(Html.fromHtml(getModelObject().infoText(itemView.getResources(), accountId)));
        CharSequence relativeTimeSpanString = DateTimeUtils.getRelativeTimeSpanString(itemView.getResources(),
                getModelObject().getCreatedAt().getTime());
        notificationTime.setText(relativeTimeSpanString);

        if (getModelObject().getType() == Type.UNDEFINED || getModelObject().getType() == Type.POST) {
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

    private void open(FeedItem item) {
        if (item.getType() != Type.UNDEFINED)
            openByType(item.getType(), item.getAction());
        else if (item.getAction() != null)
            openByAction(getModelObject().getLinks(), item.getAction());
        else Timber.w("Can't open event model by type or action");
    }

    private void openByType(Type type, FeedItem.Action action) {
        switch (type) {
            case PHOTO:
                if (action == FeedItem.Action.TAG_PHOTO) {
                    openFullscreenPhoto();
                    break;
                }
            case TRIP:
            case BUCKET_LIST_ITEM:
            case POST:
                openDetails();
                break;
        }
    }

    private void openByAction(Links links, FeedItem.Action action) {
        switch (action) {
            case REJECT_REQUEST:
            case SEND_REQUEST:
            case ACCEPT_REQUEST:
                openProfile(links.getUsers().get(0));
        }
    }

    private void openProfile(User user) {
        router.moveTo(profileRouteCreator.createRoute(user.getId()), NavigationConfigBuilder.forActivity()
                .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                .data(new UserBundle(user))
                .build());
    }

    private void openDetails() {
        router.moveTo(Route.FEED_ITEM_DETAILS, NavigationConfigBuilder.forActivity()
                .data(new FeedDetailsBundle(getModelObject()))
                .build());
    }

    private void openFullscreenPhoto() {
        ArrayList<IFullScreenObject> list = new ArrayList<>();
        list.add((IFullScreenObject) getModelObject().getItem());
        FullScreenImagesBundle bundle = new FullScreenImagesBundle.Builder()
                .position(0)
                .userId(getModelObject().getItem().getOwner().getId())
                .type(TripImagesType.FIXED)
                .route(Route.SOCIAL_IMAGE_FULLSCREEN)
                .fixedList(list)
                .build();

        router.moveTo(Route.FULLSCREEN_PHOTO_LIST, NavigationConfigBuilder.forActivity()
                .data(bundle)
                .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                .build());
    }

    @Override
    public void prepareForReuse() {

    }
}
