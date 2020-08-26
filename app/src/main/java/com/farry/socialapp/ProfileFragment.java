package com.farry.socialapp;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static com.google.firebase.storage.FirebaseStorage.getInstance;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {


    Uri image_uri;
    ImageView profileImage,coverImage;
    FloatingActionButton editProfile;
    TextView UserNameText,UserEmailText,UserPhoneText;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    String StoragePath="AppUsersPictures/";
    String email="",name="",phone="",profile_image="",cover_image;
    static final int Storage_request_code=200;
    static final int Camera_request_code=100;
    static final int Image_pick_camera_code=400;
    static final int Image_pick_gallery_code=300;
    String cameraPermission[];
    String storagePermission[];
    String ProfileOrCover;
    ProgressDialog progressDialog;
    public ProfileFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {

        View view=inflater.inflate(R.layout.fragment_profile, container, false);
        // initialize firebase database ..
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("AppUsers");
        storageReference=getInstance().getReference();
        progressDialog=new ProgressDialog(getActivity());

        // initializing permissions ..
        cameraPermission=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();

        profileImage=view.findViewById(R.id.profile_image);
        UserNameText=view.findViewById(R.id.user_name);
        UserEmailText=view.findViewById(R.id.user_email);
        UserPhoneText=view.findViewById(R.id.user_phone);


        Query query=databaseReference.orderByChild("email").equalTo(firebaseUser.getEmail());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    name=""+ds.child("name").getValue();
                    email=""+ds.child("email").getValue();
                    phone=""+ds.child("phone").getValue();
                    profile_image=""+ds.child("profile").getValue();
                    cover_image=""+ds.child("cover").getValue();


                }
                UserEmailText.setText(email);
                UserNameText.setText(name);
                UserPhoneText.setText(phone);
                try {
                    Picasso.get().load(profile_image).into(profileImage);
                }
                catch (Exception e)
                {

                    Picasso.get().load(R.drawable.add_image).into(profileImage);
                }
                try {
                    Picasso.get().load(cover_image).into(coverImage);
                }
                catch (Exception e)
                {

                    Picasso.get().load(R.drawable.add_image).into(coverImage);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        editProfile=view.findViewById(R.id.edit_profile);
        coverImage=view.findViewById(R.id.cover_image);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileDialogue();
            }
        });
        return view;
    }


    public boolean checkStoragePermission()
    {
     boolean result= ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)== (PackageManager.PERMISSION_GRANTED);

        return result;
    }

    public void requesStoragePermission()
    {
        requestPermissions(storagePermission,Storage_request_code);
    }

    public boolean checkCameraPermission()
    {
        boolean result= ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)== (PackageManager.PERMISSION_GRANTED);
        boolean result1= ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.CAMERA)== (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }

    public void requesCameraPermission()
    {
        Toast.makeText(getActivity(),"Request for Camera permission",Toast.LENGTH_SHORT).show();
        requestPermissions(cameraPermission,Camera_request_code);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Toast.makeText(getActivity(),"in OnrequestPermissions Result",Toast.LENGTH_SHORT).show();
        switch (requestCode)
        {
            case Camera_request_code:
            {
                if(grantResults.length>0)
                {
                    boolean CameraAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    boolean StorageAccepted=grantResults[1]==PackageManager.PERMISSION_GRANTED;
                    if(CameraAccepted && StorageAccepted)
                    {
                        pickFromCamera();
                    }
                }
                else {
                    // permission  not granted ..
                }

            } break;
            case Storage_request_code:
            {
                if(grantResults.length>0)
                {
                    boolean StorageAccepted=grantResults[1]==PackageManager.PERMISSION_GRANTED;
                    if(StorageAccepted)
                    {
                        pickFromGallery();
                    }
                }
                else {
                    // permission  not granted ..
                }
            } break;
        }
    }

    private void pickFromGallery() {
        Intent galleryIntent=new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(galleryIntent,Image_pick_gallery_code);

    }

    private void pickFromCamera() {

        ContentValues values=new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Temp Desc");
        image_uri=getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent,Image_pick_camera_code);
    }



    private void showEditProfileDialogue()
    {
        String[] optionsItems = {"Update Profile Picture", "Update Cover Photo","Edit Name","Edit Phone",};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Action : ").setItems(optionsItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0)
                {
                    // profile pic
                    ProfileOrCover="profile";
                    showEditProfilePicture();

                }
                else if(which==1)
                {
                    // cover photo
                    ProfileOrCover="cover";
                    showEditProfilePicture();
                }
                else if(which==2)
                {
                    // name
                    showEditNamePhoneDialoge("name");
                }

                else if(which==3)
                {
                    // phone
                    showEditNamePhoneDialoge("phone");


                }


            }

        });
        builder.create().show();
    }

    private void showEditNamePhoneDialoge(final String key) {

        AlertDialog.Builder alertDialog=new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Update "+key);
        LinearLayout linearLayout=new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        final EditText editText=new EditText(getActivity());

        editText.setHint("Enter "+key);
        linearLayout.addView(editText);
        alertDialog.setView(linearLayout);

        alertDialog.setPositiveButton("Update",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {

                String value=editText.getText().toString().trim();
                HashMap<String,Object> values=new HashMap<>();
                values.put(key,value);
                if(value.equalsIgnoreCase(""))
                { }
                else {
                      databaseReference.child(firebaseUser.getUid()).updateChildren(values).addOnSuccessListener(new OnSuccessListener<Void>() {
                          @Override
                          public void onSuccess(Void aVoid) {

                          }
                      }).addOnFailureListener(new OnFailureListener() {
                          @Override
                          public void onFailure(@NonNull Exception e) {

                          }
                      });
                }
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });
    alertDialog.create().show();
    }

    private void showEditProfilePicture() {
        String[] optionsItems = {"Camera","Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pick Image :").setItems(optionsItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0)
                {
                    // pick from camera ..
                    if(!checkCameraPermission())
                    {
                       requesCameraPermission();
                    }
                    else {
                        Toast.makeText(getActivity(),"Pick from camera",Toast.LENGTH_SHORT).show();
                        pickFromCamera();
                    }
                }
                else if(which==1) {
                    // pick from gallery ..
                    if(!checkStoragePermission())
                    {
                     //   Toast.makeText(getActivity(),"Storage Permission not granted",Toast.LENGTH_SHORT).show();
                       requesStoragePermission();
                    }
                    else {
                       // Toast.makeText(getActivity(),"Pick from Gallery",Toast.LENGTH_SHORT).show();
                        pickFromGallery();
                    }
                }
            }

        });
        builder.create().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode==RESULT_OK)
        {

            if(requestCode==Image_pick_camera_code)
            {
                progressDialog.setTitle("Updating Photo");
                progressDialog.show();
                image_uri=data.getData();
                UploadImage(image_uri);
            }

        if(requestCode==Image_pick_gallery_code)
        {

            progressDialog.setTitle("Updating Photo");
            progressDialog.show();
            image_uri=data.getData();
            UploadImage(image_uri);

        }
        }
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu,menu);

        super.onCreateOptionsMenu(menu,inflater);
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

    private void UploadImage(Uri image_uri) {

        String FileAndPathName=StoragePath+""+ProfileOrCover+"_"+firebaseUser.getUid();
        StorageReference storageReferenceSecond=storageReference.child(FileAndPathName);
        storageReferenceSecond.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                while(!uriTask.isSuccessful());
                Uri downloadUrl=uriTask.getResult();
                if(uriTask.isComplete())
                {
                    HashMap<String,Object> results=new HashMap<>();
                    results.put(ProfileOrCover,downloadUrl.toString());

                    databaseReference.child(firebaseUser.getUid()).updateChildren(results).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();

                        }
                    });

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(),"Failed to pick image",Toast.LENGTH_SHORT).show();

            }
        });
    }
}