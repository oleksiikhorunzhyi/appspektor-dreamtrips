package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.feed.bundle.PostBundle;
import com.worldventures.dreamtrips.modules.feed.presenter.TextualPostDetailsPresenter;

import butterknife.InjectView;


@Layout(R.layout.fragment_textual_post_details)
public class TextualPostDetailsFragment extends BaseFragmentWithArgs<TextualPostDetailsPresenter, PostBundle> {

    @InjectView(R.id.post)
    TextView post;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        post.setText(getArgs().getTextualPost().getDescription());
    }

    @Override
    protected TextualPostDetailsPresenter createPresenter(Bundle savedInstanceState) {
        return new TextualPostDetailsPresenter(getArgs());
    }

}
