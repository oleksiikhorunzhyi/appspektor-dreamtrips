package com.worldventures.dreamtrips.modules.dtl.helper;

import android.app.Activity;
import android.support.annotation.MenuRes;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.core.utils.events.ActionBarHideEvent;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.dtl.model.DtlLocation;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class DtlPlacesToolbarHelper {

    Activity activity;
    FragmentCompass fragmentCompass;
    EventBus eventBus;

    @InjectView(R.id.toolbar_actionbar)
    Toolbar toolbar;
    @InjectView(R.id.spinnerStyledTitle)
    TextView title;

    public DtlPlacesToolbarHelper(Activity activity, FragmentCompass fragmentCompass, EventBus eventBus) {
        this.activity = activity;
        this.fragmentCompass = fragmentCompass;
        this.eventBus = eventBus;
    }

    public void attach(View rootView) {
        ButterKnife.inject(this, rootView);
    }

    public void inflateMenu(@MenuRes int res, Toolbar.OnMenuItemClickListener listener) {
        toolbar.inflateMenu(res);
        toolbar.setOnMenuItemClickListener(listener);
    }

    public void onResume() {
        toolbar.setVisibility(View.VISIBLE);
        eventBus.post(new ActionBarHideEvent(true));
    }

    public void onPause() {
        toolbar.setVisibility(View.GONE);
        eventBus.post(new ActionBarHideEvent(false));
    }

    public void setPlaceForToolbar(DtlLocation location) {
        if (!ViewUtils.isLandscapeOrientation(activity)) {
            toolbar.setNavigationIcon(R.drawable.ic_menu_hamburger);
        }
        toolbar.setNavigationOnClickListener(view -> ((MainActivity) activity).openLeftDrawer());
        title.setText(location.getLongName());
        title.setOnClickListener(v -> {
            fragmentCompass.disableBackStack();
            NavigationBuilder.create().with(fragmentCompass).move(Route.DTL_LOCATIONS);
        });
    }
}
