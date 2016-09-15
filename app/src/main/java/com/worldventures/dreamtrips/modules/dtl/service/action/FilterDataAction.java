package com.worldventures.dreamtrips.modules.dtl.service.action;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.FilterData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.ImmutableFilterData;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class FilterDataAction extends Command<FilterData> {

   private final FilterData filterData;

   public static FilterData reset() {
      return ImmutableFilterData.builder().build();
   }

   public FilterDataAction(FilterData filterData) {
      this.filterData = filterData;
   }

   @Override
   protected void run(CommandCallback<FilterData> callback) throws Throwable {
      callback.onSuccess(filterData);
      // TODO :: 15.09.16 implement more stuff, maybe change to valuecommand
   }

   public FilterData filterData() {
      return filterData;
   }
}
