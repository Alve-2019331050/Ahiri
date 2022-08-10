package ahiri;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * This is a model class for favourite song list.Each table of a favourite song list will contain an object of this class.
 * @author Alve
 */
public class Song {
    private SimpleStringProperty title,duration,artist,cover;
    private SimpleIntegerProperty serialNo;

    /**
     * 
     * @param serialNo - Serial no of the song in the table 
     * @param title - Name of the song
     * @param artist - Artist name of the the song
     * @param duration - Total duration of the song
     * @param cover - path of background image of the song
     */
    public Song(Integer serialNo,String title,String artist,String duration,String cover) {
        this.title = new SimpleStringProperty(title);
        this.duration = new SimpleStringProperty(duration);
        this.serialNo = new SimpleIntegerProperty(serialNo);
        this.artist = new SimpleStringProperty(artist);
        this.cover = new SimpleStringProperty(cover);
    }
    
    /**
     * Get name of the song
     * @return name of the song
     */
    public String getTitle() {
        return title.get();
    }
    
    /**
     * Get duration of the song
     * @return duration of the song
     */
    public String getDuration() {
        return duration.get();
    }

    /**
     * Get serial no of the song
     * @return serial no of the song
     */
    public Integer getSerialNo() {
        return serialNo.get();
    }
    
    /**
     * Get artist name of the song
     * @return artist name of the song
     */
    public String getArtist(){
        return artist.get();
    }
    
    /**
     * Get path of background image
     * @return path of background image
     */
    public String getCover(){
        return cover.get();
    }
}
