package com.worldventures.dreamtrips.modules.tripsimages.view.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.view.View;
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
import com.worldventures.dreamtrips.modules.tripsimages.view.activity.FullScreenPhotoActivity;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.ScaleImageView;

import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_fullscreen_photo)
public class FullScreenPhotoFragment<T extends IFullScreenObject>
        extends BaseFragment<FullScreenPresenter<T>> implements FullScreenPresenter.View {

    public static final String EXTRA_POSITION = "EXTRA_POSITION";

    @InjectView(R.id.iv_image)
    protected ScaleImageView ivImage;
    @InjectView(R.id.ll_global_content_wrapper)
    protected LinearLayout llContentWrapper;
    @InjectView(R.id.ll_top_container)
    protected LinearLayout llTopContainer;
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
    @InjectView(R.id.iv_comment)
    protected ImageView ivComment;
    @InjectView(R.id.iv_share)
    protected ImageView ivShare;
    @InjectView(R.id.iv_flag)
    protected ImageView ivFlag;
    @InjectView(R.id.iv_delete)
    protected ImageView ivDelete;
    @InjectView(R.id.user_photo)
    protected SimpleDraweeView civUserPhoto;
    @InjectView(R.id.progress_flag)
    protected ProgressBar progressBar;

    private TripImagesListFragment.Type type;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);

        FullScreenPhotoActivity activity = (FullScreenPhotoActivity) getActivity();
        type = activity.getType();
        IFullScreenObject photo = activity.getPhoto(getArguments().getInt(EXTRA_POSITION));

        if (photo != null) {
            getPresenter().setupPhoto((T) photo);
            getPresenter().setupType(type);
        }

        if (type == TripImagesListFragment.Type.BUCKET_PHOTOS) {
            ivShare.setVisibility(View.GONE);
            actionSeeLess();
            tvSeeMore.setVisibility(View.GONE);
        } else if (type == TripImagesListFragment.Type.INSPIRE_ME) {
            actionSeeLess();
            tvSeeMore.setVisibility(View.GONE);
        } else {
            actionSeeMore();
        }
    }

    @Override
    public void loadImage(Image image) {
        String medium = image.getThumbUrl(getResources());
        String original = image.getUrl(ViewUtils.getScreenWidth(getActivity()),
                ViewUtils.getScreenHeight(getActivity()));
        loadImage(medium, original);
    }

    private void loadImage(String lowUrl, String url) {
        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setLowResImageRequest(ImageRequest.fromUri(lowUrl))
                .setImageRequest(ImageRequest.fromUri(url))
                .build();
        ivImage.setController(draweeController);
    }

    @Override
    protected FullScreenPresenter createPresenter(Bundle savedInstanceState) {
        FullScreenPhotoActivity activity = (FullScreenPhotoActivity) getActivity();
        int position = getArguments().getInt(EXTRA_POSITION);
        IFullScreenObject photo = activity.getPhoto(position);

        return FullScreenPresenter.create(photo);
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

    @OnClick(R.id.iv_image)
    public void actionImageClick() {
        if (llContentWrapper.getVisibility() == View.VISIBLE) {
            llContentWrapper.setVisibility(View.GONE);
        } else {
            llContentWrapper.setVisibility(View.VISIBLE);
        }
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
    }

    @OnClick(R.id.ll_top_container)
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
                        String desc = ((EditText) dialog.getCustomView()
                                .findViewById(R.id.tv_description))
                                .getText().toString();
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
            tvDate.setText(date.toUpperCase());
        }
    }

    @Override
    public void setLocation(String location) {
        if (TextUtils.isEmpty(location)) {
            tvLocation.setVisibility(View.GONE);
        } else {
            tvLocation.setVisibility(View.VISIBLE);
            tvLocation.setText(location.toUpperCase());
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
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }
}
