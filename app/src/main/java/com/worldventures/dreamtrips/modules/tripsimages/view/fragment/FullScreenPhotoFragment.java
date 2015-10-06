package com.worldventures.dreamtrips.modules.tripsimages.view.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.view.custom.FlagView;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.feed.api.GetFeedEntityQuery;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.EditPhotoBundle;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.FullScreenPhotoBundle;
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
        extends BaseFragmentWithArgs<FullScreenPresenter<T>, FullScreenPhotoBundle> implements FullScreenPresenter.View {

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
    @InjectView(R.id.flag)
    protected FlagView flag;
    @InjectView(R.id.edit)
    protected ImageView edit;
    @InjectView(R.id.delete)
    protected ImageView delete;
    @InjectView(R.id.user_photo)
    protected SimpleDraweeView civUserPhoto;
    @InjectView(R.id.checkBox)
    protected CheckBox checkBox;
    @InjectView(R.id.iv_comment)
    protected ImageView ivComment;
    @InjectView(R.id.content_divider)
    protected ImageView contentDivider;

    private TripImagesListFragment.Type type;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);

        if (type == TripImagesListFragment.Type.FIXED_LIST) {
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
        IFullScreenObject photo = getArgs().getPhoto();
        type = getArgs().getType();

        FullScreenPresenter fullScreenPresenter = FullScreenPresenter.create(photo, getArgs().isForeign());
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
        if (type != TripImagesListFragment.Type.FIXED_LIST) {
            llMoreInfo.setVisibility(View.GONE);
            tvDescription.setSingleLine(true);
            tvDescription.setVisibility(View.VISIBLE);
            tvSeeMore.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.edit)
    public void actionEdit() {
        PopupMenu popup = new PopupMenu(getContext(), edit);
        popup.inflate(R.menu.menu_photo_edit);
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    deletePhoto();
                    break;
                case R.id.action_edit:
                    getPresenter().onEdit();
                    break;
            }

            return true;
        });
        popup.show();
    }

    @OnClick(R.id.delete)
    public void delete() {
        deletePhoto();
    }

    private void deletePhoto() {
        Dialog dialog = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getResources().getString(R.string.photo_delete))
                .setContentText(getResources().getString(R.string.photo_delete_caption))
                .setConfirmText(getResources().getString(R.string.post_delete_confirm))
                .setConfirmClickListener(sDialog -> {
                    sDialog.dismissWithAnimation();
                    getPresenter().onDeleteAction();
                });
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    @Override
    public void setEditVisibility(boolean visible) {
        if (visible) {
            edit.setVisibility(View.VISIBLE);
        } else {
            edit.setVisibility(View.GONE);
        }

    }

    @OnClick(R.id.iv_like)
    public void actionLike() {
        getPresenter().onLikeAction();
    }

    @OnClick(R.id.flag)
    public void actionFlag() {
        getPresenter().onFlagAction();
    }

    @OnClick({R.id.iv_comment, R.id.tv_comments_count})
    public void actionComment() {
        getPresenter().onCommentsAction();
    }

    @OnClick(R.id.tv_likes_count)
    public void actionLikes() {
        getPresenter().onLikesAction();
    }

    @Override
    public void openEdit(EditPhotoBundle bundle) {
        NavigationBuilder.create()
                .with(activityRouter)
                .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                .data(bundle)
                .attach(Route.PHOTO_EDIT);
    }

    @Override
    public void setShareVisibility(boolean shareVisible) {
        ivShare.setVisibility(shareVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setFlags(List<Flag> flags) {
        flag.showFlagsPopup(flags, (reason, desc) -> getPresenter().sendFlagAction(reason, desc));
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
    public void setTitleSpanned(Spanned titleSpanned) {
        tvTitle.setText(titleSpanned);
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
        if (count > 0) {
            tvCommentsCount.setText(getString(R.string.comments, count));
            tvCommentsCount.setVisibility(View.VISIBLE);
        } else {
            tvCommentsCount.setVisibility(View.GONE);
        }
    }

    @Override
    public void setLikeCount(int count) {
        if (count > 0) {
            tvLikesCount.setText(getString(R.string.likes, count));
            tvLikesCount.setVisibility(View.VISIBLE);
        } else {
            tvLikesCount.setVisibility(View.GONE);
        }
    }

    @Override
    public void setDescription(String desc) {
        tvDescription.setText(desc);
        if (type != TripImagesListFragment.Type.FIXED_LIST) {
            actionSeeMore();
        }
    }

    @Override
    public void setContentDividerVisibility(boolean show) {
        contentDivider.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setCommentVisibility(boolean commentVisible) {
        ivComment.setVisibility(commentVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setLiked(boolean isLiked) {
        ivLike.setSelected(isLiked);
    }

    @Override
    public void setFlagVisibility(boolean isVisible) {
        if (isVisible) {
            flag.setVisibility(View.VISIBLE);
        } else {
            flag.setVisibility(View.GONE);
        }
    }

    @Override
    public void setDeleteVisibility(boolean isVisible) {
        if (isVisible) {
            delete.setVisibility(View.VISIBLE);
        } else {
            delete.setVisibility(View.GONE);
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
        flag.showProgress();
    }

    @Override
    public void hideProgress() {
        flag.hideProgress();
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
