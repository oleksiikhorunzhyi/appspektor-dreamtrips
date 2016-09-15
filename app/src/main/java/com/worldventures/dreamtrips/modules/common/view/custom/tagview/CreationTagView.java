package com.worldventures.dreamtrips.modules.common.view.custom.tagview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.AbsListView;

import com.badoo.mobile.util.WeakHandler;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.PhotoTag;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.TagPosition;
import com.worldventures.dreamtrips.modules.common.view.util.TextWatcherAdapter;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;

public class CreationTagView extends TagView<TagCreationActionsListener> {

   @Optional @InjectView(R.id.new_user_input_name) public FriendsAutoCompleteTextView inputFriendName;

   private TagFriendAdapter adapter;
   private PhotoTag suggestionTagView;
   private boolean loading = true;
   private int page = 1;
   private WeakHandler weakHandler = new WeakHandler();

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
      adapter.addFriends(userFriends);
      inputFriendName.post(() -> {
         if (!inputFriendName.isPopupShowing()) inputFriendName.showDropDown();
         loading = false;
      });
   }

   @Override
   protected void initialize() {
      LayoutInflater.from(getContext()).inflate(getLayout(), this, true);
      ButterKnife.inject(this);
      adapter = new TagFriendAdapter(getContext(), constraint -> tagListener.requestFriendList(constraint, page));
      if (pointerTop != null) {
         pointerTop.requestFocus();
      }
      inputFriendName.setAdapter(adapter);
      inputFriendName.setDropDownBackgroundResource(R.drawable.background_common_tag_view);
      inputFriendName.setDropDownWidth(getSize().getWidth());
      inputFriendName.setDropDownVerticalOffset(0);
      inputFriendName.setThreshold(0);
      inputFriendName.setDropDownAnchor(R.id.new_user_suggestions_popup_anchor);
      inputFriendName.setOnItemClickListener((parent, view, position, id) -> {
         TagPosition tagPosition = photoTag.getProportionalPosition();
         PhotoTag tag = new PhotoTag(tagPosition, adapter.getItem(position).getId());
         tag.setUser(adapter.getItem(position));
         tagListener.onTagCreated(this, suggestionTagView, tag);
      });
      inputFriendName.setOnTouchListener((v, event) -> {
         if (event.getAction() == MotionEvent.ACTION_UP) {
            inputFriendName.requestFocus();
            if (!inputFriendName.isPopupShowing() || !inputFriendName.isFocused()) {
               weakHandler.postDelayed(() -> inputFriendName.showDropDown(), 500);
            }
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
      inputFriendName.setOnScrollListener(new AbsListView.OnScrollListener() {
         @Override
         public void onScrollStateChanged(AbsListView view, int scrollState) {
         }

         @Override
         public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (!(loading) && (totalItemCount - visibleItemCount) <= (firstVisibleItem)) {
               page++;
               tagListener.requestFriendList(inputFriendName.getText().toString(), page);
               loading = true;
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
      tagListener.requestFriendList("", page);
   }

   @OnClick({R.id.new_user_delete_tag})
   public void onClick() {
      deleteTag();
   }

   public void setSuggestionTag(PhotoTag suggestionTagView) {
      this.suggestionTagView = suggestionTagView;
   }
}
