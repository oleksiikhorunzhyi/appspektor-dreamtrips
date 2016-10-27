package com.worldventures.dreamtrips.core.api.action;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public abstract class ApiActionCommand<Action extends com.worldventures.dreamtrips.api.api_common.BaseHttpAction, T>
      extends BaseApiActionCommand<Action, T, T> {
}
