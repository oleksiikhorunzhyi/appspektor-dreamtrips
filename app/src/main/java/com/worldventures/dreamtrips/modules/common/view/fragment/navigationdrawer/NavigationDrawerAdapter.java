package com.worldventures.dreamtrips.modules.common.view.fragment.navigationdrawer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.TypefaceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.di.MessengerModule;
import com.worldventures.core.component.ComponentDescription;
import com.worldventures.core.ui.view.custom.BadgeView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.NavigationDrawerListener;

import com.worldventures.dreamtrips.social.di.SocialAppModule;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

public class NavigationDrawerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

   private static final int TYPE_HEADER = 0;
   private static final int TYPE_ITEM = 1;

   private NavigationHeader navigationHeader;
   private int headerSize = 0;

   private final List<ComponentDescription> componentDescriptions;
   private NavigationDrawerListener navigationDrawerListener;
   private int selectedComponent;
   private int notificationCount;
   private int unreadMessageCount;

   public NavigationDrawerAdapter(List<ComponentDescription> data) {
      componentDescriptions = data;
   }

   public void setNavigationDrawerCallbacks(NavigationDrawerListener navigationDrawerListener) {
      this.navigationDrawerListener = navigationDrawerListener;
   }

   @Override
   public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
      switch (viewType) {
         case TYPE_ITEM: {
            View v = LayoutInflater.from(viewGroup.getContext())
                  .inflate(R.layout.adapter_item_navigation_drawer, viewGroup, false);
            return new ItemHolder(v);
         }
         case TYPE_HEADER: {
            View v = LayoutInflater.from(viewGroup.getContext())
                  .inflate(R.layout.header_navigation_drawer, viewGroup, false);
            return new HeaderHolder(v);
         }
         default:
            break;
      }
      return null;
   }

   @Override
   public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
      switch (getItemViewType(i)) {
         case TYPE_HEADER:
            bindHeaderViewHolder((HeaderHolder) viewHolder);
            break;
         case TYPE_ITEM:
            bindItemViewHolder((ItemHolder) viewHolder, i);
            break;
         default:
            break;
      }
   }

   private void bindHeaderViewHolder(HeaderHolder holder) {
      holder.userPhoto.setImageURI(navigationHeader.getUserPhoto());
      holder.userCover.setImageURI(navigationHeader.getUserCover());
      holder.userName.setText(navigationHeader.getUserName());
      holder.userEmail.setText(navigationHeader.getUserEmail());

      holder.userPhoto.setOnClickListener(v -> navigationDrawerListener.onNavigationDrawerItemSelected(Queryable.from(componentDescriptions)
            .filter(element -> element.getKey().equals(SocialAppModule.ACCOUNT_PROFILE))
            .first()));
   }

   private void bindItemViewHolder(ItemHolder holder, int i) {
      ComponentDescription item = getItem(i);
      holder.sectionIcon.setImageResource(item.getIcon());
      holder.sectionIcon.setContentDescription(item.getKey().toLowerCase());
      if (holder.itemName != null) {
         setupTitleWithBetaLabel(holder.itemName, item.getNavMenuTitle());
      }

      boolean isSelected = isComponentSelected(i);
      holder.itemView.setActivated(isSelected);

      holder.itemView.setOnClickListener(v -> {
         navigationDrawerListener.onNavigationDrawerItemSelected(item);
      });

      if (item.getKey().equals(MessengerModule.MESSENGER) && unreadMessageCount > 0) {
         holder.badgeView.setVisibility(View.VISIBLE);
         holder.badgeView.setBadgeBackgroundColor(holder.itemView.getResources().getColor(R.color.bucket_red));
         holder.badgeView.setText(String.valueOf(unreadMessageCount));
      } else if (item.getKey().equals(SocialAppModule.NOTIFICATIONS) && notificationCount > 0) {
         holder.badgeView.setVisibility(View.VISIBLE);
         holder.badgeView.setBadgeBackgroundColor(holder.itemView.getResources().getColor(R.color.bucket_red));
         holder.badgeView.setText(String.valueOf(notificationCount));
      } else {
         holder.badgeView.setVisibility(View.GONE);
      }
   }

   private void setupTitleWithBetaLabel(TextView tvTitle, int resIdNavMenuTitle) {
      Context context = tvTitle.getContext();
      String navMenuTitle = context.getString(resIdNavMenuTitle);
      if (resIdNavMenuTitle == R.string.wallet) {
         String betaLabel = context.getString(R.string.beta_label);
         SpannableString spannedBetaLabel = new SpannableString(betaLabel);
         SpannableStringBuilder spannedNavTitleBuilder = new SpannableStringBuilder(navMenuTitle);
         spannedNavTitleBuilder.append(" ");
         spannedBetaLabel.setSpan(
               new RelativeSizeSpan(0.7f),
               0,
               betaLabel.length(),
               Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
         );
         spannedBetaLabel.setSpan(new TypefaceSpan("normal"),
               0,
               betaLabel.length(),
               Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
         );
         spannedNavTitleBuilder.append(spannedBetaLabel);

         tvTitle.setText(spannedNavTitleBuilder);
      } else {
         tvTitle.setText(resIdNavMenuTitle);
      }
   }

   public void selectComponent(ComponentDescription component) {
      int lastPosition = selectedComponent;
      selectedComponent = componentDescriptions.indexOf(component);
      notifyItemChanged(lastPosition + headerSize);
      notifyItemChanged(selectedComponent + headerSize);
   }

   private boolean isComponentSelected(int i) {
      return (selectedComponent + headerSize) == i;
   }

   @Override
   public int getItemCount() {
      return componentDescriptions.size() + headerSize;
   }

   @Override
   public int getItemViewType(int position) {
      return isHeader(position) ? TYPE_HEADER : TYPE_ITEM;
   }

   private boolean isHeader(int position) {
      return navigationHeader != null && position == 0;
   }

   public boolean setHeader(NavigationHeader navigationHeader) {
      boolean isChange = (this.navigationHeader == null && navigationHeader != null) || (this.navigationHeader != null && navigationHeader == null);
      this.navigationHeader = navigationHeader;
      headerSize = navigationHeader == null ? 0 : 1;
      return isChange;
   }

   public ComponentDescription getItem(int position) {
      return componentDescriptions.get(position - headerSize);
   }

   @Override
   public long getItemId(int position) {
      switch (getItemViewType(position)) {
         case TYPE_HEADER:
            return 0;
         case TYPE_ITEM:
            return getItem(position).hashCode();
         default:
            break;
      }
      return super.getItemId(position);
   }

   public void setNotificationCount(int notificationCount) {
      if (this.notificationCount != notificationCount) {
         this.notificationCount = notificationCount;
         notifyDataSetChanged();
      }
   }

   public void setUnreadMessageCount(int unreadMessageCount) {
      if (this.unreadMessageCount != unreadMessageCount) {
         this.unreadMessageCount = unreadMessageCount;
         notifyDataSetChanged();
      }
   }

   static class HeaderHolder extends RecyclerView.ViewHolder {
      @InjectView(R.id.user_cover) public ImageView userCover;
      @InjectView(R.id.user_photo) public ImageView userPhoto;
      @InjectView(R.id.user_name) public TextView userName;
      @InjectView(R.id.user_email) public TextView userEmail;

      public HeaderHolder(View itemView) {
         super(itemView);
         ButterKnife.inject(this, itemView);
      }
   }

   static class ItemHolder extends RecyclerView.ViewHolder {
      @Optional @InjectView(R.id.item_name) public TextView itemName;
      @InjectView(R.id.section_icon) public ImageView sectionIcon;
      @InjectView(R.id.badge) public BadgeView badgeView;

      public ItemHolder(View itemView) {
         super(itemView);
         ButterKnife.inject(this, itemView);
      }
   }
}
