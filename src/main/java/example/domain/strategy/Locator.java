package example.domain.strategy;

import java.util.ArrayList;
import java.util.Collection;
import example.domain.game.Location;

import example.domain.Response;
import example.domain.game.Player;

public class Locator {


    public Location locate(Collection<Response.StateLocations.PlayerLocation> playerLocations, Player myPlayer){
        Location position = null;
        for(Response.StateLocations.PlayerLocation playerLocation : playerLocations) {
            position = playerLocation.location();
            if (playerLocation.entity().equals(myPlayer)) {
                return position;
            }
        }
        return position;
    }

   /*  public ArrayList<Location> neighbours(Location location, String[][] map){
        ArrayList<Location> neighbours = new ArrayList<Location>();
        if(location.row() > 0)
            neighbours.add(new Location(location.row() - 1, location.column()));
        if(location.row() < map.length - 1)
            neighbours.add(new Location(location.row() + 1, location.column()));
        if(location.column() < map[0].length - 1)
            neighbours.add(new Location(location.row(), location.column() + 1));
        if(location.column() > 0)
            neighbours.add(new Location(location.row(), location.column() - 1));
    
        return neighbours;
    } */
    public ArrayList<Location> neighbours(Location location, String[][] map){
        ArrayList<Location> neighbours = new ArrayList<Location>();
        if(location.row() > 0 && map[location.row() - 1][location.column()] != "x")
            neighbours.add(new Location(location.row() - 1, location.column()));
        if(location.row() < map.length - 1 && map[location.row() + 1][location.column()] != "x")
            neighbours.add(new Location(location.row() + 1, location.column()));
        if(location.column() < map[0].length - 1 && map[location.row()][location.column() + 1] != "x")
            neighbours.add(new Location(location.row(), location.column() + 1));
        if(location.column() > 0 && map[location.row()][location.column() - 1] != "x")
            neighbours.add(new Location(location.row(), location.column() - 1));

        return neighbours;
    }
}
