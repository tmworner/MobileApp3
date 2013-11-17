package edu.sdsmt.WornerTillma.App3;

/**
 * The listener interface.
 * 
 * <p>
 * <div style="font-weight:bold">
 * Description:
 * </div>
 * 		<div style="padding-left:3em">
 * 		This is a simple interface with associated methods that needed to be overwritten.
 * 		</div>
 * </p>
 * 
 * @since November 10, 2013
 * @author James Tillma and Teresa Worner
 */
public interface IListeners
{
    public void onLocationLoaded(ForecastLocation forecastLocation);
    public void onForecastLoaded(Forecast forecast);
}
