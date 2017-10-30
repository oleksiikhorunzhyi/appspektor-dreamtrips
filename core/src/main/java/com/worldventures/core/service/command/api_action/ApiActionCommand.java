package com.worldventures.core.service.command.api_action;

public abstract class ApiActionCommand<ACTION extends com.worldventures.dreamtrips.api.api_common.BaseHttpAction, T>
      extends BaseApiActionCommand<ACTION, T, T> {

   @Override
   protected T mapHttpActionResult(ACTION httpAction) {
      return null;
   }
}
