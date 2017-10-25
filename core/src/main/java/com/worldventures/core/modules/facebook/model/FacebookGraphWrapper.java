package com.worldventures.core.modules.facebook.model;

import com.facebook.GraphResponse;

public class FacebookGraphWrapper {

   private GraphResponse graphResponse;

   public FacebookGraphWrapper(GraphResponse graphResponse) {
      this.graphResponse = graphResponse;
   }

   public GraphResponse getGraphResponse() {
      return graphResponse;
   }

   public void setGraphResponse(GraphResponse graphResponse) {
      this.graphResponse = graphResponse;
   }
}
