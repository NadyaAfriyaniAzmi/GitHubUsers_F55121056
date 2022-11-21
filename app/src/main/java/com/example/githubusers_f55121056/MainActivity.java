package com.example.githubusers_f55121056;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.entity.mime.Header;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private ListView listUsers;
    private ProgressBar progressBar;
    private UserAdapter adapter;
    private ArrayList<User> user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("List of Github Users");
        }
        progressBar = findViewById(R.id.progressbar);
        listUsers = findViewById(R.id.lv_list);
        adapter = new UserAdapter(this);

        getListUsers();
        listUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MainActivity.this, user.get(i).getName(), Toast.LENGTH_SHORT).show();
                Log.d("Lihat", user.get(i).getName());
            }
        });
    }
    private void getListUsers(){
        progressBar.setVisibility(View.VISIBLE);
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "https://api.github.com/users";
        client.addHeader("Authorization", "" +
                "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");
        client.addHeader("User-Agent", "request");
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                //jika koneksi berhasil
                progressBar.setVisibility(View.INVISIBLE);
                ArrayList<User> listUser = new ArrayList<>();
                String result = new String(responseBody);
                Log.d(TAG, result);
                try {
                    JSONArray dataArray = new JSONArray (result);
                    for (int i=0; i<dataArray.length(); i++){
                        JSONObject dataJson = dataArray.getJSONObject(i);
                        String name = dataJson.getString("login");
                        String type = dataJson.getString("type");
                        String photo = dataJson.getString("avatar_url");

                        User user = new User();
                        user.setPhoto(photo);
                        user.setName(name);
                        user.setType(type);
                        listUser.add(user);
                    }
                    user = listUser;
                    adapter = new  UserAdapter (MainActivity.this);
                    adapter.setUsers(listUser);
                    listUsers.setAdapter(adapter);
                } catch (Exception e){
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                //jika koneksi gagal
                progressBar.setVisibility(View.INVISIBLE);
                String errorMassage;
                switch (statusCode){
                    case 401:
                        errorMassage = statusCode + " : Bad Request";
                        break;
                    case 403:
                        errorMassage = statusCode + " : Forbidden";
                        break;
                    case 404:
                        errorMassage = statusCode + " : Not Found";
                        break;
                    default:
                        errorMassage = statusCode + " : " + error.getMessage();
                        break;
                }
                Toast.makeText(MainActivity.this, errorMassage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}