package com.worldventures.dreamtrips.modules.common.view.custom.tagview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.AutoCompleteTextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.tripsimages.model.PhotoTag;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class CreationTagView extends TagView<TagCreationActionsListener> {

    @InjectView(R.id.new_user_input_name)
    public AutoCompleteTextView inputFriendName;

    private TagFriendAdapter adapter;

    public CreationTagView(Context context) {
        super(context);
    }

    public CreationTagView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CreationTagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setUserFriends(@Nullable List<User> userFriends) {
        super.setUserFriends(userFriends);
        adapter.setFriendList(userFriends);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void initialize() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_tag_view_new, this, true);
        ButterKnife.inject(this);

        adapter = new TagFriendAdapter(getContext(), userFriends);
        inputFriendName.setAdapter(adapter);
        inputFriendName.setDropDownBackgroundResource(R.drawable.background_common_tag_view);
        inputFriendName.setDropDownWidth(getSize().getWidth());
        inputFriendName.setDropDownVerticalOffset(0);
        inputFriendName.setDropDownAnchor(R.id.new_user_suggestions_popup_anchor);
        inputFriendName.setOnItemClickListener((parent, view, position, id) -> {
            PhotoTag.TagPosition tagPosition = photoTag.getPosition();
            tagListener.onTagCreated(this, new PhotoTag(tagPosition, adapter.getItem(position)));
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public void setTagListener(TagCreationActionsListener tagListener) {
        super.setTagListener(tagListener);
        adapter.setTagListener(tagListener);
    }

    @OnClick({R.id.new_user_delete_tag})
    public void onClick() {
        deleteTag();
    }


}
