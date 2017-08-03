package com.worldventures.dreamtrips.wallet.ui.settings.help.documents.holder;

import com.worldventures.dreamtrips.databinding.AdapterItemDocumentBinding;
import com.worldventures.dreamtrips.wallet.ui.settings.common.model.WalletDocument;
import com.worldventures.dreamtrips.wallet.ui.common.adapter.BaseHolder;

public class DocumentHolder extends BaseHolder<WalletDocument> {

   private final AdapterItemDocumentBinding binding;
   private final Callback callback;

   public DocumentHolder(AdapterItemDocumentBinding binding, Callback callback) {
      super(binding.getRoot());
      this.binding = binding;
      this.callback = callback;
   }

   @Override
   public void setData(WalletDocument data) {
      binding.documentName.setText(data.getName());
      binding.documentName.setOnClickListener(view -> {
         callback.openDocument(data);
      });
   }

   public interface Callback {

      void openDocument(WalletDocument walletDocument);
   }
}
