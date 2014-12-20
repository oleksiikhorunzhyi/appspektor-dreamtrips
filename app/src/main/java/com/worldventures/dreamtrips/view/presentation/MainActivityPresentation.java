package com.worldventures.dreamtrips.view.presentation;

import android.os.Bundle;

import com.worldventures.dreamtrips.core.navigation.State;
import com.worldventures.dreamtrips.view.activity.Injector;
import com.worldventures.dreamtrips.view.fragment.WebViewFragment;

public class MainActivityPresentation extends BasePresentation {

    private static final int HEADER_SIZE = 1;
    private View view;


    public MainActivityPresentation(View view, Injector graf) {
        super(graf);
        this.view = view;
    }

    public void onNavigationClick(int position) {
        State state = State.findByKey(position);
        Bundle bundle = null;
        if (state == State.FAQ) {
            bundle = new Bundle();
            bundle.putString(WebViewFragment.HTTP_URL, "http://google.com");
        } else if (state == State.TERMS_AND_CONDITIONS) {
            bundle = new Bundle();
            bundle.putString(WebViewFragment.HTTP_URL, "http://google.com");
        }
        fragmentCompass.switchBranch(state, bundle);
        view.setTitle(state.getTitle());
    }

    public static interface View {
        void setTitle(String title);
    }
}
