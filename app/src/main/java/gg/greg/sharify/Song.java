package gg.greg.sharify;

public class Song {
    String title;
    String titleLower;
    String author;
    String album;


    public Song(String title, String titleLower, String author, String album) {
        this.title = title;
        this.titleLower = titleLower;
        this.author = author;
        this.album = album;
    }

    public Song() {
    }

    public String getTitle() {
        return title;
    }

    public String getTitleLower() {
        return titleLower;
    }

    public String getAuthor() {
        return author;
    }

    public String getAlbum() {
        return album;
    }
}
