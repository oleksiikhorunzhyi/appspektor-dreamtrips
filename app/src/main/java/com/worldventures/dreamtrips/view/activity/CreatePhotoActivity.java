package com.worldventures.dreamtrips.view.activity;

import android.net.Uri;
import android.os.Bundle;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.view.presentation.CreatePhotoActivityPM;

public class CreatePhotoActivity extends BaseActivity {
    public static final String EXTRA_FILE_URI = "EXTRA_FILE_URI";
    CreatePhotoActivityPM pm;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_photo);
        imageUri = getIntent().getBundleExtra(ActivityRouter.EXTRA_BUNDLE).getParcelable(EXTRA_FILE_URI);
        pm = new CreatePhotoActivityPM(this, this);
        pm.onCreate();
    }

    public Uri getImageUri() {
        return imageUri;
    }
}
