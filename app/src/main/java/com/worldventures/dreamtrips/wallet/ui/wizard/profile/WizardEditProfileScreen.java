package com.worldventures.dreamtrips.wallet.ui.wizard.profile;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.MediaPickerService;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletFrameLayout;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import rx.Observable;

public class WizardEditProfileScreen extends WalletFrameLayout<WizardEditProfilePresenter.Screen, WizardEditProfilePresenter, WizardEditProfilePath> implements WizardEditProfilePresenter.Screen {
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    private MediaPickerService mediaPickerService;

    public WizardEditProfileScreen(Context context) {
        super(context);
    }

    public WizardEditProfileScreen(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @NonNull
    @Override
    public WizardEditProfilePresenter createPresenter() {
        return new WizardEditProfilePresenter(getContext(), getInjector());
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
        presenter.doOnNext();
    }

    @OnEditorAction(R.id.person_name)
    public boolean actionNext(int action) {
        if (action != EditorInfo.IME_ACTION_NEXT) return false;
        presenter.doOnNext();
        return true;
    }

    @OnClick(R.id.imageContainer)
    public void choosePhotoClick() {
        presenter.choosePhoto();
    }

    @Override
    public Observable<String> showPhotoPicker() {
        return mediaPickerService.pickPhotoAndCrop();
    }

    @Override
    public void hidePhotoPicker() {
        mediaPickerService.hidePicker();
    }
}
