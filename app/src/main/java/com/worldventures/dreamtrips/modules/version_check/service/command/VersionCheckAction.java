package com.worldventures.dreamtrips.modules.version_check.service.command;

import com.worldventures.dreamtrips.modules.version_check.model.api.UpdateRequirementDTO;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Response;

// TODO Should be moved to mobile SDK once it is ready
@HttpAction("/ConfigService/api/config/application")
public class VersionCheckAction {

   @Response
   UpdateRequirementDTO updateRequirementDTO;

   public UpdateRequirementDTO getUpdateRequirement() {
      return updateRequirementDTO;
   }
}
