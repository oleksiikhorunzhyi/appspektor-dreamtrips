package com.worldventures.dreamtrips.modules.config.service.command

import android.text.TextUtils

import com.worldventures.core.model.session.SessionHolder
import com.worldventures.core.service.command.api_action.MappableApiActionCommand
import com.worldventures.core.utils.LocaleHelper
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.api.config.GetConfigAction
import com.worldventures.dreamtrips.modules.config.model.Configuration

import java.util.Locale

import javax.inject.Inject

import io.techery.janet.command.annotations.CommandAction

@CommandAction
class LoadConfigurationCommand : MappableApiActionCommand<GetConfigAction, Configuration, Configuration>() {

   @Inject lateinit var sessionHolder: SessionHolder

   override fun getMappingTargetClass() = Configuration::class.java

   override fun mapHttpActionResult(httpAction: GetConfigAction) = httpAction.response()

   override fun getHttpAction(): GetConfigAction {
      val action = GetConfigAction()
      action.appLanguageHeader = LocaleHelper.formatLocale(
            if (sessionHolder.get().isPresent && !TextUtils.isEmpty(sessionHolder.get().get().locale())) {
               LocaleHelper.buildFromLanguageCode(sessionHolder.get().get().locale())
            } else Locale.getDefault())
      return action
   }

   override fun getHttpActionClass() = GetConfigAction::class.java

   override fun getFallbackErrorMessage() = R.string.smth_went_wrong
}
