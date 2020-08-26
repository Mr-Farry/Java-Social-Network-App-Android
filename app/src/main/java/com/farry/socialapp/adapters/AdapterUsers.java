package com.farry.socialapp.adapters;


import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.farry.socialapp.ChatActivity;
import com.farry.socialapp.models.ModelUsers;
import com.farry.socialapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;


public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder> {


    Context context;
    List<ModelUsers> usersList;

    public AdapterUsers(Context context, List<ModelUsers> usersList) {
        this.context = context;
        this.usersList = usersList;
    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_users,viewGroup,false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {

        final String Uid=usersList.get(i).getUid();
        final String name=usersList.get(i).getName();
        final String userImage=usersList.get(i).getProfile();

        myHolder.nameTV.setText(name);
        try
        {
            Picasso.get().load(userImage).placeholder(R.drawable.ic_profile).into(myHolder.circularImageView);
        }
        catch (Exception e)
        {

            Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
        }

        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ChatActivity.class);
                intent.putExtra("Uid",Uid);
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }



    public class MyHolder extends RecyclerView.ViewHolder
    {
        CircularImageView circularImageView;
        TextView nameTV;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            circularImageView=itemView.findViewById(R.id.circular_image);
            nameTV=itemView.findViewById(R.id.user_name);

        }
    }
}
