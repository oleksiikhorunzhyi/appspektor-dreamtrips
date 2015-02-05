package com.worldventures.dreamtrips.view.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.ImageSizeUtils;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.FlagContent;
import com.worldventures.dreamtrips.core.model.Image;
import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.presentation.FullScreenPhotoFragmentPM;
import com.worldventures.dreamtrips.utils.UniversalImageLoader;
import com.worldventures.dreamtrips.utils.ViewUtils;
import com.worldventures.dreamtrips.view.activity.FullScreenPhotoActivity;
import com.worldventures.dreamtrips.view.util.TextWatcherAdapter;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

import static com.worldventures.dreamtrips.view.fragment.TripImagesListFragment.Type.MEMBER_IMAGES;
import static com.worldventures.dreamtrips.view.fragment.TripImagesListFragment.Type.MY_IMAGES;

@Layout(R.layout.fragment_fullscreen_photo)
public class FullScreenPhotoFragment extends BaseFragment<FullScreenPhotoFragmentPM> implements FullScreenPhotoFragmentPM.View {

    public static final String EXTRA_PHOTO = "EXTRA_PHOTO";
    public static final String EXTRA_POSITION = "EXTRA_POSITION";

    @InjectView(R.id.iv_image)
    ImageView ivImage;
    @InjectView(R.id.tv_title)
    TextView tvTitle;
    @InjectView(R.id.iv_like)
    ImageView ivLike;
    @InjectView(R.id.iv_flag)
    ImageView ivFlag;
    @InjectView(R.id.pb)
    ProgressBar progressBar;

    @Inject
    UniversalImageLoader imageLoader;
    private SimpleImageLoadingListener originalCallback;
    private SimpleImageLoadingListener mediumCallback;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        ImageSize maxImageSize = new ImageSize(ViewUtils.getScreenWidth(getActivity()), ViewUtils.getScreenHeight(getActivity()));
        ImageSizeUtils.defineTargetSizeForView(new ImageViewAware(ivImage), maxImageSize);
        TripImagesListFragment.Type type = ((FullScreenPhotoActivity) getActivity()).getType();
        if (type != MY_IMAGES && type != MEMBER_IMAGES) {
            ivFlag.setVisibility(View.GONE);
            ivLike.setVisibility(View.GONE);
        }
        Photo photo = ((FullScreenPhotoActivity) getActivity()).getPhoto(getArguments().getInt(EXTRA_POSITION));

        if (photo != null) {
            getPresentationModel().setPhoto((Photo) photo);
        }
        System.gc();
        Image images = getPresentationModel().getPhoto().getImages();
        String medium = images.getMedium().getUrl();
        String original = images.getOriginal().getUrl();
        originalCallback = new SimpleImageLoadingListener() {

            @Override
            public void onLoadingStarted(String imageUri, View view) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                progressBar.setVisibility(View.GONE);
                informUser("Error while loading image");
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                progressBar.setVisibility(View.GONE);
            }
        };
        mediumCallback = new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                imageLoader.loadImage(original, ivImage, UniversalImageLoader.OP_FULL_SCREEN, originalCallback);
            }
        };
        imageLoader.loadImage(medium, ivImage, UniversalImageLoader.OP_FULL_SCREEN, mediumCallback);


        getPresentationModel().onCreate();
    }

    @Override
    protected FullScreenPhotoFragmentPM createPresentationModel(Bundle savedInstanceState) {
        return new FullScreenPhotoFragmentPM(this);
    }

    @OnClick(R.id.iv_like)
    public void actionLike() {
        getPresentationModel().onLikeAction();
    }

    @OnClick(R.id.iv_flag)
    public void actionFlag() {
        PopupMenu popup = new PopupMenu(getActivity(), ivFlag);
        FlagContent[] values = FlagContent.values();
        for (int i = 0; i < values.length; i++) {
            FlagContent flagContent = values[i];
            popup.getMenu().add(0, i, i, flagContent.getTitle());
        }
        popup.setOnMenuItemClickListener(item -> {
            getPresentationModel().showFlagAction(item.getItemId());
            return true;
        });
        popup.show();
    }

    public void showFlagConfirmDialog(String reason, String desc) {
        String content = "WorldVentures personnel will be notified review this image for " + reason.toLowerCase() + ". Do you want continue?";
        new MaterialDialog.Builder(getActivity())
                .title("Confirm")
                .content(content)
                .positiveText("Send")
                .negativeText("Cancel")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        getPresentationModel().sendFlagAction(reason, desc);
                    }
                })
                .show();
    }

    public void showFlagDescription(String reason) {
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title("Flag")
                .customView(R.layout.dialog_flag_description)
                .positiveText("Accept")
                .negativeText("Cancel")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        String desc = ((EditText) dialog.getCustomView()
                                .findViewById(R.id.tv_description))
                                .getText().toString();
                        showFlagConfirmDialog(reason, desc);
                    }
                }).build();
        dialog.show();
        Button positiveButton = dialog.getActionButton(DialogAction.POSITIVE);
        EditText etDesc = (EditText) dialog.getCustomView().findViewById(R.id.tv_description);
        etDesc.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                positiveButton.setEnabled(s.toString().trim().length() > 0);
            }
        });
    }

    @Override
    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    @Override
    public void setLiked(boolean isLiked) {
        ivLike.setSelected(isLiked);
    }
}
