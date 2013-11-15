package edu.sdsmt.WornerTillma.App3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class Receiver extends BroadcastReceiver
{
	private static String message;

	public Receiver()
	{
	}
	
	public static void SetMessage(String message)
	{
		Receiver.message = message;
	}
	
	@Override
	public void onReceive( Context context, Intent intent)
	{
		if(intent.getAction() == Common.ExceptionIntent)
			Toast.makeText(context, Receiver.message, Toast.LENGTH_LONG).show();
	}
}
