package com.farry.socialapp;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.farry.socialapp.adapters.AdapterChat;
import com.farry.socialapp.models.ModelChat;
import com.farry.socialapp.models.ModelUsers;
import com.farry.socialapp.models.ModelVoiceCall;
import com.farry.socialapp.notifications.Data;
import com.farry.socialapp.notifications.Sender;
import com.farry.socialapp.notifications.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {


    EditText MessageText;
    RecyclerView recyclerView;
    ImageButton SendButton;
    CircularImageView Userimage;
    TextView nameTV,StatusTV;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ImageView VoiceCallButton,VideoCallButton;

    ValueEventListener isSeenListener;
    DatabaseReference isSeenDBReference;

    List<ModelChat> chatList;
    AdapterChat adapterChat;
    String recievedUid;
    String MyUid;
    String hisImage;

    private RequestQueue requestQueue;
    boolean notify=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // initializing views ..

        Intent intent=getIntent();
        recievedUid=intent.getStringExtra("Uid");
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        MyUid=firebaseUser.getUid();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("AppUsers");

        requestQueue= Volley.newRequestQueue(getApplicationContext());
       Toolbar toolbar=findViewById(R.id.user_info_toolbar);
       setSupportActionBar(toolbar);
       toolbar.setTitle("");
       VoiceCallButton=findViewById(R.id.button_voice_call);
       VideoCallButton=findViewById(R.id.button_video_call);

       VoiceCallButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               ModelVoiceCall modelVoiceCall=new ModelVoiceCall();
               modelVoiceCall.setCaller(MyUid);
               modelVoiceCall.setReciever(recievedUid);
               Intent intent1=new Intent(ChatActivity.this,VoiceCallActivity.class);
               intent1.putExtra("obj",modelVoiceCall);
               startActivity(intent1);
           }
       });

       MessageText=findViewById(R.id.message_ET);
       recyclerView=findViewById(R.id.chat_recyclerview);
       SendButton=findViewById(R.id.send_button);
       Userimage=findViewById(R.id.user_image);
       nameTV=findViewById(R.id.nameTV);
       StatusTV=findViewById(R.id.statusTV);
        Query searchQuery=databaseReference.orderByChild("uid").equalTo(recievedUid);
        searchQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name="";
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                     name=""+ds.child("name").getValue();
                    hisImage=""+ds.child("profile").getValue();
                }
                nameTV.setText(name);
                try
                {
                    Picasso.get().load(hisImage).into(Userimage);

                }
                catch (Exception e)
                {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        //

        // api service thing here ..
        readAllMessages();
        // send message button click ..

//        seenMessages();

        SendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message=MessageText.getText().toString().trim();
                if(!message.equals(""))
                {
                     notify=true;
                    SendMessage(message);
                }
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
  //  isSeenDBReference.removeEventListener(isSeenListener);
    }

    private void seenMessages() {

    isSeenDBReference=FirebaseDatabase.getInstance().getReference("SocialAppChats").child(recievedUid);
    isSeenListener=isSeenDBReference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            for (DataSnapshot ds: dataSnapshot.getChildren())
            {
                ModelChat chat=ds.getValue(ModelChat.class);
                if(chat.getReceiver().equals(MyUid) && chat.getSender().equals(recievedUid))
                {
                    HashMap<String,Object> isSeenHashMap=new HashMap<>();
                    isSeenHashMap.put("isSeen",true);
                    isSeenDBReference.getRef().updateChildren(isSeenHashMap);
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
    }

    private void readAllMessages() {
        chatList=new ArrayList<>();
    DatabaseReference DBRef=FirebaseDatabase.getInstance().getReference("SocialAppChats");
    DBRef.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
        {

            chatList.clear();
            for (DataSnapshot ds : dataSnapshot.getChildren())
            {
                ModelChat chat = ds.getValue(ModelChat.class);
                if (chat.getReceiver().equals(MyUid) && chat.getSender().equals(recievedUid)
                        || chat.getSender().equals(MyUid) && chat.getReceiver().equals(recievedUid))
                {
                    chatList.add(chat);

                }

            adapterChat = new AdapterChat(ChatActivity.this, chatList, hisImage);
            adapterChat.notifyDataSetChanged();
            recyclerView.setAdapter(adapterChat);
            }
         }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
    }

    private void SendMessage(final String message) {
        String time=String.valueOf(System.currentTimeMillis());
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("sender",MyUid);
        hashMap.put("receiver",recievedUid);
        hashMap.put("message",message);
        hashMap.put("timestamp",time);
        hashMap.put("isSeen",false);
        DatabaseReference databaseReference1=FirebaseDatabase.getInstance().getReference();
        databaseReference1.child("SocialAppChats").push().setValue(hashMap);

        MessageText.setText("");

        String msg=message;

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("AppUsers").child(MyUid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ModelUsers modelUsers=dataSnapshot.getValue(ModelUsers.class);

                if(notify)
                {
                    sendNotification(recievedUid,modelUsers.getName(),message);
                }
                notify=false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendNotification(final String recievedUid, final String name, final String message) {

        DatabaseReference allTokens=FirebaseDatabase.getInstance().getReference("Tokens");
        Query query=allTokens.orderByKey().equalTo(recievedUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    Token token=ds.getValue(Token.class);
                   Data data=new Data(MyUid,name+":"+message,"New Message",recievedUid,R.drawable.ic_profile);

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


    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null)
        {           // Name, email address, and profile photo Url
            String name = currentUser.getDisplayName();
            String email = currentUser.getEmail();
            // Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = currentUser.isEmailVerified();
            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = currentUser.getUid();

        }

        else
        {
            startActivity(new Intent(ChatActivity.this,LoginActivity.class));
            finish();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        menu.findItem(R.id.action_search).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id=item.getItemId();
        if(id==R.id.action_logout)
        {
            firebaseAuth.signOut();
            updateUI(firebaseAuth.getCurrentUser());
        }
        return super.onOptionsItemSelected(item);
    }

}
