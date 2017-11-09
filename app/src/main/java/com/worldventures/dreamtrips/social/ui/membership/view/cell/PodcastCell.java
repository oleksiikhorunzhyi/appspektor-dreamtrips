package com.worldventures.dreamtrips.social.ui.membership.view.cell;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.core.modules.video.utils.CachedModelHelper;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.util.GraphicUtils;
import com.worldventures.core.ui.view.custom.PinProgressButton;
import com.worldventures.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractDelegateCell;
import com.worldventures.dreamtrips.social.ui.membership.model.Podcast;
import com.worldventures.dreamtrips.social.ui.membership.view.cell.delegate.PodcastCellDelegate;
import com.worldventures.dreamtrips.social.ui.video.cell.ProgressVideoCellHelper;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_podcast)
public class PodcastCell extends BaseAbstractDelegateCell<Podcast, PodcastCellDelegate> {

   @InjectView(R.id.image) SimpleDraweeView image;
   @InjectView(R.id.play) ImageView play;
   @InjectView(R.id.title) TextView title;
   @InjectView(R.id.category) TextView category;
   @InjectView(R.id.date) TextView date;
   @InjectView(R.id.duration) TextView duration;
   @InjectView(R.id.description) TextView description;
   @InjectView(R.id.download_progress) PinProgressButton downloadProgress;

   @Inject Context context;
   @Inject CachedModelHelper cachedModelHelper;

   private ProgressVideoCellHelper progressVideoCellHelper;

   public PodcastCell(View view) {
      super(view);
   }

   @Override
   public void afterInject() {
      super.afterInject();
      progressVideoCellHelper = new ProgressVideoCellHelper(downloadProgress, cachedModelHelper);
   }

   @Override
   protected void syncUIStateWithModel() {
      final Podcast podcast = getModelObject();
      final int imageSize = image.getResources().getDimensionPixelSize(R.dimen.size_huge);
      image.setController(GraphicUtils.provideFrescoResizingController(podcast.getImageUrl(), image.getController(),
            imageSize, imageSize));
      title.setText(podcast.getTitle());
      if (TextUtils.isEmpty(podcast.getCategory())) {
         category.setVisibility(View.GONE);
      } else {
         category.setVisibility(View.VISIBLE);
         category.setText(podcast.getCategory());
      }
      date.setText(DateTimeUtils.convertDateToString(podcast.getDate(), DateTimeUtils.PODCAST_DATE_FORMAT));

      if (podcast.getDuration() == 0) {
         duration.setVisibility(View.INVISIBLE);
      } else {
         duration.setVisibility(View.VISIBLE);
         duration.setText(String.format("%s %s", DateUtils.formatElapsedTime(podcast.getDuration()), context.getString(R.string.min)));
      }

      if (TextUtils.isEmpty(podcast.getDescription())) {
         description.setVisibility(View.GONE);
      } else {
         description.setVisibility(View.VISIBLE);
         description.setText(podcast.getDescription());
      }

      progressVideoCellHelper.setModelObject(podcast.getCachedModel());
      progressVideoCellHelper.syncUIStateWithModel();
   }

   @OnClick(R.id.play)
   public void onItemClick() {
      cellDelegate.play(getModelObject());
   }

   @OnClick(R.id.download_progress)
   public void onDownloadClick() {
      progressVideoCellHelper.onDownloadClick(cellDelegate, getModelObject());
   }

   @Override
   public void prepareForReuse() {
      image.setImageResource(0);
   }
}
