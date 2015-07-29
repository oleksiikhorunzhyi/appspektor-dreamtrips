package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.common.view.util.TextWatcherAdapter;
import com.worldventures.dreamtrips.modules.feed.presenter.PostPresenter;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.layout_post)
public class PostFragment extends BaseFragment<PostPresenter> implements PostPresenter.View {

    @InjectView(R.id.avatar)
    SimpleDraweeView avatar;
    @InjectView(R.id.name)
    TextView name;
    @InjectView(R.id.post)
    EditText post;
    @InjectView(R.id.post_button)
    Button postButton;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        post.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence constraint, int start, int before, int count) {
                super.onTextChanged(constraint, start, before, count);
                getPresenter().postInputChanged(constraint.toString());
            }
        });
    }

    @Override
    protected PostPresenter createPresenter(Bundle savedInstanceState) {
        return new PostPresenter();
    }

    @OnClick(R.id.close)
    void onClose() {
        getPresenter().cancel();
        getActivity().onBackPressed();
    }

    @OnClick(R.id.post_button)
    void onPost() {
        getPresenter().post();
    }

    @Override
    public void enableButton() {
        postButton.setTextColor(getResources().getColor(R.color.bucket_detailed_text_color));
    }

    @Override
    public void disableButton() {
        postButton.setTextColor(getResources().getColor(R.color.gray));
    }

    @Override
    public void setName(String userName) {
        name.setText(userName);
    }

    @Override
    public void setAvatar(String avatarUrl) {
        avatar.setImageURI(Uri.parse(avatarUrl));
    }
}
