package info.starseeker.timetable;

public class SubjectInfo
{
	private String subject;
	private String room;
	private String teacher;

	//コンストラクタ
	public SubjectInfo(String subject, String room, String teacher)
	{
		this.subject = subject;
		this.room = room;
		this.teacher = teacher;
	}
	
	//教科取得
	public String getSubject()
	{
		return this.subject;
	}
	
	//教室取得
	public String getRoom()
	{
		return room;
	}
	
	//教師取得
	public String getTeacher()
	{
		return teacher;
	}
}
