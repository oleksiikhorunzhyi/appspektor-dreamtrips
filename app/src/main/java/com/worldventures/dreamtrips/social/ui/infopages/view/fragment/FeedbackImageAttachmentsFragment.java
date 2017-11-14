package com.worldventures.dreamtrips.social.ui.infopages.view.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.modules.infopages.bundle.FeedbackImageAttachmentsBundle;
import com.worldventures.core.modules.infopages.model.FeedbackImageAttachment;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.common.view.viewpager.BaseStatePagerAdapter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;
import com.worldventures.dreamtrips.modules.common.view.viewpager.OnPageChangedAdapter;
import com.worldventures.dreamtrips.social.ui.infopages.presenter.FeedbackImageAttachmentsPresenter;

import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_feedback_image_attachments)
public class FeedbackImageAttachmentsFragment extends RxBaseFragmentWithArgs<FeedbackImageAttachmentsPresenter,
      FeedbackImageAttachmentsBundle> implements FeedbackImageAttachmentsPresenter.View {

   @InjectView(R.id.feedback_toolbar_actionbar) Toolbar toolbar;
   @InjectView(R.id.feedback_toolbar_title) TextView toolbarTitle;
   @InjectView(R.id.feedback_pager) ViewPager viewPager;

   private BaseStatePagerAdapter<FragmentItem> adapter;

   @Override
   protected FeedbackImageAttachmentsPresenter createPresenter(Bundle savedInstanceState) {
      return new FeedbackImageAttachmentsPresenter(getArgs().getAttachments(), getArgs().getPosition());
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      setupActionBar();
      setupViewPager();
   }

   private void setupActionBar() {
      AppCompatActivity activity = (AppCompatActivity) getActivity();
      activity.setSupportActionBar(toolbar);
      activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_photo_back_rounded);
      activity.getSupportActionBar().setTitle("");
      toolbar.setNavigationOnClickListener(v -> activity.onBackPressed());
   }

   private void setupViewPager() {
      adapter = new BaseStatePagerAdapter<>(getActivity().getSupportFragmentManager());
      viewPager.setAdapter(adapter);
      viewPager.addOnPageChangeListener(new OnPageChangedAdapter() {
         @Override
         public void onPageSelected(int position) {
            refreshToolbarTitle();
         }
      });
   }

   @Override
   public void addItems(List<FeedbackImageAttachment> imageAttachments) {
      Queryable.from(imageAttachments)
            .forEachR(item -> adapter.add(new FragmentItem(FeedbackImageAttachmentFullscreenFragment.class, "", item)));
      refreshToolbarTitle();
      adapter.notifyDataSetChanged();
   }

   @Override
   public void setPosition(int position) {
      viewPager.setCurrentItem(position, false);
      refreshToolbarTitle();
   }

   @Override
   public void removeItem(int position) {
      if (adapter.getCount() == 1) {
         getActivity().onBackPressed();
         return;
      }
      int currentItem = viewPager.getCurrentItem();
      adapter.remove(position);
      adapter.notifyDataSetChanged();
      viewPager.setAdapter(adapter);
      viewPager.setCurrentItem(Math.min(currentItem, adapter.getCount() - 1));
      refreshToolbarTitle();
   }

   private void refreshToolbarTitle() {
      String text = getContext().getString(R.string.feedback_fullscreen_attachments_format,
            viewPager.getCurrentItem() + 1, adapter.getCount());
      toolbarTitle.setText(text);
   }

   @OnClick(R.id.feedback_toolbar_remove)
   void onRemoveClicked() {
      new MaterialDialog.Builder(getContext()).title(R.string.delete_photo_title)
            .content(R.string.delete_photo_text)
            .positiveText(R.string.delete_photo_positiove)
            .negativeText(R.string.delete_photo_negative)
            .onPositive((materialDialog, dialogAction) -> getPresenter().onRemoveItem(viewPager.getCurrentItem()))
            .onNegative((materialDialog, dialogAction) -> materialDialog.dismiss())
            .show();
   }
}
