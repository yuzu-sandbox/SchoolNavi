package info.starseeker.schoolnavi;

import android.content.Loader;

public interface NewsInterface
{
	/**
	 * ニュースの通信処理が終わった時に呼び出されるメソッド
	 * @param loadernews NewsLoaderの通信情報
	 * @param newstitle 取得したNewsのタイトル
	 */
	public void newsLoaderCallbacks(Loader<String> newsl, String newstitle);
}
