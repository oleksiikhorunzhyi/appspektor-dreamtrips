package com.worldventures.dreamtrips.modules.dtl.service.action;

import com.worldventures.dreamtrips.core.api.action.ValueCommandAction;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.FilterData;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class FilterDataAction extends ValueCommandAction<FilterData> {

   public FilterDataAction(FilterData value) {
      super(value);
   }
}
