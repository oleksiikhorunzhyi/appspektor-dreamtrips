package com.worldventures.dreamtrips.modules.reptools;

import com.worldventures.dreamtrips.modules.reptools.presenter.RepToolsPresenter;
import com.worldventures.dreamtrips.modules.reptools.presenter.SuccessStoryDetailsPresenter;
import com.worldventures.dreamtrips.modules.reptools.presenter.SuccessStoryListPresenter;
import com.worldventures.dreamtrips.modules.reptools.view.cell.SuccessStoryCell;
import com.worldventures.dreamtrips.modules.reptools.view.fragment.RepToolsFragment;
import com.worldventures.dreamtrips.modules.reptools.view.fragment.SuccessStoryDetailsFragment;
import com.worldventures.dreamtrips.modules.reptools.view.fragment.SuccessStoryListFragment;

import dagger.Module;

@Module(
      injects = {
            SuccessStoryDetailsFragment.class,
            SuccessStoryCell.class,
            SuccessStoryListFragment.class,
            SuccessStoryListPresenter.class,
            SuccessStoryDetailsPresenter.class,
            RepToolsFragment.class,
            RepToolsPresenter.class,},
      complete = false,
      library = true)
public class ReptoolsActivityModule {

}
