package com.example.swagat_pc.test;

import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by swagat_pc on 30/10/16.
 */

public class SecondActivity extends FragmentActivity implements View.OnClickListener
{




    public static final String UPDATEDATES_URL = "http://192.168.0.105:8026/feeder/activedatesrequest/" ;
    public static final String DATEEVENTS_URL = "http://192.168.0.105:8026/feeder/dateinforequest/";
    String[] values = new String[]{};
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Button buttonLogout;

        buttonLogout = (Button) findViewById(R.id.logoutbutton);

        buttonLogout.setOnClickListener(this);



            CaldroidFragment caldroidFragment = new CaldroidFragment();
            Bundle args = new Bundle();
            Calendar cal = Calendar.getInstance();
            args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
            args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
            args.putBoolean(CaldroidFragment.ENABLE_CLICK_ON_DISABLED_DATES, true);
            args.putInt(CaldroidFragment.THEME_RESOURCE, com.caldroid.R.style.CaldroidDefaultDark);
            caldroidFragment.setArguments(args);
            //AdapterView.OnItemClickListener temp = caldroidFragment.getDateItemClickListener();
            //caldroidFragment.setBackgroundDrawableForDate(new ColorDrawable(Color.GREEN), ParseDate("28/11/2016"));
            //caldroidFragment.setBackgroundDrawableForDate(new ColorDrawable(Color.BLUE), ParseDate("27/11/2016"));
            android.support.v4.app.FragmentTransaction t = getSupportFragmentManager().beginTransaction();
            t.replace(R.id.cal, caldroidFragment);
            t.commit();

