package com.exampledemo.parsaniahardik.google_login_demonuts;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

public class MainActivity extends AppCompatActivity implements  GoogleApiClient.OnConnectionFailedListener{

    private SignInButton signInButton;
    private GoogleSignInOptions gso;
    private GoogleApiClient mGoogleApiClient;
    private int SIGN_IN = 30;
    private TextView tv;
    private ImageView iv;
    private AQuery aQuery;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Plus.API)
                .build();

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, SIGN_IN);
            }
        });

        tv = (TextView) findViewById(R.id.text);
        iv = (ImageView) findViewById(R.id.iv);
        btn = (Button) findViewById(R.id.btn);
        aQuery = new AQuery(this);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                Toast.makeText(MainActivity.this, "Logout Successfully!", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //If signin
        if (requestCode == SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            //Calling a new function to handle signin
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        //If the login succeed
        if (result.isSuccess()) {
            //Getting google account
            final GoogleSignInAccount acct = result.getSignInAccount();

            //Displaying name and email
            String name = acct.getDisplayName();
            final String mail = acct.getEmail();
            // String photourl = acct.getPhotoUrl().toString();

            final String givenname="",familyname="",displayname="",birthday="";

            Plus.PeopleApi.load(mGoogleApiClient, acct.getId()).setResultCallback(new ResultCallback<People.LoadPeopleResult>() {
                @Override
                public void onResult(@NonNull People.LoadPeopleResult loadPeopleResult) {
                    Person person = loadPeopleResult.getPersonBuffer().get(0);

                    Log.d("GivenName ", person.getName().getGivenName());
                    Log.d("FamilyName ",person.getName().getFamilyName());
                    Log.d("DisplayName ",person.getDisplayName());
                    Log.d("gender ", String.valueOf(person.getGender())); //0 = male 1 = female
                    String gender="";
                    if(person.getGender() == 0){
                        gender = "Male";
                    }else {
                        gender = "Female";
                    }

                    if(person.hasBirthday()){
                        tv.setText(person.getName().getGivenName()+" \n"+person.getName().getFamilyName()+" \n"+gender+"\n"+person.getBirthday());
                    }else {
                        tv.setText(person.getName().getGivenName()+" \n"+person.getName().getFamilyName()+" \n"+gender);

                    }
                    aQuery.id(iv).image(acct.getPhotoUrl().toString());
                   Log.d("Uriddd",acct.getPhotoUrl().toString());
                  /*   Log.d(TAG,"CurrentLocation "+person.getCurrentLocation());
                    Log.d(TAG,"AboutMe "+person.getAboutMe());*/
                    // Log.d("Birthday ",person.getBirthday());
                    // Log.d(TAG,"Image "+person.getImage());
                }
            });
        } else {
            //If login fails
            Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
