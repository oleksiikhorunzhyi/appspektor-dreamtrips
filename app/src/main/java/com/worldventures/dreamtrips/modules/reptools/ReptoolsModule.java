package com.worldventures.dreamtrips.modules.reptools;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.reptools.presenter.RepToolsPresenter;
import com.worldventures.dreamtrips.modules.reptools.presenter.SuccessStoryDetailsPresenter;
import com.worldventures.dreamtrips.modules.reptools.presenter.SuccessStoryListPresenter;
import com.worldventures.dreamtrips.modules.reptools.view.cell.SuccessStoryCell;
import com.worldventures.dreamtrips.modules.reptools.view.fragment.RepToolsFragment;
import com.worldventures.dreamtrips.modules.reptools.view.fragment.SuccessStoryDetailsFragment;
import com.worldventures.dreamtrips.modules.reptools.view.fragment.SuccessStoryListFragment;

import dagger.Module;
import dagger.Provides;

@Module(
      injects = {SuccessStoryDetailsFragment.class, SuccessStoryCell.class, SuccessStoryListFragment.class, SuccessStoryListPresenter.class, SuccessStoryDetailsPresenter.class, RepToolsFragment.class, RepToolsPresenter.class,},
      complete = false,
      library = true)
public class ReptoolsModule {

   public static final String REP_TOOLS = Route.REP_TOOLS.name();

   @Provides(type = Provides.Type.SET)
   ComponentDescription provideRepToolsComponent() {
      return new ComponentDescription(REP_TOOLS, R.string.rep_tools, R.string.rep_tools, R.drawable.ic_rep_tools, RepToolsFragment.class);
   }
}
