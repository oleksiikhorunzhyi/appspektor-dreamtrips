package com.worldventures.dreamtrips.wallet.ui.settings.removecards;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;

import com.techery.spares.adapter.BaseDelegateAdapter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.selectable.SingleSelectionManager;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.common.cell.SectionDividerCell;
import com.worldventures.dreamtrips.wallet.ui.settings.common.cell.SettingsRadioCell;
import com.worldventures.dreamtrips.wallet.ui.settings.common.model.SectionDividerModel;
import com.worldventures.dreamtrips.wallet.ui.settings.common.model.SettingsRadioModel;
import com.worldventures.dreamtrips.wallet.ui.settings.common.provider.AutoClearSmartCardItemProvider;

import butterknife.InjectView;

public class WalletAutoClearCardsScreen extends WalletLinearLayout<WalletAutoClearCardsPresenter.Screen, WalletAutoClearCardsPresenter, WalletAutoClearCardsPath>
      implements WalletAutoClearCardsPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.recycler_view) RecyclerView recyclerView;

   private final AutoClearSmartCardItemProvider itemProvider = new AutoClearSmartCardItemProvider();
   private SingleSelectionManager selectionManager;

   public WalletAutoClearCardsScreen(Context context) {
      super(context);
   }

   public WalletAutoClearCardsScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @NonNull
   @Override
   public WalletAutoClearCardsPresenter createPresenter() {
      return new WalletAutoClearCardsPresenter(getContext(), getInjector());
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      toolbar.setNavigationOnClickListener(v -> onNavigationClick());
   }

   @Override
   protected void onAttachedToWindow() {
      prepareRecyclerView();
      super.onAttachedToWindow();
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return new DialogOperationScreen(this);
   }

   @Override
   public void selectedTime(long millis) {
      selectionManager.setSelection(itemProvider.getPositionForValue(millis) + 1, true); // +1 is Header
   }

   private void onNavigationClick() {
      presenter.goBack();
   }

   private void prepareRecyclerView() {
      BaseDelegateAdapter adapter = new BaseDelegateAdapter(getContext(), getInjector());
      adapter.registerCell(SectionDividerModel.class, SectionDividerCell.class);
      adapter.registerCell(SettingsRadioModel.class, SettingsRadioCell.class, new SettingsRadioCell.Delegate() {
         @Override
         public boolean isLast(int position) {
            return adapter.getCount() - 1 == position;
         }

         @Override
         public void onCellClicked(SettingsRadioModel model) {
            getPresenter().onTimeSelected(model.getValue());

         }
      });
      adapter.addItem(new SectionDividerModel(R.string.wallet_settings_clear_flye_card_description));
      adapter.addItems(itemProvider.items());
      selectionManager = new SingleSelectionManager(recyclerView);
      recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
      recyclerView.setAdapter(selectionManager.provideWrappedAdapter(adapter));
   }

   @Override
   protected boolean hasToolbar() {
      return true;
   }
}
