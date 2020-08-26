package com.farry.socialapp;


import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.farry.socialapp.adapters.AdapterUsers;
import com.farry.socialapp.models.ModelUsers;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class UsersFragment extends Fragment {

    // Recycler view ..
    RecyclerView recyclerView;
    AdapterUsers adapterUsers;
    List<ModelUsers> usersList;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    public UsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_users, container, false);

        recyclerView=view.findViewById(R.id.users_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

       firebaseAuth =FirebaseAuth.getInstance();
        usersList=new ArrayList<>();
        getAllUsers();
        return view;
    }

    private void getAllUsers() {

        final FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("AppUsers");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                usersList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    ModelUsers modelUsers=ds.getValue(ModelUsers.class);

                    if(!user.getUid().equals(modelUsers.getUid()) )
                    {
                     usersList.add(modelUsers);
                    }
                    adapterUsers=new AdapterUsers(getActivity(),usersList);
                }
                recyclerView.setAdapter(adapterUsers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu,menu);
        super.onCreateOptionsMenu(menu,inflater);

        MenuItem item=menu.findItem(R.id.action_search);
        SearchView searchView=(SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if(!s.equals(""))
                {                    // search using this string ..
                    searchUser(s);
                }
                else
                {                    getAllUsers();
                }

                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                if(!s.equals(""))
                {                    // search using this string ..
                    searchUser(s);
                }
                else
                {                    getAllUsers();
                }
                return false;
            }
        });

        super.onCreateOptionsMenu(menu,inflater);

    }




    private void searchUser(final String s) {

        final FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("AppUsers");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                usersList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    ModelUsers modelUsers=ds.getValue(ModelUsers.class);

                    // change display criteria resembling to search ..

                    if(!user.getUid().equals(modelUsers.getUid())) {
                        if (modelUsers.getName().toLowerCase().contains(s.toLowerCase())) {

                            usersList.add(modelUsers);
                        }
                    }
                    adapterUsers=new AdapterUsers(getActivity(),usersList);

                    // notify update ..
                    adapterUsers.notifyDataSetChanged();
                }

                recyclerView.setAdapter(adapterUsers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.action_logout)
        {
            firebaseAuth.signOut();
            updateUI(null);
        }
        return super.onOptionsItemSelected(item);
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
            startActivity(new Intent(getActivity(),LoginActivity.class));
            getActivity().finish();
        }

    }
}
