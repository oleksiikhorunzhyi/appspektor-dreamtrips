package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.view.View;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.feed.model.feed.hashtag.HashtagSuggestion;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_hashtag_suggestion)
public class HashtagSuggestionCell extends AbstractDelegateCell<HashtagSuggestion, HashtagSuggestionCell.Delegate> {

    @InjectView(R.id.suggestionText)
    TextView suggestionText;

    public HashtagSuggestionCell(View view) {
        super(view);
    }

    public void syncUIStateWithModel() {
        suggestionText.setText(String.format("#%s", getModelObject().getName()));
        itemView.setOnClickListener(v -> {
            cellDelegate.onCellClicked(getModelObject());
        });
    }

    public void prepareForReuse() {

    }

    public interface Delegate extends CellDelegate<HashtagSuggestion> {

    }
}
