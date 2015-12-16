package com.worldventures.dreamtrips.modules.bucketlist.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.ui.fragment.ImageBundle;
import com.worldventures.dreamtrips.core.utils.IntentUtils;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.core.utils.events.ImageClickedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.model.DiningItem;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketItemDetailsPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.view.dialog.DeleteBucketDialog;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.common.view.activity.ComponentActivity;
import com.worldventures.dreamtrips.modules.common.view.bundle.BucketBundle;
import com.worldventures.dreamtrips.modules.common.view.dialog.ProgressDialogFragment;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.common.view.viewpager.BaseStatePagerAdapter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityEditClickEvent;
import com.worldventures.dreamtrips.modules.feed.view.popup.FeedItemMenuBuilder;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.FullScreenImagesBundle;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagePagerFragment;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;
import butterknife.OnCheckedChanged;
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

    @Inject
    @ForActivity
    Provider<Injector> injector;


    @InjectView(R.id.contentView)
    ViewGroup contentView;

    protected ProgressDialogFragment progressDialog;

    @Override
    public void afterCreateView(View view) {
        super.afterCreateView(view);

        boolean slave = getArgs().isSlave();
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (!slave && actionBar != null && ViewUtils.isFullVisibleOnScreen(this)) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");
        }
        setForeignIntentAction();

        progressDialog = ProgressDialogFragment.create();
    }

    @Override
    protected T createPresenter(Bundle savedInstanceState) {
        return (T) new BucketItemDetailsPresenter(getArgs());
    }

    public void onEvent(FeedEntityEditClickEvent event) {
        if (isVisibleOnScreen()) {
            event.getAnchor().setEnabled(false);
            FeedItemMenuBuilder.create(getActivity(), event.getAnchor(), R.menu.menu_feed_entity_edit)
                    .onDelete(() -> {
                        if (isVisibleOnScreen()) getPresenter().onDelete();
                    })
                    .onEdit(() -> {
                        if (isVisibleOnScreen()) getPresenter().onEdit();
                    })
                    .dismissListener(menu -> event.getAnchor().setEnabled(true))
                    .show();
        }
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
            startActivity(intent);
        });
        diningPhone.setOnClickListener(v -> {
            Intent intent = IntentUtils.callIntnet(diningPhone.getText().toString());
            startActivity(intent);
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
        if (getActivity() instanceof ComponentActivity)
            getActivity().onBackPressed();
    }

    @Override
    public void openFullscreen(FullScreenImagesBundle data) {
        NavigationBuilder.create()
                .with(activityRouter)
                .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                .data(data)
                .move(Route.FULLSCREEN_PHOTO_LIST);
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
                adapter.add(new FragmentItem(TripImagePagerFragment.class, ""))
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

    @Override
    public void showDeletionDialog(BucketItem bucketItem) {
        DeleteBucketDialog dialog = new DeleteBucketDialog();
        dialog.setBucketItemId(bucketItem.getUid());
        dialog.show(getFragmentManager());
    }

    @Override
    public void showProgressDialog() {
        progressDialog.show(getFragmentManager());
    }

    @Override
    public void dismissProgressDialog() {
        progressDialog.dismiss();
    }
}
