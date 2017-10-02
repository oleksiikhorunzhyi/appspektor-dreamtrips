package com.worldventures.core.service.command.api_action;

public abstract class ApiActionCommand<Action extends com.worldventures.dreamtrips.api.api_common.BaseHttpAction, T>
      extends BaseApiActionCommand<Action, T, T> {

   @Override
   protected T mapHttpActionResult(Action httpAction) {
      return null;
   }
}
