package com.worldventures.dreamtrips.core.navigation;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.techery.spares.annotations.Layout;
import com.techery.spares.utils.ui.OrientationUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.presenter.ComponentPresenter;
import com.worldventures.dreamtrips.modules.common.view.activity.BaseActivity;
import com.worldventures.dreamtrips.modules.common.view.dialog.BaseDialogFragment;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;

public class DialogFragmentNavigator implements Navigator {

    private final FragmentManager fragmentManager;

    public DialogFragmentNavigator(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void attach(Route route, Bundle bundle) {
        move(route, bundle);
    }

    @Override
    public void move(Route route, Bundle bundle) {
        NavigationDialogFragment.newInstance(route, bundle).show(fragmentManager, route.name());
    }

    @Layout(R.layout.dialog_container)
    public static class NavigationDialogFragment extends BaseDialogFragment {

        private FragmentCompass compass;

        private Route route;
        private Bundle bundle;

        public static NavigationDialogFragment newInstance(Route route, Bundle bundle) {
            NavigationDialogFragment fragment = new NavigationDialogFragment();
            Bundle args = new Bundle();
            args.putSerializable(Route.class.getName(), route);
            args.putBundle(Bundle.class.getName(), bundle);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            compass = new FragmentCompass((BaseActivity) activity, -1);

        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            route = (Route) getArguments().getSerializable(Route.class.getName());
            bundle = getArguments().getBundle(Bundle.class.getName());
            //
            setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = super.onCreateView(inflater, container, savedInstanceState);
            compass.setContainerId(view.getId());
            compass.setFragmentManager(getChildFragmentManager());
            return view;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            BaseFragment currentFragment = compass.getCurrentFragment();
            if (currentFragment != null && currentFragment.getClass().getName().equals(route.getClazzName()))
                return;
            NavigationBuilder.create()
                    .with(compass)
                    .data(bundle.getParcelable(ComponentPresenter.EXTRA_DATA))
                    .attach(route);
        }

        @Override
        public void onResume() {
            super.onResume();
            OrientationUtil.lockOrientation(getActivity());
        }

        @Override
        public void onPause() {
            super.onPause();
            OrientationUtil.unlockOrientation(getActivity());
        }
    }
}
