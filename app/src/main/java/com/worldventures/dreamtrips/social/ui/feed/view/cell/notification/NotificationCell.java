package com.worldventures.dreamtrips.social.ui.feed.view.cell.notification;

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
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.custom.SmartAvatarView;
import com.worldventures.dreamtrips.social.ui.feed.bundle.FeedItemDetailsBundle;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntityHolder.Type;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.feed.item.Links;
import com.worldventures.dreamtrips.social.ui.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.BaseMediaEntity;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.PhotoMediaEntity;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.args.TripImagesFullscreenArgs;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Photo;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import butterknife.InjectView;
import butterknife.Optional;
import timber.log.Timber;

@Layout(R.layout.adapter_item_notification)
public class NotificationCell extends AbstractCell<FeedItem> {

   @Optional @InjectView(R.id.notification_avatar) SmartAvatarView notificationAvatar;
   @Optional @InjectView(R.id.notification_owner) TextView notificationOwner;
   @Optional @InjectView(R.id.notification_text) TextView notificationText;
   @Optional @InjectView(R.id.notification_time) TextView notificationTime;
   @Optional @InjectView(R.id.notification_header_image) SimpleDraweeView notificationImage;

   @Inject @Named(RouteCreatorModule.PROFILE) RouteCreator<Integer> profileRouteCreator;
   @Inject SessionHolder appSessionHolder;
   @Inject @ForActivity Provider<Injector> injectorProvider;
   @Inject Router router;

   public NotificationCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      if (!appSessionHolder.get().isPresent()) return;

      User firstUser = getModelObject().getLinks().getUsers().get(0);
      notificationAvatar.setImageURI(Uri.parse(firstUser.getAvatar().getThumb()));
      notificationAvatar.setup(firstUser, injectorProvider.get());
      notificationOwner.setText(firstUser.getFullName());

      int currentAccountId = appSessionHolder.get().get().getUser().getId();
      notificationText.setText(Html.fromHtml(getModelObject().infoText(itemView.getResources(), currentAccountId)));
      notificationTime.setText(DateTimeUtils.getRelativeTimeSpanString(itemView.getResources(),
            getModelObject().getCreatedAt().getTime()));

      if (getModelObject().getType() == Type.UNDEFINED || getModelObject().getType() == Type.POST) {
         notificationImage.setVisibility(View.GONE);
      } else {
         notificationImage.setVisibility(View.VISIBLE);
         String url = getModelObject().previewImage(itemView.getResources());

         if (url != null) notificationImage.setImageURI(Uri.parse(url));
      }

      itemView.setOnClickListener(v -> open(getModelObject()));
      notificationAvatar.setOnClickListener(v -> openProfile(getModelObject().getLinks().getUsers().get(0)));
   }

   private void open(FeedItem item) {
      if (item.getType() != Type.UNDEFINED) openByType(item.getType(), item.getAction());
      else if (item.getAction() != null) openByAction(getModelObject().getLinks(), item.getAction());
      else Timber.w("Can't open event model by type or action");
   }

   private void openByType(Type type, FeedItem.Action action) {
      TrackingHelper.sendActionItemFeed(TrackingHelper.ATTRIBUTE_VIEW, getModelObject().getItem()
            .getUid(), getModelObject().getType());
      switch (type) {
         case PHOTO:
            if (action == FeedItem.Action.TAG_PHOTO) {
               openFullscreenPhoto();
               break;
            }
         case VIDEO:
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
            .manualOrientationActivity(true)
            .data(new FeedItemDetailsBundle.Builder().feedItem(getModelObject()).showAdditionalInfo(true).build())
            .build());
   }

   private void openFullscreenPhoto() {
      List<BaseMediaEntity> items = new ArrayList<>();
      items.add((new PhotoMediaEntity((Photo) getModelObject().getItem())));
      router.moveTo(Route.TRIP_IMAGES_FULLSCREEN, NavigationConfigBuilder.forActivity()
            .data(TripImagesFullscreenArgs.builder()
                  .mediaEntityList(items)
                  .build())
            .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
            .build());
   }
}
