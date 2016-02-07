package com.example.MultiUserPack;

import java.text.SimpleDateFormat;
import java.util.*;

import com.example.locationassistant.*;
import android.app.*;
import android.os.Bundle;
import android.widget.*;


public class DatePickM extends android.support.v4.app.DialogFragment implements DatePickerDialog.OnDateSetListener 
{	Calendar c;
	SimpleDateFormat sdf;
	public DatePickM()
	{	super();
		c = Calendar.getInstance();
		sdf = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault());
	}
	
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
		return new DatePickerDialog(getActivity(), this, year, month, day);
	}
	
	public void onDateSet(DatePicker view, int year,int month,int day) {
		c.set(Calendar.YEAR,year);
		c.set(Calendar.MONTH,month);
		c.set(Calendar.DAY_OF_MONTH, day);
		AddMultiuserTaskActivity act = (AddMultiuserTaskActivity) getActivity();
		((EditText) act.findViewById(R.id.editTextDate)).setText(sdf.format(c.getTime()));
	}
	
}
