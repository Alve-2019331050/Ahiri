package ahiri;

import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Alve
 */
/*
    This is a model class for PlayToSongList.Each table of a particular playlist will contain an object of this class.
*/
public class PlaylistSongs {
    private SimpleStringProperty name,artist;

    public PlaylistSongs(String name, String artist) {
        this.name = new SimpleStringProperty(name);
        this.artist = new SimpleStringProperty(artist);
    }

    public String getName() {
        return name.get();
    }

    public String getArtist() {
        return artist.get();
    }
    
}
