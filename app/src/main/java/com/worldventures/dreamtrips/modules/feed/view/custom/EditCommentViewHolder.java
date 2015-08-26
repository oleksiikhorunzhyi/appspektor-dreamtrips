package com.worldventures.dreamtrips.modules.feed.view.custom;

import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.utils.ui.SoftInputUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.util.TextWatcherAdapter;
import com.worldventures.dreamtrips.modules.feed.presenter.EditCommentPresenter;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class EditCommentViewHolder extends BaseViewHolder<EditCommentPresenter> implements EditCommentPresenter.View {

    @InjectView(R.id.user_photo)
    SimpleDraweeView userPhoto;
    @InjectView(R.id.user_name)
    TextView userName;
    @InjectView(R.id.comment_edit)
    EditText editComment;
    @InjectView(R.id.save)
    Button save;

    public EditCommentViewHolder() {
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(com.orhanobut.dialogplus.R.layout.dialog_view, parent, false);
        ViewGroup contentContainer = (ViewGroup) view.findViewById(com.orhanobut.dialogplus.R.id.view_container);

        contentView = inflater.inflate(R.layout.fragment_edit_comment, parent, false);
        contentContainer.addView(contentView);

        contentContainer.setBackgroundColor(parent.getResources().getColor(R.color.white_semi_transparent));

        ButterKnife.inject(this, view);

        presenter.takeView(this);
        presenter.onStart();

        editComment.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                save.setEnabled(!TextUtils.isEmpty(s));
            }
        });

        return view;
    }

    @OnClick(R.id.save)
    public void onSave() {
        SoftInputUtil.hideSoftInputMethod(editComment);
        presenter.onSave();
    }

    @OnClick(R.id.cancel)
    public void onCancel() {
        SoftInputUtil.hideSoftInputMethod(editComment);
        close();
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
    public void close() {
        presenter.onStop();
        presenter.dropView();
        if (dialog != null) dialog.dismiss();
    }
}
