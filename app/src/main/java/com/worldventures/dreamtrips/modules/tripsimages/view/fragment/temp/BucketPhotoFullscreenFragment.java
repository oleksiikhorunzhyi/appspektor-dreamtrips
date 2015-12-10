package com.worldventures.dreamtrips.modules.tripsimages.view.fragment.temp;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.BucketFullscreenPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.ScaleImageView;

import butterknife.InjectView;
import cn.pedant.SweetAlert.SweetAlertDialog;

@Layout(R.layout.fragment_fullscreen_bucket_photo)
public class BucketPhotoFullscreenFragment extends FullScreenPhotoFragment<BucketFullscreenPresenter, BucketPhoto> implements BucketFullscreenPresenter.View {


    @InjectView(R.id.iv_image)
    ScaleImageView ivImage;
    @InjectView(R.id.checkBox)
    CheckBox checkBox;
    @InjectView(R.id.delete)
    ImageView delete;

    private SweetAlertDialog progressDialog;

    @Override
    protected BucketFullscreenPresenter createPresenter(Bundle savedInstanceState) {
        return new BucketFullscreenPresenter((BucketPhoto) getArgs().getPhoto(), getArgs().getTab(), getArgs().isForeign());
    }

    @Override
    public void setContent(IFullScreenObject photo) {
        super.setContent(photo);
    }


    @Override
    public void showCoverProgress() {
        progressDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        progressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        progressDialog.setTitleText(getString(R.string.uploading_photo));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    public void hideCoverProgress() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismissWithAnimation();
    }

    @Override
    public void hideDeleteBtn() {
        delete.setVisibility(View.GONE);
    }

    @Override
    public void showDeleteBtn() {
        delete.setVisibility(View.VISIBLE);
    }

    @Override
    public void showCheckbox(boolean status) {
        checkBox.setText(status ? R.string.bucket_current_cover : R.string.bucket_photo_cover);
        checkBox.setClickable(!status);
        checkBox.setVisibility(View.VISIBLE);
        checkBox.setChecked(status);
    }

    @Override
    public void onResume() {
        super.onResume();
        checkBox.setOnCheckedChangeListener((cb, b) -> {
            checkBox.setClickable(!b);
            getPresenter().onCheckboxPressed(b);
        });
    }

}
