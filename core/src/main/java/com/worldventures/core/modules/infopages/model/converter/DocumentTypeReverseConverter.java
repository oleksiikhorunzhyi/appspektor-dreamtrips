package com.worldventures.core.modules.infopages.model.converter;

import com.worldventures.core.converter.Converter;
import com.worldventures.core.modules.infopages.service.command.GetDocumentsCommand;
import com.worldventures.dreamtrips.api.documents.model.DocumentType;

import io.techery.mappery.MapperyContext;

public class DocumentTypeReverseConverter implements Converter<GetDocumentsCommand.DocumentType, DocumentType> {

   @Override
   public DocumentType convert(MapperyContext mapperyContext, GetDocumentsCommand.DocumentType documentType) {
      switch (documentType) {
         case HELP:
            return DocumentType.GENERAL;
         case LEGAL:
            return DocumentType.LEGAL;
         case SMARTCARD:
            return DocumentType.SMARTCARD;
      }
      throw new IllegalArgumentException("Unknown document type");
   }

   @Override
   public Class<GetDocumentsCommand.DocumentType> sourceClass() {
      return GetDocumentsCommand.DocumentType.class;
   }

   @Override
   public Class<DocumentType> targetClass() {
      return DocumentType.class;
   }
}
