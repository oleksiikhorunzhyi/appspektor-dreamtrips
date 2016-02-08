package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.badoo.mobile.util.WeakHandler;
import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.utils.ui.SoftInputUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.BackStackDelegate;
import com.worldventures.dreamtrips.core.utils.GraphicUtils;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.common.view.custom.KeyCallbackEditText;
import com.worldventures.dreamtrips.modules.common.view.custom.PhotoPickerLayout;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.common.view.util.TextWatcherAdapter;
import com.worldventures.dreamtrips.modules.feed.bundle.PostBundle;
import com.worldventures.dreamtrips.modules.feed.presenter.PostEditPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.PostPresenter;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import icepick.State;
import mbanje.kurt.fabbutton.CircleImageView;
import mbanje.kurt.fabbutton.FabButton;

@Layout(R.layout.layout_post)
public class PostFragment extends BaseFragmentWithArgs<PostPresenter, PostBundle> implements PostPresenter.View {

    @Inject
    BackStackDelegate backStackDelegate;

    @InjectView(R.id.avatar)
    SimpleDraweeView avatar;
    @InjectView(R.id.attached_photo)
    SimpleDraweeView attachedPhoto;
    @InjectView(R.id.name)
    TextView name;
    @InjectView(R.id.post)
    KeyCallbackEditText post;
    @InjectView(R.id.post_button)
    Button postButton;
    @InjectView(R.id.fab_progress)
    FabButton fabProgress;
    @InjectView(R.id.fabbutton_circle)
    CircleImageView circleView;
    @InjectView(R.id.shadow)
    View shadow;
    @InjectView(R.id.image_container)
    FrameLayout imageContainer;
    @InjectView(R.id.image)
    ImageView image;
    @InjectView(R.id.photo_picker)
    PhotoPickerLayout photoPickerLayout;

    @State
    boolean pickerDisabled;

    SweetAlertDialog dialog;

    private WeakHandler handler;

