package com.worldventures.dreamtrips.view.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.balysv.materialripple.MaterialRippleLayout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.view.activity.BaseActivity;
import com.worldventures.dreamtrips.view.presentation.LoginFragmentPresentation;

import org.robobinding.ViewBinder;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LoginFragment extends BaseFragment<BaseActivity> implements LoginFragmentPresentation.View {

    @InjectView(R.id.btn_login)
    Button btnLogin;

    public LoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LoginFragmentPresentation presentationModel = new LoginFragmentPresentation(this,getAbsActivity());
        ViewBinder viewBinder = getAbsActivity().createViewBinder();
        return viewBinder.inflateAndBindWithoutAttachingToRoot(R.layout.fragment_login, presentationModel, container);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.inject(this, view);
        MaterialRippleLayout.on(btnLogin)
                .rippleColor(getResources().getColor(R.color.theme_main_darker))
                .create();
    }

}
