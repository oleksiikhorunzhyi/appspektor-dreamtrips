package com.worldventures.dreamtrips.modules.feed.presenter;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.session.SessionHolder;
import com.techery.spares.storage.complex_objects.Optional;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.util.DrawableUtil;
import com.worldventures.dreamtrips.modules.common.view.util.Size;
import com.worldventures.dreamtrips.modules.media_picker.model.PhotoPickerModel;
import com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils;

import java.util.ArrayList;
import java.util.List;

import icepick.Icepick;
import icepick.State;
import rx.Observable;
import rx.Subscriber;
import timber.log.Timber;

public class SuggestedPhotoCellPresenterHelper {

   public static final int MAX_SELECTION_SIZE = 15;
   private static final int SUGGESTION_ITEM_CHUNK = 20;
   private static final long DEFAULT_START_SYNC_TIMESTAMP = Long.MAX_VALUE;

   private Context context;
   private SnappyRepository db;
   private SessionHolder<UserSession> appSessionHolder;
   private DrawableUtil drawableUtil;

   @State ArrayList<PhotoPickerModel> suggestionItems;
   @State ArrayList<PhotoPickerModel> selectedPhotos;
   @State long syncTimestampLast = DEFAULT_START_SYNC_TIMESTAMP;

   private View view;
   private Observable.Transformer<List<PhotoPickerModel>, List<PhotoPickerModel>> stopper;

   public SuggestedPhotoCellPresenterHelper(Context context, SnappyRepository db, SessionHolder<UserSession> appSessionHolder,
         DrawableUtil drawableUtil) {
      this.context = context;
      this.db = db;
      this.appSessionHolder = appSessionHolder;
      this.drawableUtil = drawableUtil;
   }

   public void takeView(View view, Observable.Transformer<List<PhotoPickerModel>, List<PhotoPickerModel>> stopper, Bundle bundle) {
      checkView(view);
      this.view = view;
      this.stopper = stopper;

      restoreInstanceState(bundle);

      if (suggestionItems == null) {
         suggestionItems = new ArrayList<>(SUGGESTION_ITEM_CHUNK);
      }
      if (selectedPhotos == null) {
         selectedPhotos = new ArrayList<>(MAX_SELECTION_SIZE);
      }

      if (suggestionItems.isEmpty()) {
         preloadSuggestionPhotos(null);
      } else {
         view.appendPhotoSuggestions(suggestionItems);
      }
   }

   public void preloadSuggestionPhotos(@Nullable PhotoPickerModel model) {
      syncTimestampLast = getLastSyncOrDefault(model);

      getSuggestionObservable(syncTimestampLast)
            .compose(stopper)
            .subscribe(photoGalleryModels -> {
               suggestionItems.addAll(photoGalleryModels);
               view.appendPhotoSuggestions(photoGalleryModels);
            }, throwable -> Timber.e(throwable, "Cannot prefetch suggestions"));
   }

   public void subscribeNewPhotoNotifications(Observable<Void> notificationObservable) {
      notificationObservable.concatMap(aVoid -> getSuggestionObservable(DEFAULT_START_SYNC_TIMESTAMP))
            .compose(stopper)
            .subscribe(photoGalleryModels -> {
               clearCache();
               resetSyncTimestamp();
               sync();
               suggestionItems.addAll(photoGalleryModels);
               view.replacePhotoSuggestions(photoGalleryModels);
            }, throwable -> Timber.e(throwable, "Cannot fetch new suggestion items"));
   }

   public void sync() {
      Optional<UserSession> userSessionOptional = appSessionHolder.get();
      if (userSessionOptional.isPresent()) {
         view.setUser(userSessionOptional.get().getUser());
      }
      setSuggestionTitle();
   }

   public long lastSyncTime() {
      return syncTimestampLast;
   }

   public void reset() {
      clearCacheAndUpdate();

      db.saveLastSuggestedPhotosSyncTime(System.currentTimeMillis());
      resetSyncTimestamp();
   }

   public void selectPhoto(PhotoPickerModel model) {
      int selectedSize = selectedPhotos.size();
      boolean isChecked = !model.isChecked();

      if (isChecked) {
         if (selectedSize == MAX_SELECTION_SIZE) {
            view.showMaxSelectionMessage();
            return;
         }
         selectedPhotos.add(model);
      } else {
         selectedPhotos.remove(model);
      }
      model.setChecked(isChecked);

      setSuggestionTitle();
   }

