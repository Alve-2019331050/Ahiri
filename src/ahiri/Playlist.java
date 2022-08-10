package ahiri;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * This is a model class for playlist
 * Each row of table of playlist contains one object of this class
 */
public class Playlist {
    private SimpleStringProperty name;
    private SimpleIntegerProperty songCount;

    /**
     * 
     * @param name - Name of the playlist
     * @param songCount - No of song currently in the playlist
     */
    public Playlist(String name, Integer songCount) {
        this.name = new SimpleStringProperty(name);
        this.songCount = new SimpleIntegerProperty(songCount);
    }

    /**
     * Get the name of the playlist
     * @return name of the playlist
     */
    public String getName() {
        return name.get();
    }

    /**
     * Get No of song in the playlist
     * @return No of song in the playlist
     */
    public Integer getSongCount() {
        return songCount.get();
    }
}
