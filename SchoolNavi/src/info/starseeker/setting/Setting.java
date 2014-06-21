/**
 * 全機能の設定情報を更新するアクティビティ
 * @author yuzu, Tsukamoto
 */
package info.starseeker.setting;

import info.starseeker.customwidget.MultiDirectionSlidingDrawer;
import info.starseeker.customwidget.MultiDirectionSlidingDrawer.OnDrawerCloseListener;
import info.starseeker.customwidget.MultiDirectionSlidingDrawer.OnDrawerOpenListener;
import info.starseeker.datacontroller.PreferenceInfo;
import info.starseeker.network.NetworkStatus;
import info.starseeker.otheractivity.MyActivity;
import info.starseeker.schoolnavi.CheckClassLoader;
import info.starseeker.schoolnavi.R;
import info.starseeker.schoolnavi.TimetableInterface;
import info.starseeker.schoolnavi.TimetableLoader;
import info.starseeker.sqlite.TimetableDataController;
import info.starseeker.timetableService.TimetableInfo;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class Setting extends MyActivity implements OnClickListener, TimetableInterface,OnDrawerOpenListener,OnDrawerCloseListener ,OnCheckedChangeListener
{
	private View allcover;
	private MultiDirectionSlidingDrawer mDrawer;
	private PreferenceInfo pi;

	//通信チェック用の定数
	private static final int CHECK = 0;
	private static final int TimeT = 1;

	//Loader用の定数
	private static final int CLASS = 0;
	private static final int TIMETABLE = 2;

	// 通信中のダイアログ
	private ProgressDialog progressDialog;

	private LoaderManager lm = getLoaderManager();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// setContentViewの前でタイトル非表示
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		//プリファレンス準備
		pi = new PreferenceInfo(this);

		mDrawer = (MultiDirectionSlidingDrawer)findViewById(R.id.drawer);
		mDrawer.setOnDrawerOpenListener(this);
		mDrawer.setOnDrawerCloseListener(this);

		findViewById(R.id.menuListBtn).setOnClickListener(this);
		findViewById(R.id.setting_class_number_layout).setOnClickListener(this);

		// SideMenuでの画面遷移のListenerのセット
		findViewById(R.id.sideCal).setOnClickListener(this);
		findViewById(R.id.sideNews).setOnClickListener(this);
		findViewById(R.id.sideSCR).setOnClickListener(this);
		findViewById(R.id.sideHome).setOnClickListener(this);
		findViewById(R.id.sideTT).setOnClickListener(this);

		allcover = findViewById(R.id.allcover);
		allcover.setOnClickListener(this);
		
		setfindView();
		
		LinearLayout LL = (LinearLayout)findViewById(R.id.sideSetting);
		LL.setBackgroundColor(Color.rgb(51,51,51));
		LL.setClickable(false);
	}
	@Override
	public void onResume()
	{
		super.onResume();
		setfindView();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is
		// present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode==KeyEvent.KEYCODE_BACK)
		{
			if (mDrawer.isOpened())
				mDrawer.animateClose();
			else
				this.finish();
			return true;
		}
		return false;
	}

	@Override
	public void onCheckedChanged(CompoundButton view, boolean isChecked)
	{
		
		int id = view.getId();
		
		switch(id)
		{
		case R.id.notifies:
			pi.setNotifies(isChecked);
			break;
		}
	}

	@Override
	public void onClick(View v)
	{
		Intent intent = null;
		int id = v.getId();
		if(id == R.id.menuListBtn)
		{
			if( !mDrawer.isOpened() )
				mDrawer.animateOpen();
		}
		else if(v == allcover)
		{
			mDrawer.animateClose();
		}
		else if(id == R.id.setting_class_number_layout)
		{
			showClassNumberSettingDialog();
		}
		else
		{
			switch(id)
			{
				case R.id.sideCal :
				case R.id.sideNews :
				case R.id.sideSCR :
				case R.id.sideHome :
				case R.id.sideTT :
					intent = new Intent(getIntent(id, this));
					break;
			}
			try
			{
				if (mDrawer.isOpened())
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

	private void showClassNumberSettingDialog()
	{
		class Dialog extends DialogFragment
		{
			int grade = 1;
			int classname = 0;
			int course = 0;
			String classname_s = "";
			String course_s = "";
			String[] nums_class = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
			String[] nums_course = {"SI","SN","KS","KN","KW","JT","JN","IS","IN"};

			@Override
			public AlertDialog onCreateDialog(Bundle savedInstanceState)
			{
				LayoutInflater inflater  = getActivity().getLayoutInflater();
				View view = inflater.inflate(R.layout.dialog, null, false);

				//学年選択のNumberPicker
				NumberPicker np = (NumberPicker)view.findViewById(R.id.numberPicker);
				np.setMaxValue(4);
				np.setMinValue(1);
				np.setOnValueChangedListener(new OnValueChangeListener()
				{
				    @Override
				    public void onValueChange(NumberPicker picker, int oldVal, int newVal)
				    {
				        //入力値が変更されるたびに入れたい処理
				    	grade = newVal;
				    }
				});

				//クラス選択のNumberPicker
				NumberPicker np2 = (NumberPicker)view.findViewById(R.id.numberPicker2);
				np2.setMaxValue(nums_class.length - 1);
				np2.setMinValue(0);
				np2.setDisplayedValues(nums_class);
				np2.setOnValueChangedListener(new OnValueChangeListener()
				{
				    @Override
				    public void onValueChange(NumberPicker picker, int oldVal, int newVal)
				    {
				        //入力値が変更されるたびに入れたい処理
				    	classname = newVal;
				    }
				});

				//コース選択のNumberPicker
				NumberPicker np3 = (NumberPicker)view.findViewById(R.id.numberPicker3);
				np3.setMaxValue(nums_course.length - 1);
				np3.setMinValue(0);
				np3.setDisplayedValues(nums_course);
				np3.setOnValueChangedListener(new OnValueChangeListener()
				{
				    @Override
				    public void onValueChange(NumberPicker picker, int oldVal, int newVal)
				    {
				        //入力値が変更されるたびに入れたい処理
				    	course = newVal;
				    }
				});

				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				//builder.setTitle("Number Picker");
				builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						classname_s = nums_class[classname];
						course_s = nums_course[course];
						AlertDialog.Builder checkDialog = new AlertDialog.Builder(getActivity());
						checkDialog.setTitle("確認");
						checkDialog.setMessage(grade + classname_s + course_s + "\nでよろしいですか?");
						checkDialog.setPositiveButton("はい", new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								//プリファレンスの書き換え
								pi.setClass(grade, classname_s, course_s);

								TextView classNumber = (TextView)findViewById(R.id.setting_class_number);
								classNumber.setText(pi.getClassNumber());
								checkConnect(CHECK);
							}
						});
						checkDialog.setNegativeButton("いいえ", new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
							}
						});
						checkDialog.setCancelable(false);
						checkDialog.show();
					}
				});
				builder.setView(view);
				return builder.create();
			}
		}//end Dialog Class

		Dialog dialog = new Dialog();
		dialog.setCancelable(true);
		dialog.show(getFragmentManager(), "span_dialog");
	}

	@Override
	public void timetableLoaderCallbacks(Loader<ArrayList<TimetableInfo>> tts, ArrayList<TimetableInfo> ttss) {
		TimetableDataController ttdc = new TimetableDataController(Setting.this);
		ttdc.setTimeTable(ttss);
		progressDialog.dismiss();
	}

	@Override
	public void checkclassLoaderCallbacks(Loader<Boolean> lcheck, Boolean bcheck)
	{
		progressDialog.dismiss();
		if(bcheck)
			checkConnect(TimeT);
		else
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("クラスがありません。");
			builder.setMessage("クラスが見つかりませんでした。" +
								"\nもう一度設定してください。");
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					showClassNumberSettingDialog();
				}
			});
			builder.show();
		}
	}

	/**
	 * 通信ステータスを取得確認し、通信可能ならローダーをスタートする
	 */
	public void checkConnect(int id)
	{
		NetworkStatus ns = new NetworkStatus(this);

		int grade = pi.getGrade();
		String classname = pi.getClassName();
		String course = pi.getCourse();

		if(ns.getNetworsStatus())
		{
			//通信ができる状態の時の処理
			if (id == CHECK)
			{
				progressDialog = new ProgressDialog(Setting.this);
				progressDialog.setMessage("クラスを確認しています。");
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressDialog.setCancelable(false);
				progressDialog.show();
				lm.restartLoader(CLASS, null, new CheckClassLoader(Setting.this, Setting.this, grade, classname, course));
			}
			else if (id == TimeT)
			{
				progressDialog = new ProgressDialog(Setting.this);
				progressDialog.setMessage("データを取得しています");
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressDialog.setCancelable(false);
				progressDialog.show();
				lm.initLoader(TIMETABLE, null, new TimetableLoader(Setting.this, Setting.this, grade, classname, course));
			}
		}
		else
		{
			errorConnectDialog(id);
		}
	}
	/**
	 * 通信ステータスがエラーの時に再度通信を促すダイアログを表示するメソッド
	 */
	public void errorConnectDialog(final int id)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("通信エラー");
		builder.setMessage("ネットワークに接続できませんでした\n" +
				"電波状態を確認して再実行してください");
		builder.setPositiveButton("再実行", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				checkConnect(id);
			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				Toast.makeText(getApplicationContext(), "予定が取得できませんでした", Toast.LENGTH_LONG).show();
			}
		});
		builder.setCancelable(false);
		builder.show();
	}
	
	public void setfindView()
	{
		TextView classNumber = (TextView)findViewById(R.id.setting_class_number);
		classNumber.setText(pi.getClassNumber());
		
		Switch notifies = (Switch)findViewById(R.id.notifies);
		notifies.setOnCheckedChangeListener(this);
		notifies.setChecked(pi.getNotifies());
	}

}