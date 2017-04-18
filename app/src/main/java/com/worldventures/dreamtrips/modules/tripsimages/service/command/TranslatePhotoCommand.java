package com.worldventures.dreamtrips.modules.tripsimages.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.modules.feed.service.command.TranslateTextCachedCommand;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.schedulers.Schedulers;

@CommandAction
public class TranslatePhotoCommand extends Command<String> implements InjectableAction {

   private Photo photo;

   @Inject Janet janet;

   public TranslatePhotoCommand(Photo photo) {
      this.photo = photo;
   }

   @Override
   protected void run(CommandCallback<String> callback) throws Throwable {
      janet.createPipe(TranslateTextCachedCommand.class, Schedulers.io())
            .createObservableResult(new TranslateTextCachedCommand(photo.getFSDescription(),
                  LocaleHelper.getDefaultLocaleFormatted()))
            .map(TranslateTextCachedCommand::getResult)
            .subscribe(callback::onSuccess, callback::onFail);
   }

   public Photo getPhoto() {
      return photo;
   }
}
