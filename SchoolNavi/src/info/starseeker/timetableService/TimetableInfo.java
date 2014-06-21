package info.starseeker.timetableService;

public class TimetableInfo
{
	private int week;
	private int period;
	private String subject;
	private String room;
	private String teacher;

	//コンストラクタ
	public TimetableInfo(int week, int period, String subject, String room,  String teacher)
	{
		this.week = week;
		this.period = period;
		this.subject = subject;
		this.room = room;
		this.teacher = teacher;
	}
	
	public int getWeek()
	{
		return this.week;
	}
	
	public int getPeriod()
	{
		return this.period;
	}
	
	public String getSubject()
	{
		return this.subject;
	}
	
	public String getRoom()
	{
		return this.room;
	}
	
	public String getTeacher()
	{
		return this.teacher;
	}
}
