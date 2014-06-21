package info.starseeker.startup;

import info.starseeker.schoolnavi.MainActivity;
import info.starseeker.schoolnavi.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class Startup extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// タイトルを非表示にします。
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// splash.xmlをViewに指定します。
		setContentView(R.layout.splash);
		Handler hdl = new Handler();
		// 1400ms遅延させてsplashHandlerを実行します。
		hdl.postDelayed(new splashHandler(), 1400);
	}
	class splashHandler implements Runnable {
		public void run() {
			// スプラッシュ完了後に実行するActivityを指定します。
			Intent intent = new Intent(getApplication(), MainActivity.class);
			startActivity(intent);
			// SplashActivityを終了させます。
			Startup.this.finish();
		}
	}
}
