package utar.edu.fyp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import utar.edu.fyp.R;
import utar.edu.fyp.model.Video;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private final Context context;
    private List<Video> videoList;
    private final OnVideoClickListener onVideoClickListener;

    public VideoAdapter(Context context, List<Video> videoList, OnVideoClickListener onVideoClickListener) {
        this.context = context;
        this.videoList = videoList;
        this.onVideoClickListener = onVideoClickListener;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_item, parent, false);
        return new VideoViewHolder(view, onVideoClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        Video video = videoList.get(position);
        holder.title.setText(video.getTitle());

        // Load thumbnail using Glide
        Glide.with(context)
                .load(video.getThumbnailUrl())
                .placeholder(R.drawable.placeholder) // Placeholder image while loading
                .error(R.drawable.error_image) // Error image if loading fails
                .into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public void updateList(List<Video> newList) {
        videoList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView title;
        private final ImageView thumbnail;
        private final OnVideoClickListener onVideoClickListener;

        public VideoViewHolder(@NonNull View itemView, OnVideoClickListener onVideoClickListener) {
            super(itemView);
            title = itemView.findViewById(R.id.video_title);
            thumbnail = itemView.findViewById(R.id.video_thumbnail);
            this.onVideoClickListener = onVideoClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Log.d("VideoAdapter", "Video item clicked at position: " + position);
            if (onVideoClickListener != null && position != RecyclerView.NO_POSITION) {
                onVideoClickListener.onVideoClick(position);
            }
        }
    }

    public interface OnVideoClickListener {
        void onVideoClick(int position);
    }
}
