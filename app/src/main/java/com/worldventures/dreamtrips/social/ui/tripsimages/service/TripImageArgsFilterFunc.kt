package com.worldventures.dreamtrips.social.ui.tripsimages.service

import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.BaseMediaCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.view.args.TripImagesArgs

import io.techery.janet.ActionState
import rx.functions.Func1

class TripImageArgsFilterFunc(private val currentArgs: TripImagesArgs) : Func1<ActionState<BaseMediaCommand>, Boolean> {

   override fun call(actionState: ActionState<BaseMediaCommand>) =
         actionState.action.args == currentArgs && (actionState.action.isLoadMore || actionState.action.isReload)
}
