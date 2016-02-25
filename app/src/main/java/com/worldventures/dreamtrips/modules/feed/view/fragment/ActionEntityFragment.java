package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.utils.ui.SoftInputUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.common.view.custom.KeyCallbackEditText;
import com.worldventures.dreamtrips.modules.common.view.util.TextWatcherAdapter;
import com.worldventures.dreamtrips.modules.feed.presenter.ActionEntityPresenter;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import mbanje.kurt.fabbutton.CircleImageView;
import mbanje.kurt.fabbutton.FabButton;

public abstract class ActionEntityFragment<PM extends ActionEntityPresenter, P extends Parcelable>
        extends RxBaseFragmentWithArgs<PM, P> implements ActionEntityPresenter.View {

    @InjectView(R.id.avatar)
    protected SimpleDraweeView avatar;
    @InjectView(R.id.attached_photo)
    protected SimpleDraweeView attachedPhoto;
    @InjectView(R.id.name)
    protected TextView name;
    @InjectView(R.id.post)
    protected KeyCallbackEditText post;
    @InjectView(R.id.post_button)
    protected Button postButton;
    @InjectView(R.id.fab_progress)
    protected FabButton fabProgress;
    @InjectView(R.id.fabbutton_circle)
    protected CircleImageView circleView;
    @InjectView(R.id.shadow)
    protected View shadow;
    @InjectView(R.id.image_container)
    protected FrameLayout imageContainer;
    @InjectView(R.id.image)
    protected ImageView image;

    SweetAlertDialog dialog;

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
        postButton.setText(getPostButtonText());
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
        setupTextField();
    }

    protected void setupTextField() {
        post.addTextChangedListener(textWatcher);
        post.setOnKeyPreImeListener((keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                post.clearFocus();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        post.removeTextChangedListener(textWatcher);
        post.setOnFocusChangeListener(null);
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
    public void setText(String text) {
        post.setText(text);
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
        router.moveTo(getRoute(), NavigationConfigBuilder.forRemoval()
                .fragmentManager(getFragmentManager())
                .build());
    }

    @Override
    public void enableButton() {
        postButton.setTextColor(getResources().getColor(R.color.bucket_detailed_text_color));
        postButton.setClickable(true);
    }

    @Override
    public void disableButton() {
        postButton.setTextColor(getResources().getColor(R.color.grey));
        postButton.setClickable(false);
    }

    @Override
    public void onPostError() {
        postButton.setEnabled(true);
        post.setFocusable(true);
        post.setFocusableInTouchMode(true);
    }

    @OnClick(R.id.post_button)
    void onPost() {
        SoftInputUtil.hideSoftInputMethod(post);
        postButton.setEnabled(false);
        post.setFocusable(false);
        getPresenter().post();
    }

    @OnClick(R.id.close)
    void onClose() {
        getPresenter().cancelClicked();
    }

    @OnClick(R.id.space)
    void onSpaceClicked() {
        if (ViewUtils.isTablet(getActivity())) getPresenter().cancelClicked();
    }

    protected abstract int getPostButtonText();

    protected abstract Route getRoute();
}
