package info.starseeker.news;

public class NewsData
{
	private int color;
	private String day;
	private String from;
	private String text;

	NewsData(int color,String from, String day, String text)
	{
		this.color = color;
		this.from = from;
		this.day = day;
		this.text = text;
	}
	public int getColor()
	{
		return color;
	}
	public String getDay()
	{
		return day;
	}
	public String getFrom()
	{
		return from;
	}
	public String getText()
	{
		return text;
	}
}