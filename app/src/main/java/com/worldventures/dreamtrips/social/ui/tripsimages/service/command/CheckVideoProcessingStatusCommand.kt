package com.worldventures.dreamtrips.social.ui.tripsimages.service.command

import com.worldventures.dreamtrips.social.ui.background_uploading.model.CompoundOperationState
import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostWithVideoAttachmentBody
import com.worldventures.dreamtrips.social.ui.background_uploading.service.CompoundOperationsInteractor
import com.worldventures.dreamtrips.social.ui.background_uploading.service.command.CompoundOperationsCommand
import com.worldventures.dreamtrips.social.ui.background_uploading.service.command.QueryCompoundOperationsCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.model.BaseMediaEntity
import com.worldventures.dreamtrips.social.ui.tripsimages.model.VideoMediaEntity
import com.worldventures.janet.injection.InjectableAction
import io.techery.janet.Command
import io.techery.janet.command.annotations.CommandAction
import rx.Observable
import javax.inject.Inject

@CommandAction
class CheckVideoProcessingStatusCommand(private val mediaEntities: List<BaseMediaEntity<*>>) : Command<Any>(), InjectableAction {

   @Inject internal lateinit var compoundOperationsInteractor: CompoundOperationsInteractor

   @Throws(Throwable::class)
   override fun run(commandCallback: Command.CommandCallback<Any>) {
      val videoFeedIds: List<String> =
            mediaEntities.filter { feedEntity -> feedEntity is VideoMediaEntity }
                  .map { feedEntity -> feedEntity.item.uid }
                  .toList()
      compoundOperationsInteractor.compoundOperationsPipe()
            .createObservableResult(QueryCompoundOperationsCommand())
            .flatMap { compoundOperationsCommand ->
               Observable.from(compoundOperationsCommand.result)
                     .filter { model -> model.state() == CompoundOperationState.PROCESSING }
                     .filter { postCompoundOperationModel -> postCompoundOperationModel.body() is PostWithVideoAttachmentBody }
                     .toList()
            }
            .doOnNext { processingModels ->
               for (model in processingModels) {
                  val modelBody = model.body()
                  if (modelBody is PostWithVideoAttachmentBody) {
                     if (videoFeedIds.contains(modelBody.videoUid())) {
                        compoundOperationsInteractor.compoundOperationsPipe()
                              .send(CompoundOperationsCommand.compoundCommandRemoved(model))
                     }
                  }

               }
            }
            .subscribe(commandCallback::onSuccess, commandCallback::onFail)
   }
}
