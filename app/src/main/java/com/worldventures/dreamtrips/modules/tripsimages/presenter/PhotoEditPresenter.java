package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import android.net.Uri;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.api.GetFeedEntityQuery;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityChangedEvent;
import com.worldventures.dreamtrips.modules.trips.model.Location;
import com.worldventures.dreamtrips.modules.tripsimages.api.EditTripPhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.EditPhotoBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import icepick.State;


public class PhotoEditPresenter extends Presenter<PhotoEditPresenter.View> {

   @State Photo photo;

   public PhotoEditPresenter(EditPhotoBundle editPhotoBundle) {
      photo = editPhotoBundle.getPhoto();
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      updatePhotoInfo();
      syncUi();
      view.setupTaggingHolder(this.photo);
   }

   public void updatePhotoInfo() {
      doRequest(new GetFeedEntityQuery(photo.getFSId()), entity -> {
         this.photo = (Photo) entity.getItem();
         view.setupTaggingHolder(this.photo);
      }, spiceException -> view.setupTaggingHolder(photo));
   }

   private void syncUi() {
      Calendar calendar = Calendar.getInstance();
      Date photoDate = photo.getShotAt();
      if (photoDate != null) calendar.setTime(photoDate);

      int year = calendar.get(Calendar.YEAR);
      int month = calendar.get(Calendar.MONTH);
      int day = calendar.get(Calendar.DAY_OF_MONTH);
      int hour = calendar.get(Calendar.HOUR_OF_DAY);
      int minute = calendar.get(Calendar.MINUTE);

      onDataSet(year, month, day);
      onTimeSet(hour, minute);

      view.setImage(Uri.parse(photo.getFSImage().getUrl()));
      view.setTitle(photo.getTitle());
      view.setLocation(photo.getFSLocation());

      if (photo.getTags() != null) view.setTags(TextUtils.join(", ", photo.getTags()));
   }

   public void onDataSet(int year, int month, int day) {
      String format = DateTimeUtils.convertDateToString(year, month, day);
      view.setDate(format);
   }

   public void onTimeSet(int h, int m) {
      String format = DateTimeUtils.convertTimeToString(h, m);
      view.setTime(format);
   }

   public void saveAction() {
      view.setEnabledSaveButton(false);
      UploadTask imageUploadTask = new UploadTask();
      imageUploadTask.setTitle(view.getTitle());

      List<String> tags = Queryable.from(view.getTags().split(",")).map(String::trim).toList();
      imageUploadTask.setTags(new ArrayList<>(tags));
      imageUploadTask.setLatitude(0);
      imageUploadTask.setLongitude(0);
      imageUploadTask.setLocationName(view.getLocation());

      Date date = DateTimeUtils.dateFromString(view.getDate());
      Date time = DateTimeUtils.timeFromString(view.getTime());
      imageUploadTask.setShotAt(DateTimeUtils.mergeDateTime(date, time));

      doRequest(new EditTripPhotoCommand(photo.getUid(), imageUploadTask), updatedPhoto -> {
         photo.setTitle(view.getTitle());
         photo.setTags(new ArrayList<>(tags));
         Location location = new Location(0, 0);
         location.setName(view.getLocation());
         photo.setCoordinates(location);
         photo.setShotAt(DateTimeUtils.mergeDateTime(date, time));
         eventBus.post(new FeedEntityChangedEvent((updatedPhoto)));
         view.pushTags();
      }, spiceException -> {
         super.handleError(spiceException);
         view.setEnabledSaveButton(true);
      });
   }

   public interface View extends Presenter.View {

      void finish();

      void setImage(Uri uri);

      void setTitle(String title);

      void setLocation(String location);

      void setTags(String tags);

      String getLocation();

      String getTags();

      String getTitle();

      String getDate();

      void setDate(String format);

      String getTime();

      void setTime(String format);

      void setEnabledSaveButton(boolean enabled);

      void setupTaggingHolder(Photo photo);

      void pushTags();
   }
}
