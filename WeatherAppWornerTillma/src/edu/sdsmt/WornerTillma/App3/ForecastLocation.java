package edu.sdsmt.WornerTillma.App3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class ForecastLocation implements Parcelable
{
	public String ZipCode;
    public String City;
    public String State;
    public String Country;
    

    private static final String TAG = "App3_ForecastLocation";
    
    // http://developer.weatherbug.com/docs/read/WeatherBug_API_JSON
    // NOTE:  See example JSON in doc folder.
    private String URL = "http://i.wxbug.net/REST/Direct/GetLocation.ashx?zip=" + "%s" + 
                                 "&api_key=u6vtegq4c7p72xk5cdwpcwtw";
    

    public ForecastLocation()
    {
		this.ZipCode = null;
        this.City = null;
        this.State = null;
        this.Country = null;
    }
    
    public void GetLocation(String zip, IListeners listener)
    {
    	LoadForecastLocation loadLocation = new LoadForecastLocation(this, listener);
    	loadLocation.execute(this.URL, zip);
    }
    
    private ForecastLocation(Parcel parcel)
    {
            this.ZipCode = parcel.readParcelable(Bitmap.class.getClassLoader());
            this.City = parcel.readString();
            this.State = parcel.readString();
            this.Country = parcel.readString();
    }

    @Override
    public int describeContents()
    {
            return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
            dest.writeString(this.ZipCode);
            dest.writeString(this.City);
            dest.writeString(this.State);
            dest.writeString(this.Country);
    }

    public static final Parcelable.Creator<ForecastLocation> CREATOR = new Parcelable.Creator<ForecastLocation>()
    {
            @Override
            public ForecastLocation createFromParcel(Parcel pc)
            {
                    return new ForecastLocation(pc);
            }
            
            @Override
            public ForecastLocation[] newArray(int size)
            {
                    return new ForecastLocation[size];
            }
    };
    
    public class LoadForecastLocation extends AsyncTask<String, Void, ForecastLocation>
    {
            private IListeners _listener;
            private ForecastLocation location;

            public LoadForecastLocation(ForecastLocation location, IListeners listener)
            {
                    this.location = location;
                    _listener = listener;
            }

            // params[0] = URL
            // params[2] = Zip
            protected ForecastLocation doInBackground(String... params)
            {
                    try
                    {
                    	StringBuilder stringBuilder = new StringBuilder();
                    	HttpClient client = new DefaultHttpClient();
                    	HttpResponse response;
                	
                		response = client.execute(new HttpGet(String.format(params[0], params[1])));
                		if(response.getStatusLine().getStatusCode() == 200)
                		{
                			HttpEntity entity = response.getEntity();
                			InputStream content = entity.getContent();
                			BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                			
                			String line;
                			while((line = reader.readLine()) != null)
                			{
                				stringBuilder.append(line);
                			}
                			
                			this.ReadJSON(stringBuilder.toString());
                		}

                    }
                	catch(ClientProtocolException e)
                	{
                		Log.e(ForecastLocation.TAG, e.getMessage());
                	}
                	catch(IOException e)
                	{
                		Log.e(ForecastLocation.TAG, e.getMessage());
                	}
                    catch (Exception e)
                    {
                            Log.e(TAG, e.toString());
                    }

                    return location;
            }
            
            private void ReadJSON(String json)
            {
            	try
            	{
            		JSONObject jToken = new JSONObject(json);
            		
            		if(jToken.has("location"))
            		{
            			JSONObject loc = jToken.getJSONObject("location");
            			
            			this.location.City = loc.getString("city");
            			this.location.State = loc.getString("state");
            			this.location.Country = loc.getString("country");
            			this.location.ZipCode = loc.getString("zipCode");
            		}
            	}
            	catch(JSONException e)
            	{
            		Log.e(ForecastLocation.TAG, e.getMessage());
            	}
            }

            protected void onPostExecute(ForecastLocation location)
            {
                    _listener.onLocationLoaded(location);
            }
    }
}
