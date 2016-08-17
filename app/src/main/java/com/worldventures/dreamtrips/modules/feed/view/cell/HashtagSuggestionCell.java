package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.view.View;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.QuantityHelper;
import com.worldventures.dreamtrips.modules.feed.model.feed.hashtag.HashtagSuggestion;

import java.util.Locale;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_hashtag_suggestion)
public class HashtagSuggestionCell extends AbstractDelegateCell<HashtagSuggestion, HashtagSuggestionCell.Delegate> {

   @InjectView(R.id.suggestionText) TextView suggestionText;
   @InjectView(R.id.suggestionCount) TextView suggestionCount;

   public HashtagSuggestionCell(View view) {
      super(view);
   }

   public void syncUIStateWithModel() {
      suggestionText.setText(String.format("#%s", getModelObject().getName()));
      if (getModelObject().getUsageCount() > 0) {
         int usageCount = getModelObject().getUsageCount();
         suggestionCount.setText(String.format(Locale.getDefault(), getSuggestionsCountString(usageCount), usageCount));
         suggestionCount.setVisibility(View.VISIBLE);
      } else {
         suggestionCount.setVisibility(View.GONE);
      }
      itemView.setOnClickListener(v -> cellDelegate.onCellClicked(getModelObject()));
   }

   public interface Delegate extends CellDelegate<HashtagSuggestion> {

   }

   private String getSuggestionsCountString(int suggestionsCount) {
      return itemView.getResources()
            .getString(QuantityHelper.chooseResource(suggestionsCount, R.string.hashtag_one_post, R.string.hashtag_multiple_posts));
   }
}
