package com.worldventures.dreamtrips.wallet.ui.settings.help.documents.holder;

import com.worldventures.dreamtrips.databinding.AdapterItemDocumentBinding;
import com.worldventures.dreamtrips.wallet.ui.common.adapter.BaseHolder;
import com.worldventures.dreamtrips.wallet.ui.settings.help.documents.model.WalletDocumentModel;

public class DocumentHolder extends BaseHolder<WalletDocumentModel> {

   private final AdapterItemDocumentBinding binding;
   private WalletDocumentModel data;

   DocumentHolder(AdapterItemDocumentBinding binding, Callback callback) {
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