   public Observable<MediaAttachment> mediaAttachmentObservable() {
      return Observable.from(selectedPhotos)
            .map(element -> {
               Pair<String, Size> pair = ImageUtils.generateUri(drawableUtil, element.getAbsolutePath());
               return new PhotoPickerModel(pair.first, pair.second);
            })
            .map(photoGalleryModel -> {
               List<PhotoPickerModel> chosenImages = new ArrayList<>();
               chosenImages.add(photoGalleryModel);
               return new MediaAttachment(chosenImages, MediaAttachment.Source.GALLERY);
            });
   }

   void saveInstanceState(Bundle bundle) {
      Icepick.saveInstanceState(this, bundle);
      // can happen if it hasn't taken view yet
      if (view != null) view.saveInstanceState(bundle);
   }

   private void restoreInstanceState(Bundle bundle) {
      Icepick.restoreInstanceState(this, bundle);
      view.restoreInstanceState(bundle);
   }

   @NonNull
   private Observable<List<PhotoPickerModel>> getSuggestionObservable(long toTimestamp) {
      return Observable.create(new Observable.OnSubscribe<PhotoPickerModel>() {
         @Override
         public void call(Subscriber<? super PhotoPickerModel> subscriber) {
            Cursor cursor = null;
            String[] projectionPhotos = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_TAKEN};
            //
            try {
               cursor = MediaStore.Images.Media.query(context.getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projectionPhotos, selection(), new String[]{String.valueOf(toTimestamp), ImageUtils.MIME_TYPE_GIF}, MediaStore.Images.Media.DATE_TAKEN + " DESC LIMIT " + SUGGESTION_ITEM_CHUNK);

               int dataColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
               int dateColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);
               while (cursor.moveToNext()) {
                  String path = cursor.getString(dataColumn);
                  long dateTaken = cursor.getLong(dateColumn);

                  if (!subscriber.isUnsubscribed() && !ImageUtils.getImageExtensionFromPath(path)
                        .toLowerCase()
                        .contains("gif")) {
                     subscriber.onNext(new PhotoPickerModel(path, dateTaken));
                  }
               }

               if (!subscriber.isUnsubscribed()) {
                  subscriber.onCompleted();
               }
            } catch (Exception e) {
               Timber.e(e, "Cannot fetch suggestions");

               if (!subscriber.isUnsubscribed()) {
                  subscriber.onError(e);
               }
            } finally {
               if (cursor != null) {
                  cursor.close();
               }
            }
         }
      }).toList().compose(new IoToMainComposer<>());
   }

   @NonNull
   private String selection() {
      return MediaStore.Images.Media.DATE_TAKEN + " < " +
            " ? AND " + MediaStore.Images.Media.MIME_TYPE + " != ?";
   }

   private void checkView(View view) {
      if (this.view != null) {
         if (this.view != view) {
            throw new AssertionError("Cannot take another view");
         }
      }
   }

   private void setSuggestionTitle() {
      view.setSuggestionTitle(selectedPhotos.size());
   }

   private long getLastSyncOrDefault(@Nullable PhotoPickerModel model) {
      return model == null ? DEFAULT_START_SYNC_TIMESTAMP : model.getDateTaken();
   }

   private void clearCacheAndUpdate() {
      clearCache();
      view.notifyListChange();
   }

   private void clearCache() {
      Queryable.from(selectedPhotos).forEachR(model -> model.setChecked(false));

      selectedPhotos.clear();
      suggestionItems.clear();
   }

   private void resetSyncTimestamp() {
      syncTimestampLast = Long.MAX_VALUE;
   }

   public interface View {

      void appendPhotoSuggestions(List<PhotoPickerModel> items);

      void replacePhotoSuggestions(List<PhotoPickerModel> items);

      void notifyListChange();

      void setUser(User user);

      void setSuggestionTitle(int sizeOfSelectedPhotos);

      void showMaxSelectionMessage();

      void saveInstanceState(@Nullable Bundle bundle);

      void restoreInstanceState(@Nullable Bundle bundle);
   }
}