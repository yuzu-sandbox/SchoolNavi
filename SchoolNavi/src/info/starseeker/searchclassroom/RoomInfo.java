package info.starseeker.searchclassroom;

public class RoomInfo
{
	String rmname;
	int pcrm;
	
	public RoomInfo(String rmname, int pcrm)
	{
		this.rmname = rmname;
		this.pcrm = pcrm;
	}
	
	public String getRoomName()
	{
		return rmname;
	}
	
	public int getPcRoom()
	{
		return pcrm;
	}
}
