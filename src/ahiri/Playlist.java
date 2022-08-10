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

    public Playlist(String name, Integer songCount) {
        this.name = new SimpleStringProperty(name);
        this.songCount = new SimpleIntegerProperty(songCount);
    }

    public String getName() {
        return name.get();
    }

    public Integer getSongCount() {
        return songCount.get();
    }
}
