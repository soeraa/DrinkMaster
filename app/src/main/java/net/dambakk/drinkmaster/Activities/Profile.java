package net.dambakk.drinkmaster.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import net.dambakk.drinkmaster.R;

/**
 * Created by chris on 29.07.2016.
 */
public class Profile extends Fragment {

    FirebaseUser user;
    Button btnEditProfile;


    public Profile() {
        //Required empty constructor
        System.out.println("My Profile was pressed");
    }

    public static Profile newInstance(){
        Profile fragment = new Profile();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("My profile");
        user = FirebaseAuth.getInstance().getCurrentUser();
        String username = user.getDisplayName();
        if(username != null)((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Profile: " + username);

        btnEditProfile = (Button) view.findViewById(R.id.btn_edit_profile);
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start edit profile intent here...
                startActivity(new Intent(getContext(), EditProfile.class));
            }
        });


        return view;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }
}
