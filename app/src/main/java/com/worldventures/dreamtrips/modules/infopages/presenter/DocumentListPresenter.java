package com.worldventures.dreamtrips.modules.infopages.presenter;

import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.infopages.model.Document;

import java.util.List;

public class DocumentListPresenter extends Presenter<DocumentListPresenter.View> {

   public interface View extends Presenter.View {

      void setDocumentList(List<Document> documentList);
   }
}
