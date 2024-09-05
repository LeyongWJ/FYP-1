package utar.edu.fyp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;
import utar.edu.fyp.R;
import utar.edu.fyp.LoginActivity;

public class ProfileFragment extends Fragment {

    private CircleImageView userProfileImage;
    private TextView userProfileName;
    private TextView email;
    private Button btnLogout;
    private TextView txtHistory, txtSavedVideo, txtLikedVideo;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        userProfileImage = view.findViewById(R.id.user_profile_image);
        userProfileName = view.findViewById(R.id.user_profile_name);
        email = view.findViewById(R.id.email);
        txtHistory = view.findViewById(R.id.txt_history);
        txtSavedVideo = view.findViewById(R.id.txt_saved_video);
        txtLikedVideo = view.findViewById(R.id.txt_liked_video);
        btnLogout = view.findViewById(R.id.btn_logout);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadUserProfile();

        txtHistory.setOnClickListener(v -> navigateToFragment(new HistoryFragment()));
        txtSavedVideo.setOnClickListener(v -> navigateToFragment(new SavedVideoFragment()));
        txtLikedVideo.setOnClickListener(v -> navigateToFragment(new LikedVideoFragment()));

        btnLogout.setOnClickListener(v -> logout());

        return view;
    }

    private void navigateToFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            // Retrieve user details from FirebaseAuth
            String name = currentUser.getDisplayName();
            String emailAddress = currentUser.getEmail();
            String profileImageUrl = currentUser.getPhotoUrl() != null ? currentUser.getPhotoUrl().toString() : null;

            userProfileName.setText(name != null ? name : "No Name");
            email.setText(emailAddress != null ? emailAddress : "No Email");

            // Load profile image using Glide
            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                Glide.with(ProfileFragment.this)
                        .load(profileImageUrl)
                        .placeholder(R.drawable.baseline_account_circle_24)
                        .error(R.drawable.baseline_account_circle_24)
                        .into(userProfileImage);
            } else {
                // Set a default image if no profile URL is available
                userProfileImage.setImageResource(R.drawable.baseline_account_circle_24);
            }
        } else {
            Toast.makeText(getContext(), "No user logged in.", Toast.LENGTH_SHORT).show();
        }
    }

    private void logout() {
        mAuth.signOut();
        // Navigate to login screen
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }
}
