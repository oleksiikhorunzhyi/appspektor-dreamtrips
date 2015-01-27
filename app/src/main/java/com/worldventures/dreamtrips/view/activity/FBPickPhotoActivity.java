package com.worldventures.dreamtrips.view.activity;

import android.content.Intent;
import android.os.Bundle;

import com.facebook.AppEventsLogger;
import com.facebook.Session;
import com.facebook.UiLifecycleHelper;
import com.google.gson.Gson;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.presentation.FacebookPickPhotoActivityPM;

@Layout(R.layout.activity_login)
public class FBPickPhotoActivity extends PresentationModelDrivenActivity<FacebookPickPhotoActivityPM> implements FacebookPickPhotoActivityPM.View {
    public static final String RESULT_PHOTO = "RESULT_PHOTO";
    public static final int REQUEST_CODE_PICK_FB_PHOTO = 123;
    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = (session, state, exception) -> {
        // onSessionStateChange(session, state, exception);
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        getPresentationModel().create();
    }

    @Override
    protected void onResume() {
        super.onResume();
        uiHelper.onResume();
        AppEventsLogger.activateApp(this); //facebook SDK event logger. Really needed?
    }

    @Override
    protected void onPause() {
        super.onPause();
        uiHelper.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    protected FacebookPickPhotoActivityPM createPresentationModel(Bundle savedInstanceState) {
        return new FacebookPickPhotoActivityPM(this);
    }


    public void preFinishProcessing(ChosenImage image) {
        Intent intent = new Intent();
        intent.putExtra(RESULT_PHOTO, new Gson().toJson(image));
        setResult(RESULT_OK, intent);
        finish();
    }
}
