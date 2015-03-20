package com.worldventures.dreamtrips.view.adapter;

import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.ContentItem;
import com.worldventures.dreamtrips.utils.HtmlTagHandler;
import com.worldventures.dreamtrips.view.custom.ExpandableTextViewCallable;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Edward on 23.01.15.
 */
//TODO replace with recycler adapter
public class ContentAdapter extends BaseAdapter {

    private List<ContentItem> contentItems;
    private LayoutInflater inflater;

    public ContentAdapter(List<ContentItem> contentItems, Context context) {
        this.contentItems = contentItems;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return contentItems.size();
    }

    @Override
    public ContentItem getItem(int position) {
        return contentItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(R.layout.adapter_item_content, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        ContentItem contentItem = getItem(position);
        holder.textViewContentHeader.setText(contentItem.getName());
        holder.textViewContent.setText(Html.fromHtml(contentItem.getDescription(), null, new HtmlTagHandler()));

        return view;
    }

    static class ViewHolder {
        @InjectView(R.id.textViewContentHeader)
        TextView textViewContentHeader;
        @InjectView(R.id.textViewContent)
        ExpandableTextViewCallable textViewContent;
        @InjectView(R.id.imageViewArrow)
        ImageView imageViewArrow;


        private boolean isExpanded = false;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
            textViewContent.setMovementMethod(new LinkMovementMethod());
            textViewContent.setOnTouchedListener(() -> {
                isExpanded = !isExpanded;
                checkArrowState();
            });
        }

        @OnClick(R.id.layoutContentHeader)
        void onClick() {
            isExpanded = !isExpanded;
            textViewContent.onClick(textViewContent);
            checkArrowState();
        }

        private void checkArrowState() {
            imageViewArrow.setImageResource(isExpanded ? R.drawable.arrow_up : R.drawable.arrow_down);
        }
    }
}
