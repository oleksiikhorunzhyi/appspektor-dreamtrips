package com.worldventures.dreamtrips.wallet.ui.records.tokenize_migration;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.service.command.TokenizeRecordsMigrationCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.DialogErrorView;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.widget.WalletProgressWidget;

import butterknife.InjectView;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;

public class TokenizeRecordsMigrationScreen extends WalletLinearLayout<TokenizeRecordsMigrationPresenter.Screen, TokenizeRecordsMigrationPresenter, TokenizeRecordsMigrationPath>
      implements TokenizeRecordsMigrationPresenter.Screen {

   @InjectView(R.id.tokenize_migration_progress) WalletProgressWidget migrationProgress;
   @InjectView(R.id.tokenize_migration_cards_count) TextView migrationCardsCount;
   @InjectView(R.id.toolbar) Toolbar toolbar;

   public TokenizeRecordsMigrationScreen(Context context) {
      super(context);
   }

   public TokenizeRecordsMigrationScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @NonNull
   @Override
   public TokenizeRecordsMigrationPresenter createPresenter() {
      return new TokenizeRecordsMigrationPresenter(getContext(), getInjector());
   }

   @Override
   public OperationScreen provideOperationDelegate() { return new DialogOperationScreen(this); }

   @Override
   public void showMigrateCardsCount(int count) {
      migrationCardsCount.setText(getResources().getQuantityString(R.plurals.wallet_tokenize_migration_sub_text, count, count));
   }

   @Override
   protected void onFinishInflate() {
      supportConnectionStatusLabel(false);
      super.onFinishInflate();
      toolbar.setNavigationIcon(new ColorDrawable(Color.TRANSPARENT));
      migrationProgress.start();
   }

   @Override
   protected boolean hasToolbar() {
      return true;
   }

   @Override
   public OperationView<TokenizeRecordsMigrationCommand> provideOperationView() {
      return new ComposableOperationView<>(ErrorViewFactory.<TokenizeRecordsMigrationCommand>builder()
            .defaultErrorView(new DialogErrorView<TokenizeRecordsMigrationCommand>(getContext()) {
               @Override
               protected Dialog createDialog(TokenizeRecordsMigrationCommand command, Throwable throwable, Context context) {
                  return new MaterialDialog.Builder(getContext())
                        .title(R.string.wallet_tokenize_migration_error_alert_title)
                        .content(createDialogContentText())
                        .positiveText(R.string.retry)
                        .onPositive((dialog, which) -> getPresenter().retry())
                        .negativeText(R.string.cancel)
                        .onNegative((dialog, which) -> getPresenter().cancelMigration())
                        .cancelable(false)
                        .build();
               }
            })
            .build()
      );
   }

   private CharSequence createDialogContentText() {
      SpannableString supportPhoneNumber = new SpannableString(getString(R.string.wallet_firmware_install_customer_support_phone_number));
      supportPhoneNumber.setSpan(new StyleSpan(Typeface.BOLD), 0, supportPhoneNumber.length(), 0);
      supportPhoneNumber.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.wallet_alert_phone_number_color)), 0, supportPhoneNumber
            .length(), 0);
      Linkify.addLinks(supportPhoneNumber, Linkify.PHONE_NUMBERS);

      return new SpannableStringBuilder()
            .append(getString(R.string.wallet_tokenize_migration_error_alert_content))
            .append("\n\n")
            .append(getString(R.string.wallet_firmware_install_error_alert_content_customer_support))
            .append("\n")
            .append(supportPhoneNumber);
   }

}