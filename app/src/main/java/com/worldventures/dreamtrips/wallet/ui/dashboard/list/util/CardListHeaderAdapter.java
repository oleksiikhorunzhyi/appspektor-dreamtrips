package com.worldventures.dreamtrips.wallet.ui.dashboard.list.util;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eowise.recyclerview.stickyheaders.StickyHeadersAdapter;
import com.techery.spares.adapter.HeaderItem;
import com.worldventures.dreamtrips.R;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static android.view.LayoutInflater.from;

public class CardListHeaderAdapter implements StickyHeadersAdapter<CardListHeaderAdapter.CardListHeaderViewHolder> {

    private List<? extends HeaderItem> items;

    public CardListHeaderAdapter(List<? extends HeaderItem> items) {
        this.items = items;
    }

    @Override
    public CardListHeaderViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        View itemView = from(viewGroup.getContext()).inflate(R.layout.adapter_item_wallet_cardlist_header, viewGroup, false);
        return new CardListHeaderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CardListHeaderViewHolder headerViewHolder, int i) {
        headerViewHolder.letter.setText(items.get(i).getHeaderTitle());
    }

    @Override
    public long getHeaderId(int i) {
        String category = items.get(i).getHeaderTitle();
        return category.hashCode();
    }

    public static class CardListHeaderViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.tv_title)
        protected TextView letter;

        public CardListHeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

}
