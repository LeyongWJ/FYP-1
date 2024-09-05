package utar.edu.fyp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import utar.edu.fyp.R;
import utar.edu.fyp.VideoPlaybackActivity;
import utar.edu.fyp.adapter.VideoAdapter;
import utar.edu.fyp.model.Video;

public class HomeFragment extends Fragment implements VideoAdapter.OnVideoClickListener {

    private RecyclerView recyclerView;
    private VideoAdapter adapter;
    private List<Video> dataList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        dataList = new ArrayList<>();
        populateSampleData();

        adapter = new VideoAdapter(getContext(), dataList, this);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void populateSampleData() {
        dataList.add(new Video("A New World Record! - Men's 100m Freestyle - #Paris2024 Highlights", "Description 1", "https://firebasestorage.googleapis.com/v0/b/olympic-sport-games-video.appspot.com/o/A%20New%20World%20Record!%20-%20Men's%20100m%20Freestyle%20-%20%23Paris2024%20Highlights.mp4?alt=media&token=3a01591f-d965-40e4-80ae-8ce2588cc2aa", "https://firebasestorage.googleapis.com/v0/b/olympic-sport-games-video.appspot.com/o/thumbnail1.png?alt=media&token=a0e298de-f43e-405b-a5de-8fbb5cb2eb96"));
        dataList.add(new Video("Men's 4x100m Final - Paris Champions", "Description 2", "https://firebasestorage.googleapis.com/v0/b/olympic-sport-games-video.appspot.com/o/Men's%204x100m%20Final%20-%20Paris%20Champions.mp4?alt=media&token=7e19b92e-b975-4ffc-864e-0e4fe8b660d4", "https://firebasestorage.googleapis.com/v0/b/olympic-sport-games-video.appspot.com/o/thumbnail2.png?alt=media&token=2167084b-baa2-442c-8411-ec9f446f536a"));
        dataList.add(new Video("Women's 4x100m Final - Paris Champions", "Description 3", "https://firebasestorage.googleapis.com/v0/b/olympic-sport-games-video.appspot.com/o/Women's%204x100m%20Final%20-%20Paris%20Champions.mp4?alt=media&token=b99aa27a-a998-4a78-9485-cb0861934404", "https://firebasestorage.googleapis.com/v0/b/olympic-sport-games-video.appspot.com/o/thumbnail3.png?alt=media&token=6b611c52-2324-4b77-bab5-30216ed91f2f"));
        dataList.add(new Video("Title 4", "Description 4", "url4", "thumbnailUrl4"));
        dataList.add(new Video("Title 5", "Description 5", "url5", "thumbnailUrl5"));
    }

    public void filterResults(String query) {
        List<Video> filteredList = new ArrayList<>();
        for (Video item : dataList) {
            if (item.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(item);
            }
        }
        if (filteredList.isEmpty()) {
            Toast.makeText(getContext(), "No results found", Toast.LENGTH_SHORT).show();
        }
        adapter.updateList(filteredList);
    }

    @Override
    public void onVideoClick(int position) {
        Video selectedVideo = dataList.get(position);
        Intent intent = new Intent(getActivity(), VideoPlaybackActivity.class);
        intent.putExtra("title", selectedVideo.getTitle());
        intent.putExtra("description", selectedVideo.getDescription());
        intent.putExtra("videoUrl", selectedVideo.getVideoUrl());
        startActivity(intent);
    }
}
