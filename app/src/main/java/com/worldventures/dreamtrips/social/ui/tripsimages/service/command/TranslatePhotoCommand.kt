package com.worldventures.dreamtrips.social.ui.tripsimages.service.command

import com.worldventures.core.utils.LocaleHelper
import com.worldventures.dreamtrips.social.ui.feed.service.command.TranslateTextCachedCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Photo
import com.worldventures.janet.injection.InjectableAction
import io.techery.janet.Command
import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction
import rx.schedulers.Schedulers
import javax.inject.Inject

@CommandAction
class TranslatePhotoCommand(val photo: Photo) : Command<String>(), InjectableAction {

   @field:Inject internal lateinit var janet: Janet

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<String>) {
      janet.createPipe(TranslateTextCachedCommand::class.java, Schedulers.io())
            .createObservableResult(TranslateTextCachedCommand(photo.title,
                  LocaleHelper.getDefaultLocaleFormatted()))
            .map { it.result }
            .subscribe(callback::onSuccess, callback::onFail)
   }
}
