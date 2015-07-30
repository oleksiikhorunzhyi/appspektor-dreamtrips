package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.common.view.custom.PinProgressButton;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.common.view.util.TextWatcherAdapter;
import com.worldventures.dreamtrips.modules.feed.presenter.PostPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.dialog.PickImageDelegate;
import com.worldventures.dreamtrips.modules.tripsimages.view.dialog.PickImageDialog;

import butterknife.InjectView;
import butterknife.OnClick;
import icepick.Icicle;

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
    @InjectView(R.id.download_progress)
    PinProgressButton pinProgressButton;
    @InjectView(R.id.shadow)
    View shadow;
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
        pickImageDelegate = new PickImageDelegate(getActivity(), this, pidTypeShown);
        pickImageDelegate.setChooseImageCallback(getPresenter().provideSelectImageCallback());
        pickImageDelegate.setFbImageCallback(getPresenter().provideFbCallback());
        pickImageDelegate.setMakePhotoImageCallback(getPresenter().provideSelectImageCallback());

        pinProgressButton.setVisibility(View.GONE);
        shadow.setVisibility(View.GONE);

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

    @OnClick(R.id.close)
    void onClose() {
        getPresenter().cancel();
        getActivity().onBackPressed();
    }

    @OnClick(R.id.post_button)
    void onPost() {
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
                });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (pidTypeShown != 0) {
            pickImageDelegate.handlePickDialogActivityResult(requestCode, resultCode, data);
            pidTypeShown = 0;
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
            image.setImageResource(R.drawable.ic_post_add_image_selected);
        } else {
            image.setImageResource(R.drawable.ic_post_add_image_normal);
        }
    }

    @Override
    public void showProgress() {
        pinProgressButton.setFailed(false);
        pinProgressButton.setVisibility(View.VISIBLE);
        shadow.setVisibility(View.VISIBLE);
    }

    @Override
    public void setText(String text) {
        post.setText(text);
    }

    @Override
    public void imageError() {
        pinProgressButton.setFailed(true);
    }

    @Override
    public void setProgress(int progress) {
        pinProgressButton.setProgress(progress);
    }

    @Override
    public void hideProgress() {
        pinProgressButton.setVisibility(View.GONE);
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
