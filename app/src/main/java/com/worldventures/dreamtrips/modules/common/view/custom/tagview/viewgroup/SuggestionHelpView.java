package com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.TagView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SuggestionHelpView extends TagView {

   @InjectView(R.id.tagged_user_name) protected TextView title;

   public SuggestionHelpView(Context context) {
      super(context);
   }

   public SuggestionHelpView(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   public SuggestionHelpView(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
   }

   @Override
   protected void initialize() {
      LayoutInflater.from(getContext()).inflate(R.layout.layout_tag_view_exist, this, true);
      ButterKnife.inject(this);
      setClickable(true);
      title.setText(R.string.who_is_this);
   }
}
