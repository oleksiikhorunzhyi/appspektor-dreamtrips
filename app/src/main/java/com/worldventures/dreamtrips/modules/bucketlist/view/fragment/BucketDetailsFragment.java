package com.worldventures.dreamtrips.modules.bucketlist.view.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.ui.fragment.FragmentUtil;
import com.techery.spares.utils.ui.OrientationUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.ui.fragment.ImageBundle;
import com.worldventures.dreamtrips.core.utils.IntentUtils;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.core.utils.events.ImageClickedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.model.DiningItem;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketItemDetailsPresenter;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.common.view.activity.ComponentActivity;
import com.worldventures.dreamtrips.modules.common.view.bundle.BucketBundle;
import com.worldventures.dreamtrips.modules.common.view.dialog.ProgressDialogFragment;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.common.view.viewpager.BaseStatePagerAdapter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;
import com.worldventures.dreamtrips.modules.feed.view.popup.FeedItemMenuBuilder;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.FullScreenImagesBundle;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagePagerFragment;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import me.relex.circleindicator.CircleIndicator;

@Layout(R.layout.layout_bucket_item_details)
public class BucketDetailsFragment<T extends BucketItemDetailsPresenter> extends BaseFragmentWithArgs<T, BucketBundle>
        implements BucketItemDetailsPresenter.View {

    @InjectView(R.id.textViewName)
    protected TextView textViewName;
    @InjectView(R.id.textViewFriends)
    protected TextView textViewFriends;
    @InjectView(R.id.textViewTags)
    protected TextView textViewTags;
    @InjectView(R.id.textViewDescription)
    protected TextView textViewDescription;
    @InjectView(R.id.textViewCategory)
    protected TextView textViewCategory;
    @InjectView(R.id.textViewDate)
    protected TextView textViewDate;
    @InjectView(R.id.textViewPlace)
    protected TextView textViewPlace;
    @InjectView(R.id.checkBoxDone)
    protected CheckBox markAsDone;
    @InjectView(R.id.viewPagerBucketGallery)
    protected ViewPager viewPagerBucketGallery;
    @InjectView(R.id.circleIndicator)
    protected CircleIndicator circleIndicator;
    @InjectView(R.id.bucket_tags_container)
    View bucketTags;
    @InjectView(R.id.bucket_who_container)
    View bucketWho;
    @InjectView(R.id.diningName)
    TextView diningName;
    @InjectView(R.id.diningPriceRange)
    TextView diningPriceRange;
    @InjectView(R.id.diningAddress)
    TextView diningAddress;
    @InjectView(R.id.diningSite)
    TextView diningSite;
    @InjectView(R.id.diningPhone)
    TextView diningPhone;
    @InjectView(R.id.diningContainer)
    View diningContainer;
    @InjectView(R.id.diningDivider)
    View diningDivider;
    @InjectView(R.id.contentView)
    ViewGroup contentView;
    @InjectView(R.id.edit)
    ImageView edit;
    @InjectView(R.id.toolbar_actionbar)
    Toolbar toolbar;

    @Inject
    @ForActivity
    Provider<Injector> injector;

    protected ProgressDialogFragment progressDialog;

    @Override
    public void afterCreateView(View view) {
        super.afterCreateView(view);

        setForeignIntentAction();

        progressDialog = ProgressDialogFragment.create();
    }

    @Override
    protected T createPresenter(Bundle savedInstanceState) {
        return (T) new BucketItemDetailsPresenter(getArgs());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isVisible() && ViewUtils.isTablet(getActivity()) && !ViewUtils.isLandscapeOrientation(getActivity())) {
            OrientationUtil.lockOrientation(getActivity());
        }
        if (!getArgs().isSlave()) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");
            toolbar.getBackground().mutate().setAlpha(0);
        } else {
            toolbar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        OrientationUtil.unlockOrientation(getActivity());
    }

    @OnClick(R.id.edit)
    void onEditClicked() {
        FeedItemMenuBuilder.create(getActivity(), edit, R.menu.menu_feed_entity_edit)
                .onEdit(() -> {
                    if (isVisibleOnScreen()) getPresenter().onEdit();
                })
                .onDelete(() -> {
                    if (isVisibleOnScreen()) showDeletionDialog();
                })
                .dismissListener(menu -> edit.setEnabled(true))
                .show();
    }

    @Override
    public boolean isVisibleOnScreen() {
        return ViewUtils.isPartVisibleOnScreen(this);
    }

    @Override
    public void setTitle(String title) {
        textViewName.setText(title);
    }

    @Override
    public void setDescription(String description) {
        setText(textViewDescription, description);
    }

    @Override
    public void setTime(String time) {
        textViewDate.setText(time);
    }

    @Override
    public void setPeople(String people) {
        if (!TextUtils.isEmpty(people)) {
            bucketWho.setVisibility(View.VISIBLE);
            textViewFriends.setText(people);
        } else {
            bucketWho.setVisibility(View.GONE);
        }
    }

    @Override
    public void setTags(String tags) {
        if (!TextUtils.isEmpty(tags)) {
            bucketTags.setVisibility(View.VISIBLE);
            textViewTags.setText(tags);
        } else {
            bucketTags.setVisibility(View.GONE);
        }
    }

    @Override
    public void setStatus(boolean completed) {
        markAsDone.setChecked(completed);
    }

    @OnCheckedChanged(R.id.checkBoxDone)
    protected void onCheckedChanged(boolean isChecked) {
        getPresenter().onStatusUpdated(isChecked);
    }

    private void setForeignIntentAction() {
        diningSite.setOnClickListener(v -> {
            Intent intent = IntentUtils.browserIntent(diningSite.getText().toString());
            FragmentUtil.startSafely(this, intent);
        });
        diningPhone.setOnClickListener(v -> {
            Intent intent = IntentUtils.callIntnet(diningPhone.getText().toString());
            FragmentUtil.startSafely(this, intent);
        });
    }

    @Override
    public void enableMarkAsDone() {
        markAsDone.setEnabled(true);
    }

    @Override
    public void disableMarkAsDone() {
        markAsDone.setEnabled(false);
    }

    @Override
    public void setupDiningView(DiningItem diningItem) {
        if (diningItem == null) {
            diningContainer.setVisibility(View.GONE);
            return;
        }
        //
        diningContainer.setVisibility(View.VISIBLE);
        setText(diningName, diningItem.getName());
        setText(diningPriceRange, diningItem.getPriceRange());
        setText(diningAddress, diningItem.getAddress());
        setText(diningPhone, diningItem.getPhoneNumber());
        setText(diningSite, diningItem.getUrl());
        if (TextUtils.isEmpty(diningItem.getUrl()) && TextUtils.isEmpty(diningItem.getPhoneNumber())) {
            diningDivider.setVisibility(View.GONE);
        } else {
            diningDivider.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setCategory(String category) {
        setText(textViewCategory, category);
    }

    @Override
    public void setPlace(String place) {
        setText(textViewPlace, place);
    }

    private void setText(TextView view, String text) {
        if (TextUtils.isEmpty(text)) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
            view.setText(text);
        }
    }

    @Override
    public void done() {
        if (getActivity() instanceof ComponentActivity
                && !ViewUtils.isLandscapeOrientation(getActivity()))
            getActivity().onBackPressed();
    }

    @Override
    public void openFullscreen(FullScreenImagesBundle data) {
        router.moveTo(
                Route.FULLSCREEN_PHOTO_LIST,
                NavigationConfigBuilder
                        .forActivity()
                        .toolbarConfig(
                                ToolbarConfig.Builder
                                        .create()
                                        .visible(false)
                                        .build())
                        .data(data)
                        .build());
    }

    @Override
    public void setImages(List<BucketPhoto> photos) {
        BaseStatePagerAdapter adapter = new BaseStatePagerAdapter(getChildFragmentManager()) {
            @Override
            public void setArgs(int position, Fragment fragment) {
                BucketPhoto photo = photos.get(position);
                ((TripImagePagerFragment) fragment).setArgs(new ImageBundle<>(photo));
            }
        };
        viewPagerBucketGallery.setAdapter(adapter);
        viewPagerBucketGallery.setCurrentItem(0);
        Queryable.from(photos).forEachR(photo ->
                        adapter.add(new FragmentItem(Route.TRIP_IMAGES_PAGER, ""))
        );
        adapter.notifyDataSetChanged();
        circleIndicator.setViewPager(viewPagerBucketGallery);
    }


    public void onEvent(ImageClickedEvent event) {
        if (ViewUtils.isPartVisibleOnScreen(this))
            getPresenter().openFullScreen(viewPagerBucketGallery.getCurrentItem());
    }

    @Override
    public void addImages(List<UploadTask> tasks) {

    }

    @Override
    public void addImage(UploadTask uploadTask) {

    }

    @Override
    public void deleteImage(UploadTask task) {

    }

    @Override
    public void deleteImage(BucketPhoto bucketPhoto) {

    }

    @Override
    public void itemChanged(UploadTask uploadTask) {

    }

    @Override
    public void replace(UploadTask bucketPhotoUploadTask, BucketPhoto bucketPhoto) {

    }

    @Override
    public UploadTask getBucketPhotoUploadTask(String taskId) {
        return null;
    }

    public void showDeletionDialog() {
        Dialog dialog = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getResources().getString(R.string.photo_delete))
                .setContentText(getResources().getString(R.string.photo_delete_caption))
                .setConfirmText(getResources().getString(R.string.post_delete_confirm))
                .setConfirmClickListener(sDialog -> {
                    sDialog.dismissWithAnimation();
                    getPresenter().onDelete();
                });
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    @Override
    public void showProgressDialog() {
        progressDialog.show(getFragmentManager());
    }

    @Override
    public void dismissProgressDialog() {
        progressDialog.dismiss();
    }

    @Override
    public void showEdit(BucketBundle bucketBundle) {
        int containerId = R.id.container_details_floating;
        router.moveTo(Route.BUCKET_EDIT, NavigationConfigBuilder.forRemoval()
                .containerId(containerId)
                .fragmentManager(getActivity().getSupportFragmentManager())
                .build());
        if (isTabletLandscape()) {
            router.moveTo(Route.BUCKET_EDIT, NavigationConfigBuilder.forFragment()
                    .backStackEnabled(true)
                    .containerId(containerId)
                    .fragmentManager(getActivity().getSupportFragmentManager())
                    .data(bucketBundle)
                    .build());
            showContainer(containerId);
        } else {
            bucketBundle.setLock(true);
            router.moveTo(Route.BUCKET_EDIT, NavigationConfigBuilder.forActivity()
                    .data(bucketBundle)
                    .build());
        }
    }

    private void showContainer(int containerId) {
        View container = ButterKnife.findById(getActivity(), containerId);
        if (container != null) container.setVisibility(View.VISIBLE);
    }
}
