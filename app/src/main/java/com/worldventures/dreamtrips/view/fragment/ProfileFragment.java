package com.worldventures.dreamtrips.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.utils.Logs;
import com.worldventures.dreamtrips.view.activity.MainActivity;
import com.worldventures.dreamtrips.view.custom.DTEditText;
import com.worldventures.dreamtrips.view.presentation.ProfileFragmentPresentation;

import org.robobinding.ViewBinder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ProfileFragment extends BaseFragment<MainActivity> implements DatePickerDialog.OnDateSetListener, View.OnTouchListener {
    @InjectView(R.id.user_cover)
    ImageView userCover;
    @InjectView(R.id.user_photo)
    ImageView userPhoto;
    @InjectView(R.id.user_photo_2)
    ImageView userPhoto2;
    @InjectView(R.id.user_photo_3)
    ImageView userPhoto3;
    @InjectView(R.id.user_nome)
    TextView userNome;
    @InjectView(R.id.user_email)
    TextView userEmail;
    @InjectView(R.id.et_date_of_birth)
    DTEditText dateOfBirth;
    private ProfileFragmentPresentation presentationModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        presentationModel = new ProfileFragmentPresentation(getAbsActivity());
        ViewBinder viewBinder = getAbsActivity().createViewBinder();
        View view = viewBinder.inflateAndBindWithoutAttachingToRoot(R.layout.fragment_profile, presentationModel, container);
        ButterKnife.inject(this, view);
        userCover.setImageResource(R.drawable.fake_cover);
        userPhoto.setImageResource(R.drawable.fake_avatar);
        dateOfBirth.setOnTouchListener(this);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getAbsActivity().makeActionBarTransparent(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.item_feed:
                informUser("TODO: will be implement");
                return true;
            case R.id.item_logout:
                // do s.th.
                presentationModel.logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnClick(R.id.btn_save)
    public void onSaveClick() {
        informUser("TODO: will be implement");
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        Logs.d("datePicker: ", year + " " + month + " " + day);
       presentationModel.onDataSet(year,month,day);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_UP){
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);
            datePickerDialog.setYearRange(1963, 2015);
            datePickerDialog.show(getAbsActivity().getSupportFragmentManager(), null);
        }
        return false;
    }
}
