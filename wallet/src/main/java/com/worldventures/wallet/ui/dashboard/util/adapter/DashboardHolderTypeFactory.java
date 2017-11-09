package com.worldventures.wallet.ui.dashboard.util.adapter;


import com.worldventures.wallet.ui.common.adapter.HolderTypeFactory;
import com.worldventures.wallet.ui.dashboard.util.model.CardGroupHeaderModel;
import com.worldventures.wallet.ui.dashboard.util.model.CommonCardViewModel;

public interface DashboardHolderTypeFactory extends HolderTypeFactory {

   int type(CommonCardViewModel viewModel);

   int type(CardGroupHeaderModel viewModel);
}
