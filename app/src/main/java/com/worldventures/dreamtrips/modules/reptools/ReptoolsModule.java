package com.worldventures.dreamtrips.modules.reptools;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
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
import dagger.Provides;

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

    public static final String REP_TOOLS = "rep_tools";

    @Provides(type = Provides.Type.SET)
    ComponentDescription provideRepToolsComponent() {
        return new ComponentDescription(REP_TOOLS, R.string.rep_tools, R.drawable.ic_rep_tools, RepToolsFragment.class);
    }
}
