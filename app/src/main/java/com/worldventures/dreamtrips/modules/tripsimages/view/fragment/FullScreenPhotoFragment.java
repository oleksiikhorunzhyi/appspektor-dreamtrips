package com.worldventures.dreamtrips.modules.tripsimages.view.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.worldventures.dreamtrips.core.utils.UniversalImageLoader;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.common.view.util.TextWatcherAdapter;
import com.worldventures.dreamtrips.modules.tripsimages.model.Flag;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenAvailableObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Image;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.FullScreenPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.activity.FullScreenPhotoActivity;

import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_fullscreen_photo)
public class FullScreenPhotoFragment<T extends IFullScreenAvailableObject> extends BaseFragment<FullScreenPresenter<T>> implements FullScreenPresenter.View {

    public static final String EXTRA_POSITION = "EXTRA_POSITION";

    @InjectView(R.id.iv_image)
    protected ImageView ivImage;
    @InjectView(R.id.pb)
    protected ProgressBar progressBar;
    @InjectView(R.id.ll_content_wrapper)
    protected LinearLayout llContentWraper;
    @InjectView(R.id.tv_title)
    protected TextView tvTitle;
    @InjectView(R.id.tv_description)
    protected TextView tvDescription;
    @InjectView(R.id.tv_see_more)
    protected TextView tvSeeMore;
    @InjectView(R.id.tv_location)
    protected TextView tvLocation;
    @InjectView(R.id.tv_date)
    protected TextView tvDate;
    @InjectView(R.id.tv_likes_count)
    protected TextView tvLikesCount;
    @InjectView(R.id.tv_comments_count)
    protected TextView tvCommentsCount;
    @InjectView(R.id.iv_like)
    protected ImageView ivLike;
    @InjectView(R.id.iv_comment)
    protected ImageView ivComment;
    @InjectView(R.id.iv_share)
    protected ImageView ivShare;
    @InjectView(R.id.iv_flag)
    protected ImageView ivFlag;
    @InjectView(R.id.iv_delete)
    protected ImageView ivDelete;

    @Inject
    protected UniversalImageLoader imageLoader;
    private SimpleImageLoadingListener originalCallback;
    private SimpleImageLoadingListener mediumCallback;
    private TripImagesListFragment.Type type;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);

        FullScreenPhotoActivity activity = (FullScreenPhotoActivity) getActivity();
        type = activity.getType();
        IFullScreenAvailableObject photo = activity.getPhoto(getArguments().getInt(EXTRA_POSITION));

        getPresenter().onCreate();

        ImageSize maxImageSize = new ImageSize(ViewUtils.getScreenWidth(getActivity()), ViewUtils.getScreenHeight(getActivity()));
        ImageSizeUtils.defineTargetSizeForView(new ImageViewAware(ivImage), maxImageSize);

        if (photo != null) {
            getPresenter().setupPhoto((T) photo);
            getPresenter().setupType(type);
        }
        getPresenter().setupActualViewState();

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
    protected FullScreenPresenter createPresenter(Bundle savedInstanceState) {
        FullScreenPhotoActivity activity = (FullScreenPhotoActivity) getActivity();
        int position = getArguments().getInt(EXTRA_POSITION);
        IFullScreenAvailableObject photo = activity.getPhoto(position);

        return FullScreenPresenter.create(this, photo);
    }


    @OnClick(R.id.iv_share)
    public void actionShare() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
        builder.title("Share")
                .items(R.array.share_dialog_items)
                .itemsCallback((dialog, view, which, text) -> {
                    if (which == 0) {
                        getPresenter().onFbShare();
                    } else {
                        getPresenter().onTwitterShare();
                    }
                }).show();
    }

    @OnClick(R.id.iv_image)
    public void actionImageClick() {
        llContentWraper.setVisibility(llContentWraper.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    @OnClick(R.id.tv_see_more)
    public void actionSeeMore() {
        tvTitle.setVisibility(View.VISIBLE);
        tvLocation.setVisibility(View.VISIBLE);
        tvDate.setVisibility(View.VISIBLE);
        tvDescription.setSingleLine(false);

        tvSeeMore.setVisibility(View.GONE);
        if (tvDescription.getText().length() == 0) {
            tvDescription.setVisibility(View.GONE);
        }
        if (tvDate.getText().length() == 0) {
            tvDate.setVisibility(View.GONE);
        }
        if (tvLocation.getText().length() == 0) {
            tvLocation.setVisibility(View.GONE);
        }
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
                        getPresenter().onDeleteAction();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @OnClick(R.id.iv_like)
    public void actionLike() {
        getPresenter().onLikeAction();
    }

    @OnClick(R.id.iv_flag)
    public void actionFlag() {
        PopupMenu popup = new PopupMenu(getActivity(), ivFlag);
        List<Flag> values = getPresenter().getFlagContent();
        for (int i = 0; i < values.size(); i++) {
            Flag flagContent = values.get(i);
            popup.getMenu().add(0, i, i, flagContent.getCode());
        }
        popup.setOnMenuItemClickListener(item -> {
            getPresenter().showFlagAction(item.getItemId());
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
                        getPresenter().sendFlagAction(reason, desc);
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
    public void setLikeCountVisibility(boolean likeCountVisible) {
        tvLikesCount.setVisibility(likeCountVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    @Override
    public void setDate(String date) {
        if (TextUtils.isEmpty(date)) {
            tvDate.setVisibility(View.GONE);
            date = "";
        }
        tvDate.setText(date.toUpperCase());
    }

    @Override
    public void setLocation(String location) {
        if (TextUtils.isEmpty(location)) {
            tvLocation.setVisibility(View.GONE);
            location = "";
        }
        tvLocation.setText(location.toUpperCase());
    }

    @Override
    public void setCommentCount(int count) {
        if (count == -1) {
            tvCommentsCount.setVisibility(View.GONE);
        }
        tvCommentsCount.setText(count + getString(R.string.comments));
    }

    @Override
    public void setLikeCount(int count) {
        if (count == -1) {
            tvCommentsCount.setVisibility(View.GONE);
        }
        tvLikesCount.setText(count + getString(R.string.likes));
    }

    @Override
    public void setDescription(String desc) {
        tvDescription.setText(desc);
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
        ivLike.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }
}
