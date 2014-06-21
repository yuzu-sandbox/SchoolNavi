/**
 * TimeTable画面の表示等を管理するクラス
 * @author tanpopo, tsukamoto, yuzu
 */
package info.starseeker.timetable;

import info.starseeker.customwidget.MultiDirectionSlidingDrawer;
import info.starseeker.customwidget.MultiDirectionSlidingDrawer.OnDrawerCloseListener;
import info.starseeker.customwidget.MultiDirectionSlidingDrawer.OnDrawerOpenListener;
import info.starseeker.otheractivity.MyActivity;
import info.starseeker.schoolnavi.R;
import info.starseeker.sqlite.TimetableDataController;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class Timetable extends MyActivity implements OnClickListener,OnDrawerOpenListener,OnDrawerCloseListener
{
	private View allcover;
	private MultiDirectionSlidingDrawer mDrawer;

	private OnClickListener TTSubjectListener;
	@Override protected void onCreate(Bundle savedInstanceState)
	{
		// setContentViewの前でタイトル非表示
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timetable);

		findViewById(R.id.menuListBtn).setOnClickListener(this);

		mDrawer = (MultiDirectionSlidingDrawer)findViewById(R.id.drawer);
		mDrawer.setOnDrawerOpenListener(this);
		mDrawer.setOnDrawerCloseListener(this);

		// SideMenuでの画面遷移のListenerのセット
		findViewById(R.id.sideCal).setOnClickListener(this);
		findViewById(R.id.sideNews).setOnClickListener(this);
		findViewById(R.id.sideHome).setOnClickListener(this);
		findViewById(R.id.sideSetting).setOnClickListener(this);
		findViewById(R.id.sideSCR).setOnClickListener(this);

		allcover = findViewById(R.id.allcover);
		allcover.setOnClickListener(this);

		LinearLayout LL = (LinearLayout)findViewById(R.id.sideTT);
		LL.setBackgroundColor(Color.rgb(51, 51, 51));
		LL.setClickable(false);

		// 何時限目か表示部分
		LinearLayout periodLayout = (LinearLayout)findViewById(R.id.periodLL);
		ArrayList<Integer> periodColor = new ArrayList<Integer>();
		periodColor.add(Color.rgb(47, 164, 231));
		periodColor.add(Color.rgb(199, 28, 34));
		periodColor.add(Color.rgb(115, 168, 57));
		periodColor.add(Color.rgb(3, 60, 115));
		periodColor.add(Color.rgb(221, 86, 0));

		for(int i = 0;i < 5;i++ )
		{
			TextView periodView = new TextView(this);
			periodView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));
			periodView.setGravity(Gravity.CENTER);
			periodView.setTextColor(Color.rgb(255, 255, 255));
			periodView.setTypeface(null, Typeface.BOLD);
			periodView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
			periodView.setBackgroundColor(periodColor.get(i));
			periodView.setText(Integer.toString(i + 1));

			View line = new View(this);
			line.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 3));
			line.setBackgroundColor(Color.rgb(51, 51, 51));

			periodLayout.addView(line);
			periodLayout.addView(periodView);
		}
		showTimeTable();
	}

	@Override public void onResume()
	{
		super.onResume();
		showTimeTable();
	}

	private void showDialog(int period, int week)
	{
		class Dialog extends DialogFragment
		{
			String subject = null;
			String teacher = null;
			String room = null;

			Dialog(int period, int week)
			{
				TimetableDataController ttdc = new TimetableDataController(Timetable.this);
				SubjectInfo si = ttdc.getSubject(period, week);
				if(si != null)
				{
					subject = si.getSubject();
					teacher = si.getTeacher();
					room = si.getRoom();
				}
			}
			@Override public AlertDialog onCreateDialog(Bundle savedInstanceState)
			{
				LayoutInflater inflater = getActivity().getLayoutInflater();
				View view = inflater.inflate(R.layout.tt_subjectdialog, null, false);

				// 内容を入力
				((TextView)view.findViewById(R.id.subjectText)).setText(subject);
				((TextView)view.findViewById(R.id.teacherText)).setText(teacher);
				((TextView)view.findViewById(R.id.roomText)).setText(room);

				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				// タイトルに予定の日付を設定
				// builder.setTitle("");
				builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
				{
					@Override public void onClick(DialogInterface dialog, int which)
					{
						//何もしない
					}
				});
				builder.setView(view);
				return builder.create();
			}
		}
		Dialog dialog = new Dialog(period, week);
		dialog.setCancelable(true);
		dialog.show(getFragmentManager(), "span_dialog");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is
		// present.
		//TODO: このコメント意味調べてないけど意味あるの？ by yuzu
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			if(mDrawer.isOpened())
				mDrawer.animateClose();
			else
				this.finish();
			return true;
		}
		return false;
	}

	@Override
	public void onClick(View v)
	{
		Intent intent = null;
		int id = v.getId();
		if(id == R.id.menuListBtn)
		{
			if(!mDrawer.isOpened())
				mDrawer.animateOpen();
		}
		else if(v == allcover)
		{
			mDrawer.animateClose();
		}
		else
		{
			switch(id)
			{
				case R.id.sideCal :
				case R.id.sideNews :
				case R.id.sideSetting :
				case R.id.sideHome :
				case R.id.sideSCR :
					intent = new Intent(getIntent(v.getId(), this));
					break;
			}
			try
			{
				if(mDrawer.isOpened())
					mDrawer.animateClose();
				startActivity(intent);
			}
			catch(Exception ex)
			{
				Toast.makeText(this, "画面遷移に失敗しました。", Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onDrawerOpened()
	{
		allcover.setVisibility(View.VISIBLE);
	}
	@Override
	public void onDrawerClosed()
	{
		allcover.setVisibility(View.GONE);
	}
	// 時間割表示部分
	public void showTimeTable()
	{
		// 全体のLayoutView変数
		LinearLayout dispLayout = (LinearLayout)findViewById(R.id.time_week_layout);
		if(dispLayout.getChildCount() > 0) // すでに時間割を表示している場合は1度消す。
			dispLayout.removeAllViews();

		TimetableDataController ttdc  = new TimetableDataController(Timetable.this);

		for(int i = 0;i < 5;i++ )// 5日分
		{
			// 1日のLayoutView変数
			LinearLayout day = new LinearLayout(this);
			day.setOrientation(LinearLayout.VERTICAL);
			for(int j = 0;j < 5;j++ )// 5時間分
			{
				// 線を引く
				View hLine = new View(this);
				hLine.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 3));
				hLine.setBackgroundColor(Color.rgb(51, 51, 51));
				day.addView(hLine);

				// 1時間のLayoutView変数
				View child = getLayoutInflater().inflate(R.layout.timetable_daylayout, null);

				TextView class_name = (TextView)child.findViewById(R.id.class_name);
				TextView class_room = (TextView)child.findViewById(R.id.class_room);
				TextView pushWeekDay = (TextView)child.findViewById(R.id.pushWeekDay);
				TextView pushPeriod = (TextView)child.findViewById(R.id.pushPeriod);
				pushWeekDay.setText(Integer.toString(i + 1));
				pushPeriod.setText(Integer.toString(j + 1));

				SubjectInfo si = ttdc.getTimeTable(j+1, i+1);
				if(si != null)
				{
					class_name.setText(si.getSubject());
					class_room.setText(si.getRoom());

					// 教科の詳細ダイアログを開くためのクリックリスナー
					TTSubjectListener = new View.OnClickListener()
					{
						@Override public void onClick(View v)
						{
							TextView period = (TextView)v.findViewById(R.id.pushPeriod);
							TextView weekday = (TextView)v.findViewById(R.id.pushWeekDay);
							int periodI = Integer.parseInt((String)period.getText());
							int weekdayI = Integer.parseInt((String)weekday.getText());
							showDialog(periodI, weekdayI);
						}
					};
					child.setOnClickListener(TTSubjectListener);
				}
				else
				{
					class_name.setText("");
					class_room.setText("");
				}

				LinearLayout.LayoutParams dayparams = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT, 0, 1);
				day.addView(child, dayparams);
			}
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1);
			dispLayout.addView(day, params);

			// 線を引く
			View vLine = new View(this);
			vLine.setLayoutParams(new LinearLayout.LayoutParams(3, LinearLayout.LayoutParams.MATCH_PARENT));
			vLine.setBackgroundColor(Color.rgb(51, 51, 51));
			dispLayout.addView(vLine);
		}
	}
}
