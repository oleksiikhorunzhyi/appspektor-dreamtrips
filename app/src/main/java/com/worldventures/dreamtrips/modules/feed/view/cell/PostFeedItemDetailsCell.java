package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.modules.tripsimages.view.args.TripImagesFullscreenArgs;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

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
      imagesList.setLayoutManager(layout);
      if (adapter != imagesList.getAdapter()) imagesList.setAdapter(adapter);

      super.syncUIStateWithModel();
   }

   private void openFullsreenPhoto(Photo model) {
       router.moveTo(Route.TRIP_IMAGES_FULLSCREEN, NavigationConfigBuilder.forActivity()
            .data(TripImagesFullscreenArgs.builder()
                  .currentItemPosition(getPositionOfPhoto(model))
                  .mediaEntityList(Queryable.from(getModelObject().getItem().getAttachments())
                        .filter(element -> element.getType() == FeedEntityHolder.Type.PHOTO)
                        .map(element -> {
                           Photo photo = (Photo) element.getItem();
                           photo.setUser(getModelObject().getItem().getOwner());
                           return photo.castToMediaEntity();
                        })
                        .toList())
                  .build())
            .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
            .build());
   }

   protected int getPositionOfPhoto(Photo model) {
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
}
