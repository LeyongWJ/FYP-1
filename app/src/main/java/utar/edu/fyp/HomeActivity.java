package utar.edu.fyp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import utar.edu.fyp.adapter.VideoAdapter;
import utar.edu.fyp.fragment.HomeFragment;
import utar.edu.fyp.fragment.ProfileFragment;
import utar.edu.fyp.fragment.SearchResultsFragment;
import utar.edu.fyp.fragment.ShortsFragment;
import utar.edu.fyp.model.Video;

public class HomeActivity extends AppCompatActivity implements VideoAdapter.OnVideoClickListener {

    private static final String TAG = "HomeActivity";
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;
    private RecyclerView videoList;
    private VideoAdapter videoAdapter;
    private List<Video> videos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        frameLayout = findViewById(R.id.frame_layout);
        videoList = findViewById(R.id.video_list);

        videoList.setLayoutManager(new LinearLayoutManager(this));
        videos = new ArrayList<>();
        videoAdapter = new VideoAdapter(this, videos, this);
        videoList.setAdapter(videoAdapter);

        loadVideosFromFirebase();

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            try {
                int itemId = item.getItemId();
                if (itemId == R.id.home) {
                    selectedFragment = new HomeFragment();
                } else if (itemId == R.id.shorts) {
                    selectedFragment = new ShortsFragment();
                } else if (itemId == R.id.profile) {
                    selectedFragment = new ProfileFragment();
                }

                if (selectedFragment != null) {
                    selectedFragment(selectedFragment);
                } else {
                    Log.e(TAG, "Selected fragment is null");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in navigation item selection", e);
                Toast.makeText(HomeActivity.this, "Navigation error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            return true;
        });

        // Set default selection to 'home'
        bottomNavigationView.setSelectedItemId(R.id.home);
    }

    private void loadVideosFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("videos");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                videos.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Video video = snapshot.getValue(Video.class);
                    if (video != null) {
                        // Fetch and set both video and thumbnail URLs
                        fetchVideoAndThumbnailUrls(video);
                    }
                }
                videoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HomeActivity.this, "Failed to load videos.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchVideoAndThumbnailUrls(Video video) {
        // Fetch the thumbnail URL
        StorageReference thumbnailRef = FirebaseStorage.getInstance().getReferenceFromUrl(video.getThumbnailUrl());
        thumbnailRef.getDownloadUrl().addOnSuccessListener(thumbnailUri -> {
            video.setThumbnailUrl(thumbnailUri.toString());

            // Fetch the video URL
            StorageReference videoRef = FirebaseStorage.getInstance().getReferenceFromUrl(video.getVideoUrl());
            videoRef.getDownloadUrl().addOnSuccessListener(videoUri -> {
                video.setVideoUrl(videoUri.toString());
                // Add video to the list and notify the adapter
                videos.add(video);
                videoAdapter.notifyDataSetChanged();
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Failed to get video URL", e);
                // Optionally handle the failure, such as setting a placeholder URL
            });

        }).addOnFailureListener(e -> {
            Log.e(TAG, "Failed to get thumbnail URL", e);
            // Optionally handle the failure, such as setting a placeholder URL
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                redirectToSearchResults(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    private void redirectToSearchResults(String query) {
        Fragment searchResultsFragment = new SearchResultsFragment();

        Bundle args = new Bundle();
        args.putString("query", query);
        searchResultsFragment.setArguments(args);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, searchResultsFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void selectedFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onVideoClick(int position) {
        Video selectedVideo = videos.get(position);
        Intent intent = new Intent(this, VideoPlaybackActivity.class);
        intent.putExtra("title", selectedVideo.getTitle());
        intent.putExtra("description", selectedVideo.getDescription());
        intent.putExtra("videoUrl", selectedVideo.getVideoUrl());
        startActivity(intent);
    }
}
