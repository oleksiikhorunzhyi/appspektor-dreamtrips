package com.worldventures.dreamtrips.social.service.users.circle.command

import com.worldventures.core.janet.CommandWithError
import com.worldventures.core.model.Circle
import com.worldventures.dreamtrips.R
import io.techery.janet.ActionPipe
import io.techery.janet.command.annotations.CommandAction
import rx.android.schedulers.AndroidSchedulers

@CommandAction
class GetCirclesDecoratorCommand(private val circlePipe: ActionPipe<GetCirclesCommand>,
                                 private val successAction: (List<Circle>) -> Unit) : CommandWithError<Unit>() {

   override fun run(callback: CommandCallback<Unit>) {
      circlePipe.createObservableResult(GetCirclesCommand())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ callback.onSuccess(successAction.invoke(it.result)) },
                  callback::onFail)
   }

   override fun getFallbackErrorMessage() = R.string.error_fail_to_load_circles
}
