package gg.greg.sharify;

public class Favourite {
    String userId;

    String songId;

    Favourite() {

    }

    Favourite(String userId, String songId) {
        this.userId = userId;
        this.songId = songId;
    }

    public String getUserId() {
        return userId;
    }

    public String getSongId() {
        return songId;
    }
}
