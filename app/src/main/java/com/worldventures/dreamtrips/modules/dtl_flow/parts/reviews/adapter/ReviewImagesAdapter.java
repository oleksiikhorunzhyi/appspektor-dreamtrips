package com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.ReviewImages;

import java.util.List;

public class ReviewImagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

   private List<ReviewImages> imagesList;
   private String userComment;
   private int HEADER_TYPE = 0;
   private int ITEM_TYPE = 1;

   public ReviewImagesAdapter(String userComment, List<ReviewImages> imagesList) {
      this.userComment = userComment;
      this.imagesList = imagesList;
   }

   @Override
   public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

      if (viewType == ITEM_TYPE) {
         View itemType = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review_image, parent, false);
         return new ImagesViewHolder(itemType);

      } else if (viewType == HEADER_TYPE) {
         View headerType = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review_header, parent, false);
         return new HeaderViewHolder(headerType);
      }

      return null;
   }

   @Override
   public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

      if (holder instanceof ImagesViewHolder) {

         if (imagesList.size() > 0 && position > 0) {

            ImagesViewHolder imagesViewHolder = (ImagesViewHolder) holder;
            String urlString = imagesList.get(position - 1).normalUrl();

            if (!TextUtils.isEmpty(urlString)) {
               Uri imageUri = Uri.parse(urlString);
               imagesViewHolder.reviewPhoto.setImageURI(imageUri);
            }
         }

      } else if (holder instanceof HeaderViewHolder) {
         HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
         headerViewHolder.reviewComment.setText(userComment);
      }

   }

   @Override
   public int getItemViewType(int position) {
      if (position == 0)
         return HEADER_TYPE;

      return ITEM_TYPE;
   }

   @Override
   public int getItemCount() {
      return imagesList.size() + 1;
   }

   public class ImagesViewHolder extends RecyclerView.ViewHolder {

      public SimpleDraweeView reviewPhoto;

      public ImagesViewHolder(View itemView) {
         super(itemView);
         reviewPhoto = (SimpleDraweeView) itemView.findViewById(R.id.review_image);
      }
   }

   public class HeaderViewHolder extends RecyclerView.ViewHolder {

      public TextView reviewComment;

      public HeaderViewHolder(View view) {
         super(view);
         reviewComment = (TextView) view.findViewById(R.id.user_comment);
      }
   }

}
