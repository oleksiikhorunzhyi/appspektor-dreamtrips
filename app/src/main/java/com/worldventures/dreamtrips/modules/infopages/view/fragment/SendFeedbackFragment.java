package com.worldventures.dreamtrips.modules.infopages.view.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.techery.spares.utils.ui.SoftInputUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.common.view.util.TextWatcherAdapter;
import com.worldventures.dreamtrips.modules.infopages.model.FeedbackType;
import com.worldventures.dreamtrips.modules.infopages.presenter.SendFeedbackPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

@Layout(R.layout.fragment_send_feedback)
@MenuResource(R.menu.menu_send_feedback)
public class SendFeedbackFragment extends BaseFragment<SendFeedbackPresenter> implements SendFeedbackPresenter.View {

   @InjectView(R.id.spinner) Spinner spinner;
   @InjectView(R.id.tv_message) EditText message;
   @InjectView(R.id.progressBar) ProgressBar progressBar;

   MenuItem menuItemSend;

   @Override
   protected SendFeedbackPresenter createPresenter(Bundle savedInstanceState) {
      return new SendFeedbackPresenter();
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      ArrayList<FeedbackType> feedbackTypes = new ArrayList<>();
      setupSpinner(feedbackTypes, false);
      spinner.setEnabled(false);
      progressBar.setVisibility(View.GONE);
      message.addTextChangedListener(new TextWatcherAdapter() {
         @Override
         public void afterTextChanged(Editable s) {
            validateSendButton();
         }
      });
   }

   protected void setupSpinner(List<FeedbackType> feedbackTypes, boolean isSpinnerEnabled) {
      feedbackTypes = new ArrayList<>(feedbackTypes); //for recreation link
      feedbackTypes.add(0, new FeedbackType(-1, getContext().getString(R.string.feedback_select_category)));
      ArrayAdapter<FeedbackType> adapter = new ArrayAdapter<>(getContext(), R.layout.adapter_item_feedback_type, android.R.id.text1, feedbackTypes);
      adapter.setDropDownViewResource(R.layout.adapter_item_feedback_type);
      spinner.setAdapter(adapter);
      spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
         @Override
         public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            validateSendButton();
         }

         @Override
         public void onNothingSelected(AdapterView<?> parent) {

         }
      });
      spinner.setEnabled(isSpinnerEnabled);
   }

   protected void validateSendButton() {
      if (menuItemSend != null) menuItemSend.setEnabled(!isMessageEmpty() && isReasonTypeSelected());
   }

   @Override
   protected void onMenuInflated(Menu menu) {
      super.onMenuInflated(menu);
      menuItemSend = menu.findItem(R.id.send);
      menuItemSend.setEnabled(false);
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
   public void showProgressDialog() {
      progressBar.setVisibility(View.VISIBLE);
   }

   @Override
   public void hideProgressBar() {
      progressBar.setVisibility(View.GONE);
   }

   protected boolean isMessageEmpty() {
      return message.getText().toString().trim().isEmpty();
   }

   protected boolean isReasonTypeSelected() {
      Object selectedItem = spinner.getSelectedItem();
      return selectedItem != null && !(((FeedbackType) selectedItem).getId() == -1);
   }
}
