package com.worldventures.dreamtrips.wallet.ui.dashboard.util.adapter;


import android.view.ViewGroup;

import com.worldventures.dreamtrips.wallet.ui.dashboard.util.model.CardGroupHeaderModel;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.model.CommonCardViewModel;

public interface HolderTypeFactory {

   BaseHolder holder(ViewGroup parent, int viewType);

   int type(CommonCardViewModel viewModel);

   int type(CardGroupHeaderModel viewModel);
}
