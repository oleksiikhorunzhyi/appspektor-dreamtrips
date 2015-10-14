package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.os.Bundle;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;
import com.worldventures.dreamtrips.modules.dtl.model.DtlTransactionResult;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlTransactionSucceedPresenter;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_transaction_succeed)
public class DtlTransactionSucceedFragment extends BaseFragmentWithArgs<DtlTransactionSucceedPresenter, DtlPlace>
        implements DtlTransactionSucceedPresenter.View {

    @InjectView(R.id.congratulations)
    TextView congratulations;

    @Override
    public void setCongratulations(DtlTransactionResult result) {
        congratulations.setText(result.generateSuccessMessage(getResources()));
    }

    @OnClick(R.id.done)
    void onDoneClicked() {
        getActivity().finish();
    }

    @Override
    protected DtlTransactionSucceedPresenter createPresenter(Bundle savedInstanceState) {
        return new DtlTransactionSucceedPresenter(getArgs());
    }
}