            final CaldroidListener listener = new CaldroidListener() {

                @Override
                public void onSelectDate(Date date, View view) {
                    // Toast.makeText(getApplicationContext(),"hello", Toast.LENGTH_SHORT).show();
                    String dayOfTheWeek = (String) android.text.format.DateFormat.format("EEEE", date);//Thursday
                    String stringMonth = (String) android.text.format.DateFormat.format("MMM", date); //Jun
                    String intMonth = (String) android.text.format.DateFormat.format("MM", date); //06
                    String year = (String) android.text.format.DateFormat.format("yyyy", date); //2013
                    String day = (String) android.text.format.DateFormat.format("dd", date); //20
//                TextView t = (TextView) findViewById(R.id.dateview);
//                t.setText("You have selected "+dayOfTheWeek+" the "+day+"th "+stringMonth+" "+year);
                    //getevents(date);

                    TextView t = (TextView) findViewById(R.id.dateview);
                    t.setText(day + "/" + intMonth + "/" + year);

                    String dated = (year+"-"+intMonth+"-"+day);
                    String[] assignments = new String[]{};
                    assignments = openEvent(dated);

                    // Defined Array values to show in ListView

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(SecondActivity.this,
                            android.R.layout.simple_list_item_1, android.R.id.text1, assignments);

                    final ListView listView = (ListView) findViewById(R.id.events);

                    // Assign adapter to ListView
                    listView.setAdapter(adapter);

                    // ListView Item Click Listener
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {

                            // ListView Clicked item index
                            int itemPosition = position;


//                SharedPreferences  logcheck = getSharedPreferences("user",MODE_PRIVATE);
//                logcheck.edit().putString("user",KEY_USERNAME);
//                logcheck.edit().putString("pass",KEY_PASSWORD);
//                logcheck.edit().putBoolean("loginstatus",true).commit();
//


                            // ListView Clicked item value
                            String itemValue = (String) listView.getItemAtPosition(position);

                            // Show Alert
//                Toast.makeText(getApplicationContext(),
//                        "Position :"+itemPosition+"  ListItem : " +itemValue , Toast.LENGTH_LONG)
//                        .show();

                            Intent i = new Intent(SecondActivity.this, DisplayActivity.class);
                            startActivity(i);
                            //finish();
                        }

                    });
                   //listupdate(assignments);
                }

                @Override
                public void onChangeMonth(int month, int year) {
                    String text = "month: " + month + " year: " + year;
                    //Toast.makeText(getApplicationContext(), text,
                    //     Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onLongClickDate(Date date, View view) {
                    Toast.makeText(getApplicationContext(),
                            "Long click " + date.toString(),
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCaldroidViewCreated() {
              /*  Toast.makeText(getApplicationContext(),
                        "Caldroid view is created",
                        Toast.LENGTH_SHORT).show();*/
                }

            };

            caldroidFragment.setCaldroidListener(listener);







    }





    private void updateDates() {
        //username = editTextUsername.getText().toString().trim();
        //password = editTextPassword.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPDATEDATES_URL,
                new Response.Listener<String>() {



                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray Activedates = jsonResponse.getJSONArray("ActiveDates");

//                            CaldroidFragment temp = new CaldroidFragment();
//                            temp= (CaldroidFragment) findViewById(R.id.cal);
//                            Calendar temp = (Calendar.getInstance());
//                            temp =

                            for (int i=0; i<Activedates.length();i++)
                            {
                                JSONObject dates = Activedates.getJSONObject(i);
                                String  date = dates.getString("date");
                                String course = dates.getString("course");
                                //if(course.equals("CS207")) {
                                   // c.setBackgroundDrawableForDate(new ColorDrawable(Color.BLUE), ParseDate(date));
                                Toast.makeText(getApplicationContext(),date,Toast.LENGTH_LONG).show();
                                //s[i]=date;

                                //set(date);
//                        "Position :"+itemPosition+"  ListItem : " +itemValue , Toast.LENGTH_LONG)
//                        .show();
                                //}
                                //else if(course.equals("CS251"))
                                //{
                                  //  c.setBackgroundDrawableForDate(new ColorDrawable(Color.GREEN), ParseDate(date));
                                //}
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getBaseContext(),"Unable to Connect to Server",Toast.LENGTH_LONG ).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                // map.put(KEY_USERNAME,editTextUsername.getText().toString());
                //map.put(KEY_PASSWORD,editTextPassword.getText().toString());
                //
                // map.put("id","6");
                return map;
            }
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError
            {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;

            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }





    public void logout()
    {
//        Intent i;
//        i =new Intent(SecondActivity.this,MainActivity.class);
//        startActivity(i);
//        finish();
        SharedPreferences logcheck = getSharedPreferences("user",MODE_PRIVATE);
        SharedPreferences.Editor edit = logcheck.edit();
        edit.putString("user",null);
        edit.putString("pass",null);
        edit.putBoolean("loginstatus",false);
        edit.apply();

        Intent intent = new Intent(this, MainActivity.class);
       // intent.putExtra(KEY_USERNAME, username);
        startActivity(intent);
        finish();
    }


    public class Refresh extends AsyncTask<String,String,String>
    {
        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try
            {
                URL url = new URL("databaseurl");
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream istream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(istream));
                StringBuffer buffer = new StringBuffer();

                String jsondata ="";
                while((jsondata = reader.readLine()) != null)
                {
                    buffer.append(jsondata);
                }
                return buffer.toString();



            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            finally {
                if(connection != null)
                {
                    connection.disconnect();
                }
                try
                {
                    if(reader != null)
                    {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    @Override
    public void onClick(View view) {
        logout();
    }



    private String[] openEvent(final String date) {

        final String[] val = new String[]{};
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DATEEVENTS_URL,
                new Response.Listener<String>() {


                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray assignAndfeedback = jsonResponse.getJSONArray("Assignments&Feedbackforms");
                            JSONObject Assignmentsobject = assignAndfeedback.getJSONObject(0);
                            JSONObject feedbacksobject = assignAndfeedback.getJSONObject(1);
                            JSONArray Assignments = Assignmentsobject.getJSONArray("Assignments");
                            JSONArray Feedbacks = feedbacksobject.getJSONArray("Feedbacks");


                            StringBuffer buffer = new StringBuffer();


                            for (int i=0; i<Assignments.length();i++)
                            {
                                JSONObject Assignment = Assignments.getJSONObject(i);
                                String  title = Assignment.getString("title");
                                String coursecode = Assignment.getString("coursecode");
                                buffer.append(coursecode + ": " + title + "\n");


                               // val[i]=title;
                            }
                            buffer.append("\n");
                            for (int i=0; i<Feedbacks.length();i++)
                            {
                                JSONObject Feedback = Feedbacks.getJSONObject(i);
                                String  title = Feedback.getString("title");
                                String coursecode = Feedback.getString("coursecode");
                                buffer.append(coursecode + ": " + title + "\n");


                                // val[i]=title;
                            }

                            String result = buffer.toString();
                            if(response!=null) {
                                showMessage("Assignments and Feedbacks", result);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ListView temp = (ListView) findViewById(R.id.events);
//                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                                android.R.layout.simple_list_item_1, android.R.id.text1, values);
                       // temp.setAdapter(adapter);;


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getBaseContext(),"Unable to Connect to Server",Toast.LENGTH_LONG ).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();

                map.put("date",date);
                return map;
            }
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError
            {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;

            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        return val;
    }
    public void showMessage(String title,String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }
}
