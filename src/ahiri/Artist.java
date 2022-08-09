package ahiri;

import java.util.HashMap;

/**
 *
 * This is an utility class for getting artist name associated with each song.
 * Song and artist name are stored as key value pair in the HashMap.
 */
public class Artist {
    private HashMap<String,String> hashmap = new HashMap<String,String>();
    
    /**
     * This function add a key value pair in HashMap.
     */
    public void add(String name,String artist){
        hashmap.put(name, artist);
    }
    
    public String getName(String name){
        return hashmap.get(name);
    }
}
