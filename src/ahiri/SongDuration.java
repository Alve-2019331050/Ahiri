package ahiri;

import java.util.HashMap;

/**
 * This is an utility class for getting duration of each song.
 * Song and duration are stored as key value pair in the HashMap.
 * @author Alve
 */
public class SongDuration {
    private HashMap<String,String> hashmap = new HashMap<String,String>();
    /**
     * This function add a key value pair in HashMap.
     * 
     * @param name - Name of the song
     * @param duration - Duration of the song
     */
    public void add(String name,String duration){
        hashmap.put(name, duration);
    }
    /**
     * This function returns duration associated with a particular song
     * 
     * @param name - Name of the Song
     * @return - duration of the song
     */
    public String getDuration(String name){
        return hashmap.get(name);
    }
}
