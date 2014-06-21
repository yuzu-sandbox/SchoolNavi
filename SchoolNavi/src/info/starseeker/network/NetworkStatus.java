package info.starseeker.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkStatus
{
	Context context;
	public NetworkStatus(Context context)
	{
		this.context = context;
	}
	public boolean getNetworsStatus()
	{
		boolean check = false;
		
		ConnectivityManager cm = (ConnectivityManager)context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo network = cm.getActiveNetworkInfo();
		
		if(network != null)
		{
			check = network.isConnected();
		}
		return check;
	}
}
