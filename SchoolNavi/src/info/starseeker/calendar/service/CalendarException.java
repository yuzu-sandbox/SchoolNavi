/**
 * カレンダーの例外を扱うクラス
 * @auther yuzu
 */
package info.starseeker.calendar.service;

public class CalendarException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * throwsするときに引数にエラーメッセージを記述してください
	 * @param errorMessege
	 */
	public CalendarException(String errorMessege)
	{
		super(errorMessege);
	}
}
