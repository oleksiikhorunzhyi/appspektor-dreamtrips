package com.messenger.ui.view.conversation;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.PathAttrs;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;

@Layout(R.layout.screen_conversation_list)
public class ConversationsPath extends StyledPath {

   /**
    * don't create instance of this path, use static instance instead provided below
    */
   private ConversationsPath() {
   }

   @Override
   public PathAttrs getAttrs() {
      return PathAttrs.WITH_DRAWER;
   }

   @Override
   public MasterDetailPath getMaster() {
      return MASTER_PATH;
   }

   public static final ConversationsPath MASTER_PATH = new ConversationsPath();
}
