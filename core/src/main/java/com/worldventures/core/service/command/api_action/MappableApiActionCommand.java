package com.worldventures.core.service.command.api_action;

import javax.inject.Inject;

import io.techery.mappery.MapperyContext;

public abstract class MappableApiActionCommand<Action extends com.worldventures.dreamtrips.api.api_common.BaseHttpAction, T, M>
      extends BaseApiActionCommand<Action, T, Object> {

   @Inject protected MapperyContext mapperyContext;

   @Override
   protected T mapCommandResult(Object httpCommandResult) {
      if (httpCommandResult instanceof Iterable) {
         return (T) mapperyContext.convert((Iterable<?>) httpCommandResult, getMappingTargetClass());
      } else {
         return (T) mapperyContext.convert(httpCommandResult, getMappingTargetClass());
      }
   }

   protected abstract Class<M> getMappingTargetClass();
}
