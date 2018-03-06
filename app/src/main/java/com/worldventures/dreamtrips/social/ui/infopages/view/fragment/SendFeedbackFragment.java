package com.worldventures.dreamtrips.social.ui.infopages.view.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.worldventures.core.model.EntityStateHolder;
import com.worldventures.core.modules.infopages.bundle.FeedbackImageAttachmentsBundle;
import com.worldventures.core.modules.infopages.custom.AttachmentImagesHorizontalView;
import com.worldventures.core.modules.infopages.model.FeedbackImageAttachment;
import com.worldventures.core.modules.infopages.model.FeedbackType;
import com.worldventures.core.modules.picker.helper.PickerPermissionChecker;
import com.worldventures.core.modules.picker.helper.PickerPermissionUiHandler;
import com.worldventures.core.modules.picker.view.dialog.MediaPickerDialog;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.annotations.MenuResource;
import com.worldventures.core.ui.util.SoftInputUtil;
import com.worldventures.core.ui.util.permission.PermissionUtils;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.social.ui.infopages.presenter.SendFeedbackPresenter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import icepick.State;
import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

@Layout(R.layout.fragment_send_feedback)
@MenuResource(R.menu.menu_send_feedback)
public class SendFeedbackFragment extends BaseFragment<SendFeedbackPresenter> implements SendFeedbackPresenter.View {

   @Inject PickerPermissionUiHandler pickerPermissionUiHandler;
   @Inject PermissionUtils permissionUtils;

   @InjectView(R.id.spinner) Spinner spinner;
   @InjectView(R.id.tv_message) EditText message;
   @InjectView(R.id.progressBar) ProgressBar progressBar;
   @InjectView(R.id.feedback_attachments) AttachmentImagesHorizontalView attachmentImagesHorizontalView;
   @InjectView(R.id.feedback_add_photos) View addPhotosButton;
   @Inject Router router;
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
         default:
            break;
      }
      return super.onOptionsItemSelected(item);
   }

   @SuppressWarnings("unchecked")
   @Override
   public void setFeedbackTypes(@NotNull List<? extends FeedbackType> feedbackTypes) {
      setupSpinner((List<FeedbackType>) feedbackTypes, true);
   }

   @Override
   public void feedbackSent() {
      Dialog dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.CUSTOM_IMAGE_TYPE)
            .setTitleText(getString(R.string.congrats))
            .setContentText(getString(R.string.feedback_has_been_sent))
            .setConfirmClickListener(sweetAlertDialog -> {
               sweetAlertDialog.cancel();
               router.back();
            });
      dialog.setCanceledOnTouchOutside(false);
      dialog.show();
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
      if (doneButtonMenuItem != null) {
         doneButtonMenuItem.setEnabled(enable);
      }
   }

   @Override
   public void changeAddPhotosButtonState(boolean enable) {
      addPhotosButton.setAlpha(enable ? 1f : 0.5f);
      addPhotosButton.setEnabled(enable);
   }

   ///////////////////////////////////////////////////////////////////////////
   // Attachments
   ///////////////////////////////////////////////////////////////////////////

   private void initAttachments() {
      attachmentImagesHorizontalView.setPhotoCellDelegate(getPresenter()::onFeedbackAttachmentClicked);
      attachmentImagesHorizontalView.init(this);
   }

   @Override
   public void showAttachments(@NotNull EntityStateHolder<FeedbackImageAttachment> holder, @NotNull FeedbackImageAttachmentsBundle bundle) {
      switch (holder.state()) {
         case DONE:
         case PROGRESS:
            router.moveTo(FeedbackImageAttachmentsFragment.class, NavigationConfigBuilder.forActivity()
                  .data(bundle).toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                  .build());
            break;
         case FAIL:
            showRetryUploadingUiForAttachment(holder);
            break;
         default:
            //do nothing
      }
   }

   @OnClick(R.id.feedback_add_photos)
   void onAddPhotosClicked() {
      getPresenter().onShowMediaPicker();
   }

   @Override
   public void showPermissionDenied(String[] permissions) {
      if (permissionUtils.equals(permissions, PickerPermissionChecker.PERMISSIONS)) {
         pickerPermissionUiHandler.showPermissionDenied(getView());
      }
   }

   @Override
   public void showPermissionExplanationText(String[] permissions) {
      if (permissionUtils.equals(permissions, PickerPermissionChecker.PERMISSIONS)) {
         pickerPermissionUiHandler.showRational(getContext(), answer -> getPresenter().recheckPermission(permissions, answer));
      }
   }

   @Override
   public void showMediaPicker(int maxImages) {
      MediaPickerDialog mediaPickerDialog = new MediaPickerDialog(getContext());
      mediaPickerDialog.setOnDoneListener(pickerAttachment -> getPresenter().imagesPicked(pickerAttachment));
      mediaPickerDialog.show(maxImages);
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
                  default:
                     break;
               }
            }).show();
   }
}