    private TextWatcherAdapter textWatcher = new TextWatcherAdapter() {
        @Override
        public void onTextChanged(CharSequence constraint, int start, int before, int count) {
            super.onTextChanged(constraint, start, before, count);
            getPresenter().postInputChanged(constraint.toString().trim());
        }
    };

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        handler = new WeakHandler();
        inject(photoPickerLayout);
        photoPickerLayout.setup(getChildFragmentManager(), false);
        photoPickerLayout.hidePanel();
        photoPickerLayout.setOnDoneClickListener((chosenImages, type) -> getPresenter().attachImages(chosenImages));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).disableLeftDrawer();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).enableLeftDrawer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        post.addTextChangedListener(textWatcher);
        post.setOnKeyPreImeListener((keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                post.clearFocus();
            }
        });
        post.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && photoPickerLayout.isPanelVisible())
                handler.postDelayed(photoPickerLayout::hidePanel, 250);
            else if (!hasFocus)
                name.requestFocus();
        });
        backStackDelegate.setListener(this::onBackPressed);
        if (getArgs() != null && getArgs().getType() == PostBundle.PHOTO) {
            photoPickerLayout.showPanel();
        }
        updatePickerState();
    }

    @Override
    public void onPause() {
        super.onPause();
        post.removeTextChangedListener(textWatcher);
        backStackDelegate.setListener(null);
        post.setOnFocusChangeListener(null);
    }

    @Override
    protected PostPresenter createPresenter(Bundle savedInstanceState) {
        if (getArgs() != null && getArgs().getTextualPost() != null)
            return new PostEditPresenter(getArgs());
        else
            return new PostPresenter();
    }

    @OnClick(R.id.cancel_action)
    void onPhotoCancel() {
        getPresenter().removeImage();
    }

    @OnClick(R.id.fab_progress)
    void onProgressClick() {
        getPresenter().onProgressClicked();
    }

    @OnClick(R.id.close)
    void onClose() {
        getPresenter().cancelClicked();
    }

    @OnClick(R.id.space)
    void onSpaceClicked() {
        if (ViewUtils.isTablet(getActivity())) getPresenter().cancelClicked();
    }

    @OnClick(R.id.post_button)
    void onPost() {
        SoftInputUtil.hideSoftInputMethod(post);
        postButton.setEnabled(false);
        post.setFocusable(false);
        getPresenter().post();
    }

    @Override
    public void onPostError() {
        postButton.setEnabled(true);
        post.setFocusable(true);
        post.setFocusableInTouchMode(true);
    }

    @OnClick(R.id.image)
    void onImage() {
        if (photoPickerLayout.isPanelVisible()) {
            photoPickerLayout.hidePanel();
        } else {
            post.clearFocus();
            photoPickerLayout.showPanel();
        }
    }

    @Override
    public void enableButton() {
        postButton.setTextColor(getResources().getColor(R.color.bucket_detailed_text_color));
        postButton.setClickable(true);
    }

    @Override
    public void attachPhoto(Uri uri) {
        photoPickerLayout.hidePanel();

        if (uri != null) {
            attachedPhoto.setController(GraphicUtils.provideFrescoResizingController(uri, attachedPhoto.getController()));
            post.setHint(R.string.photo_hint);
            imageContainer.setVisibility(View.VISIBLE);
            image.setImageResource(R.drawable.ic_post_add_image_selected);
        } else {
            attachedPhoto.setImageURI(null);
            post.setHint(R.string.post_hint);
            imageContainer.setVisibility(View.GONE);
            image.setImageResource(R.drawable.ic_post_add_image_normal);
        }
    }

    @Override
    public void showProgress() {
        shadow.setVisibility(View.VISIBLE);
        fabProgress.setVisibility(View.VISIBLE);
        fabProgress.setIcon(R.drawable.ic_upload_cloud, R.drawable.ic_upload_cloud);
        fabProgress.setIndeterminate(true);
        fabProgress.showProgress(true);
        int color = getResources().getColor(R.color.bucket_blue);
        circleView.setColor(color);
    }

    @Override
    public void setText(String text) {
        post.setText(text);
    }

    @Override
    public void imageError() {
        fabProgress.showProgress(false);
        fabProgress.setIcon(R.drawable.ic_upload_retry, R.drawable.ic_upload_retry);
        int color = getResources().getColor(R.color.bucket_red);
        circleView.setColor(color);
    }

    @Override
    public void hideProgress() {
        fabProgress.setVisibility(View.GONE);
        shadow.setVisibility(View.GONE);
    }

    @Override
    public void disableButton() {
        postButton.setTextColor(getResources().getColor(R.color.grey));
        postButton.setClickable(false);
    }

    @Override
    public void setName(String userName) {
        name.setText(userName);
    }

    @Override
    public void setAvatar(String avatarUrl) {
        avatar.setImageURI(Uri.parse(avatarUrl));
    }

    @Override
    public void enableImagePicker() {
        pickerDisabled = false;
        updatePickerState();
    }

    @Override
    public void disableImagePicker() {
        pickerDisabled = true;
        updatePickerState();
    }

    private void updatePickerState() {
        image.setEnabled(!pickerDisabled);
    }

    @Override
    public void showCancelationDialog() {
        if (dialog == null || !dialog.isShowing()) {
            dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(getString(R.string.app_name))
                    .setContentText(getString(R.string.post_cancel_message))
                    .setConfirmText(getString(R.string.social_add_friend_yes))
                    .setCancelText(getString(R.string.social_add_friend_no))
                    .setConfirmClickListener(sweetAlertDialog -> {
                        sweetAlertDialog.dismissWithAnimation();
                        dialog = null;
                        cancel();
                    })
                    .setCancelClickListener(SweetAlertDialog::dismissWithAnimation);
            dialog.show();
        }
    }

    @Override
    public void cancel() {
        if (dialog != null && dialog.isShowing()) dialog.dismiss();

        SoftInputUtil.hideSoftInputMethod(post);
        router.moveTo(Route.POST_CREATE, NavigationConfigBuilder.forRemoval()
                .fragmentManager(getFragmentManager())
                .build());
    }

    private boolean onBackPressed() {
        if (photoPickerLayout.isPanelVisible()) {
            photoPickerLayout.hidePanel();
            return true;
        } else {
            getPresenter().cancelClicked();
            return true;
        }
    }

    @Override
    public void hidePhotoControl() {
        image.setVisibility(View.GONE);
    }

    @Override
    public int getEventBusPriority() {
        return 1;
    }
}

