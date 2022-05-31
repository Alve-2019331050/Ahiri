
package ahiri.controllers;

import ahiri.Song;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


public class ContentController{
    
    List<Song> recentlyPlayed;
    List<Song> favourites;
            
    public List<Song> getRecentlyPlayed(){
        List<Song> recentlyPlayedSongs = new ArrayList<>();
        
        Song song = new Song(1,"TUMI RABE NIROBE","","",""," Arnob","../images/tumi robe nirobe(2).jpg");
        recentlyPlayedSongs.add(song);
        
        song = new Song(1,"BHALOBESHEY SHOKHI JODI NIBHRITE JOTONE","","",""," Borno Chokroborty","../images/bhalobeshey shokhi jodi nibhrite jotone.jpg");
        recentlyPlayedSongs.add(song);
            
        song = new Song(1,"AMI CHINIGO CHINI TOMARE","","",""," Borno Chokroborty","../images/ami chinigo chini tomare.jpg");
        recentlyPlayedSongs.add(song);
        
        song = new Song(1,"AMI KAN PETE ROI","","",""," Borno Chokroborty","../images/ami kan petey roi.jpg");
        recentlyPlayedSongs.add(song);
        
        return recentlyPlayedSongs;
    }
}
