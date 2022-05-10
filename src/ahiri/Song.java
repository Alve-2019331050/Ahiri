
package ahiri;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;


public class Song {
    private SimpleStringProperty title,album,date,duration;
    private SimpleIntegerProperty serialNo;

    public Song(Integer serialNo,String title,String album,String date,String duration) {
        this.title = new SimpleStringProperty(title);
        this.album = new SimpleStringProperty(album);
        this.date = new SimpleStringProperty(date);
        this.duration = new SimpleStringProperty(duration);
        this.serialNo = new SimpleIntegerProperty(serialNo);
    }

    public String getTitle() {
        return title.get();
    }

    public String getAlbum() {
        return album.get();
    }

    public String getDate() {
        return date.get();
    }
    
    public String getDuration() {
        return duration.get();
    }

    public Integer getSerialNo() {
        return serialNo.get();
    }
}
