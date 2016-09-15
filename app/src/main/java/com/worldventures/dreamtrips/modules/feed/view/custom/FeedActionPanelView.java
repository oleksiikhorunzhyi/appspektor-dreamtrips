package com.worldventures.dreamtrips.modules.feed.view.custom;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.MenuRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.PopupMenu;
import android.text.Html;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badoo.mobile.util.WeakHandler;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.QuantityHelper;
import com.worldventures.dreamtrips.modules.common.view.custom.FlagPopupMenu;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.view.cell.Flaggable;
import com.worldventures.dreamtrips.modules.feed.view.popup.FeedItemMenuBuilder;
import com.worldventures.dreamtrips.modules.tripsimages.model.Flag;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class FeedActionPanelView extends LinearLayout implements Flaggable {

   @Optional @InjectView(R.id.comments_count) TextView tvCommentsCount;
   @Optional @InjectView(R.id.comments) ImageView comments;
   @Optional @InjectView(R.id.likes_count) TextView tvLikesCount;
   @Optional @InjectView(R.id.likes) ImageView likes;
   @Optional @InjectView(R.id.more) ImageView more;

   @InjectView(R.id.feed_share) ImageView share;

   OnViewClickListener onCommentIconClickListener;
   OnViewClickListener onLikeIconClickListener;
   OnViewClickListener onLikersClickListener;
   OnViewClickListener onShareClickListener;
   OnViewClickListener onFlagClickListener;
   OnViewClickListener onMoreClickListener;
   OnViewClickListener onEditClickListener;
   OnViewClickListener onDeleteClickListener;

   OnFlagDialogClickListener onFlagDialogClickListener;

   private FeedItem feedItem;
   private boolean foreign;

   private WeakHandler handler;

   public FeedActionPanelView(Context context) {
      this(context, null);
   }

   public FeedActionPanelView(Context context, AttributeSet attrs) {
      this(context, attrs, 0);
   }

   public FeedActionPanelView(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      LayoutInflater.from(context).inflate(R.layout.adapter_item_feed_comment_footer, this, true);
      ButterKnife.inject(this);
      setOrientation(VERTICAL);
      setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
      handler = new WeakHandler();
   }

   @OnClick(R.id.likes)
   public void onLikeIconClick() {
      if (onLikeIconClickListener != null) {
         onLikeIconClickListener.onClick(feedItem);
      }
   }

   @OnClick({R.id.comments})
   public void onCommentIconClick() {
      if (onCommentIconClickListener != null) {
         onCommentIconClickListener.onClick(feedItem);
      }
   }

   @OnClick(R.id.likes_count)
   public void onLikersClick() {
      if (onLikersClickListener != null) {
         onLikersClickListener.onClick(feedItem);
      }
   }

   @OnClick(R.id.feed_share)
   public void onShareClick() {
      if (onShareClickListener != null) {
         onShareClickListener.onClick(feedItem);
      }
   }

   @OnClick(R.id.more)
   public void onMoreClick() {
      if (foreign) {
         if (onMoreClickListener != null) onMoreClickListener.onClick(feedItem);
      } else {
         PopupMenu popup = new PopupMenu(getContext(), more);
         popup.inflate(R.menu.menu_feed_flag);
         popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
               case R.id.action_flag:
                  if (onFlagClickListener != null) {
                     onFlagClickListener.onClick(feedItem);
                  }

                  break;
            }

            return true;
         });
         popup.show();
      }
   }

   public void showMoreDialog(@MenuRes int menuRes, @StringRes int headerDelete, @StringRes int textDelete) {
      more.setEnabled(false);
      FeedItemMenuBuilder.create(getContext(), more, menuRes)
            .onDelete(() -> showDeleteDialog(headerDelete, textDelete))
            .onEdit(this::onEdit)
            .dismissListener(menu -> handler.postDelayed(() -> more.setEnabled(true), 500))
            .show();
   }

   private void showDeleteDialog(@StringRes int headerDelete, @StringRes int textDelete) {
      Dialog dialog = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE).setTitleText(getResources().getString(headerDelete))
            .setContentText(getResources().getString(textDelete))
            .setConfirmText(getResources().getString(R.string.post_delete_confirm))
            .setConfirmClickListener(sDialog -> {
               sDialog.dismissWithAnimation();
               onDelete();
            });
      dialog.setCanceledOnTouchOutside(true);
      dialog.show();
   }

   protected void onDelete() {
      onDeleteClickListener.onClick(feedItem);
   }

   protected void onEdit() {
      onEditClickListener.onClick(feedItem);
   }

   public void setState(FeedItem feedItem, boolean foreign) {
      this.feedItem = feedItem;
      this.foreign = foreign;
      FeedEntity feedEntity = feedItem.getItem();
      Resources res = getResources();

      int likesCount = feedEntity.getLikesCount();
      int commentsCount = feedEntity.getCommentsCount();
      if (likesCount > 0) {
         if (tvLikesCount != null) {
            tvLikesCount.setVisibility(View.VISIBLE);
            Spanned text = Html.fromHtml(String.format(res.getString(QuantityHelper.chooseResource(likesCount, R.string.likes_count_one, R.string.likes_count_other)), likesCount));
            tvLikesCount.setText(text);
         }
      } else {
         tvLikesCount.setVisibility(View.GONE);
      }

      if (tvCommentsCount != null) {
         if (commentsCount > 0) {
            tvCommentsCount.setVisibility(View.VISIBLE);
            Spanned text = Html.fromHtml(String.format(res.getString(QuantityHelper.chooseResource(commentsCount, R.string.comments_count_one, R.string.comments_count_other)), commentsCount));
            tvCommentsCount.setText(text);
         } else tvCommentsCount.setVisibility(View.GONE);
      }

      if (likes != null) {
         likes.setEnabled(true);
         likes.setImageResource(feedEntity.isLiked() ? R.drawable.ic_feed_thumb_up_blue : R.drawable.ic_feed_thumb_up);
      }

      if (comments != null) {
         comments.setEnabled(true);
      }

      boolean isEditableItem = feedItem.getType() != FeedEntityHolder.Type.TRIP;
      if ((foreign && isEditableItem) || isEditableItem) {
         more.setVisibility(View.VISIBLE);
      } else {
         more.setVisibility(View.GONE);
      }

      if (feedItem.getType() == FeedEntityHolder.Type.POST || feedItem.getType() == FeedEntityHolder.Type.TRIP) {
         share.setVisibility(View.GONE);
      } else {
         share.setVisibility(View.VISIBLE);
      }
   }

   public void setOnCommentIconClickListener(OnViewClickListener onCommentIconClickListener) {
      this.onCommentIconClickListener = onCommentIconClickListener;
   }

   public void setOnLikeIconClickListener(OnViewClickListener onLikeIconClickListener) {
      this.onLikeIconClickListener = onLikeIconClickListener;
   }

   public void setOnLikersClickListener(OnViewClickListener onLikersClickListener) {
      this.onLikersClickListener = onLikersClickListener;
   }

   public void setOnShareClickListener(OnViewClickListener onShareClickListener) {
      this.onShareClickListener = onShareClickListener;
   }

   public void setOnFlagClickListener(OnViewClickListener onFlagClickListener) {
      this.onFlagClickListener = onFlagClickListener;
   }

   public void setOnFlagDialogClickListener(OnFlagDialogClickListener onFlagDialogClickListener) {
      this.onFlagDialogClickListener = onFlagDialogClickListener;
   }

   public void setOnMoreClickListener(OnViewClickListener onMoreClickListener) {
      this.onMoreClickListener = onMoreClickListener;
   }

   public void setOnEditClickListener(OnViewClickListener onEditClickListener) {
      this.onEditClickListener = onEditClickListener;
   }

   public void setOnDeleteClickListener(OnViewClickListener onDeleteClickListener) {
      this.onDeleteClickListener = onDeleteClickListener;
   }

   @Override
   public void showFlagDialog(List<Flag> flags) {
      FlagPopupMenu popupMenu = new FlagPopupMenu(getContext(), more);
      popupMenu.show(flags, (flagReasonId, reason) -> {
         if (onFlagDialogClickListener != null) {
            onFlagDialogClickListener.onClick(feedItem, flagReasonId, reason);
         }
      });
   }

   public interface OnViewClickListener {
      void onClick(FeedItem feedItem);
   }

   public interface OnFlagDialogClickListener {
      void onClick(FeedItem feedItem, int flagReasonId, String reason);
   }
}
