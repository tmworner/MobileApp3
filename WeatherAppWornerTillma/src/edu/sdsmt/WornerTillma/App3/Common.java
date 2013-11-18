package edu.sdsmt.WornerTillma.App3;

/**
 * Contains strings common to several places in the app.
 * 
 * <p>
 * <div style="font-weight:bold">
 * Description:
 * </div>
 * 		<div style="padding-left:3em">
 * 		This class contains 3 strings that are common to several points in the app.
 * 		Using a common file saves some space and makes it closer to a "proper" structure.
 * 		</div>
 * </p>
 * 
 * @since November 14, 2013
 * @author James Tillma and Teresa Worner
 */
public class Common 
{
	/** key for location values in parcelable */
	public static final String LOCATION_KEY = "key_location";
	
	/** key for forecast values in parcelable */
    public static final String FORECAST_KEY = "key_forecast";
    
    /** path for the intent broadcast that will trigger the Toast */
	public static final String ExceptionIntent = "edu.sdsmt.WornerTillma.App3.intent.Exception";
}
