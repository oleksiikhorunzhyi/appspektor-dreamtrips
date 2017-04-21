package com.worldventures.dreamtrips.modules.common.command;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;
import com.worldventures.dreamtrips.modules.video.model.VideoCategory;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class UpdateStatusCachedEntityCommand extends Command<List<VideoCategory>> implements InjectableAction {

   @Inject SnappyRepository db;

   private final List<VideoCategory> videoCategories;

   public UpdateStatusCachedEntityCommand(List<VideoCategory> videoCategories) {
      this.videoCategories = videoCategories;
   }

   @Override
   protected void run(CommandCallback<List<VideoCategory>> callback) throws Throwable {
      Observable.just(videoCategories)
            .map(categories -> {
               Queryable.from(categories).forEachR(cat -> Queryable.from(cat.getVideos()).forEachR(video -> {
                  CachedEntity e = db.getDownloadMediaEntity(video.getUid());
                  video.setCacheEntity(e);
               }));
               return categories;
            })
            .subscribe(callback::onSuccess, callback::onFail);
   }
}
