package com.worldventures.dreamtrips.modules.dtl.event;

import com.worldventures.dreamtrips.core.api.action.ValueCommandAction;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class MapInfoReadyAction extends ValueCommandAction<Integer> {

   public static MapInfoReadyAction create(int height) {
      return new MapInfoReadyAction(height);
   }

   public MapInfoReadyAction(int height) {
      super(height);
   }

}
