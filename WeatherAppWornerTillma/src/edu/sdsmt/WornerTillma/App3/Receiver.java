package edu.sdsmt.WornerTillma.App3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * This class is shows Toast
 * 
 * <p>
 * <div style="font-weight:bold">
 * Description:
 * </div>
 * 		<div style="padding-left:3em">
 * 		This class handels the showing of toast. In this app, it is used when an exception is
 * 		caught. The message used is specific to the exception that was caught.
 * 		</div>
 * </p>
 * 
 * @since November 10, 2013
 * @author James Tillma and Teresa Worner
 */
public class Receiver extends BroadcastReceiver
{
	private static String message; //holds the message for the toast to display
	private static Toast toast = null; //the Toast object to show
	
	/**
	 * Class's constructor.
	 */
	public Receiver()
	{
	}
	
	/**
	 * Set's the class's message value
	 * @param message The message to set the receiver class's message to be
	 */
	public static void SetMessage(String message)
	{
		Receiver.message = message; //set the class's message
	}
	
	/**
	 * Handles receiving the intent. If toast has already been displayed, it get's canceled
	 * and reset to be null before trying to show toast again.
	 * @param context The context of the toast to show
	 * @intent intent Used to get the action of the intent that was sent.
	 */
	@Override
	public void onReceive( Context context, Intent intent)
	{
		//if toast is being or has been displayed, cancel it and reset the object
		if(toast != null)
		{
			toast.cancel();
			toast = null;
		}
		//if the broadcast is received, show toast
		if(intent.getAction() == Common.ExceptionIntent)
		{
			toast = Toast.makeText(context, Receiver.message, Toast.LENGTH_LONG);
			toast.show();
		}
	}
}
