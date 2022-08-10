package ahiri;

import java.util.HashMap;

/**
 * This is an utility class for getting artist name associated with each song.
 * Song and artist name are stored as key value pair in the HashMap.
 */
public class Artist {
    private HashMap<String,String> hashmap = new HashMap<String,String>();
    
    /**
     * This function add a key value pair in HashMap.
     * 
     * @param name - Name of the song
     * @param artist - Name of the artist of the song
     */
    public void add(String name,String artist){
        hashmap.put(name, artist);
    }
    /**
     * This function returns artist name associated with a particular song
     * 
     * @param name - Name of the Song
     * @return - Name of the artist of the song
     */
    public String getName(String name){
        return hashmap.get(name);
    }
}
