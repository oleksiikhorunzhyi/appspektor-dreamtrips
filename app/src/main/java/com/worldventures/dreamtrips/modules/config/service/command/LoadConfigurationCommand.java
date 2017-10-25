package com.worldventures.dreamtrips.modules.config.service.command;

import android.text.TextUtils;

import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.service.command.api_action.MappableApiActionCommand;
import com.worldventures.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.config.GetConfigAction;
import com.worldventures.dreamtrips.modules.config.model.Configuration;

import java.util.Locale;

import javax.inject.Inject;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class LoadConfigurationCommand extends MappableApiActionCommand<GetConfigAction, Configuration, Configuration> {

   @Inject SessionHolder sessionHolder;

   @Override
   protected Class<Configuration> getMappingTargetClass() {
      return Configuration.class;
   }

   @Override
   protected Object mapHttpActionResult(GetConfigAction httpAction) {
      return httpAction.response();
   }

   @Override
   protected GetConfigAction getHttpAction() {
      GetConfigAction action = new GetConfigAction();
      action.setAppLanguageHeader(LocaleHelper.formatLocale(getLocale()));
      return action;
   }

   private Locale getLocale() {
      if (sessionHolder.get().isPresent() && !TextUtils.isEmpty(sessionHolder.get().get().getLocale())) {
         return LocaleHelper.buildFromLanguageCode(sessionHolder.get().get().getLocale());
      } else {
         return Locale.getDefault();
      }
   }

   @Override
   protected Class<GetConfigAction> getHttpActionClass() {
      return GetConfigAction.class;
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.smth_went_wrong;
   }
}
