package com.zekunwang.nytimessearch;

import com.zekunwang.nytimessearch.models.Setting;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Switch;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by zwang_000 on 7/23/2016.
 */
public class SettingDialogFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    static final int REQUEST_DATE = 20;
    final String[] ORDERS = {"Off", "Newest", "Oldest"};
    @BindView(R.id.btnBeginDate) Button btnBeginDate;
    @BindView(R.id.swBeginDate) Switch swBeginDate;
    @BindView(R.id.spnSort) Spinner spnSort;
    @BindView(R.id.cbArt) CheckBox cbArt;
    @BindView(R.id.cbFashion) CheckBox cbFashion;
    @BindView(R.id.cbSports) CheckBox cbSports;
    @BindView(R.id.btnSave) Button btnSave;
    Setting setting;
    private Unbinder unbinder;

    // define listener to pass setting to activity
    public interface SettingDialogListener {
        void onFinishSettingDialog(Setting setting);
    }

    public SettingDialogFragment() {}

    public static SettingDialogFragment newInstance(Setting setting) {
        // pass setting to fragment
        SettingDialogFragment settingDialogFragment = new SettingDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("setting", setting);
        settingDialogFragment.setArguments(args);
        return settingDialogFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container);
        // bind fragment with ButterKnife
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
        // set dialog title
        getDialog().setTitle("Setting");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, ORDERS);
        spnSort.setAdapter(adapter);

        // read stored setting
        setting = new Setting((Setting) getArguments().getSerializable("setting"));

        btnBeginDate.setText(setting.beginDate.toString());
        swBeginDate.setChecked(setting.beginDate.year != 0);
        spnSort.setSelection(setting.sort);
        cbArt.setChecked(setting.art);
        cbFashion.setChecked(setting.fashion);
        cbSports.setChecked(setting.sports);

        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @OnClick({R.id.btnBeginDate, R.id.btnSave})
    public void onClick(View v) {
        if (v.getId() == R.id.btnBeginDate) {
            DatePickerFragment newFragment = DatePickerFragment
                    .newInstance(setting.beginDate.year, setting.beginDate.month - 1,
                            setting.beginDate.day);
            // bind parent fragment with child fragment
            newFragment.setTargetFragment(SettingDialogFragment.this, REQUEST_DATE);
            // trigger dialog with getFragmentManager()
            newFragment.show(getFragmentManager(), "datePicker");
        } else {
            if (!swBeginDate.isChecked()) {
                setting.beginDate.year = 0;
            }
            setting.sort = spnSort.getSelectedItemPosition();

            // pass setting to activity via listener
            SettingDialogListener settingDialogListener = (SettingDialogListener) getActivity();
            settingDialogListener.onFinishSettingDialog(setting);
            // close fragment
            dismiss();
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        // pass date dialog data via listener
        setting.beginDate.year = year;
        setting.beginDate.month = monthOfYear + 1;   // read month (0 - 11)
        setting.beginDate.day = dayOfMonth;
        btnBeginDate.setText(setting.beginDate.toString()); // set date button text
        swBeginDate.setChecked(true);   // check date switch
    }

    @OnCheckedChanged({R.id.swBeginDate, R.id.cbArt, R.id.cbFashion, R.id.cbSports})
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == swBeginDate) {
            if (isChecked) {
                if (setting.beginDate.year == 0) {
                    Calendar cal = Calendar.getInstance();
                    setting.beginDate.year = cal.get(Calendar.YEAR);
                    setting.beginDate.month = cal.get(Calendar.MONTH) + 1;
                    setting.beginDate.day = cal.get(Calendar.DAY_OF_MONTH);
                }
                btnBeginDate.setText(setting.beginDate.toString());
            } else {
                btnBeginDate.setText("Off");
            }
        } else if (buttonView == cbArt) {
            setting.art = isChecked;
        } else if (buttonView == cbFashion) {
            setting.fashion = isChecked;
        } else if (buttonView == cbSports) {
            setting.sports = isChecked;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // unbind frrament and ButterKnife
        unbinder.unbind();
    }
}
