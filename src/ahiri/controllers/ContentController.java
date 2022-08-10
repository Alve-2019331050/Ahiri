
package ahiri.controllers;

import ahiri.DatabaseConnection;
import java.sql.Connection;
import ahiri.Song;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.Statement;
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

/**
 * @author Alve
 */
public class ContentController{
    
    List<Song> recentlyPlayed;
    List<Song> favourites;
            
    public List<Song> getRecentlyPlayed(){
        List<Song> recentlyPlayedSongs = new ArrayList<>();
        
        // To store song name taken from database in the reverse order
        List<String> songList = new ArrayList<>();
        
        Connection conn =new DatabaseConnection().getConnection();
        String query = "SELECT * FROM recentlyplayed;";
        try{
            Statement statement = conn.createStatement();
            ResultSet queryResult = statement.executeQuery(query);
            while(queryResult.next()){
                String songName = queryResult.getString("song_name");
                songList.add(songName);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        // Traverse song list in reverse order to get the recently played song in order
        for(int i=songList.size()-1;i>=0;i--){
            String artistName = MediaBarController.artist.getName(songList.get(i));
            Song song = new Song(1,songList.get(i),artistName,"","../images/"+songList.get(i).toLowerCase()+".jpg");
            recentlyPlayedSongs.add(song);
        }
        
        return recentlyPlayedSongs;
    }
}
