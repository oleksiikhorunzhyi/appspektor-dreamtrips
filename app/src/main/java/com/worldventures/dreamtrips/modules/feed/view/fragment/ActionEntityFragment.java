package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.utils.ui.SoftInputUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.BackStackDelegate;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.common.view.custom.KeyCallbackEditText;
import com.worldventures.dreamtrips.modules.common.view.custom.SmartAvatarView;
import com.worldventures.dreamtrips.modules.common.view.util.TextWatcherAdapter;
import com.worldventures.dreamtrips.modules.feed.presenter.ActionEntityPresenter;
import com.worldventures.dreamtrips.modules.feed.view.cell.PhotoPostCreationCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.delegate.PhotoPostCreationDelegate;
import com.worldventures.dreamtrips.modules.feed.view.util.PhotoPostCreationItemDecorator;
import com.worldventures.dreamtrips.modules.trips.model.Location;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.EditPhotoTagsBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.PhotoTag;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.EditPhotoTagsFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

public abstract class ActionEntityFragment<PM extends ActionEntityPresenter, P extends Parcelable>
        extends RxBaseFragmentWithArgs<PM, P> implements ActionEntityPresenter.View, EditPhotoTagsFragment.Callback,
        LocationFragment.Callback, PhotoPostCreationDelegate {

    @Inject
    BackStackDelegate backStackDelegate;
    @Inject
    @ForActivity
    Provider<Injector> injectorProvider;

    @InjectView(R.id.avatar)
    protected SmartAvatarView avatar;
    @InjectView(R.id.name)
    protected TextView name;
    @InjectView(R.id.post)
    protected KeyCallbackEditText post;
    @InjectView(R.id.post_button)
    protected Button postButton;
    @InjectView(R.id.image)
    protected ImageView image;
    @InjectView(R.id.location)
    protected ImageView locationBtn;
    @InjectView(R.id.photos)
    protected RecyclerView photosList;

    BaseDelegateAdapter adapter;
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
        //
        adapter = new BaseDelegateAdapter(getContext(), this);
        adapter.registerCell(UploadTask.class, PhotoPostCreationCell.class);
        adapter.registerDelegate(UploadTask.class, this);
        photosList.setLayoutManager(new LinearLayoutManager(getContext()));
        photosList.addItemDecoration(new PhotoPostCreationItemDecorator());
        photosList.setAdapter(adapter);
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
        backStackDelegate.setListener(this::onBack);
        setupTextField();
        updateLocationButtonState();
    }

    @Override
    public void attachPhotos(List<UploadTask> images) {
        adapter.addItems(images);
        adapter.notifyDataSetChanged();
    }

    private void updateLocationButtonState() {
        updateLocationButtonState(!TextUtils.isEmpty(getPresenter().getLocation().getName()));
    }

    private void updateLocationButtonState(boolean isSelected) {
        locationBtn.setSelected(isSelected);
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
        backStackDelegate.setListener(null);
        post.setOnFocusChangeListener(null);
        SoftInputUtil.hideSoftInputMethod(getActivity());
    }

    @Override
    public void setName(String userName) {
        name.setText(userName);
    }

    @Override
    public void setAvatar(User user) {
        avatar.setImageURI(Uri.parse(user.getAvatar().getThumb()));
        avatar.setup(user, injectorProvider.get());
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

    protected boolean onBack() {
        getPresenter().cancelClicked();
        return true;
    }

    @OnClick(R.id.location)
    void onLocation() {
        getPresenter().onLocationClicked();
    }

    @OnClick(R.id.close)
    void onClose() {
        getPresenter().cancelClicked();
    }

    @OnClick(R.id.space)
    void onSpaceClicked() {
        if (ViewUtils.isTablet(getActivity())) getPresenter().cancelClicked();
    }

    public void showPhotoTagView(EditPhotoTagsBundle.PhotoEntity photoEntity, List<PhotoTag> photoTags) {
        router.moveTo(Route.EDIT_PHOTO_TAG_FRAGMENT, NavigationConfigBuilder
                .forFragment()
                .backStackEnabled(true)
                .fragmentManager(getActivity().getSupportFragmentManager())
                .containerId(R.id.container_details_floating)
                .targetFragment(this)
                .data(new EditPhotoTagsBundle(photoEntity, photoTags))
                .build());
    }

    @Override
    public void onTagSelected(ArrayList<PhotoTag> addedTags, ArrayList<PhotoTag> removedTags) {
        getPresenter().onTagSelected(addedTags, removedTags);
    }

    @Override
    public void openLocation(Location location) {
        router.moveTo(Route.ADD_LOCATION, NavigationConfigBuilder
                .forFragment()
                .backStackEnabled(true)
                .fragmentManager(getActivity().getSupportFragmentManager())
                .containerId(R.id.container_details_floating)
                .targetFragment(ActionEntityFragment.this)
                .data(location)
                .build());
    }

    @Override
    public void onLocationDone(Location location) {
        getPresenter().updateLocation(location);
    }

    //////////////////////////////////////////
    // Cell callbacks
    //////////////////////////////////////////

    @Override
    public void onCellClicked(UploadTask model) {
        // nothing to do
    }

    @Override
    public void onTagClicked(UploadTask uploadTask) {
        //TODO handler tag clicked
//        showPhotoTagView();
    }

    @Override
    public void onProgressClicked(UploadTask uploadTask) {
        // nothing to do
    }

    @Override
    public void onRemoveClicked(UploadTask uploadTask) {

    }

    protected abstract int getPostButtonText();

    protected abstract Route getRoute();
}
