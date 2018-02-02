package com.example.swagat_pc.test;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String LOGIN_URL = "http://192.168.0.105:8026/feeder/applogincheck/" ;

    public static final String KEY_USERNAME="student";
    public static final String KEY_PASSWORD="password";

    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonLogin;

    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences logcheck = getSharedPreferences("user",MODE_PRIVATE);
        if(logcheck.getBoolean("loginstatus",false)){
         Intent intent = new Intent(this,SecondActivity.class);
            startActivity(intent);
        }

        editTextUsername = (EditText) findViewById(R.id.username);
        editTextPassword = (EditText) findViewById(R.id.password);
        String u = editTextUsername.getText().toString();
        String p = editTextPassword.getText().toString();
        buttonLogin = (Button) findViewById(R.id.loginbutton);

        buttonLogin.setOnClickListener(this);
    }


    private void userLogin() {
        //username = editTextUsername.getText().toString().trim();
        //password = editTextPassword.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                new Response.Listener<String>() {


                    @Override
                    public void onResponse(String response) {
                        if(response.trim().equals("success")){
                        //if(response!=null){
                            String usr= editTextUsername.toString();
                            String pss = editTextPassword.toString();
                            SharedPreferences logcheck = getSharedPreferences("user",MODE_PRIVATE);
                            SharedPreferences.Editor temp = logcheck.edit();
                            temp.putString("user",usr);
                            temp.putString("pass",pss);
                            temp.apply();
                            openProfile();
                        }else{
                           // openProfile();
                           Toast.makeText(MainActivity.this,response,Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //openProfile();
                        Toast.makeText(getBaseContext(),"Unable to Connect to Server",Toast.LENGTH_LONG ).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                map.put(KEY_USERNAME,editTextUsername.getText().toString());
                map.put(KEY_PASSWORD,editTextPassword.getText().toString());
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

    private void openProfile(){
        SharedPreferences  logcheck = getSharedPreferences("user",MODE_PRIVATE);
        logcheck.edit().putString("user",KEY_USERNAME);
        logcheck.edit().putString("pass",KEY_PASSWORD);
        logcheck.edit().putBoolean("loginstatus",true).commit();


        Intent intent = new Intent(this, SecondActivity.class);
        intent.putExtra(KEY_USERNAME, username);
        startActivity(intent);
        finish();
    }

    //@Override
    //public void onclick(View v) {
      //  userLogin();
    //}

    @Override
    public void onClick(View view) {

        userLogin();
    }


}