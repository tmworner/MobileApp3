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
/**
 * Class that controls getting the location information.
 * 
 * <p>
 * <div style="font-weight:bold">
 * Description:
 * </div>
 * 		<div style="padding-left:3em">
 * 		This class has methods for getting the location information, canceling the
 * 		get location, writing to the parcel, and creating the parcel. All of these
 * 		for the location information (nothing involving the forecast itself).
 * 		</div>
 * </p>
 * 
 * @since November 10, 2013
 * @author James Tillma and Teresa Worner
 */
public class ForecastLocation implements Parcelable
{
	public String ZipCode; //the class's value for the zip code
    public String City; //the class's value for the city
    public String State; //the class's value for the state
    public String Country; //the classes value for the country
    
    //the url to get the location information at
    private String URL = "http://i.wxbug.net/REST/Direct/GetLocation.ashx?zip=" + "%s" + 
                                 "&api_key=u6vtegq4c7p72xk5cdwpcwtw";
          
    private LoadForecastLocation loadLocation;
    
	/**
	 * Constructor for the class. This version sets the values to null.
	 */
    public ForecastLocation()
    {
    	//set the values to be null when nothing is given to the constructor
		this.ZipCode = null;
        this.City = null;
        this.State = null;
        this.Country = null;
    }
    
	/**
	 * A different version of the constructor. This passes in a parcel that contains
	 * the information that has been received from WeatherBug requests.
	 * @param parcel The parcel to get the information from to be used in this class.
	 */
    private ForecastLocation(Parcel parcel)
    {
    	//set the location information to be the information found in the parcel
        this.ZipCode = parcel.readParcelable(Bitmap.class.getClassLoader());
        this.City = parcel.readString();
        this.State = parcel.readString();
        this.Country = parcel.readString();
    }
    
	/**
	 * Gets the location information
	 * @param zip The zipcode used as the location
	 * @param listener The IListener object.
	 */
    public void GetLocation(String zip, IListeners listener)
    {
    	//create a LoadForecastLocation object and gets the location information
    	this.loadLocation = new LoadForecastLocation(listener);
    	this.loadLocation.execute(this.URL, zip);
    }
    
	/**
	 * Cancels the request to get the location (method used during lifecycle events).
	 */
    public boolean CancelGetLocation()
    {
    	//if there is a valid location instance that hasn't finished its async task, then cancel it
    	if(this.loadLocation != null && this.loadLocation.getStatus() != AsyncTask.Status.FINISHED)
    		return this.loadLocation.cancel(true);
		return false;
    }

	/**
	 * Content description, not entirely useful in this case.
	 */
    @Override
    public int describeContents()
    {
        return 0;//return 0 because it has to return something, but this app doesn't use it
    }

	/**
	 * dest The destination parcel for the information received by the class.
	 */
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
    	//set the destination parcel values
        dest.writeString(this.ZipCode);
        dest.writeString(this.City);
        dest.writeString(this.State);
        dest.writeString(this.Country);
    }

	/**
	 * Creator for the parcel
	 */
    public static final Parcelable.Creator<ForecastLocation> CREATOR = new Parcelable.Creator<ForecastLocation>()
    {
    	/**
    	 * Creates and returns ForecastLocation object based on a parcel.
    	 * @param The parcel to use to create the object
    	 * @return The created object
    	 */
        @Override
        public ForecastLocation createFromParcel(Parcel pc)
        {
            return new ForecastLocation(pc);//return a new ForecastLoaciton object
        }
        
    	/**
    	 * Creates and returns a ForecastLocation array object based on size;
    	 * @param size The size of the array to create
    	 * @return The object created
    	 */
        @Override
        public ForecastLocation[] newArray(int size)
        {
            return new ForecastLocation[size];//return a new array of ForecastLocation objects
        }
    };
    
    /**
     * This class loads the forecast location.
     * 
     * <p>
     * <div style="font-weight:bold">
     * Description:
     * </div>
     * 		<div style="padding-left:3em">
     * 		Sets the city, state country, and zipCode of the location. Not all of
     * 		this is used in the app. It has methods for getting the forecast location
     * 		information, reading the JSON, and then a method for calling the listerner's
     * 		onLocationLoaded method.
     * 		</div>
     * </p>
     * 
     * @since November 10, 2013
     * @author James Tillma and Teresa Worner
     */
    public class LoadForecastLocation extends AsyncTask<String, Void, ForecastLocation>
    {
        private IListeners listener;//the class's listener object
        //private Context context;

    	/**
    	 * Constructor for the class, gives the class's listener a value
    	 * @param listener the value to give the class's listener object
    	 */
        public LoadForecastLocation(IListeners listener)
        {
            this.listener = listener;//set the class's listener object
            //this.context = context;
        }

    	/**
    	 * Gets the forecast location information from WeaterhBug (in a try-catch block). If
    	 * any exception is triggered, an intent is sent from here.
    	 * @param params The array of parameters used in the HttpGet function call
    	 * @return location The location object.
    	 */

        protected ForecastLocation doInBackground(String... params)
        {
        	//create a new instance of ForecastLocation
        	ForecastLocation location = new ForecastLocation();
        
            try
            { //create a string builder and HTTP client
            	StringBuilder stringBuilder = new StringBuilder();
            	HttpClient client = new DefaultHttpClient();
            	HttpResponse response;
            	//get information from the URL
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
        			
        			this.ReadJSON(location, stringBuilder.toString());
        		}

            }
            //exceptions are used to show toast
        	catch(ClientProtocolException e)
        	{
        		//if a client protocol exception was caught, set the receiver's message
        		Receiver.SetMessage("The wireless communication has failed\n"+e.toString());
        		// Log.e(ForecastLocation.TAG, e.getMessage());
        	}
        	catch(IOException e)
        	{
        		//if an IO exception was caught, set the receiver's message
        		Receiver.SetMessage(e.toString());
        		// Log.e(ForecastLocation.TAG, e.getMessage());
        	}
            catch (Exception e)
            {
            	//if a general exception was caught, set the receiver's message
            	Receiver.SetMessage(e.toString());
        		// Log.e(TAG, e.toString());
            }

            //return the location object with its values (or potentially empty)
            return location;
        }
        
    	/**
    	 * Reads the JSON object and stores the required information in class values.
    	 * @param location The location object to store the location from the JSON in.
    	 * @param json The JSON object itself
    	 */
        private void ReadJSON(ForecastLocation location, String json)
        {
        	try
        	{
        		//create a new JSON object
        		JSONObject jToken = new JSONObject(json);
        		//if it has a location
        		if(jToken.has("location"))
        		{
        			//get the location and set the class's values
        			JSONObject loc = jToken.getJSONObject("location");
        			
        			location.City = loc.getString("city");
        			location.State = loc.getString("state");
        			location.Country = loc.getString("country");
        			location.ZipCode = loc.getString("zipCode");
        		}
        	}
        	catch(JSONException e)
        	{
        		//if a JSON excption was caught, set the receiver's message
        		Receiver.SetMessage(e.toString());
        		//Log.e(ForecastLocation.TAG, e.getMessage());
        	}
        }

    	/**
    	 * Calls the IListener's onLocationLoaded method
    	 * @param lcoation The location object
    	 */
        protected void onPostExecute(ForecastLocation location)
        {
        	//call the listner's onLocationLoaded method passing in the location information
            this.listener.onLocationLoaded(location);
        }
    }
}
