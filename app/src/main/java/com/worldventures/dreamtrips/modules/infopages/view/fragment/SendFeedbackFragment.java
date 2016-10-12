package com.worldventures.dreamtrips.modules.infopages.view.fragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.techery.spares.utils.ui.SoftInputUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.common.model.EntityStateHolder;
import com.worldventures.dreamtrips.modules.common.view.bundle.PickerBundle;
import com.worldventures.dreamtrips.modules.common.view.custom.PhotoPickerLayout;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.common.view.util.PhotoPickerDelegate;
import com.worldventures.dreamtrips.modules.infopages.model.FeedbackImageAttachment;
import com.worldventures.dreamtrips.modules.infopages.model.FeedbackType;
import com.worldventures.dreamtrips.modules.infopages.presenter.SendFeedbackPresenter;
import com.worldventures.dreamtrips.modules.infopages.view.custom.AttachmentImagesHorizontalView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import icepick.State;
import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

@Layout(R.layout.fragment_send_feedback)
@MenuResource(R.menu.menu_send_feedback)
public class SendFeedbackFragment extends BaseFragment<SendFeedbackPresenter> implements SendFeedbackPresenter.View {

   @InjectView(R.id.spinner) Spinner spinner;
   @InjectView(R.id.tv_message) EditText message;
   @InjectView(R.id.progressBar) ProgressBar progressBar;
   @InjectView(R.id.feedback_attachments) AttachmentImagesHorizontalView attachmentImagesHorizontalView;
   @InjectView(R.id.feedback_add_photos) View addPhotosButton;
   @InjectView(R.id.add_photos_image_view) ImageView addPhotosImageView;
   @Inject PhotoPickerDelegate photoPickerDelegate;

   private MenuItem doneButtonMenuItem;
   private Observable<CharSequence> messageObservable;
   private PublishSubject<FeedbackType> feedbackTypeObservable = PublishSubject.create();
   private BehaviorSubject<Boolean> photoPickerVisibilityObservable = BehaviorSubject.create(false);

   @State int lastFeedbackTypeSelectedPosition;

   @Override
   protected SendFeedbackPresenter createPresenter(Bundle savedInstanceState) {
      return new SendFeedbackPresenter();
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      inject(this);
      ArrayList<FeedbackType> feedbackTypes = new ArrayList<>();
      setupSpinner(feedbackTypes, false);
      spinner.setEnabled(false);
      progressBar.setVisibility(View.GONE);
      messageObservable = RxTextView.textChanges(message);
      initAttachments();
   }

   private void setupSpinner(List<FeedbackType> apiFeedbackTypes, boolean isSpinnerEnabled) {
      ArrayList<FeedbackType> feedbackTypes = new ArrayList<>(apiFeedbackTypes);
      feedbackTypes.add(0, new FeedbackType(-1, getContext().getString(R.string.feedback_select_category)));
      ArrayAdapter<FeedbackType> adapter = new ArrayAdapter<>(getContext(), R.layout.adapter_item_feedback_type, android.R.id.text1, feedbackTypes);
      adapter.setDropDownViewResource(R.layout.adapter_item_feedback_type);
      spinner.setAdapter(adapter);
      if (apiFeedbackTypes == null || apiFeedbackTypes.isEmpty()) {
         return;
      }
      spinner.setSelection(lastFeedbackTypeSelectedPosition);
      spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
         @Override
         public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            feedbackTypeObservable.onNext(adapter.getItem(position));
            lastFeedbackTypeSelectedPosition = position;
         }

