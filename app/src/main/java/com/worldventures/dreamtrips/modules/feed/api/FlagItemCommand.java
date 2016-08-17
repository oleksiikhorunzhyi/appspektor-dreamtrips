package com.worldventures.dreamtrips.modules.feed.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Command;
import com.worldventures.dreamtrips.modules.common.model.FlagData;

public class FlagItemCommand extends Command<Void> {
   private FlagData data;

   public FlagItemCommand(FlagData data) {
      super(Void.class);
      this.data = data;
   }

   @Override
   public Void loadDataFromNetwork() throws Exception {
      return getService().flagItem(data.uid, data.flagReasonId, data.reason);
   }

   @Override
   public int getErrorMessage() {
      return R.string.error_fail_to_flag_item;
   }
}
