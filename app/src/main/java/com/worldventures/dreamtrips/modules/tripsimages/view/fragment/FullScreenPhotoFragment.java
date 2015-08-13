package com.worldventures.dreamtrips.modules.tripsimages.view.fragment;

import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.common.view.util.TextWatcherAdapter;
import com.worldventures.dreamtrips.modules.tripsimages.model.Flag;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Image;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.FullScreenPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.ScaleImageView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

@Layout(R.layout.fragment_fullscreen_photo)
public class FullScreenPhotoFragment<T extends IFullScreenObject>
        extends BaseFragment<FullScreenPresenter<T>> implements FullScreenPresenter.View {

    public static final String EXTRA_TYPE = "EXTRA_TYPE";
    public static final String EXTRA_PHOTO = "EXTRA_PHOTO";

    @InjectView(R.id.iv_image)
    protected ScaleImageView ivImage;
    @InjectView(R.id.ll_global_content_wrapper)
    protected LinearLayout llContentWrapper;
    @InjectView(R.id.ll_more_info)
    protected LinearLayout llMoreInfo;
    @InjectView(R.id.tv_title)
    protected TextView tvTitle;
    @InjectView(R.id.tv_description)
    protected TextView tvDescription;
    @InjectView(R.id.tv_see_more)
    protected TextView tvSeeMore;
    @InjectView(R.id.tv_location)
    protected TextView tvLocation;
    @InjectView(R.id.textViewInspireMeTitle)
    protected TextView textViewInspireMeTitle;
    @InjectView(R.id.tv_date)
    protected TextView tvDate;
    @InjectView(R.id.tv_likes_count)
    protected TextView tvLikesCount;
    @InjectView(R.id.tv_comments_count)
    protected TextView tvCommentsCount;
    @InjectView(R.id.iv_like)
    protected ImageView ivLike;
    @InjectView(R.id.iv_share)
    protected ImageView ivShare;
    @InjectView(R.id.iv_flag)
    protected ImageView ivFlag;
    @InjectView(R.id.iv_delete)
    protected ImageView ivDelete;
    @InjectView(R.id.user_photo)
    protected SimpleDraweeView civUserPhoto;
    @InjectView(R.id.checkBox)
    protected CheckBox checkBox;
    @InjectView(R.id.progress_flag)
    protected ProgressBar progressFlag;
    @InjectView(R.id.iv_comment)
    protected ImageView ivComment;

    private TripImagesListFragment.Type type;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);

        if (type == TripImagesListFragment.Type.BUCKET_PHOTOS) {
            ivShare.setVisibility(View.GONE);
            tvSeeMore.setVisibility(View.GONE);
        }

        ivImage.setSingleTapListener(this::toggleContent);
        ivImage.setDoubleTapListener(this::hideContent);

        if (ViewUtils.isLandscapeOrientation(getActivity())) {
            hideContent();
        } else {
            showContent();
        }
    }

    @Override
    public void onDestroyView() {
        if (ivImage != null && ivImage.getController() != null)
            ivImage.getController().onDetach();
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void loadImage(Image image) {
        String lowUrl = image.getThumbUrl(getResources());
        ivImage.requestLayout();
        ViewTreeObserver viewTreeObserver = ivImage.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (ivImage != null) {
                    int size = Math.max(ivImage.getWidth(), ivImage.getHeight());
                    DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                            .setLowResImageRequest(ImageRequest.fromUri(lowUrl))
                            .setImageRequest(ImageRequest.fromUri(image.getUrl(size, size)))
                            .build();
                    ivImage.setController(draweeController);

                    ViewTreeObserver viewTreeObserver = ivImage.getViewTreeObserver();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        viewTreeObserver.removeOnGlobalLayoutListener(this);
                    } else {
                        viewTreeObserver.removeGlobalOnLayoutListener(this);
                    }
                }
            }
        });
    }

    @Override
    protected FullScreenPresenter createPresenter(Bundle savedInstanceState) {
        IFullScreenObject photo = (IFullScreenObject) getArguments().getSerializable(EXTRA_PHOTO);
        type = (TripImagesListFragment.Type) getArguments().getSerializable(EXTRA_TYPE);

        FullScreenPresenter fullScreenPresenter = FullScreenPresenter.create(photo);
        if (photo != null) {
            fullScreenPresenter.setPhoto(photo);
            fullScreenPresenter.setType(type);
        }

        return fullScreenPresenter;
    }


    @OnClick(R.id.iv_share)
    public void actionShare() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
        builder.title(R.string.action_share)
                .items(R.array.share_dialog_items)
                .itemsCallback((dialog, view, which, text) -> {
                    if (which == 0) {
                        getPresenter().onFbShare();
                    } else {
                        getPresenter().onTwitterShare();
                    }
                }).show();
    }

    public void toggleContent() {
        if (llContentWrapper.getVisibility() == View.VISIBLE) {
            hideContent();
        } else {
            showContent();
        }
    }

    private void hideContent() {
        llContentWrapper.setVisibility(View.GONE);
    }

    private void showContent() {
        llContentWrapper.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.tv_see_more)
    public void actionSeeMore() {
        llMoreInfo.setVisibility(View.VISIBLE);
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

    @OnClick(R.id.user_photo)
    void onUserClicked() {
        getPresenter().onUserClicked();
    }

    @OnClick({R.id.bottom_container, R.id.title_container})
    public void actionSeeLess() {
        if (type != TripImagesListFragment.Type.BUCKET_PHOTOS) {
            llMoreInfo.setVisibility(View.GONE);
            tvDescription.setSingleLine(true);
            tvDescription.setVisibility(View.VISIBLE);
            tvSeeMore.setVisibility(View.VISIBLE);
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
        getPresenter().onFlagAction();
    }

    @OnClick(R.id.iv_comment)
    public void actionComment() {
    }


    @Override
    public void setFlags(List<Flag> flags) {
        PopupMenu popup = new PopupMenu(getActivity(), ivFlag);
        for (int i = 0; i < flags.size(); i++) {
            Flag flagContent = flags.get(i);
            popup.getMenu().add(0, i, i, flagContent.getName());
        }
        popup.setOnMenuItemClickListener(item -> {
            getPresenter().showFlagAction(item.getOrder());
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
                .customView(R.layout.dialog_flag_description, true)
                .positiveText(R.string.flag_description_positive)
                .negativeText(R.string.flag_description_negative)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        EditText et = ButterKnife.findById(dialog, R.id.tv_description);
                        String desc = et.getText().toString();
                        showFlagConfirmDialog(reason, desc);
                    }
                }).build();
        dialog.show();
        View positiveButton = dialog.getActionButton(DialogAction.POSITIVE);
        EditText etDesc = (EditText) dialog.getCustomView().findViewById(R.id.tv_description);
        etDesc.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                positiveButton.setEnabled(s.toString().trim().length() > 0);
            }
        });
    }

    @Override
    public void setLikeCountVisibility(boolean isVisible) {
        if (isVisible) {
            tvLikesCount.setVisibility(View.VISIBLE);
        } else {
            tvLikesCount.setVisibility(View.GONE);
        }
    }

    @Override
    public void setUserPhoto(String fsPhoto) {
        if (TextUtils.isEmpty(fsPhoto)) {
            civUserPhoto.setVisibility(View.GONE);
        } else {
            civUserPhoto.setImageURI(Uri.parse(fsPhoto));
        }
    }

    @Override
    public void setTitle(String title) {
        if (type == TripImagesListFragment.Type.INSPIRE_ME) {
            textViewInspireMeTitle.setText("- " + title);
            tvTitle.setVisibility(View.GONE);
        } else {
            tvTitle.setText(title);
        }
    }

    @Override
    public void setDate(String date) {
        if (TextUtils.isEmpty(date)) {
            tvDate.setVisibility(View.GONE);
        } else {
            tvDate.setVisibility(View.VISIBLE);
            tvDate.setText(date);
        }
    }

    @Override
    public void setLocation(String location) {
        if (TextUtils.isEmpty(location)) {
            tvLocation.setVisibility(View.GONE);
        } else {
            tvLocation.setVisibility(View.VISIBLE);
            tvLocation.setText(location);
        }
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
            tvLikesCount.setVisibility(View.GONE);
        }
        tvLikesCount.setText(getString(R.string.likes, count));
    }

    @Override
    public void setDescription(String desc) {
        tvDescription.setText(desc);
        if (type != TripImagesListFragment.Type.BUCKET_PHOTOS) {
            actionSeeMore();
        }
    }

    @Override
    public void setLiked(boolean isLiked) {
        ivLike.setSelected(isLiked);
    }

    @Override
    public void setFlagVisibility(boolean isVisible) {
        if (isVisible) {
            ivFlag.setVisibility(View.VISIBLE);
        } else {
            ivFlag.setVisibility(View.GONE);
        }
    }

    @Override
    public void setDeleteVisibility(boolean isVisible) {
        if (isVisible) {
            ivDelete.setVisibility(View.VISIBLE);
        } else {
            ivDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public void setLikeVisibility(boolean isVisible) {
        if (isVisible) {
            ivLike.setVisibility(View.VISIBLE);
        } else {
            ivLike.setVisibility(View.GONE);
        }
    }

    @Override
    public void showProgress() {
        progressFlag.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressFlag.setVisibility(View.GONE);
    }

    private SweetAlertDialog progressDialog;

    @Override
    public void showCoverProgress() {
        progressDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        progressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        progressDialog.setTitleText(getString(R.string.uploading_photo));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    public void hideCoverProgress() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismissWithAnimation();
    }

    @Override
    public void showCheckbox(boolean status) {
        checkBox.setText(status ? R.string.bucket_current_cover : R.string.bucket_photo_cover);
        checkBox.setClickable(!status);
        checkBox.setVisibility(View.VISIBLE);
        checkBox.setChecked(status);
    }

    @Override
    public void onResume() {
        super.onResume();
        checkBox.setOnCheckedChangeListener((cb, b) -> {
            checkBox.setClickable(!b);
            getPresenter().onCheckboxPressed(b);
        });
    }

    @Override
    public void setSocial(Boolean isEnabled) {
        civUserPhoto.setEnabled(isEnabled);
    }
}
