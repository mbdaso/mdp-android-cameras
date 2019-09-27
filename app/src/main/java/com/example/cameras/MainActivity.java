package com.example.cameras;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.view.View;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.example.cameras.CCTV;

public class MainActivity extends AppCompatActivity {

    List<String> cameraNames = new ArrayList<>();
    List<String> camerasURLS_ArrayList = new ArrayList<>();
    String TAG = "pepe";
    ArrayAdapter adapter;

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Crear un array con los nombres de las cámaras

        listView = findViewById(R.id.listview);

        adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_checked,
                camerasURLS_ArrayList);
    }

    public String [] parseCameraURLsXML(){
        XmlPullParserFactory parserFactory;
        try {
            parserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserFactory.newPullParser();
            InputStream is = getApplicationContext().getAssets().open("CCTV.kml");
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(is, null);
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String elementName;
                elementName = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if ("description".equals(elementName)) {
                            String cameraURL = parser.nextText();
                            cameraURL = cameraURL.substring(cameraURL.indexOf("http:"));
                            cameraURL = cameraURL.substring(0, cameraURL.indexOf(".jpg") + 4);
                            camerasURLS_ArrayList.add(cameraURL);
                        }
//                        else if ("ExtendedData".equals(elementName)){
//                            Log.d(TAG, "Data tag 1! " + parser.getName());
//                            parser.next();
//                            Log.d(TAG, "Data tag 2! " + parser.getName());
//                            if (parser.getName() == "Data")
//                                Log.d(TAG, "My Data: " + parser.getAttributeValue(null, "name"));
//                            parser.next();
//                            Log.d(TAG, "Data tag 3! " + parser.getName());
//                        }
                    break;
                } //switch
                eventType = parser.next();
            } //while
        } //Try
        catch (Exception e){
            Log.d(TAG, "Exception");
            Log.d(TAG, e.getMessage());
        } //Catch
        String [] pepe = new String [100];
        return pepe;
    } //parseCameraNames

    public void parseCameraURLsJSON(){
        camerasURLS_ArrayList.clear();
        try {
            Gson gson = new Gson();
            InputStream is = getApplicationContext().getAssets().open("CCTV.json");
            CCTV cctv = gson.fromJson(new InputStreamReader(is), CCTV.class);

            int nCameras = cctv.kml.Document.Placemark.length;
//            String firstCameraName = cctv.kml.Document.Placemark[0].ExtendedData.Data[1].Value );
//            String firstCameraDescription = cctv.kml.Document.Placemark[0].description;

            for(PLACEMARK pm : cctv.kml.Document.Placemark){
                Log.d(TAG, "placemark");
            }
        }
        catch(Exception e){
            Log.d(TAG, "Exception!");
            Log.d(TAG, e.getMessage());
        }
    }

    public void onXMLClick(View v){
        parseCameraURLsXML();
        listView.setAdapter(adapter);
    }

    public void onJSONClick(View v){
        parseCameraURLsJSON();
        listView.setAdapter(adapter);
    }
} //MainActivity


