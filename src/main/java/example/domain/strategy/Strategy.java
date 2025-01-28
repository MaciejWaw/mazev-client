package example.domain.strategy;

import java.util.ArrayList;
import java.util.Collection;

import example.domain.MapAnalyzer;
import example.domain.Response;
import example.domain.game.Direction;
import example.domain.game.Item;
import example.domain.game.Location;
import example.domain.game.Player;


public class Strategy {
    //TODO make it so bfs only chooses coins, hearts will be explored if no coins are uncontested
    public static Direction strategy(String[][] map,
                                     Collection<Response.StateLocations.ItemLocation> itemLocations,
                                     Collection<Response.StateLocations.PlayerLocation> playerLocations,
                                     Player me,
                                     int health,
                                     int gold){
        BFS bfs = new BFS();
        MapAnalyzer mapAnalyzer = new MapAnalyzer();
        Locator locator = new Locator();
        ArrayList<ItemInfo> playersTarget = new ArrayList<>();

        //TODO if we are lower or equal 30 hp chose closest heart as the target

        Location myLocation = locator.locate(playerLocations, me);

        //System.out.println("to jest tutaj halo halo hal          halo      " + myCoinInfos);

        // the search for closest health
        Location closestHealthLocation = null;
        ArrayList<ItemInfo> myItemInfos = bfs.bfs(mapAnalyzer.deepCopy(map), myLocation, itemLocations, false);
        ItemInfo closestItem = null;
        int closestDistance = Integer.MAX_VALUE;
        for (ItemInfo target : myItemInfos) {
            if (target != null && target.distance < closestDistance) {
                closestItem = target;
                closestDistance = target.distance;
            }
        }
        if (closestItem != null) {
            closestHealthLocation = closestItem.itemLocation;
        }

        // the search for closest coin
        Location closestGoldLocation = null;
        myItemInfos = bfs.bfs(mapAnalyzer.deepCopy(map), myLocation, itemLocations, true);
        //bfs the clostes coin for each player (their assumed target)
        for(Response.StateLocations.PlayerLocation player : playerLocations){
            if (!player.entity().equals(me))
                playersTarget.add(bfs.bfsInterrupted(mapAnalyzer.deepCopy(map), player.location(), itemLocations));
        }

        //make it so distance of my coins, that are closer for the others than for me, is "infinite"
        for (ItemInfo target : playersTarget) {
            for (ItemInfo myItem : myItemInfos) {
                if (target.positionEquals(myItem) && target.distance < myItem.distance) {
                    myItem.distance = Integer.MAX_VALUE;
                }
            }
        }

        //choose closest "uncontested" coin
        int closestGoldDistance = Integer.MAX_VALUE;
        for(ItemInfo myItem : myItemInfos){
            if(myItem.distance < closestGoldDistance){
                closestGoldLocation = myItem.itemLocation;
                closestGoldDistance = myItem.distance;
            }
        }

        Location targetLocation = null;
        if (health <= 60) {
            if (closestHealthLocation != null)
                targetLocation = closestHealthLocation;
            else
                targetLocation = closestGoldLocation;
        }
        else{
            if (closestGoldLocation != null)
                targetLocation = closestGoldLocation;
            else
                targetLocation = closestHealthLocation;
        }

        System.out.printf("health / gold: %d / %d\n", health, gold);
        System.out.println("location value:" + targetLocation);
        if(targetLocation!=null){

            String[][] mapToItem = bfs.findPathToItem(mapAnalyzer.deepCopy(map), targetLocation, myLocation);
            //mapAnalyzer.drawMapDebug(mapToItem);

            //find neightbour with lowest value. This is going to be the next step in the shortest path to our target
            int lowest = Integer.MAX_VALUE;
            Location target = null;
            ArrayList<Location> neighbours = locator.neighbours(myLocation, mapToItem);
        
            for (Location neighbour : neighbours){
                if(Integer.parseInt(mapToItem[neighbour.row()][neighbour.column()]) < lowest){
                    lowest = Integer.parseInt(mapToItem[neighbour.row()][neighbour.column()]);
                    target = neighbour;
                }
            }

            System.out.println("target value:" + target);
            
            if(target.row() == myLocation.row() && target.column() == myLocation.column() + 1){
                return Direction.Right;
            }
            else if(target.row() == myLocation.row() && target.column() == myLocation.column() - 1){
                return Direction.Left;
            }
            else if(target.row() == myLocation.row() + 1 && target.column() == myLocation.column()){
                return Direction.Down;
            }
            else if(target.row() == myLocation.row() - 1 && target.column() == myLocation.column()){
                return Direction.Up;
            }
            else return null;
        }
        else return null;
    }
}
