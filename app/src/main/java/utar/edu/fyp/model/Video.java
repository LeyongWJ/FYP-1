package utar.edu.fyp.model;

public class Video {
    private String title;
    private String description;
    private String videoUrl;
    private String thumbnailUrl; // New property for thumbnail image

    // Default constructor required for calls to DataSnapshot.getValue(Video.class)
    public Video() {}

    public Video(String title, String description, String videoUrl, String thumbnailUrl) {
        this.title = title;
        this.description = description;
        this.videoUrl = videoUrl;
        this.thumbnailUrl = thumbnailUrl;
    }

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
}
