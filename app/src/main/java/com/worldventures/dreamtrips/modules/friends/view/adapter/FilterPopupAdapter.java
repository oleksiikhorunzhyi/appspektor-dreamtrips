package com.worldventures.dreamtrips.modules.friends.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class FilterPopupAdapter<T> extends BaseAdapter {

    private List<T> items;
    private LayoutInflater inflater;

    public FilterPopupAdapter(List<T> contentItems, Context context) {
        this.items = contentItems;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public T getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View contentView, ViewGroup viewGroup) {
        ViewHolder holder;
        View view = contentView;

        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(R.layout.adapter_item_content, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        T contentItem = getItem(position);
        holder.name.setText(contentItem.toString());

        return view;}

    static class ViewHolder {
        @InjectView(R.id.name)
        protected TextView name;
        @InjectView(R.id.checkBox)
        protected CheckBox checkBox;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }


    }

}
