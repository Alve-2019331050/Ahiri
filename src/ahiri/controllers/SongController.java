
package ahiri.controllers;

import ahiri.Song;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class SongController implements Initializable {

    @FXML
    private ImageView img;
    @FXML
    private Label songName;
    @FXML
    private Label artist;

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    
    
    public void setData(Song song){
        Image image = new Image(getClass().getResourceAsStream(song.getCover()));
        img.setImage(image);
        songName.setText(song.getTitle());
        artist.setText(song.getArtist());
    }
}
