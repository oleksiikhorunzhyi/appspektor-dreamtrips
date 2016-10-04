package com.worldventures.dreamtrips.modules.common.command;



import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class ImageCapturedCommand extends Command<String> {

   private String filePath;

   public ImageCapturedCommand(String filePath) {
      this.filePath = filePath;
   }

   @Override
   protected void run(CommandCallback<String> callback) throws Throwable {
      Observable.just(filePath)
            .subscribe(callback::onSuccess, callback::onFail);
   }
}
