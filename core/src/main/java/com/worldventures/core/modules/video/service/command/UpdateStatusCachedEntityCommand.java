package com.worldventures.core.modules.video.service.command;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.janet.dagger.InjectableAction;
import com.worldventures.core.model.CachedModel;
import com.worldventures.core.modules.video.model.VideoCategory;
import com.worldventures.core.modules.video.service.storage.MediaModelStorage;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class UpdateStatusCachedEntityCommand extends Command<List<VideoCategory>> implements InjectableAction {

   @Inject MediaModelStorage db;

   private final List<VideoCategory> videoCategories;

   public UpdateStatusCachedEntityCommand(List<VideoCategory> videoCategories) {
      this.videoCategories = videoCategories;
   }

   @Override
   protected void run(CommandCallback<List<VideoCategory>> callback) throws Throwable {
      Observable.just(videoCategories)
            .map(categories -> {
               Queryable.from(categories).forEachR(cat -> Queryable.from(cat.getVideos()).forEachR(video -> {
                  CachedModel e = db.getDownloadMediaModel(video.getUid());
                  video.setCacheEntity(e);
               }));
               return categories;
            })
            .subscribe(callback::onSuccess, callback::onFail);
   }
}
