package com.syntones.syntones_mobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.syntones.model.Playlist;
import com.syntones.model.User;
import com.syntones.remote.SyntonesWebAPI;
import com.syntones.response.LoginResponse;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private LoginActivity lContext;
    EditText UsernameEt, PasswordEt;
    @BindView(R.id.btnLogIn)
    Button login;
    Button btn_signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        UsernameEt = (EditText) findViewById(R.id.etUsername);
        PasswordEt = (EditText) findViewById(R.id.etPassword);
    }

    @OnClick(R.id.btnLogIn)
    public void logIn(View view) {

        final String username = UsernameEt.getText().toString().trim();
        String password = new String(Hex.encodeHex(DigestUtils.md5(PasswordEt.getText().toString())));

        SyntonesWebAPI syntonesWebAPI = SyntonesWebAPI.Factory.getInstance(lContext);

        User user = new User();

        user.setUsername(username);
        user.setPassword(password);

        syntonesWebAPI.logInUser(user);

        SyntonesWebAPI.Factory.getInstance(lContext).logInUser(user).enqueue(new Callback<LoginResponse>() {

            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                LoginResponse loginResponse = response.body();


                SharedPreferences sharedPrefUserInfo = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorUserInfo = sharedPrefUserInfo.edit();


                editorUserInfo.putString("username", username);
                editorUserInfo.commit();

                Intent intent = new Intent(LoginActivity.this, YourLibraryActivity.class);
                startActivity(intent);

//                Log.e("Login Response: ", loginResponse.getMessage().getMessage());

            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {

            }
        });

    }

}
