package com.worldventures.dreamtrips.core.api.action;

import javax.inject.Inject;

import io.techery.mappery.MapperyContext;

public abstract class MappableBaseApiActionCommand<Action extends com.worldventures.dreamtrips.api.api_common.BaseHttpAction, T, M>
      extends BaseApiActionCommand<Action, T, Object> {

   @Inject MapperyContext mapperyContext;

   @Override
   protected T mapCommandResult(Object httpCommandResult) {
      return (T) mapperyContext.convert((Iterable<?>) httpCommandResult, getMappingTargetClass());
   }

   protected abstract Class<M> getMappingTargetClass();
}
