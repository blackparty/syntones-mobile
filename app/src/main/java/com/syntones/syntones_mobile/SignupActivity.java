package com.syntones.syntones_mobile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.syntones.model.User;
import com.syntones.model.Message;
import com.syntones.remote.SyntonesWebAPI;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;


public class SignupActivity extends AppCompatActivity {

    private SignupActivity sContext;
    private EditText EmailEt, UsernameEt, PasswordEt, DateOfBirthEt;
    private RadioGroup GenderRg;
    private TextView UsernameTv;

    @BindView(R.id.btnSignUp)
    Button btn_signUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        EmailEt = (EditText) findViewById(R.id.etEmail);
        UsernameEt = (EditText) findViewById(R.id.etUsername);
        PasswordEt = (EditText) findViewById(R.id.etPassword);
        DateOfBirthEt = (EditText) findViewById(R.id.etDateOfBirth);
        GenderRg = (RadioGroup) findViewById(R.id.rgGender);


    }


    @OnClick(R.id.btnSignUp)
    public void signUp(View view) {

        String email = EmailEt.getText().toString().trim();
        String username = UsernameEt.getText().toString().trim();
        String password = new String(Hex.encodeHex(DigestUtils.md5(PasswordEt.getText().toString())));
        String dateOfBirth = DateOfBirthEt.getText().toString();

        int rgGender = GenderRg.getCheckedRadioButtonId();
        View rBtnGender = GenderRg.findViewById(rgGender);
        int index = GenderRg.indexOfChild(rBtnGender);
        String gender;

        if (index == 0) {

            gender = "Male";
        } else {

            gender = "Female";
        }

        SyntonesWebAPI syntonesWebAPI = SyntonesWebAPI.Factory.getInstance(sContext);

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setDateOfBirth(dateOfBirth);
        user.setEmail(email);
        user.setGender(gender);

        syntonesWebAPI.createUser(user);

        SyntonesWebAPI.Factory.getInstance(sContext).createUser(user).enqueue(new Callback<User>() {

            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("Failed", t.getMessage());
            }
        });

    }


}



