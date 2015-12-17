package com.worldventures.dreamtrips.modules.common.view.custom.tagview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.model.User;

import java.util.ArrayList;
import java.util.List;

public class TagFriendAdapter extends ArrayAdapter<User> {

    private static final int MAX_RESULT_COUNT = 5;
    private int resultCount = MAX_RESULT_COUNT;
    private Context context;
    private List<User> friendList, tempList, suggestions;

    public TagFriendAdapter(Context context, List<User> items) {
        this(context, items, MAX_RESULT_COUNT);
    }

    public TagFriendAdapter(Context context, List<User> items, int resultCount) {
        super(context, 0, 0, items);
        this.context = context;
        this.friendList = items;
        this.resultCount = resultCount;
        tempList = new ArrayList<User>(items);
        suggestions = new ArrayList<User>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FriendHolder holder = null;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.adapter_item_tag_dropdown, parent, false);

            holder = new FriendHolder();
            holder.friendName = (TextView) convertView.findViewById(R.id.friend_name);
            holder.friendPhoto = (ImageView) convertView.findViewById(R.id.friend_photo);

            convertView.setTag(holder);
        } else {
            holder = (FriendHolder) convertView.getTag();
        }

        User user = friendList.get(position);
        holder.friendName.setText(user.getFullName());
        Picasso.with(context).load(R.drawable.ic_dt_launcher).into(holder.friendPhoto);

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            public CharSequence convertResultToString(Object resultValue) {
                return ((User) resultValue).getFullName();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                if (constraint != null) {
                    suggestions.clear();
                    for (User user : tempList) {
                        if (user.getFullName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            suggestions.add(user);
                            if (suggestions.size() == resultCount) {
                                break;
                            }
                        }
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = suggestions;
                    filterResults.count = suggestions.size();
                    return filterResults;
                } else {
                    return new FilterResults();
                }
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                List<User> filterList = (ArrayList<User>) results.values;
                if (results != null && results.count > 0) {
                    clear();
                    for (User user : filterList) {
                        add(user);
                        notifyDataSetChanged();
                    }
                }
            }
        };
    }

    static class FriendHolder{
        TextView friendName;
        ImageView friendPhoto;
    }
}
