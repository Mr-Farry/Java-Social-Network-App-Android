package com.farry.socialapp.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.farry.socialapp.R;
import com.farry.socialapp.models.ModelChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MyHolder>
{

    static final int Msg_Type_Right=1;
    static final int Msg_Type_Left=0;
    Context context;
    List<ModelChat> chatList;
    String imageUrl;
    FirebaseUser firebaseUser;

    public AdapterChat(Context context, List<ModelChat> chatList, String imageUrl) {
        this.context = context;
        this.chatList = chatList;
        this.imageUrl = imageUrl;
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if(chatList.get(position).getSender().equals(firebaseUser.getUid()))
        {
            return Msg_Type_Right;
        }
        else {
            return Msg_Type_Left;
        }
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if(i==Msg_Type_Right)
        {
            View view= LayoutInflater.from(context).inflate(R.layout.row_chat_right,viewGroup,false);
            return new MyHolder(view);
        }
        else {
            View view= LayoutInflater.from(context).inflate(R.layout.row_chat_left,viewGroup,false);
            return new MyHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
        // get data and set to views ...
        String message=chatList.get(i).getMessage();
        String timeStamp=chatList.get(i).getTimestamp();

        Calendar calendar=Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(timeStamp));
        String dateformat= DateFormat.format("dd/MM/yyyy hh:mm:aa",calendar).toString();

        myHolder.MessageTV.setText(message);
        myHolder.TimeTV.setText(dateformat);
        try
        {
            Picasso.get().load(imageUrl).into(myHolder.profileImage);
        }
        catch (Exception e)
        {
        }
        // setting is seen or dilevered ..
        if(i==chatList.size()-1)
        {
            if(chatList.get(i).isSeen())
            {
                myHolder.isSeenTV.setText("Seen");
            }
            else
            {
                myHolder.isSeenTV.setText("Delivered");
            }
        }
        else {
            myHolder.isSeenTV.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }


    class MyHolder extends RecyclerView.ViewHolder
    {

        CircularImageView profileImage;
        TextView MessageTV,isSeenTV,TimeTV;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            profileImage=itemView.findViewById(R.id.sender_image);
            MessageTV=itemView.findViewById(R.id.message_TV);
            isSeenTV=itemView.findViewById(R.id.isSeenTV);
            TimeTV=itemView.findViewById(R.id.time_TV);

        }

    }
}
