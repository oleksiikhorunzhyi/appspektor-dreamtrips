package com.worldventures.dreamtrips.modules.dtl_flow.parts.transaction_detail;

      import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlLayout;

import butterknife.InjectView;

public class DtlTransactionScreenImpl extends DtlLayout<DtlTransactionScreen, DtlTransactionPresenter, DtlTransactionPath>
      implements DtlTransactionScreen {

   @InjectView(R.id.toolbar_actionbar) Toolbar toolbar;
   @InjectView(R.id.tv_title) TextView tvTitle;

   public DtlTransactionScreenImpl(Context context) {
      super(context);
   }

   public DtlTransactionScreenImpl(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   public DtlTransactionPresenter createPresenter() {
      return new DtlTransactionPresenterImpl(getContext(), injector);
   }

   @Override
   protected void onPostAttachToWindowView() {
      inflateToolbarMenu(toolbar);
      tvTitle.setText(getContext().getResources().getString(R.string.dtl_show_transaction_toolbar));

   }
}
