package com.worldventures.dreamtrips.modules.background_uploading.service.command.video;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.background_uploading.model.CompoundOperationState;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostCompoundOperationModel;
import com.worldventures.dreamtrips.modules.background_uploading.model.video.VideoProcessStatus;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.video.http.CheckVideoProcessingHttpAction;
import com.worldventures.dreamtrips.modules.background_uploading.storage.CompoundOperationRepository;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;
import rx.schedulers.Schedulers;

@CommandAction
public class UpdateVideoProcessStatusCommand extends Command<Void> implements InjectableAction {

   @Inject CompoundOperationRepository compoundOperationRepository;
   @Inject SessionHolder<UserSession> userSessionHolder;
   @Inject Janet janet;

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      String memberId = userSessionHolder.get().get().getUser().getUsername();
      String ssoToken = userSessionHolder.get().get().getLegacyApiToken();
      Observable.just(obtainProcessingVideoIds())
            .flatMap(ids -> janet.createPipe(CheckVideoProcessingHttpAction.class, Schedulers.io())
                  .createObservableResult(new CheckVideoProcessingHttpAction(ids, memberId, ssoToken)))
            .map(httpAction -> httpAction.getBunchStatus().getVideoProcessStatuses())
            .doOnNext(this::updateStatusOnStorage)
            .subscribe(videoProcessBunchStatus -> callback.onSuccess(null), callback::onFail);
   }

   private List<String> obtainProcessingVideoIds() {
      return Queryable.from(compoundOperationRepository.readCompoundOperations())
            .filter(element -> element.state() == CompoundOperationState.PROCESSING)
            .map(element -> Integer.toString(element.id())) // todo replace id() of entity with tempId of video attachment
            .toList();
   }

   private void updateStatusOnStorage(List<VideoProcessStatus> videoProcessStatuses) {
      List<PostCompoundOperationModel> postCompoundModels = compoundOperationRepository.readCompoundOperations();
      for (VideoProcessStatus videoProcessStatus : videoProcessStatuses) {
         PostCompoundOperationModel videoPostCompoundModel = findVideoPostModel(postCompoundModels, videoProcessStatus.getTempId());
         if (videoPostCompoundModel != null) updateCompoundModelStatus(videoPostCompoundModel, videoProcessStatus);
      }
   }

   private PostCompoundOperationModel findVideoPostModel(List<PostCompoundOperationModel> postCompoundModels, String tempId) {
      return Queryable.from(postCompoundModels)
            .firstOrDefault(element -> element.state() == CompoundOperationState.PROCESSING
                  && Integer.toString(element.id()).equals(tempId));
   }

   private void updateCompoundModelStatus (PostCompoundOperationModel videoPostModel, VideoProcessStatus videoProcessStatus) {
      // TODO: 5/23/17 add real implementation here
   }

}
