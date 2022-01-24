package com.taufique.covid_22;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private ListView lv;
    EditText inputSearch;
    String location, confirmedCasesIndia, confirmedCasesForeign, discharged, death, totalCases;

    ArrayList<HashMap<String, String>> statelist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        statelist = new ArrayList<>();
        lv = findViewById(R.id.listView);
        GetData getData = new GetData();
        getData.execute();

    }


    @SuppressLint("StaticFieldLeak")
    public class GetData extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... strings) {
            String current = "";

            try {
                URL url;
                HttpsURLConnection urlConnection = null;
                try {
                    String JSON_URL = "https://api.rootnet.in/covid19-in/stats/latest";
                    url = new URL(JSON_URL);
                    urlConnection = (HttpsURLConnection) url.openConnection();

                    InputStream in = urlConnection.getInputStream();
                    InputStreamReader isr = new InputStreamReader(in);

                    int data = isr.read();
                    while (data != -1) {
                        current += (char) data;
                        data = isr.read();
                    }

                    return current;


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return current;

        }


        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject jasonObject;
                jasonObject = new JSONObject(s);

                boolean success = jasonObject.getBoolean("success");
                if (success) {
                    JSONObject json = jasonObject.getJSONObject("data");

                    JSONArray jsonArray;
                    jsonArray = json.getJSONArray("regional");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        location = jsonObject1.getString("loc");
                        confirmedCasesIndia = ("Confirmed Case : " + jsonObject1.getString("confirmedCasesIndian"));
                        confirmedCasesForeign = ("confirmed Cases In Foreign: " + jsonObject1.getString("confirmedCasesForeign"));
                        discharged = ("Discharged: " + jsonObject1.getString("discharged"));
                        death = ("Deaths: " + jsonObject1.getString("deaths"));
                        totalCases = ("TotalConfirmed: " + jsonObject1.getString("totalConfirmed"));

                        HashMap<String, String> state = new HashMap<>();

                        state.put("loc", location);
                        state.put("confirmedCasesIndian", confirmedCasesIndia);
                        state.put("confirmedCasesForeign", confirmedCasesForeign);
                        state.put("discharged", discharged);
                        state.put("deaths", death);
                        state.put("totalConfirmed", totalCases);

                        statelist.add(state);


                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ListAdapter adapter = new SimpleAdapter(MainActivity.this, statelist, R.layout.draw_layout,
                    new String[]{"loc", "confirmedCasesIndian", "confirmedCasesForeign", "discharged", "deaths", "totalConfirmed"},
                    new int[]{R.id.textView, R.id.textView2, R.id.textView3, R.id.textView4, R.id.textView5, R.id.textView6});

            lv.setAdapter(adapter);
        }


    }


}