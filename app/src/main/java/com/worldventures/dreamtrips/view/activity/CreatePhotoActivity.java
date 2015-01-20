package com.worldventures.dreamtrips.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.routing.BaseRouter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.presentation.CreatePhotoActivityPM;

import butterknife.InjectView;

@Layout(R.layout.activity_create_photo)
public class CreatePhotoActivity extends PresentationModelDrivenActivity<CreatePhotoActivityPM> {
    public static final String EXTRA_FILE_URI = "EXTRA_FILE_URI";
    public static final int REQUEST_CODE_CREATE_PHOTO = 342;

    @InjectView(R.id.toolbar_actionbar)
    Toolbar toolbar;

    @Override
    protected CreatePhotoActivityPM createPresentationModel(Bundle savedInstanceState) {
        return new CreatePhotoActivityPM(this);
    }

    @Override
    protected void afterCreateView(Bundle savedInstanceState) {
        super.afterCreateView(savedInstanceState);

        Uri imageUri = getIntent().getBundleExtra(BaseRouter.EXTRA_BUNDLE).getParcelable(EXTRA_FILE_URI);

        getPresentationModel().setImageUri(imageUri);
        getPresentationModel().onCreate();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        toolbar.getBackground().setAlpha(0);
    }

    public void preFinishProcess() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }
}
