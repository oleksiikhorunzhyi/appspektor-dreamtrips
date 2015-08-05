package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.utils.ui.SoftInputUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.feed.presenter.EditCommentPresenter;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_edit_comment)
public class EditCommentFragment extends BaseFragment<EditCommentPresenter> implements EditCommentPresenter.View {

    @InjectView(R.id.user_photo)
    SimpleDraweeView userPhoto;
    @InjectView(R.id.user_name)
    TextView userName;
    @InjectView(R.id.comment_edit)
    EditText editComment;

    @OnClick(R.id.save)
    public void onSave() {
        getPresenter().onSave();
        SoftInputUtil.hideSoftInputMethod(editComment);
    }

    @OnClick(R.id.cancel)
    public void onCancel() {
        getPresenter().onCancel();
        SoftInputUtil.hideSoftInputMethod(editComment);
    }

    @Override
    public void setText(String text) {
        editComment.setText(text);
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
    public String getText() {
        return editComment.getText().toString();
    }

    @Override
    protected EditCommentPresenter createPresenter(Bundle savedInstanceState) {
        return new EditCommentPresenter(getArguments().getParcelable(EditCommentPresenter.EXTRA_COMMENT));
    }
}
