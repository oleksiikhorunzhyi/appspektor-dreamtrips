package com.worldventures.dreamtrips.modules.infopages.view.fragment;

import android.support.v4.view.ViewPager;
import android.view.View;

import com.techery.spares.utils.event.ScreenChangedEvent;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.view.viewpager.BasePagerAdapter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;

public class HelpFragment extends TermsTabFragment {

    @Override
    public void afterCreateView(View rootView) {
        if (adapter == null) {
            this.adapter = new BasePagerAdapter(getChildFragmentManager());
            this.adapter.add(new FragmentItem(Route.HELP_VIDEOS, getString(R.string.presentations)));
            this.adapter.add(new FragmentItem(Route.FAQ, getString(R.string.faq)));
        }

        pager.setAdapter(adapter);
        // Is used for block screen rotates
        pager.setOffscreenPageLimit(TERMS_OFFSCREEN_PAGES);
        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                eventBus.post(new ScreenChangedEvent());
            }
        });

        tabs.setupWithPagerBadged(pager);
    }
}
