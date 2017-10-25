package com.worldventures.dreamtrips.social.ui.feed.view.cell;

import android.view.View;
import android.widget.TextView;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.core.utils.LocaleHelper;
import com.worldventures.core.utils.QuantityHelper;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractDelegateCell;
import com.worldventures.dreamtrips.social.ui.feed.model.feed.hashtag.HashtagSuggestion;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_hashtag_suggestion)
public class HashtagSuggestionCell extends BaseAbstractDelegateCell<HashtagSuggestion, HashtagSuggestionCell.Delegate> {

   @InjectView(R.id.suggestionText) TextView suggestionText;
   @InjectView(R.id.suggestionCount) TextView suggestionCount;

   public HashtagSuggestionCell(View view) {
      super(view);
   }

   public void syncUIStateWithModel() {
      suggestionText.setText(String.format("#%s", getModelObject().getName()));
      if (getModelObject().getUsageCount() > 0) {
         int usageCount = getModelObject().getUsageCount();
         suggestionCount.setText(String.format(LocaleHelper.getDefaultLocale(), getSuggestionsCountString(usageCount), usageCount));
         suggestionCount.setVisibility(View.VISIBLE);
      } else {
         suggestionCount.setVisibility(View.GONE);
      }
      itemView.setOnClickListener(v -> cellDelegate.onCellClicked(getModelObject()));
   }

   private String getSuggestionsCountString(int suggestionsCount) {
      return itemView.getResources()
            .getString(QuantityHelper.chooseResource(suggestionsCount, R.string.hashtag_one_post, R.string.hashtag_multiple_posts));
   }

   @Override
   public boolean shouldInject() {
      return false;
   }

   public interface Delegate extends CellDelegate<HashtagSuggestion> {

   }

}
