package edu.sdsmt.WornerTillma.App3;

import edu.sdsmt.WornerTillma.App3.Forecast;
import edu.sdsmt.WornerTillma.App3.ForecastLocation;

public interface IListeners
{
    public void onLocationLoaded(ForecastLocation forecastLocation);
    public void onForecastLoaded(Forecast forecast);
}
