package com.worldventures.dreamtrips.core.api.uploadery;

import com.worldventures.dreamtrips.api.uploadery.UploadImageHttpAction;

import io.techery.janet.ActionState;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.helper.ActionStateToActionTransformer;
import rx.Observable;

@CommandAction
public class SimpleUploaderyCommand extends UploaderyImageCommand<UploadImageHttpAction> {

   public SimpleUploaderyCommand(String fileUri) {
      super(fileUri);
   }

   @Override
   protected Observable.Transformer<ActionState<UploadImageHttpAction>, UploadImageHttpAction> nextAction() {
      return uploadImageActionObservable -> uploadImageActionObservable.compose(new ActionStateToActionTransformer<>());
   }
}
