package info.starseeker.calendar.service;

public class EventRow {
	private String title;
	private String id;
	public EventRow(String title, String id){
		this.title = title;
		this.id = id;
	}
	public String getTitle() {
		return this.title;
	}
	public String getId() {
		return this.id;
	}
}