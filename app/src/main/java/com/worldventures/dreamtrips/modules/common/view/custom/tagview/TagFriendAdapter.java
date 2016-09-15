package com.worldventures.dreamtrips.modules.common.view.custom.tagview;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.model.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TagFriendAdapter extends ArrayAdapter<User> {

   private Context context;
   private OnFriendRequestListener onFriendRequestListener;
   private List<User> friendList = new ArrayList<>();
   private Set<User> cachedFriendList = new HashSet<>();

   public TagFriendAdapter(Context context, OnFriendRequestListener onFriendRequestListener) {
      super(context, 0, 0, new ArrayList<>());
      this.context = context;
      this.onFriendRequestListener = onFriendRequestListener;
   }

   public void addFriends(List<User> friendList) {
      boolean isNewList = !this.friendList.containsAll(friendList) || !friendList.containsAll(this.friendList);
      if (isNewList) {
         this.friendList.addAll(friendList);
         this.cachedFriendList.addAll(friendList);
         notifyDataSetChanged();
      }
   }

   @Override
   public User getItem(int position) {
      return friendList.get(position);
   }

   @Override
   public int getCount() {
      return friendList == null ? 0 : friendList.size();
   }

   @Override
   public View getView(int position, View convertView, ViewGroup parent) {
      FriendHolder holder;

      if (convertView == null) {
         LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         convertView = inflater.inflate(R.layout.adapter_item_tag_dropdown, parent, false);

         holder = new FriendHolder();
         holder.friendName = (TextView) convertView.findViewById(R.id.friend_name);
         holder.friendPhoto = (SimpleDraweeView) convertView.findViewById(R.id.friend_photo);

         convertView.setTag(holder);
      } else {
         holder = (FriendHolder) convertView.getTag();
      }

      User user = friendList.get(position);
      holder.friendName.setText(user.getFullName());
      holder.friendPhoto.setImageURI(Uri.parse(user.getAvatar().getThumb()));
      holder.friendPhoto.invalidate();

      return convertView;
   }

   @Override
   public Filter getFilter() {
      return new Filter() {

         @Override
         protected FilterResults performFiltering(CharSequence constraint) {
            return new FilterResults();
         }

         @Override
         protected void publishResults(CharSequence constraint, FilterResults results) {
            String trim = constraint != null ? constraint.toString().trim().toLowerCase() : "";
            if (trim.length() > 2 && onFriendRequestListener != null) {
               onFriendRequestListener.requestFriends(trim);
            } else {
               friendList = Queryable.from(cachedFriendList).filter(element -> {
                  return element.getFullName().toLowerCase().contains(trim);
               }).toList();
               notifyDataSetChanged();
            }
         }
      };
   }

   interface OnFriendRequestListener {
      void requestFriends(String constraint);
   }

   static class FriendHolder {
      TextView friendName;
      SimpleDraweeView friendPhoto;
   }
}
