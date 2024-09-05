package utar.edu.fyp;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;
import android.view.ViewGroup;
import android.content.Context;
import android.media.AudioManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class VideoPlaybackActivity extends AppCompatActivity {

    private VideoView videoView;
    private TextView titleView, descriptionView;
    private Toolbar toolbar;
    private FrameLayout videoContainer;
    private FrameLayout fullscreenContainer;
    private RelativeLayout normalModeLayout;
    private ImageButton playPauseButton;
    private ImageButton fullscreenToggle;
    private ImageButton volumeButton;
    private SeekBar seekBar;
    private SeekBar volumeSeekBar;
    private AudioManager audioManager;
    private boolean isFullscreen = false;

    // Variables to store playback state
    private int lastPosition = 0;
    private boolean wasPlaying = false;
    private int previousVolume = -1;

    // Constants
    private static final int SEEKBAR_UPDATE_INTERVAL_MS = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_playback);

        initializeViews();
        initializeAudioManager();
        setUpToolbar();
        retrieveAndSetData();
        setupVideoControls();
        setupVolumeControls();
        playVideo(getIntent().getStringExtra("videoUrl"));
    }

    private void initializeViews() {
        videoView = findViewById(R.id.video_view);
        titleView = findViewById(R.id.video_title);
        descriptionView = findViewById(R.id.video_description);
        toolbar = findViewById(R.id.toolbar);
        videoContainer = findViewById(R.id.video_container);
        fullscreenContainer = findViewById(R.id.fullscreen_container);
        normalModeLayout = findViewById(R.id.normal_mode_layout);
        playPauseButton = findViewById(R.id.exo_play);
        fullscreenToggle = findViewById(R.id.fullscreen_toggle);
        seekBar = findViewById(R.id.exo_progress);
        volumeButton = findViewById(R.id.volume_button);
        volumeSeekBar = findViewById(R.id.volume_seekbar);

        if (videoView == null || seekBar == null || volumeSeekBar == null) {
            throw new NullPointerException("VideoView or SeekBars are not initialized properly.");
        }
    }

    private void initializeAudioManager() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void retrieveAndSetData() {
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");

        if (title != null) {
            titleView.setText(title);
        } else {
            titleView.setText(R.string.no_title);
        }

        if (description != null) {
            descriptionView.setText(description);
        } else {
            descriptionView.setText(R.string.no_description);
        }
    }


    private void setupVideoControls() {
        playPauseButton.setOnClickListener(v -> togglePlayPause());
        fullscreenToggle.setOnClickListener(v -> toggleFullscreen());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && videoView != null) {
                    videoView.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void setupVolumeControls() {
        volumeButton.setOnClickListener(v -> toggleMute());

        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    setVolume(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Initialize volume SeekBar
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        volumeSeekBar.setMax(maxVolume);
        volumeSeekBar.setProgress(currentVolume);
    }

    private void togglePlayPause() {
        if (videoView != null) {
            if (videoView.isPlaying()) {
                videoView.pause();
                playPauseButton.setImageResource(android.R.drawable.ic_media_play);
            } else {
                videoView.start();
                playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
                updateSeekBar(); // Ensure seek bar starts updating when playback resumes
            }
        }
    }

    private void playVideo(String videoUrl) {
        if (videoUrl != null && videoView != null) {
            videoView.setVideoURI(Uri.parse(videoUrl));
            videoView.setOnPreparedListener(mp -> {
                seekBar.setMax(videoView.getDuration());
                updateSeekBar();
            });
            videoView.start();
        } else {
            Log.e("VideoPlaybackActivity", "Video URL is null or VideoView is not initialized.");
        }
    }

    private void updateSeekBar() {
        if (videoView != null) {
            runOnUiThread(() -> {
                if (videoView.isPlaying()) {
                    seekBar.setProgress(videoView.getCurrentPosition());
                    seekBar.postDelayed(this::updateSeekBar, SEEKBAR_UPDATE_INTERVAL_MS);
                }
            });
        }
    }

    private void toggleFullscreen() {
        if (isFullscreen) {
            exitFullscreen();
        } else {
            enterFullscreen();
        }
    }

    private void enterFullscreen() {
        isFullscreen = true;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        videoContainer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        // Remove videoContainer from its current parent, if any
        if (videoContainer.getParent() != null) {
            ((ViewGroup) videoContainer.getParent()).removeView(videoContainer);
        }

        fullscreenContainer.setVisibility(View.VISIBLE);
        normalModeLayout.setVisibility(View.GONE);
        fullscreenContainer.addView(videoContainer); // Add videoContainer to fullscreenContainer
        getSupportActionBar().hide();
        fullscreenToggle.setImageResource(R.drawable.baseline_fullscreen_exit_24);

        // Ensure seek bar is updated in fullscreen mode
        updateSeekBar();
    }

    private void exitFullscreen() {
        isFullscreen = false;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        fullscreenContainer.setVisibility(View.GONE);
        normalModeLayout.setVisibility(View.VISIBLE);
        getSupportActionBar().show();
        fullscreenToggle.setImageResource(R.drawable.baseline_fullscreen_24);

        // Remove videoContainer from its current parent (fullscreenContainer)
        if (videoContainer.getParent() != null) {
            ((ViewGroup) videoContainer.getParent()).removeView(videoContainer);
        }

        // Find the correct container in the normal mode layout
        RelativeLayout container = findViewById(R.id.video_container_parent);
        if (container != null) {
            container.addView(videoContainer); // Add videoContainer back to its normal mode container
        } else {
            Log.e("VideoPlaybackActivity", "Cannot find video container parent");
        }

        // Ensure seek bar is updated in normal mode
        updateSeekBar();
    }

    private void toggleMute() {
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (currentVolume > 0) {
            previousVolume = currentVolume;
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            volumeSeekBar.setProgress(0);
        } else {
            int volumeToRestore = previousVolume > 0 ? previousVolume : audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) / 2;
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volumeToRestore, 0);
            volumeSeekBar.setProgress(volumeToRestore);
        }
    }

    private void setVolume(int volume) {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoView != null) {
            wasPlaying = videoView.isPlaying();
            lastPosition = videoView.getCurrentPosition();
            videoView.pause(); // Pause video when activity pauses
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (videoView != null) {
            videoView.seekTo(lastPosition);
            if (wasPlaying) {
                videoView.start();
                playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
                updateSeekBar();
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            enterFullscreen();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            exitFullscreen();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isFullscreen) {
            exitFullscreen();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoView != null) {
            videoView.stopPlayback();
            videoView = null;
        }
    }
}
