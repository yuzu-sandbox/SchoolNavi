package info.starseeker.customwidget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

public class AutoScrollView extends HorizontalScrollView {

	private final static String STR_ATTR_FRAME_INTERVAL = "frameInterval";	//フレーム間の時間
	private final static String STR_ATTR_FRAME_DELTA = "frameDelta";		//フレーム間の移動量
	private final static String STR_ATTR_LAPEL_INTERVAL = "lapelInterval";	//折り返す時のフレーム間隔
	private final static String STR_ATTR_IS_LOOP = "isLoop";				//ループするか
	private final static String STR_ATTR_LOOP_INTERVAL = "loopInterval";	//ループするときのフレーム間隔

	private Handler _handlerAnimation = null;		//アニメーション用
	private int _frameInterval = 100;				//フレーム間の時間
	private int _frameDelta = 2;					//フレーム間の移動量
	private int _lapelInterval = 500;				//折り返す時のフレーム間隔
	private boolean _isDerectionLeft = true;		//左へ動いているか
	private int _prev_x = 0;						//前回の場所

	private boolean _isLoop = false;				//ループするか
	private int _loopInterval = 500;				//ループする時のフレーム間隔

	private String _innerTextBackup = "";			//元々の文字列のバックアップ
	private int _innerTextWidth = 0;				//現在の文字列の長さ（2個分）

	/**
	 * 描画スレッド
	 */
	private final Runnable _runAnimationThread = new Runnable(){
		public void run(){

			updateAutoScroll();

		}
	};

	/**
	 * アニメーション用のハンドラ
	 * @return
	 */
	private Handler getHandlerAnimation(){
		if(_handlerAnimation == null){
			_handlerAnimation = new Handler();
		}
		return _handlerAnimation;
	}

	/**
	 * 子供のテキストビューを取得する
	 * @return
	 */
	private TextView getInnerTextView(){
		TextView tv = null;
		try{
			View v = getChildAt(0);
			if(v.getClass() == TextView.class){
				tv = (TextView)v;
			}
		}catch(Exception e){
		}
		return tv;
	}

	/**
	 * 横スクロールするテキストビューのコンストラクタ
	 * @param context
	 * @param attrs
	 */
	public AutoScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);

		setSmoothScrollingEnabled(true);

		String temp = null;

		//フレーム間の時間
		temp = attrs.getAttributeValue(null, STR_ATTR_FRAME_INTERVAL);
		if(temp != null){
			_frameInterval = Integer.valueOf(temp);
		}
		//フレーム間の移動量
		temp = attrs.getAttributeValue(null, STR_ATTR_FRAME_DELTA);
		if(temp != null){
			_frameDelta = Integer.valueOf(temp);
		}
		//折り返し時のフレーム間隔
		temp = attrs.getAttributeValue(null, STR_ATTR_LAPEL_INTERVAL);
		if(temp != null){
			_lapelInterval = Integer.valueOf(temp);
		}

		//ループ時のフレーム間隔
		temp = attrs.getAttributeValue(null, STR_ATTR_LOOP_INTERVAL);
		if(temp != null){
			_loopInterval = Integer.valueOf(temp);
		}
		//ループするか
		temp = attrs.getAttributeValue(null, STR_ATTR_IS_LOOP);
		if(temp != null){
			_isLoop = Boolean.valueOf(temp);
		}
	}


	/**
	 * 表示状態が変わった
	 */
	@Override
	protected void onWindowVisibilityChanged(int visibility) {
		super.onWindowVisibilityChanged(visibility);

		if(visibility == View.VISIBLE){
			startAutoScroll();
		}else{
			stopAutoScroll();
		}
	}

	/**
	 * 手動でスクロールできないようにする
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		return true;//super.onTouchEvent(ev);
	}

	/**
	 * 自動スクロールを開始する
	 */
	public void startAutoScroll(){
		//監視を開始
		getHandlerAnimation().postDelayed(_runAnimationThread, _frameInterval);
	}

	/**
	 * 自動スクロールを止める
	 */
	public void stopAutoScroll(){
		//停止する
		getHandlerAnimation().removeCallbacks(_runAnimationThread);
		//位置を戻す
		scrollTo(0, getScrollY());

	}

	/**
	 * 1つ目のテキストへ設定する
	 * @param text
	 */
	public void setText(String text){
		if(getInnerTextView() == null){
			//テキストがない
		}else{
			getInnerTextView().setText(text);

			//再スタート
			stopAutoScroll();
			startAutoScroll();
		}
	}

	/**
	 * 1つ目のテキストを取得する
	 * @return
	 */
	public String getText(){
		return _innerTextBackup;
	}

	/**
	 * 自動スクロールの状態更新
	 */
	public void updateAutoScroll(){
		int next_interval = _frameInterval;
		int x = getScrollX();

		if(getChildAt(0) == null){
		}else if(getChildAt(0).getWidth() <= getWidth()){
			//はみ出てない
			next_interval *= 2;	//スクロールの必要が無いので間隔を広げる

			_isDerectionLeft = true;
			_prev_x = 0;
			x = 0;

			_innerTextBackup = "";
			_innerTextWidth = 0;
		}else{
			//はみ出てる

			if(_isLoop && getInnerTextView() != null){
				//ループでかつ中にテキストが含まれる

				//内容を2重にする
				if(_innerTextWidth != 0){
					//既に内容を2重にしてある
				}else{
					//初めて
					_innerTextBackup = (String) getInnerTextView().getText();
					_innerTextWidth = getInnerTextView().getWidth();
					getInnerTextView().setText(_innerTextBackup + _innerTextBackup);
				}

				//左へ
				x += _frameDelta;

				//予定の幅動いたら戻す
				if(_innerTextWidth == x){
					x = 0;
					next_interval = _loopInterval;
				}
			}else{
				//左右移動

				//位置を計算
				if(_isDerectionLeft){
					//左へ
					x += _frameDelta;
				}else{
					//右へ
					x -= _frameDelta;
				}

				//向きを変える
				if(x == _prev_x){
					_isDerectionLeft = !_isDerectionLeft;
					next_interval = _lapelInterval;
				}
			}

			_prev_x = x;

		}

		//移動
		scrollTo(x, getScrollY());

		//次のセット
		getHandlerAnimation().postDelayed(_runAnimationThread, next_interval);
	}

}