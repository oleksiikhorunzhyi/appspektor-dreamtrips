package com.worldventures.dreamtrips.social.ui.feed.view.custom.tagview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.model.User;
import com.worldventures.core.ui.util.GraphicUtils;
import com.worldventures.dreamtrips.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TagFriendAdapter extends ArrayAdapter<User> {

   private final Context context;
   private OnFriendRequestListener onFriendRequestListener;
   private List<User> friendList = new ArrayList<>();
   private Set<User> cachedFriendList = new HashSet<>();
   private String lastAppliedFilter = "";

   public TagFriendAdapter(Context context, OnFriendRequestListener onFriendRequestListener) {
      super(context, 0, 0, new ArrayList<>());
      this.context = context;
      this.onFriendRequestListener = onFriendRequestListener;
   }

   public void addFriends(List<User> friendList) {
      List<User> uniqueElements = new ArrayList<>(friendList);
      uniqueElements.removeAll(cachedFriendList);
      this.friendList.addAll(uniqueElements);
      this.cachedFriendList.addAll(uniqueElements);
      notifyDataSetChanged();
   }

   private void applyFilter() {
      friendList = Queryable.from(cachedFriendList).filter(element -> element.getFullName().toLowerCase()
            .contains(lastAppliedFilter)).toList();
   }

   @Override
   public User getItem(int position) {
      return friendList.get(position);
   }

   @Override
   public int getCount() {
      return friendList == null ? 0 : friendList.size();
   }

   @NonNull
   @Override
   public View getView(int position, View convertView, @NonNull ViewGroup parent) {
      FriendHolder holder;

      if (convertView == null) {
         LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         convertView = inflater.inflate(R.layout.adapter_item_tag_dropdown, parent, false);

         holder = new FriendHolder();
         holder.friendName = convertView.findViewById(R.id.friend_name);
         holder.friendPhoto = convertView.findViewById(R.id.friend_photo);

         convertView.setTag(holder);
      } else {
         holder = (FriendHolder) convertView.getTag();
      }

      User user = friendList.get(position);
      holder.friendName.setText(user.getFullName());
      final int photoSize = context.getResources().getDimensionPixelSize(R.dimen.tag_common_photo_size);
      holder.friendPhoto.setController(GraphicUtils.provideFrescoResizingController(user.getAvatar().getThumb(),
            holder.friendPhoto.getController(), photoSize, photoSize));
      holder.friendPhoto.invalidate();

      return convertView;
   }

   @Override
   public Filter getFilter() {
      return new Filter() {

         @Override
         protected FilterResults performFiltering(CharSequence constraint) {
            lastAppliedFilter = constraint != null ? constraint.toString().trim().toLowerCase() : "";
            applyFilter();
            notifyDataSetChanged();

            return new FilterResults();
         }

         @Override
         protected void publishResults(CharSequence constraint, FilterResults results) {
            if (lastAppliedFilter.length() > 2 && onFriendRequestListener != null) {
               onFriendRequestListener.requestFriends(lastAppliedFilter);
            }
         }
      };
   }

   interface OnFriendRequestListener {
      void requestFriends(String constraint);
   }

   private static class FriendHolder {
      TextView friendName;
      SimpleDraweeView friendPhoto;
   }
}
