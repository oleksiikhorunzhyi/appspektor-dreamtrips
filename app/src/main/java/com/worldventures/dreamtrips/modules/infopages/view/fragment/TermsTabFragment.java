package com.worldventures.dreamtrips.modules.infopages.view.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.bucketlist.view.custom.CustomViewPager;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.custom.BadgedTabLayout;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.common.view.viewpager.BasePagerAdapter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.StaticInfoFragment;

import butterknife.InjectView;


@Layout(R.layout.fragment_tab_info)
@MenuResource(R.menu.menu_mock)
public class TermsTabFragment extends BaseFragment<Presenter> implements Presenter.View {

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
            this.adapter.add(new FragmentItem(StaticInfoFragment.PrivacyPolicyFragment.class, getString(R.string.privacy)));
            this.adapter.add(new FragmentItem(StaticInfoFragment.TermsOfServiceFragment.class, getString(R.string.terms_of_service)));
            this.adapter.add(new FragmentItem(StaticInfoFragment.CookiePolicyFragment.class, getString(R.string.cookie)));
        }

        pager.setAdapter(adapter);

        tabs.setupWithPagerBadged(pager);
    }
}
