package com.worldventures.dreamtrips.social.ui.reptools;

import com.worldventures.dreamtrips.social.ui.reptools.presenter.RepToolsPresenter;
import com.worldventures.dreamtrips.social.ui.reptools.presenter.SuccessStoryDetailsPresenter;
import com.worldventures.dreamtrips.social.ui.reptools.presenter.SuccessStoryListPresenter;
import com.worldventures.dreamtrips.social.ui.reptools.view.fragment.RepToolsFragment;
import com.worldventures.dreamtrips.social.ui.reptools.view.fragment.SuccessStoryDetailsFragment;
import com.worldventures.dreamtrips.social.ui.reptools.view.fragment.SuccessStoryListFragment;

import dagger.Module;

@Module(
      injects = {
            SuccessStoryDetailsFragment.class,
            SuccessStoryListFragment.class,
            SuccessStoryListPresenter.class,
            SuccessStoryDetailsPresenter.class,
            RepToolsFragment.class,
            RepToolsPresenter.class},
      complete = false,
      library = true)
public class ReptoolsActivityModule {

}
