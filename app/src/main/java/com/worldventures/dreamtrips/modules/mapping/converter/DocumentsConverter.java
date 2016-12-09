package com.worldventures.dreamtrips.modules.mapping.converter;

import com.worldventures.dreamtrips.modules.infopages.model.Document;

import io.techery.mappery.MapperyContext;

public class DocumentsConverter implements Converter<com.worldventures.dreamtrips.api.documents.model.Document, Document> {

   @Override
   public Class<com.worldventures.dreamtrips.api.documents.model.Document> sourceClass() {
      return com.worldventures.dreamtrips.api.documents.model.Document.class;
   }

   @Override
   public Class<Document> targetClass() {
      return Document.class;
   }

   @Override
   public Document convert(MapperyContext mapperyContext, com.worldventures.dreamtrips.api.documents.model.Document document) {
      return new Document(document.id(), document.name(), document.url());
   }
}
