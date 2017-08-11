package com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants;

import android.content.Context;

import com.techery.spares.module.Injector;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.api.dtl.merchants.model.ThinMerchant;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.dtl.service.AttributesInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlLocationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.FilterDataInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.FullMerchantInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.MerchantsInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.PresentationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.MerchantsAction;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import edu.emory.mathcs.backport.java.util.Collections;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class DtlMerchantsPresenterImplTest {

   private DtlMerchantsPresenterImpl presenter;

   @Mock Context context;
   @Mock Injector injector;
   @Mock DtlMerchantsScreen screen;
   @Mock FilterDataInteractor filterDataInteractor;
   @Mock MerchantsInteractor merchantInteractor;
   @Mock DtlLocationInteractor locationInteractor;
   @Mock FullMerchantInteractor fullMerchantInteractor;
   @Mock PresentationInteractor presentationInteractor;
   @Mock SessionHolder<UserSession> appSessionHolder;
   @Mock AttributesInteractor attributesInteractor;

   @Mock MerchantsAction action;
   @Mock ThinMerchant merchant;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);
      presenter = new DtlMerchantsPresenterImpl(context, injector);
      presenter.attachView(screen);
      presenter.filterDataInteractor = filterDataInteractor;
      presenter.merchantInteractor = merchantInteractor;
      presenter.locationInteractor = locationInteractor;
      presenter.fullMerchantInteractor = fullMerchantInteractor;
      presenter.presentationInteractor = presentationInteractor;
      presenter.appSessionHolder = appSessionHolder;
      presenter.attributesInteractor = attributesInteractor;
   }

   @Test
   public void itShouldCall_SearchMerchantType() {
      List<String> merchantType = new ArrayList<>();
      presenter.setMerchantType(merchantType);
      verify(filterDataInteractor).searchMerchantType(merchantType);
   }

   @Test
   public void itShouldClearMerchants() {
      when(action.isRefresh()).thenReturn(true);
      presenter.onStartMerchantsLoad(action);
      verify(screen).clearMerchants();
   }

   @Test
   public void itShouldNotClearMerchants() {
      when(action.isRefresh()).thenReturn(false);
      presenter.onStartMerchantsLoad(action);
      verifyZeroInteractions(screen);
   }

   @Test
   public void itShouldRefreshSuccess() {
      when(action.isRefresh()).thenReturn(true);
      when(action.merchants()).thenReturn(Collections.singletonList(merchant));
      presenter.onMerchantsLoaded(action);
      verify(screen).onRefreshSuccess();
   }

   @Test
   public void itShouldLoadNextSuccess() {
      when(action.isRefresh()).thenReturn(false);
      when(action.merchants()).thenReturn(Collections.singletonList(merchant));
      presenter.onMerchantsLoaded(action);
      verify(screen).onLoadNextSuccess();
   }

   @Test
   public void itShouldRefreshProgress() {
      when(action.isRefresh()).thenReturn(true);
      presenter.onMerchantsLoading(action, 10);
      verify(screen).onRefreshProgress();
   }

   @Test
   public void itShouldLoadNextProgress() {
      when(action.isRefresh()).thenReturn(false);
      presenter.onMerchantsLoading(action, 10);
      verify(screen).onLoadNextProgress();
   }

   @Test
   public void itShouldSetRefreshedItems() {
      when(action.isRefresh()).thenReturn(false);
      when(action.merchants()).thenReturn(Collections.singletonList(merchant));
      presenter.onMerchantsLoadingError(action, new Exception());
      verify(screen).setRefreshedItems(Collections.singletonList(merchant));
      verify(screen).onLoadNextError();
   }

   @Test
   public void itShouldRefreshError() {
      when(action.isRefresh()).thenReturn(true);
      when(action.getErrorMessage()).thenReturn("error");
      presenter.onMerchantsLoadingError(action, new Exception());
      verify(screen).onRefreshError("error");
   }

   @Test
   public void itShouldCall_RequestAmenities() {
      List<String> merchantType = new ArrayList<>();
      presenter.loadAmenities(merchantType);
      verify(attributesInteractor).requestAmenities(merchantType);
   }
}