package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.utils.delegate.EditCommentCloseDelegate;
import com.techery.spares.utils.ui.SoftInputUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.common.view.util.TextWatcherAdapter;
import com.worldventures.dreamtrips.modules.feed.bundle.SingleCommentBundle;
import com.worldventures.dreamtrips.modules.feed.presenter.EditCommentPresenter;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;


@Layout(R.layout.fragment_edit_comment)
public class EditCommentFragment extends BaseFragmentWithArgs<EditCommentPresenter, SingleCommentBundle> implements EditCommentPresenter.View {

   @InjectView(R.id.user_photo) SimpleDraweeView userPhoto;
   @InjectView(R.id.user_name) TextView userName;
   @InjectView(R.id.comment_edit) EditText editComment;
   @InjectView(R.id.save) Button save;

   @Inject EditCommentCloseDelegate commentCloseDelegate;

   @Override
   public void afterCreateView(View rootView) {
      editComment.addTextChangedListener(new TextWatcherAdapter() {
         @Override
         public void onTextChanged(CharSequence s, int start, int before, int count) {
            save.setEnabled(!TextUtils.isEmpty(s.toString().trim()));
         }
      });
   }

   @OnClick(R.id.save)
   public void onSave() {
      SoftInputUtil.hideSoftInputMethod(editComment);
      getPresenter().onSave(editComment.getText().toString().trim());
   }

   @Override
   public void disableSaveButton() {
      save.setEnabled(false);
   }

   @Override
   public void enableSaveButton() {
      save.setEnabled(true);
   }

   @OnClick(R.id.cancel)
   public void onCancel() {
      SoftInputUtil.hideSoftInputMethod(editComment);
      close();
   }

   @Override
   public void setUsername(String name) {
      userName.setText(name);
   }

   @Override
   public void setImageURI(Uri uri) {
      userPhoto.setImageURI(uri);
   }

   @Override
   public void close() {
      commentCloseDelegate.post(this.getClass().getName());
   }

   @Override
   public void setText(String text) {
      editComment.setText(text);
      if (text != null) editComment.setSelection(text.length());
   }

   @Override
   protected EditCommentPresenter createPresenter(Bundle savedInstanceState) {
      return new EditCommentPresenter(getArgs());
   }
}
