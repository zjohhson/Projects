package crossword;

/**
 * Immutable data type representing a player playing the crossword game
 *
 */
public class Player {
    
    private final String playerID;
    
    // Abstraction function:
    //   AF(playerID) = the player with id playerID
    // Representation invariant:
    //   true
    // Safety from rep exposure:
    //   all fields are private, final, and immutable
    // Thread safety argument:
    //   Datatype is threadsafe immutable: 
    //     - no mutators, all fields are private and final, no rep exposure 
    
    public Player(String playerID) {
        this.playerID = playerID;
    }
    
    
    private void checkRep() {
    }
    
    public String getID() {
        checkRep();
        return playerID;
    }
    
    
    @Override
    public boolean equals(Object that) {
        return that instanceof Player && this.sameValue((Player) that);
    }    

    private boolean sameValue(Player that) {
        return this.playerID.equals(that.playerID);
    }
    
    @Override
    public int hashCode() {
        return playerID.hashCode();
    }

}