package com.worldventures.dreamtrips.view.presentation;

import android.os.Bundle;

import com.worldventures.dreamtrips.core.navigation.FragmentCompass;
import com.worldventures.dreamtrips.core.navigation.State;
import com.worldventures.dreamtrips.view.activity.Injector;
import com.worldventures.dreamtrips.view.fragment.WebViewFragment;

public class MainActivityPresentation extends BasePresentation implements FragmentCompass.OnTransactionListener {

    public static final String TERMS = "http://gs1.wpc.edgecastcdn.net/80289E/media/1/dtapp/legal/us_en/html/faq.html";
    public static final String FAQ = "http://gs1.wpc.edgecastcdn.net/80289E/media/1/dtapp/legal/us_en/html/terms_of_service.html";
    private View view;

    public MainActivityPresentation(View view, Injector graf) {
        super(graf);
        this.view = view;
        fragmentCompass.setOnTransactionListener(this);
    }

    public void onNavigationClick(int position) {
        State state = State.findByKey(position);
        Bundle bundle = null;
        if (state == State.FAQ) {
            bundle = new Bundle();
            bundle.putString(WebViewFragment.HTTP_URL, TERMS);
        } else if (state == State.TERMS_AND_CONDITIONS) {
            bundle = new Bundle();
            bundle.putString(WebViewFragment.HTTP_URL, FAQ);
        }
        fragmentCompass.switchBranch(state, bundle);
        if (state == State.MY_PROFILE) {
            view.setActionBarTitle("");
        } else {
            view.setActionBarTitle(state.getTitle());
        }
    }

    @Override
    public void onTransactionDone(State state, FragmentCompass.Action action) {
        view.resetActionBar();
    }

    public static interface View {
        void setActionBarTitle(String title);

        void resetActionBar();
    }
}
