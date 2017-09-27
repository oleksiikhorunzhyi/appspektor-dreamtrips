package com.worldventures.dreamtrips.modules.media_picker.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.media_picker.model.MediaPickerModel;
import com.worldventures.dreamtrips.modules.media_picker.model.MediaPickerModelImpl;
import com.worldventures.dreamtrips.modules.common.service.MediaInteractor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class GetMediaFromGalleryCommand extends Command<List<MediaPickerModel>> implements InjectableAction {

   @Inject MediaInteractor mediaInteractor;

   private final boolean queryVideos;
   private final int count;

   public GetMediaFromGalleryCommand(boolean queryVideos) {
      this(queryVideos, Integer.MAX_VALUE);
   }

   public GetMediaFromGalleryCommand(boolean queryVideos, int count) {
      this.queryVideos = queryVideos;
      this.count = count;
   }

   @Override
   protected void run(CommandCallback<List<MediaPickerModel>> callback) throws Throwable {
      Observable.zip(getPhotosObservable(), getVideosObservable(), this::zipPhotosAndVideos)
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private Observable<List<MediaPickerModelImpl>> getPhotosObservable() {
      return mediaInteractor.getPhotosFromGalleryPipe().createObservableResult(new GetPhotosFromGalleryCommand(count))
            .map(getPhotosFromGalleryCommand -> new ArrayList<MediaPickerModelImpl>(getPhotosFromGalleryCommand.getResult()));
   }

   private Observable<List<MediaPickerModelImpl>> getVideosObservable() {
      if (!queryVideos) return Observable.just(new ArrayList<>());
      return mediaInteractor.getVideosFromGalleryPipe().createObservableResult(new GetVideosFromGalleryCommand(count))
         .map(getVideosFromGalleryCommand -> new ArrayList<MediaPickerModelImpl>(getVideosFromGalleryCommand.getResult()));
   }

   private List<MediaPickerModel> zipPhotosAndVideos(List<MediaPickerModelImpl> photos, List<MediaPickerModelImpl> videos) {
      List<MediaPickerModel> mediaItemsList = new ArrayList<>();
      mediaItemsList.addAll(photos);
      mediaItemsList.addAll(videos);
      Collections.sort(mediaItemsList, (mediaItem1, mediaItem2)
            -> compareDatesDescending(mediaItem1.getDateTaken(), mediaItem2.getDateTaken()));
      return mediaItemsList.size() > count? mediaItemsList.subList(0, count) : mediaItemsList;
   }

   private int compareDatesDescending(long date1, long date2) {
      if (date1 == date2) return 0;
      return date1 < date2 ? 1 : -1;
   }
}
