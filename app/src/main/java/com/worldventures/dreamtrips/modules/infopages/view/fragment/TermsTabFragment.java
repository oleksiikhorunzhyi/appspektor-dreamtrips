package com.worldventures.dreamtrips.modules.infopages.view.fragment;

import android.os.Bundle;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.bucketlist.view.custom.CustomViewPager;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.custom.BadgedTabLayout;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.common.view.viewpager.BasePagerAdapter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;

import butterknife.InjectView;


@Layout(R.layout.fragment_tab_info)
@MenuResource(R.menu.menu_mock)
public class TermsTabFragment extends BaseFragment<Presenter> implements Presenter.View {
    private static final int TERMS_OFFSCREEN_PAGES = 2;

    @InjectView(R.id.tabs)
    protected BadgedTabLayout tabs;
    @InjectView(R.id.pager)
    protected CustomViewPager pager;

    private BasePagerAdapter adapter;

    @Override
    protected Presenter createPresenter(Bundle savedInstanceState) {
        return new Presenter();
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);

        if (adapter == null) {
            this.adapter = new BasePagerAdapter(getChildFragmentManager());
            this.adapter.add(new FragmentItem(Route.PRIVACY_POLICY, getString(R.string.privacy)));
            this.adapter.add(new FragmentItem(Route.TERMS_OF_SERVICE, getString(R.string.terms_of_service)));
            this.adapter.add(new FragmentItem(Route.COOKIE_POLICY, getString(R.string.cookie)));
        }

        pager.setAdapter(adapter);
        // Is used for block screen rotates
        pager.setOffscreenPageLimit(TERMS_OFFSCREEN_PAGES);

        tabs.setupWithPagerBadged(pager);
    }
}
