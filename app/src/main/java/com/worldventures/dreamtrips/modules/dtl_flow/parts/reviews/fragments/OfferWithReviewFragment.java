package com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.adapter.ReviewAdapter;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.model.ReviewObject;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.recycler.MarginDecoration;

import java.util.ArrayList;

public class OfferWithReviewFragment extends Fragment {

   private RecyclerView recyclerAdapter;
   private RatingBar ratingBar2;
   private TextView tvReview;
   private TextView tvReviewCount;
   private View lineSeparator;
   private RelativeLayout btnRateAndReview;

   private ReviewAdapter mAdapter;

   public static final String ARRAY = "arrayList";
   public static final String RATING_MERCHANT = "ratingMerchant";
   public static final String COUNT_REVIEW = "countReview";
   private ArrayList<ReviewObject> mArrayInfo;

   private float mRatingMerchant;
   private int mCountReview;

   public OfferWithReviewFragment() {
   }

   public static OfferWithReviewFragment newInstance(Bundle arguments) {
      OfferWithReviewFragment f = new OfferWithReviewFragment();
      if (arguments != null) {
         f.setArguments(arguments);
      }
      return f;
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
         Bundle savedInstanceState) {
      final View v = inflater.inflate(R.layout.activity_offer_with_review, container, false);

      recyclerAdapter = (RecyclerView) v.findViewById(R.id.recycler_adapter);
      ratingBar2 = (RatingBar) v.findViewById(R.id.ratingBar2);
      tvReview = (TextView) v.findViewById(R.id.tv_Review);
      tvReviewCount = (TextView) v.findViewById(R.id.tv_review_count);
      lineSeparator = v.findViewById(R.id.line_separator);
      btnRateAndReview = (RelativeLayout) v.findViewById(R.id.btn_rate_and_review);

      Bundle bundle = getArguments();
      mArrayInfo = new ArrayList<>();
      mArrayInfo.addAll(bundle.<ReviewObject>getParcelableArrayList(ARRAY));
      mRatingMerchant = bundle.getFloat(RATING_MERCHANT, 0f);
      mCountReview = bundle.getInt(COUNT_REVIEW, 0);
      setRetainInstance(true);

      initRecycler();
      initAdapter();

      return v;
   }

   @Override
   public void onResume() {
      super.onResume();
      setUpInfo();
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
      recyclerAdapter.setLayoutManager(new LinearLayoutManager(getActivity()));
      recyclerAdapter.addItemDecoration(new MarginDecoration(getActivity()));
      recyclerAdapter.setHasFixedSize(false);
   }

   private void initAdapter() {
      mAdapter = new ReviewAdapter(getActivity());
      recyclerAdapter.setAdapter(mAdapter);
      mAdapter.addAll(mArrayInfo);
   }

   @Override
   public void onDestroyView() {
      super.onDestroyView();
   }
}
