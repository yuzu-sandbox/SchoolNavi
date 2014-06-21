package info.starseeker.calendar.service;

import info.starseeker.schoolnavi.R;
import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class EventAdapter extends ArrayAdapter<EventRow> {
	private ArrayList<EventRow> rows = new ArrayList<EventRow>();
	private LayoutInflater inflater;
	private int layout;

	public EventAdapter(Context context, int textViewResourceId){
		super(context, textViewResourceId);
		this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.layout = textViewResourceId;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View view = convertView;
		if(convertView == null){
			view = this.inflater.inflate(this.layout, null);
		}

		EventRow row = this.rows.get(position);

		((TextView)view.findViewById(R.id.title)).setText(row.getTitle());
		((TextView)view.findViewById(R.id.eventid)).setText(row.getId());
		return view;
	}
	@Override
	public void add(EventRow row){
		super.add(row);
		this.rows.add(row);
	}
}