         @Override
         public void onNothingSelected(AdapterView<?> parent) {

         }
      });
      spinner.setEnabled(isSpinnerEnabled);
   }

   @Override
   protected void onMenuInflated(Menu menu) {
      super.onMenuInflated(menu);
      doneButtonMenuItem = menu.findItem(R.id.send);
      doneButtonMenuItem.setEnabled(false);
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      int id = ((FeedbackType) spinner.getSelectedItem()).getId();
      switch (item.getItemId()) {
         case R.id.send:
            SoftInputUtil.hideSoftInputMethod(getActivity());
            //
            if (isMessageEmpty()) {
               informUser(R.string.message_can_not_be_empty);
            } else {
               getPresenter().sendFeedback(id, message.getText().toString());
            }
            break;
      }
      return super.onOptionsItemSelected(item);
   }

   @Override
   public void setFeedbackTypes(List<FeedbackType> feedbackTypes) {
      setupSpinner(feedbackTypes, true);
   }

   @Override
   public void feedbackSent() {
      informUser(R.string.feedback_has_been_sent);
      router.back();
   }

   @Override
   public void showProgressBar() {
      progressBar.setVisibility(View.VISIBLE);
   }

   @Override
   public void hideProgressBar() {
      progressBar.setVisibility(View.GONE);
   }

   private boolean isMessageEmpty() {
      return message.getText().toString().trim().isEmpty();
   }

   @Override
   public Observable<FeedbackType> getFeedbackTypeSelectedObservable() {
      return feedbackTypeObservable;
   }

   @Override
   public Observable<CharSequence> getMessageTextObservable() {
      return messageObservable;
   }

   @Override
   public Observable<Boolean> getPhotoPickerVisibilityObservable() {
      return photoPickerVisibilityObservable;
   }

   @Override
   public void changeDoneButtonState(boolean enable) {
      if (doneButtonMenuItem != null) doneButtonMenuItem.setEnabled(enable);
   }

   @Override
   public void changeAddPhotosButtonState(boolean enable) {
      addPhotosButton.setAlpha(enable ? 1f : 0.5f);
      addPhotosButton.setEnabled(enable);
   }

   private void clearMessageFocus() {
      // because clearFocus() method does not work
      message.setFocusable(false);
      message.setFocusableInTouchMode(false);
      message.setFocusable(true);
      message.setFocusableInTouchMode(true);
   }

   ///////////////////////////////////////////////////////////////////////////
   // Attachments
   ///////////////////////////////////////////////////////////////////////////

   private void initAttachments() {
      attachmentImagesHorizontalView.setPhotoCellDelegate(getPresenter()::onFeedbackAttachmentClicked);
      attachmentImagesHorizontalView.init(this);

      photoPickerDelegate.setPhotoPickerListener(new PhotoPickerLayout.PhotoPickerListener() {

         @Override
         public void onClosed() {
            message.setOnFocusChangeListener(null);
            photoPickerVisibilityObservable.onNext(false);
         }

         @Override
         public void onOpened() {
            clearMessageFocus();
            message.setOnFocusChangeListener((v, hasFocus) -> {
               if (hasFocus) {
                  photoPickerVisibilityObservable.take(1).subscribe(photopickerVisible -> {
                     if (photopickerVisible) router.back();
                  });
               }
            });
            photoPickerVisibilityObservable.onNext(true);
         }
      });
   }

   @OnClick(R.id.feedback_add_photos)
   void onAddPhotosClicked() {
      getPresenter().onShowMediaPicker();
   }

   public void showMediaPicker(int requestId, int maxImages) {
      router.moveTo(Route.MEDIA_PICKER, NavigationConfigBuilder.forFragment()
            .backStackEnabled(false)
            .fragmentManager(getChildFragmentManager())
            .containerId(R.id.picker_container)
            .data(new PickerBundle(requestId, maxImages, true))
            .build());
   }

   @Override
   public void setAttachments(List<EntityStateHolder<FeedbackImageAttachment>> attachments) {
      attachmentImagesHorizontalView.setImages(attachments);
      updateAttachmentsViewVisibility();
   }

   @Override
   public void updateAttachment(EntityStateHolder<FeedbackImageAttachment> image) {
      attachmentImagesHorizontalView.changeItemState(image);
      updateAttachmentsViewVisibility();
   }

   @Override
   public void removeAttachment(EntityStateHolder<FeedbackImageAttachment> image) {
      attachmentImagesHorizontalView.removeItem(image);
      updateAttachmentsViewVisibility();
   }

   private void updateAttachmentsViewVisibility() {
      int itemsCount = attachmentImagesHorizontalView.getItemCount();
      attachmentImagesHorizontalView.setVisibility(itemsCount > 0 ? View.VISIBLE : View.GONE);
   }

   @Override
   public void showRetryUploadingUiForAttachment(EntityStateHolder<FeedbackImageAttachment> attachmentHolder) {
      MaterialDialog.Builder builder = new MaterialDialog.Builder(getContext());
      builder.items(R.array.dialog_action_feedback_failed_uploading_attachment)
            .itemsCallback((dialog, v, which, text) -> {
         switch (which) {
            case 0:
               getPresenter().onRetryUploadingAttachment(attachmentHolder);
               break;
            case 1:
               getPresenter().onRemoveAttachment(attachmentHolder);
               break;
         }
      }).show();
   }
}
