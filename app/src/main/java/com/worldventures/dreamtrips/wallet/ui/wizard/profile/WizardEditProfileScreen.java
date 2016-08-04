package com.worldventures.dreamtrips.wallet.ui.wizard.profile;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.MediaPickerService;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletFrameLayout;

import java.io.File;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import cn.pedant.SweetAlert.SweetAlertDialog;
import rx.Observable;

public class WizardEditProfileScreen extends WalletFrameLayout<WizardEditProfilePresenter.Screen, WizardEditProfilePresenter, WizardEditProfilePath>
        implements WizardEditProfilePresenter.Screen {
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.person_name)
    TextView personName;

    @InjectView(R.id.photo_preview)
    SimpleDraweeView previewPhotoView;

    private MediaPickerService mediaPickerService;
    private SweetAlertDialog sweetAlertDialog;

    public WizardEditProfileScreen(Context context) {
        super(context);
    }

    public WizardEditProfileScreen(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @NonNull
    @Override
    public WizardEditProfilePresenter createPresenter() {
        return new WizardEditProfilePresenter(getContext(), getInjector(), getPath().getSmartCardId());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //noinspection all
        mediaPickerService = (MediaPickerService) getContext().getSystemService(MediaPickerService.SERVICE_NAME);
        toolbar.setNavigationOnClickListener(v -> navigateButtonClick());
    }

    protected void navigateButtonClick() {
        presenter.goToBack();
    }

    @OnClick(R.id.next_button)
    public void nextClick() {
        presenter.setupUserData();
    }

    @OnEditorAction(R.id.person_name)
    public boolean actionNext(int action) {
        if (action != EditorInfo.IME_ACTION_NEXT) return false;
        presenter.setupUserData();
        return true;
    }

    @OnClick(R.id.imageContainer)
    public void choosePhotoClick() {
        presenter.choosePhoto();
    }

    @Override
    public Observable<String> choosePhotoAndCrop() {
        return mediaPickerService.pickPhotoAndCrop();
    }

    @Override
    public void hidePhotoPicker() {
        mediaPickerService.hidePicker();
    }

    @Override
    public void setPreviewPhoto(File photo) {
        previewPhotoView.setImageURI(Uri.fromFile(photo));
    }

    @Override
    public void setUserFullName(String fullName) {
        personName.setText(fullName);
    }

    @NonNull
    @Override
    public String getUserName() {
        return personName.getText().toString();
    }

    @Override
    public void showSuccessWithDelay(Runnable action, long delay) {
        if (sweetAlertDialog == null) return;
        sweetAlertDialog.setTitle(getString(R.string.wallet_edit_profile_success_dialog));
        sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);

        postDelayed(() -> {
            if (sweetAlertDialog == null) return;
            sweetAlertDialog.dismiss();
            action.run();
        }, delay);
    }

    @Override
    public void notifyError(Throwable throwable) {
        if (sweetAlertDialog == null) return;
        sweetAlertDialog.setTitleText(getString(R.string.error_something_went_wrong));
        sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
    }

    @Override
    public void showProgress() {
        sweetAlertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.setTitleText(getString(R.string.wallet_edit_profile_progress_dialog));
        sweetAlertDialog.show();
    }

    @Override
    public void hideProgress() {
        if (sweetAlertDialog == null) return;
        sweetAlertDialog.dismiss();
        sweetAlertDialog = null;
    }
}
