package com.worldventures.dreamtrips.presentation;

import android.os.Bundle;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;
import com.worldventures.dreamtrips.core.navigation.State;
import com.worldventures.dreamtrips.view.fragment.WebViewFragment;

public class MainActivityPresentation extends BasePresentation<MainActivityPresentation.View> implements FragmentCompass.OnTransactionListener {

    public MainActivityPresentation(View view) {
        super(view);
    }

    @Override
    public void resume() {
        super.resume();

        updateFaqAndTermLinks();
    }

    public void onNavigationClick(int position) {
        State state = State.findByKey(position);
        Bundle bundle = null;
        if (state == State.FAQ) {
            bundle = new Bundle();
            bundle.putString(WebViewFragment.HTTP_URL, sessionManager.getFaqUrl());
        } else if (state == State.TERMS_AND_CONDITIONS) {
            bundle = new Bundle();
            bundle.putString(WebViewFragment.HTTP_URL, sessionManager.getTermUrl());
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

    private void updateFaqAndTermLinks() {
        dataManager.getWebSiteDocumentsByCountry((jsonObject, e) -> {
            if (jsonObject != null) {
                for (JsonElement element : jsonObject.getAsJsonArray("Documents")) {
                    JsonObject obj = element.getAsJsonObject();
                    String name = obj.getAsJsonPrimitive("NameNative").getAsString();
                    String url = obj.getAsJsonPrimitive("Url").getAsString();
                    if (name.equals("FAQ")) {
                        sessionManager.setFaqUrl(url);
                    } else if (name.equals("Terms of Use")) {
                        sessionManager.setTermsUrl(url);
                    }
                }
            }
        });
    }

    public static interface View extends BasePresentation.View {
        void setActionBarTitle(String title);
        void resetActionBar();
    }
}
