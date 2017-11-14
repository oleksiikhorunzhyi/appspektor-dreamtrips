package com.worldventures.wallet.ui.settings.help.documents.holder;

import com.worldventures.wallet.databinding.ItemWalletDocumentBinding;
import com.worldventures.wallet.ui.common.adapter.BaseHolder;
import com.worldventures.wallet.ui.settings.help.documents.model.WalletDocumentModel;

public class DocumentHolder extends BaseHolder<WalletDocumentModel> {

   private final ItemWalletDocumentBinding binding;
   private WalletDocumentModel data;

   DocumentHolder(ItemWalletDocumentBinding binding, Callback callback) {
      super(binding.getRoot());
      this.binding = binding;
      binding.getRoot().setOnClickListener(view -> callback.openDocument(data));
   }

   @Override
   public void setData(WalletDocumentModel data) {
      this.data = data;
      this.binding.documentName.setText(data.getName());
   }

   public interface Callback {

      void openDocument(WalletDocumentModel walletDocumentModel);
   }
}
