package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.utils.ui.SoftInputUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.common.view.util.TextWatcherAdapter;
import com.worldventures.dreamtrips.modules.feed.presenter.PostPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.dialog.PickImageDelegate;
import com.worldventures.dreamtrips.modules.tripsimages.view.dialog.PickImageDialog;

import butterknife.InjectView;
import butterknife.OnClick;
import icepick.Icicle;
import mbanje.kurt.fabbutton.CircleImageView;
import mbanje.kurt.fabbutton.FabButton;

@Layout(R.layout.layout_post)
public class PostFragment extends BaseFragment<PostPresenter> implements PostPresenter.View {

    @InjectView(R.id.avatar)
    SimpleDraweeView avatar;
    @InjectView(R.id.attached_photo)
    SimpleDraweeView attachedPhoto;
    @InjectView(R.id.name)
    TextView name;
    @InjectView(R.id.post)
    EditText post;
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

    private PickImageDelegate pickImageDelegate;

    @Icicle
    int pidTypeShown;

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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pidTypeShown = getPresenter().getPidType();
        pickImageDelegate = new PickImageDelegate(getActivity(), this, pidTypeShown);
        pickImageDelegate.setChooseImageCallback(getPresenter().provideSelectImageCallback());
        pickImageDelegate.setFbImageCallback(getPresenter().provideFbCallback());
        pickImageDelegate.setMakePhotoImageCallback(getPresenter().provideSelectImageCallback());

        post.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence constraint, int start, int before, int count) {
                super.onTextChanged(constraint, start, before, count);
                getPresenter().postInputChanged(constraint.toString());
            }
        });
    }

    @Override
    protected PostPresenter createPresenter(Bundle savedInstanceState) {
        return new PostPresenter();
    }

    @OnClick(R.id.cancel_action)
    void onPhotoCancel() {
        getPresenter().removeImage();
    }

    @OnClick(R.id.fab_progress)
    void onProgressClick() {
        getPresenter().restartPhotoUpload();
    }

    @OnClick({R.id.close, R.id.space})
    void onClose() {
        SoftInputUtil.hideSoftInputMethod(post);
        getPresenter().cancel();
        getActivity().onBackPressed();
    }

    @OnClick(R.id.post_button)
    void onPost() {
        SoftInputUtil.hideSoftInputMethod(post);
        getPresenter().post();
    }

    @OnClick(R.id.image)
    void onImage() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
        builder.title(getString(R.string.select_photo))
                .items(R.array.dialog_add_bucket_photo)
                .itemsCallback((dialog, view, which, text) -> {
                    switch (which) {
                        case 0:
                            pickImageDelegate.actionFacebook();
                            pidTypeShown = PickImageDialog.REQUEST_FACEBOOK;
                            break;
                        case 1:
                            pickImageDelegate.actionCapture();
                            pidTypeShown = PickImageDialog.REQUEST_CAPTURE_PICTURE;
                            break;
                        case 2:
                            pickImageDelegate.actionGallery();
                            pidTypeShown = PickImageDialog.REQUEST_PICK_PICTURE;
                            break;
                    }

                    getPresenter().setPidType(pidTypeShown);
                });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (pidTypeShown != 0) {
            pickImageDelegate.handlePickDialogActivityResult(requestCode, resultCode, data);
            pidTypeShown = 0;
            getPresenter().setPidType(pidTypeShown);
        }
    }

    @Override
    public void enableButton() {
        postButton.setTextColor(getResources().getColor(R.color.bucket_detailed_text_color));
    }

    @Override
    public void attachPhoto(Uri uri) {
        attachedPhoto.setImageURI(uri);
        if (uri != null) {
            post.setHint(R.string.photo_hint);
            imageContainer.setVisibility(View.VISIBLE);
            image.setImageResource(R.drawable.ic_post_add_image_selected);
        } else {
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
        postButton.setTextColor(getResources().getColor(R.color.gray));
    }

    @Override
    public void setName(String userName) {
        name.setText(userName);
    }

    @Override
    public void setAvatar(String avatarUrl) {
        avatar.setImageURI(Uri.parse(avatarUrl));
    }
}
