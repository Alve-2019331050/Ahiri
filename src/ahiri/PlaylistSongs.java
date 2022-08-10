package ahiri;

import javafx.beans.property.SimpleStringProperty;

/**
 * This is a model class for PlayToSongList.Each table of a particular playlist will contain an object of this class.
 * @author Alve
 */
public class PlaylistSongs {
    private SimpleStringProperty name,artist;

    /**
     * 
     * @param name - Name of the song
     * @param artist - Name of the artist of the song
     */
    public PlaylistSongs(String name, String artist) {
        this.name = new SimpleStringProperty(name);
        this.artist = new SimpleStringProperty(artist);
    }

    /**
     * Get song name
     * @return song name
     */
    public String getName() {
        return name.get();
    }

    /**
     * Get name of the artist
     * @return artist name
     */
    public String getArtist() {
        return artist.get();
    }
    
}
