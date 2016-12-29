package com.worldventures.dreamtrips.modules.dtl.event;

import com.worldventures.dreamtrips.core.api.action.ValueCommandAction;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class ShowMapInfoAction extends ValueCommandAction<Object> {

   public static ShowMapInfoAction create() {
      return new ShowMapInfoAction();
   }

   public ShowMapInfoAction() {
      super(null);
   }
}
