package com.worldventures.dreamtrips.modules.membership.view.cell;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.common.view.custom.PinProgressButton;
import com.worldventures.dreamtrips.modules.membership.model.Podcast;
import com.worldventures.dreamtrips.modules.membership.view.cell.delegate.PodcastCellDelegate;
import com.worldventures.dreamtrips.modules.video.cell.ProgressVideoCellHelper;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_podcast)
public class PodcastCell extends AbstractDelegateCell<Podcast, PodcastCellDelegate> {

   @InjectView(R.id.image) SimpleDraweeView image;
   @InjectView(R.id.play) ImageView play;
   @InjectView(R.id.title) TextView title;
   @InjectView(R.id.category) TextView category;
   @InjectView(R.id.date) TextView date;
   @InjectView(R.id.duration) TextView duration;
   @InjectView(R.id.description) TextView description;
   @InjectView(R.id.download_progress) PinProgressButton downloadProgress;

   @Inject Context context;

   private ProgressVideoCellHelper progressVideoCellHelper;

   public PodcastCell(View view) {
      super(view);
      progressVideoCellHelper = new ProgressVideoCellHelper(downloadProgress);
   }

   @Override
   protected void syncUIStateWithModel() {
      Podcast podcast = getModelObject();
      image.setImageURI(Uri.parse(podcast.getImageUrl()));
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

      progressVideoCellHelper.setModelObject(podcast.getCacheEntity());
      progressVideoCellHelper.syncUIStateWithModel();
   }

   @OnClick(R.id.play)
   public void onItemClick() {
      cellDelegate.play(getModelObject());
   }

   @OnClick(R.id.download_progress)
   public void onDownloadClick() {
      progressVideoCellHelper.onDownloadClick(cellDelegate);
   }

   @Override
   public void prepareForReuse() {
      image.setImageResource(0);
   }
}
