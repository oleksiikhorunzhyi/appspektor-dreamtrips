package com.worldventures.dreamtrips.view.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
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
import com.worldventures.dreamtrips.core.model.IFullScreenAvailableObject;
import com.worldventures.dreamtrips.core.model.Image;
import com.worldventures.dreamtrips.core.model.config.Flag;
import com.worldventures.dreamtrips.presentation.fullscreen.BaseFSViewPM;
import com.worldventures.dreamtrips.utils.UniversalImageLoader;
import com.worldventures.dreamtrips.utils.ViewUtils;
import com.worldventures.dreamtrips.view.activity.FullScreenPhotoActivity;
import com.worldventures.dreamtrips.view.util.TextWatcherAdapter;

import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

import static com.worldventures.dreamtrips.view.fragment.TripImagesListFragment.Type;

@Layout(R.layout.fragment_fullscreen_photo)
public class FullScreenPhotoFragment<T extends IFullScreenAvailableObject> extends BaseFragment<BaseFSViewPM<T>> implements BaseFSViewPM.View {

    public static final String EXTRA_PHOTO = "EXTRA_PHOTO";
    public static final String EXTRA_POSITION = "EXTRA_POSITION";

    @InjectView(R.id.iv_image)
    ImageView ivImage;
    @InjectView(R.id.tv_title)
    TextView tvTitle;
    @InjectView(R.id.iv_like)
    ImageView ivLike;
    @InjectView(R.id.iv_delete)
    ImageView ivDelete;
    @InjectView(R.id.iv_flag)
    ImageView ivFlag;
    @InjectView(R.id.pb)
    ProgressBar progressBar;
    @InjectView(R.id.ripple_like)
    View vRippleLike;

    @InjectView(R.id.vg_inpire_me)
    ViewGroup vgInspireMe;
    @InjectView(R.id.tv_description)
    TextView tvDescription;

    @Inject
    UniversalImageLoader imageLoader;
    private SimpleImageLoadingListener originalCallback;
    private SimpleImageLoadingListener mediumCallback;
    private Type type;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);

        FullScreenPhotoActivity activity = (FullScreenPhotoActivity) getActivity();
        type = activity.getType();
        IFullScreenAvailableObject photo = activity.getPhoto(getArguments().getInt(EXTRA_POSITION));

        getPresentationModel().onCreate();

        ImageSize maxImageSize = new ImageSize(ViewUtils.getScreenWidth(getActivity()), ViewUtils.getScreenHeight(getActivity()));
        ImageSizeUtils.defineTargetSizeForView(new ImageViewAware(ivImage), maxImageSize);

        if (photo != null) {
            getPresentationModel().setupPhoto((T) photo);
            getPresentationModel().setupType(type);
        }
        getPresentationModel().setupActualViewState();

    }

    @Override
    public void loadImage(Image images) {
        String medium = images.getThumb().getUrl();
        String original = images.getMedium().getUrl();
        originalCallback = new SimpleImageLoadingListener() {

            @Override
            public void onLoadingStarted(String imageUri, View view) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                progressBar.setVisibility(View.GONE);
                // informUser("Error while loading image");
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
    }

    @Override
    protected BaseFSViewPM createPresentationModel(Bundle savedInstanceState) {
        FullScreenPhotoActivity activity = (FullScreenPhotoActivity) getActivity();
        int position = getArguments().getInt(EXTRA_POSITION);
        IFullScreenAvailableObject photo = activity.getPhoto(position);

        return BaseFSViewPM.create(this, photo);
    }


    @OnClick(R.id.iv_twitter)
    public void twitterShare() {
        getPresentationModel().onTwitterShare(((FullScreenPhotoActivity) getActivity()));
    }

    @OnClick(R.id.iv_facebook)
    public void fbShare() {
        getPresentationModel().onFbShare(((FullScreenPhotoActivity) getActivity()));
    }

    @OnClick(R.id.iv_delete)
    public void actionDelete() {
        new MaterialDialog.Builder(getActivity())
                .title(R.string.delete_photo_title)
                .content(R.string.delete_photo_text)
                .positiveText(R.string.delete_photo_positiove)
                .negativeText(R.string.delete_photo_negative)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        getPresentationModel().onDeleteAction();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @OnClick(R.id.iv_like)
    public void actionLike() {
        getPresentationModel().onLikeAction();
    }

    @OnClick(R.id.iv_flag)
    public void actionFlag() {
        PopupMenu popup = new PopupMenu(getActivity(), ivFlag);
        List<Flag> values = getPresentationModel().getFlagContent();
        for (int i = 0; i < values.size(); i++) {
            Flag flagContent = values.get(i);
            popup.getMenu().add(0, i, i, flagContent.getCode());
        }
        popup.setOnMenuItemClickListener(item -> {
            getPresentationModel().showFlagAction(item.getItemId());
            return true;
        });
        popup.show();
    }

    public void showFlagConfirmDialog(String reason, String desc) {
        String content = getString(R.string.flag_photo_first) + " " + reason.toLowerCase() + " " + getString(R.string.flag_photo_second);
        new MaterialDialog.Builder(getActivity())
                .title(R.string.flag_photo_title)
                .content(content)
                .positiveText(R.string.flag_photo_positive)
                .negativeText(R.string.flag_photo_negative)
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
                .title(R.string.flag_description_title)
                .customView(R.layout.dialog_flag_description)
                .positiveText(R.string.flag_description_positive)
                .negativeText(R.string.flag_description_negative)
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
    public void setInspireDescription(String desc) {
        if (!TextUtils.isEmpty(desc)) {
            tvDescription.setText(desc);
            vgInspireMe.setVisibility(View.VISIBLE);
        } else {
            vgInspireMe.setVisibility(View.GONE);
        }
    }

    @Override
    public void setLiked(boolean isLiked) {
        ivLike.setSelected(isLiked);
    }

    @Override
    public void setFlagVisibility(boolean isVisible) {
        ivFlag.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setDeleteVisibility(boolean isVisible) {
        ivDelete.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setLikeVisibility(boolean isVisible) {
        vRippleLike.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void informUser(String stringId) {
        super.informUser(stringId);
    }

}
