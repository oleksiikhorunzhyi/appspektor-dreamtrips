package com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.views;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.detailReview.DtlDetailReviewPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.adapter.ReviewAdapter;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.model.ReviewObject;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.recycler.MarginDecoration;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.recycler.RecyclerClickListener;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.recycler.RecyclerTouchListener;

import java.util.ArrayList;

import flow.Flow;

public class OfferWithReviewView extends LinearLayout {

   private RecyclerView recyclerAdapter;
   private RatingBar ratingBar2;
   private TextView tvReview;
   private TextView tvReviewCount;
   private View lineSeparator;
   private ReviewAdapter mAdapter;

   public static final String ARRAY = "arrayList";
   public static final String RATING_MERCHANT = "ratingMerchant";
   public static final String COUNT_REVIEW = "countReview";
   public static final String MERCHANT_NAME = "merchantName";
   public static final String IS_FROM_LIST_REVIEW = "isFromListReview";
   private ArrayList<ReviewObject> mArrayInfo;

   private float mRatingMerchant;
   private int mCountReview;
   private String mMerchantName;
   private boolean mIsFromListReview = false;

   public OfferWithReviewView(Context context) {
      this(context, null);
   }

   public OfferWithReviewView(Context context, @Nullable AttributeSet attrs) {
      super(context, attrs);
      init();
   }

   private void init() {
      mAdapter = new ReviewAdapter(getContext());
   }

   public void addBundle(Bundle bundle) {
      mArrayInfo = new ArrayList<>();
      mArrayInfo.addAll(bundle.<ReviewObject>getParcelableArrayList(ARRAY));
      final View v = LayoutInflater.from(getContext()).inflate(R.layout.activity_offer_with_review, this, true);
      recyclerAdapter = (RecyclerView) v.findViewById(R.id.recycler_adapter);
      ratingBar2 = (RatingBar) v.findViewById(R.id.ratingBar2);
      tvReview = (TextView) v.findViewById(R.id.tv_Review);
      tvReviewCount = (TextView) v.findViewById(R.id.tv_review_count);
      lineSeparator = v.findViewById(R.id.line_separator);

      mRatingMerchant = bundle.getFloat(RATING_MERCHANT, 0f);
      mCountReview = bundle.getInt(COUNT_REVIEW, 0);
      mMerchantName = bundle.getString(MERCHANT_NAME, "");
      mIsFromListReview = bundle.getBoolean(IS_FROM_LIST_REVIEW, false);

      initRecycler();
      initAdapter();
      initListener();
      setUpInfo();
   }

   public void showNoComments() {
      LayoutInflater.from(getContext()).inflate(R.layout.offer_details_no_review, this, true);
   }

   private void initListener() {
      recyclerAdapter.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerAdapter,
            new RecyclerClickListener() {
               @Override
               public void onClick(View view, int position) {
                  Flow.get(getContext()).set(new DtlDetailReviewPath(mMerchantName, mArrayInfo.get(position), mArrayInfo
                        .get(position)
                        .getReviewId(), mIsFromListReview));
               }

               @Override
               public void onLongClick(View view, int position) {

               }
            }));
   }

   private void setUpInfo() {
      setUpRating();
      setUpCommentReview();
   }

   private void setUpCommentReview() {
      if (null != tvReviewCount && mCountReview > 0) {
         tvReviewCount.setText(getTextReview(mCountReview));
      }
   }

   private String getTextReview(int mCountReview) {
      return mCountReview == 1 ? String.format(getContext().getResources()
            .getString(R.string.format_review_text), mCountReview) : String.format(getContext().getResources()
            .getString(R.string.format_reviews_text), mCountReview);
   }

   private void setUpRating() {
      if (null != ratingBar2 && mRatingMerchant > 0) {
         ratingBar2.setRating(mRatingMerchant);
      }
   }

   private void initRecycler() {
      recyclerAdapter.setLayoutManager(new LinearLayoutManager(getContext()));
      recyclerAdapter.addItemDecoration(new MarginDecoration(getContext()));
      recyclerAdapter.setHasFixedSize(false);
   }

   private void initAdapter() {
      recyclerAdapter.setAdapter(mAdapter);
      mAdapter.addAll(mArrayInfo);
   }
}
