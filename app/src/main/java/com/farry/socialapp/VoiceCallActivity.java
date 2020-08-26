package com.farry.socialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.farry.socialapp.databinding.ActivityVoiceCallBinding;
import com.farry.socialapp.models.ModelVoiceCall;
import com.farry.socialapp.notifications.Data;
import com.farry.socialapp.notifications.Sender;
import com.farry.socialapp.notifications.Token;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallListener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoiceCallActivity extends AppCompatActivity {

SinchClient sinchClient;
ModelVoiceCall obj;
    Call call;
    CallClient callClient;
    ActivityVoiceCallBinding binding;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_voice_call);
        requestQueue= Volley.newRequestQueue(getApplicationContext());

         obj=(ModelVoiceCall)getIntent().getSerializableExtra("obj");
        SetUpSnitch();
        sendNotification(obj.getCaller(),obj.getReciever());

        sinchClient.addSinchClientListener(new SinchClientListener() {
            @Override
            public void onClientStarted(SinchClient sinchClient) {
                if (CheckAudioPermission())
                {
                    callClient=sinchClient.getCallClient();
                    call = callClient.callUser(obj.getReciever());
                  //  Toast.makeText(MainActivity.this, "call user 1", Toast.LENGTH_SHORT).show();
// Or for video call: Call call = callClient.callUserVideo("<remote user id>");
                    call.addCallListener(new CallListener() {
                        @Override
                        public void onCallProgressing(Call call) {
                         //   Toast.makeText(MainActivity.this, "Connecting ...", Toast.LENGTH_SHORT).show();
                            binding.statusTv.setText("Connecting");
                        }

                        @Override
                        public void onCallEstablished(Call call) {

                           // Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                          binding.statusTv.setText("Connected");
                        }

                        @Override
                        public void onCallEnded(Call call) {
                            binding.statusTv.setText("Disconnected");
                        }

                        @Override
                        public void onShouldSendPushNotification(Call call, List<PushPair> list) {

                        }

                    });
                }
                else
                {

                    ActivityCompat
                            .requestPermissions(
                                    VoiceCallActivity.this,
                                    new String[] {Manifest.permission.RECORD_AUDIO },
                                    999);
                }


            }

            @Override
            public void onClientStopped(SinchClient sinchClient) {

            }

            @Override
            public void onClientFailed(SinchClient sinchClient, SinchError sinchError) {

            }

            @Override
            public void onRegistrationCredentialsRequired(SinchClient sinchClient, ClientRegistration clientRegistration) {

            }

            @Override
            public void onLogMessage(int i, String s, String s1) {

            }
        });

    }

   public void sendNotification(final String Caller,final String Reciever) {

        DatabaseReference allTokens= FirebaseDatabase.getInstance().getReference("Tokens");
        Query query=allTokens.orderByKey().equalTo(Reciever);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    Token token=ds.getValue(Token.class);
                    Data data=new Data(Caller,1);

                    // Data data=new Data(recievedUid,name+": "+message,MyUid,"New Message",R.drawable.ic_profile);
                    Sender sender= new Sender(data,token.getToken());
                    // api service queue .. hahaha

                    try {


                        JSONObject senderJsonObject = new JSONObject(new Gson().toJson(sender));
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", senderJsonObject, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                Log.d("JSONResponse",response.toString());
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        }){
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {

                                Map<String,String> headers=new HashMap<>();
                                headers.put("Content-Type","application/json");
                                headers.put("Authorization","key=AAAA8rbLTmA:APA91bEbxSBkcEzFMPEds8zpNt01DciYLrFwZIIx5_azdiZIITcxUL5EHsmrFt0w69xwdUi2Igw_1NLUx2Jjj_cDRLcjP62P9Z4k7XDioFTHWaA0NnNPfclKrYTiLM-8E4K0-rYFJtDv");

                                return headers;
                            }
                        };

                        requestQueue.add(jsonObjectRequest);
                    }
                    catch (Exception e)
                    {

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void SetUpSnitch()
    {
        android.content.Context context = VoiceCallActivity.this.getApplicationContext();
        sinchClient = Sinch.getSinchClientBuilder().context(context)
                .applicationKey("8f90a149-c8cd-4500-a5a6-3c23d52952a1")
                .applicationSecret("++7LI+9g8kO8Kh6f3l9emg==")
                .environmentHost("clientapi.sinch.com")
                .userId(obj.getCaller())
                .build();
        // "1" for reciever/callee & "2" for caller
        sinchClient.setSupportCalling(true);
        sinchClient.startListeningOnActiveConnection();
        sinchClient.start();
        sinchClient.setSupportManagedPush(true);
// or
        sinchClient.setSupportActiveConnectionInBackground(true);
        sinchClient.startListeningOnActiveConnection();
    }
    private boolean CheckAudioPermission() {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(
                VoiceCallActivity.this,
                Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_DENIED)
        {
            return false;
        }
        else
        {
            return true;
        }

    }
    private boolean CheckCameraPermission() {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(
                VoiceCallActivity.this,
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED)
        {
            return false;
        }
        else
        {
            return true;
        }

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        sinchClient.stopListeningOnActiveConnection();
        sinchClient.terminate();
    }
}
