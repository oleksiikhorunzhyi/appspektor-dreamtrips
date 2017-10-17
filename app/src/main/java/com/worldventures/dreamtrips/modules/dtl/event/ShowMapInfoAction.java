package com.worldventures.dreamtrips.modules.dtl.event;

import com.worldventures.core.janet.ValueCommandAction;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public final class ShowMapInfoAction extends ValueCommandAction<Object> {

   private ShowMapInfoAction() {
      super(null);
   }

   public static ShowMapInfoAction create() {
      return new ShowMapInfoAction();
   }
}
