package com.worldventures.dreamtrips.social.ui.feed.view.cell;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.di.qualifier.ForActivity;
import com.worldventures.core.janet.Injector;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.view.adapter.BaseDelegateAdapter;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.social.ui.feed.model.video.Video;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.BaseMediaEntity;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Photo;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.PhotoMediaEntity;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.args.TripImagesFullscreenArgs;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment.TripImagesFullscreenFragment;

import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.Optional;

@Layout(R.layout.adapter_item_feed_post_details)
public class PostFeedItemDetailsCell extends PostFeedItemCell {

   @InjectView(R.id.item_holder) View itemHolder;
   @Optional @InjectView(R.id.imagesList) RecyclerView imagesList;

   @Inject @ForActivity Injector injector;

   private BaseDelegateAdapter adapter;
   private LinearLayoutManager layout;

   public PostFeedItemDetailsCell(View view) {
      super(view);
   }

   @Override
   public void afterInject() {
      super.afterInject();
      adapter = new BaseDelegateAdapter<>(itemHolder.getContext(), injector);
      adapter.registerCell(Photo.class, SubPhotoAttachmentCell.class);
      adapter.registerDelegate(Photo.class, new CellDelegate<Photo>() {
         @Override
         public void onCellClicked(Photo model) {
            openFullsreenPhoto(model);
         }
      });
      layout = new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.VERTICAL, false);
      layout.setAutoMeasureEnabled(true);
   }

   @Override
   protected void syncUIStateWithModel() {
      super.syncUIStateWithModel();
      imagesList.setLayoutManager(layout);
      if (adapter != imagesList.getAdapter()) {
         imagesList.setAdapter(adapter);
      }
      dtVideoView.setThumbnailAction(this::playVideoIfNeeded);
      if (!displayingInList && !dtVideoView.isVideoInProgress()) {
         List<FeedEntityHolder> attachments = getModelObject().getItem().getAttachments();
         if (!attachments.isEmpty() && attachments.get(0).getItem() instanceof Video) {
            Video video = (Video) getModelObject().getItem().getAttachments().get(0).getItem();
            if (playerExistsAndCurrentItemIsSame(video)) {
               if (videoPlayerHolder.inFullscreen()) {
                  switchFromFullscreen();
               } else {
                  reattachVideo();
                  dtVideoView.pauseVideo();
               }
            }
         }
      }
   }

   private void openFullsreenPhoto(Photo model) {
      router.moveTo(TripImagesFullscreenFragment.class, NavigationConfigBuilder.forActivity()
            .manualOrientationActivity(true)
            .data(TripImagesFullscreenArgs.builder()
                  .currentItemPosition(getPositionOfPhoto(model))
                  .mediaEntityList(Queryable.from(getModelObject().getItem().getAttachments())
                        .filter(element -> element.getType() == FeedEntityHolder.Type.PHOTO)
                        .map(element -> {
                           Photo photo = (Photo) element.getItem();
                           photo.setOwner(getModelObject().getItem().getOwner());
                           return (BaseMediaEntity) new PhotoMediaEntity(photo);
                        })
                        .toList())
                  .build())
            .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
            .build());
   }

   private int getPositionOfPhoto(Photo model) {
      int result = 0;
      List<FeedEntityHolder> attachments = getModelObject().getItem().getAttachments();
      for (int i = 0; i < attachments.size(); i++) {
         FeedEntityHolder feedEntityHolder = attachments.get(i);
         if (feedEntityHolder.getItem().equals(model)) {
            result = i;
         }
      }
      return result;
   }

   @Override
   protected void processPhotos() {
      adapter.clear();
      Queryable.from(getModelObject().getItem().getAttachments()).forEachR(itemHolder -> {
         if (itemHolder.getItem() instanceof Photo) {
            adapter.addItem(itemHolder.getItem());
         }
      });
      adapter.notifyDataSetChanged();
   }

   @Override
   protected void clearImages() {
      adapter.clear();
   }

   @Override
   protected void onMore() {
      showMoreDialog(R.menu.menu_feed_entity_edit, R.string.post_delete, R.string.post_delete_caption);
   }

   @Override
   public void clearResources() {
      super.clearResources();
      dtVideoView.pauseVideo();
   }
}
