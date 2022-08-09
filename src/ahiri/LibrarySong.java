package ahiri;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Raha
 */
public class LibrarySong {
    private SimpleStringProperty track;
    private SimpleStringProperty duration;
    
    public LibrarySong(String track,String duration){
        this.track = new SimpleStringProperty(track);
        this.duration = new SimpleStringProperty(duration);
    }

    public String getTrack() {
        return track.get();
    }
    
    public String getDuration() {
        return duration.get();
    }
}
