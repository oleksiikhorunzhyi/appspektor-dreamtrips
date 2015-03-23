package com.worldventures.dreamtrips.modules.reptools;

import com.worldventures.dreamtrips.modules.reptools.presenter.RepToolsPresenter;
import com.worldventures.dreamtrips.modules.reptools.presenter.SuccessStoriesListPresenter;
import com.worldventures.dreamtrips.modules.reptools.presenter.SuccessStoryDetailsFragmentPresenter;
import com.worldventures.dreamtrips.modules.reptools.presenter.SuccessStoryDetailsPresenter;
import com.worldventures.dreamtrips.modules.reptools.view.activity.SuccessStoryDetailsActivity;
import com.worldventures.dreamtrips.modules.reptools.view.cell.SuccessStoryCell;
import com.worldventures.dreamtrips.modules.reptools.view.fragment.RepToolsFragment;
import com.worldventures.dreamtrips.modules.reptools.view.fragment.SuccessStoriesDetailsFragment;
import com.worldventures.dreamtrips.modules.reptools.view.fragment.SuccessStoriesListFragment;

import dagger.Module;

/**
 * Created by 1 on 23.03.15.
 */
@Module(
        injects = {
                SuccessStoriesDetailsFragment.class,
                SuccessStoryCell.class,
                SuccessStoriesListFragment.class,
                SuccessStoriesListPresenter.class,
                SuccessStoryDetailsFragmentPresenter.class,
                RepToolsFragment.class,
                RepToolsPresenter.class,
                SuccessStoryDetailsActivity.class,
                SuccessStoryDetailsPresenter.class,
        },
        complete = false,
        library = true
)
public class ReptoolsModule {
}
