package com.worldventures.dreamtrips.presentation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.robobinding.annotation.PresentationModel;

@PresentationModel
public class MainActivityPresentation extends BasePresentation<MainActivityPresentation.View> {

    public MainActivityPresentation(View view) {
        super(view);
    }

    @Override
    public void resume() {
        super.resume();
        updateFaqAndTermLinks();
    }

    private void updateFaqAndTermLinks() {
        dataManager.getWebSiteDocumentsByCountry((jsonObject, e) -> {
            if (jsonObject != null) {
                for (JsonElement element : jsonObject.getAsJsonArray("Documents")) {
                    JsonObject obj = element.getAsJsonObject();
                    String name = obj.getAsJsonPrimitive("NameNative").getAsString();
                    String url = obj.getAsJsonPrimitive("Url").getAsString();
                    //TODO: Add static info loading logic.
//                    if (name.equals("FAQ")) {
//                        sessionManager.setFaqUrl(url);
//                    } else if (name.equals("Terms of Use")) {
//                        sessionManager.setTermsUrl(url);
//                    }
                }
            }
        });
    }

    public static interface View extends BasePresentation.View {

    }
}
