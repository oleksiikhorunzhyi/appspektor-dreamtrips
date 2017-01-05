package com.worldventures.dreamtrips.modules.dtl.service.action;


import com.worldventures.dreamtrips.core.api.action.ValueCommandAction;
import com.worldventures.dreamtrips.modules.dtl.model.RequestSourceType;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class RequestSourceTypeAction extends ValueCommandAction<RequestSourceType> {

   public RequestSourceTypeAction(RequestSourceType value) {
      super(value);
   }

   public static RequestSourceTypeAction list() {
      return new RequestSourceTypeAction(RequestSourceType.LIST);
   }

   public static RequestSourceTypeAction map() {
      return new RequestSourceTypeAction(RequestSourceType.MAP);
   }

   public boolean isFromList() {
      return getResult() == RequestSourceType.LIST;
   }

   public boolean isFromMap() {
      return getResult() == RequestSourceType.MAP;
   }
}
