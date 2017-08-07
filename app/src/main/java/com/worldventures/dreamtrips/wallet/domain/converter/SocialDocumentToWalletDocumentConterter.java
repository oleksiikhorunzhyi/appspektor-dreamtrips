package com.worldventures.dreamtrips.wallet.domain.converter;

import com.worldventures.dreamtrips.modules.infopages.model.Document;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.wallet.ui.settings.common.model.WalletDocument;

import io.techery.mappery.MapperyContext;

public class SocialDocumentToWalletDocumentConterter implements Converter<Document, WalletDocument> {

   @Override
   public WalletDocument convert(MapperyContext mapperyContext, Document document) {
      return new WalletDocument(
            document.getName(),
            document.getOriginalName(),
            document.getUrl()
      );
   }

   @Override
   public Class<Document> sourceClass() {
      return Document.class;
   }

   @Override
   public Class<WalletDocument> targetClass() {
      return WalletDocument.class;
   }
}
