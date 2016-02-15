package com.worldventures.dreamtrips.modules.common.view.custom.tagview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.AutoCompleteTextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.util.TextWatcherAdapter;
import com.worldventures.dreamtrips.modules.tripsimages.model.PhotoTag;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;

public class CreationTagView extends TagView<TagCreationActionsListener> {

    @Optional
    @InjectView(R.id.new_user_input_name)
    public AutoCompleteTextView inputFriendName;

    private TagFriendAdapter adapter;
    private SuggestionTagView suggestionTagView;

    public CreationTagView(Context context) {
        this(context, null);
    }

    public CreationTagView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CreationTagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setUserFriends(@Nullable List<User> userFriends) {
        adapter.setFriendList(userFriends);
        inputFriendName.post(() -> {
            if (!inputFriendName.isPopupShowing()) inputFriendName.showDropDown();
        });
    }

    @Override
    protected void initialize() {
        LayoutInflater.from(getContext()).inflate(getLayout(), this, true);
        ButterKnife.inject(this);
        adapter = new TagFriendAdapter(getContext());
        inputFriendName.setAdapter(adapter);
        inputFriendName.setDropDownBackgroundResource(R.drawable.background_common_tag_view);
        inputFriendName.setDropDownWidth(getSize().getWidth());
        inputFriendName.setDropDownVerticalOffset(0);
        inputFriendName.setThreshold(0);
        inputFriendName.setDropDownAnchor(R.id.new_user_suggestions_popup_anchor);
        inputFriendName.setOnItemClickListener((parent, view, position, id) -> {
            PhotoTag.TagPosition tagPosition = photoTag.getProportionalPosition();
            tagListener.onTagCreated(this,suggestionTagView, new PhotoTag(tagPosition, adapter.getItem(position)));
        });
        inputFriendName.setOnTouchListener((v, event) -> {
            if (!inputFriendName.isPopupShowing()) {
                inputFriendName.showDropDown();
            }
            return false;
        });
        inputFriendName.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 0) {
                    if (!inputFriendName.isPopupShowing()) {
                        inputFriendName.showDropDown();
                    }
                }
            }
        });
    }

    protected int getLayout() {
        return R.layout.layout_tag_view_new;
    }

    @Override
    public void setTagListener(TagCreationActionsListener tagListener) {
        super.setTagListener(tagListener);
        adapter.setTagListener(tagListener);
        tagListener.requestFriendList("");

    }

    @OnClick({R.id.new_user_delete_tag})
    public void onClick() {
        deleteTag();
    }


    public void setSuggestionTagView(SuggestionTagView suggestionTagView) {
        this.suggestionTagView = suggestionTagView;
    }
}
