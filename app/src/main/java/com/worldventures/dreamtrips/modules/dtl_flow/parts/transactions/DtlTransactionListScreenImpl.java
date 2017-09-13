package com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlLayout;

import butterknife.InjectView;
import flow.Flow;

public class DtlTransactionListScreenImpl extends DtlLayout<DtlTransactionListScreen, DtlTransactionListPresenter, DtlTransactionListPath>
      implements DtlTransactionListScreen {

   @InjectView(R.id.toolbar_actionbar) Toolbar toolbar;
   @InjectView(R.id.tv_title) TextView tvTitle;
   @InjectView(R.id.emptyView) View emptyView;
   @InjectView(R.id.errorView) View errorView;
   @InjectView(R.id.dtlToolbarMerchantSearchInput) SearchView searchView;

   public DtlTransactionListScreenImpl(Context context) {
      super(context);
   }

   public DtlTransactionListScreenImpl(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   public DtlTransactionListPresenter createPresenter() {
      return new DtlTransactionListPresenterImpl(getContext(), injector);
   }

   @Override
   protected void onPostAttachToWindowView() {
      inflateToolbarMenu(toolbar);
      if (ViewUtils.isTabletLandscape(getContext())) {
         toolbar.setBackgroundColor(Color.WHITE);
         tvTitle.setVisibility(View.VISIBLE);
         tvTitle.setText(getContext().getResources().getString(R.string.dtl_show_transaction_toolbar));
      } else {
         toolbar.setTitle(getContext().getResources().getString(R.string.dtl_show_transaction_toolbar));
         toolbar.setNavigationIcon(ViewUtils.isTabletLandscape(getContext()) ? R.drawable.back_icon_black : R.drawable.back_icon);
         toolbar.setNavigationOnClickListener(view -> {Flow.get(getContext()).goBack();});
      }
      setupSearch();
   }

   private void setupSearch(){
      searchView.setIconifiedByDefault(false);

   }
}
