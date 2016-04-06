package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.view.KeyEvent;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.utils.ui.SoftInputUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.custom.KeyCallbackEditText;
import com.worldventures.dreamtrips.modules.common.view.util.TextWatcherAdapter;
import com.worldventures.dreamtrips.modules.feed.view.cell.delegate.PostCreationTextDelegate;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_post_text)
public class PostCreationTextCell extends AbstractDelegateCell<String, PostCreationTextDelegate> {

    @InjectView(R.id.post)
    KeyCallbackEditText post;

    private TextWatcherAdapter textWatcher = new TextWatcherAdapter() {
        @Override
        public void onTextChanged(CharSequence constraint, int start, int before, int count) {
            super.onTextChanged(constraint, start, before, count);
            cellDelegate.onTextChanged(constraint.toString().trim());
        }
    };

    public PostCreationTextCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        post.addTextChangedListener(textWatcher);
        post.setOnKeyPreImeListener((keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                post.clearFocus();
            }
        });
        post.setOnFocusChangeListener((v, hasFocus) -> cellDelegate.onFocusChanged(hasFocus));
    }

    @Override
    public void clearResources() {
        super.clearResources();
        SoftInputUtil.hideSoftInputMethod(post);
        post.removeTextChangedListener(textWatcher);
        post.setOnFocusChangeListener(null);
    }

    @Override
    public void prepareForReuse() {

    }
}
