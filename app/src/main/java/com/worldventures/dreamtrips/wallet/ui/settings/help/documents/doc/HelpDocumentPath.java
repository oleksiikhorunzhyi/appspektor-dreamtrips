package com.worldventures.dreamtrips.wallet.ui.settings.help.documents.doc;


import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.modules.infopages.model.Document;

@Layout(R.layout.screen_wallet_help_document)
public class HelpDocumentPath extends StyledPath {

   private final Document model;

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }

   public HelpDocumentPath(Document model) {
      this.model = model;
   }

   public Document getModel() {
      return model;
   }
}
