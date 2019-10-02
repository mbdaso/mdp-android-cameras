package com.example.cameras;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.view.View;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends AppCompatActivity {
    List<String> cameraNames = new ArrayList<>();
    List<String> camerasURLS_ArrayList = new ArrayList<>();
    String TAG = "pepe";
    ArrayAdapter adapter;
    ImageView imageView;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Crear un array con los nombres de las cámaras

        listView = findViewById(R.id.listview);
        imageView = findViewById(R.id.imageview);
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                // When clicked, show a toast with the TextView text
                Log.d(TAG, "Download image: " + camerasURLS_ArrayList.get(position));

                try{
                    URL url = new URL(camerasURLS_ArrayList.get(position));

                    DownloadImageTask t = new DownloadImageTask();
                    t.execute(url);
//                    Log.d(TAG, "image returned: " + image);
                    //imageView.setImageBitmap(image.);
                }
                catch(Exception e){
                    Log.d(TAG, "listView listener Exception: " + e.getMessage());
                }
            }
        });

        adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_single_choice,
                cameraNames);
    }

    public void parseCameraURLsXML(){
        camerasURLS_ArrayList.clear();
        cameraNames.clear();
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
                        else if ("Data".equals(elementName)){
                            Log.d(TAG, parser.getAttributeValue(null, "name"));
                            if ("Nombre".equals(parser.getAttributeValue(null, "name"))){
                                parser.nextTag();
                                cameraNames.add(parser.nextText());
                            }
                        }
                    break;
                } //switch
                eventType = parser.next();
            } //while
        } //Try
        catch (Exception e){
            Log.d(TAG, "Exception");
            String err = (e.getMessage()==null)?"SD Card failed":e.getMessage();
            Log.d("sdcard-err2:",err);
        } //Catch
    } //parseCameraNames

    public void parseCameraURLsJSON(){
        camerasURLS_ArrayList.clear();
        cameraNames.clear();
        try {
            Gson gson = new Gson();
            InputStream is = getApplicationContext().getAssets().open("CCTV.json");
            CCTV cctv = gson.fromJson(new InputStreamReader(is), CCTV.class);

            int nCameras = cctv.kml.Document.Placemark.length;
            PLACEMARK [] pmArray = cctv.kml.Document.Placemark;
            for(int i = 0; i < nCameras; i++){
                String cameraURL = pmArray[i].description;
                cameraURL = cameraURL.substring(cameraURL.indexOf("http:"));
                cameraURL = cameraURL.substring(0, cameraURL.indexOf(".jpg") + 4);
                camerasURLS_ArrayList.add(cameraURL);
                cameraNames.add(pmArray[i].ExtendedData.Data[1].Value);
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

    private class DownloadImageTask extends AsyncTask<URL, Integer, Bitmap>{
        @Override
        protected Bitmap doInBackground(URL ... urls){
            Bitmap image = null;
            try{
                InputStream is = urls[0].openStream();

                image = BitmapFactory.decodeStream(is);

                Log.d(TAG, "image decoded: " + image);
            }
            catch (Exception e){
                Log.d(TAG,  "Exception in DownloadImageTask: " +  e.getMessage());
            }
            return image;
        }

        @Override
        protected void onPostExecute(Bitmap image){
            try {
                imageView.setImageBitmap(image);
            }
            catch(Exception e){
                Log.d(TAG, "onPostExecute exception: " + image +  " " + e.getMessage());
            }
        }
    }
} //MainActivity

