package com.worldventures.dreamtrips.social.ui.feed.view.cell.notification;

import android.net.Uri;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.core.di.qualifier.ForActivity;
import com.worldventures.core.janet.Injector;
import com.worldventures.core.model.User;
import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.service.analytics.AnalyticsInteractor;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.module.FragmentClassProviderModule;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.creator.FragmentClassProvider;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.modules.common.utils.TimeUtils;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractCell;
import com.worldventures.dreamtrips.social.ui.feed.bundle.FeedItemDetailsBundle;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntityHolder.Type;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.feed.item.Links;
import com.worldventures.dreamtrips.social.ui.feed.service.analytics.ViewFeedEntityAction;
import com.worldventures.dreamtrips.social.ui.feed.view.fragment.FeedItemDetailsFragment;
import com.worldventures.dreamtrips.social.ui.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.social.ui.profile.view.widgets.SmartAvatarView;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.BaseMediaEntity;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Photo;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.PhotoMediaEntity;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.args.TripImagesFullscreenArgs;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment.TripImagesFullscreenFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import butterknife.InjectView;
import butterknife.Optional;
import timber.log.Timber;

@Layout(R.layout.adapter_item_notification)
public class NotificationCell extends BaseAbstractCell<FeedItem> {

   @Optional @InjectView(R.id.notification_avatar) SmartAvatarView notificationAvatar;
   @Optional @InjectView(R.id.notification_owner) TextView notificationOwner;
   @Optional @InjectView(R.id.notification_text) TextView notificationText;
   @Optional @InjectView(R.id.notification_time) TextView notificationTime;
   @Optional @InjectView(R.id.notification_header_image) SimpleDraweeView notificationImage;

   @Inject @Named(FragmentClassProviderModule.PROFILE) FragmentClassProvider<Integer> profileFragmentClassProvider;
   @Inject SessionHolder appSessionHolder;
   @Inject @ForActivity Provider<Injector> injectorProvider;
   @Inject Router router;
   @Inject AnalyticsInteractor analyticsInteractor;

   public NotificationCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      if (!appSessionHolder.get().isPresent()) {
         return;
      }

      User firstUser = getModelObject().getLinks().getUsers().get(0);
      notificationAvatar.setImageURI(Uri.parse(firstUser.getAvatar().getThumb()));
      notificationAvatar.setup(firstUser, injectorProvider.get());
      notificationOwner.setText(firstUser.getFullName());

      int currentAccountId = appSessionHolder.get().get().user().getId();
      notificationText.setText(Html.fromHtml(getModelObject().infoText(itemView.getResources(), currentAccountId)));
      notificationTime.setText(TimeUtils.getRelativeTimeSpanString(itemView.getResources(),
            getModelObject().getCreatedAt().getTime()));

      if (getModelObject().getType() == Type.UNDEFINED || getModelObject().getType() == Type.POST) {
         notificationImage.setVisibility(View.GONE);
      } else {

         notificationImage.setVisibility(View.VISIBLE);
         String url = getModelObject().previewImage(itemView.getResources());

         if (url != null) {
            notificationImage.setImageURI(Uri.parse(url));
         }
      }

      itemView.setOnClickListener(v -> open(getModelObject()));
      notificationAvatar.setOnClickListener(v -> openProfile(getModelObject().getLinks().getUsers().get(0)));
   }

   private void open(FeedItem item) {
      if (item.getType() != Type.UNDEFINED) {
         openByType(item.getType(), item.getAction());
      } else if (item.getAction() != null) {
         openByAction(getModelObject().getLinks(), item.getAction());
      } else {
         Timber.w("Can't open event model by type or action");
      }
   }

   private void openByType(Type type, FeedItem.Action action) {
      analyticsInteractor.analyticsActionPipe().send(ViewFeedEntityAction.view(getModelObject().getType(),
            getModelObject().getItem().getUid()));

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
         default:
            break;
      }
   }

   private void openByAction(Links links, FeedItem.Action action) {
      switch (action) {
         case REJECT_REQUEST:
         case SEND_REQUEST:
         case ACCEPT_REQUEST:
            openProfile(links.getUsers().get(0));
         default:
            break;
      }
   }

   private void openProfile(User user) {
      router.moveTo(profileFragmentClassProvider.provideFragmentClass(user.getId()), NavigationConfigBuilder.forActivity()
            .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
            .data(new UserBundle(user))
            .build());
   }

   private void openDetails() {
      router.moveTo(FeedItemDetailsFragment.class, NavigationConfigBuilder.forActivity()
            .data(new FeedItemDetailsBundle.Builder().feedItem(getModelObject()).showAdditionalInfo(true).build())
            .build());
   }

   private void openFullscreenPhoto() {
      List<BaseMediaEntity> items = new ArrayList<>();
      items.add(new PhotoMediaEntity((Photo) getModelObject().getItem()));
      router.moveTo(TripImagesFullscreenFragment.class, NavigationConfigBuilder.forActivity()
            .manualOrientationActivity(true)
            .data(TripImagesFullscreenArgs.builder()
                  .mediaEntityList(items)
                  .build())
            .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
            .build());
   }
}
