package info.starseeker.news;

import info.starseeker.customwidget.CustomTextView;
import info.starseeker.schoolnavi.R;
import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class NewsAdapter extends ArrayAdapter<NewsData> {
	 private LayoutInflater layoutInflater_;

	 public NewsAdapter(Context context, int textViewResourceId, List<NewsData> objects) {
	 super(context, textViewResourceId, objects);
	 layoutInflater_ = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	 }

	 @Override
	 public View getView(int position, View convertView, ViewGroup parent) {
	 // 特定の行(position)のデータを得る
	 NewsData item = (NewsData)getItem(position);

	 // convertViewは使い回しされている可能性があるのでnullの時だけ新しく作る
	 if (null == convertView) {
	 convertView = layoutInflater_.inflate(R.layout.news_row, null);
	 }

	 // NewsDataのデータをViewの各Widgetにセットする
	 View colorView = convertView.findViewById(R.id.rowColor);
	 colorView.setBackgroundColor(item.getColor());

	 TextView fromView = (TextView)convertView.findViewById(R.id.rowFrom);
	 fromView.setText(item.getFrom());

	 TextView dayView = (TextView)convertView.findViewById(R.id.rowDay);
	 dayView.setText(item.getDay());

	 CustomTextView textView = (CustomTextView)convertView.findViewById(R.id.rowText);
	 textView.setMaxLines(1);
	 textView.setText(item.getText());

	 return convertView;
	 }
	}