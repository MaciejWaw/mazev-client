package example.domain.strategy;

import example.domain.Response;
import example.domain.game.Item;
import example.domain.strategy.Locator;
import example.domain.game.Location;

import example.domain.Response.StateLocations.ItemLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class BFS {
    private Locator locator = new Locator();

    // String map implementation 
    public String[][] findPathToItem(String[][] map, Location location, Location playerLocation) {
        Queue<Location> queue = new LinkedList<>();
        Set<Location> visited = new HashSet<>();

        map[location.row()][location.column()] = "0";

        queue.add(location);
        visited.add(location);


        while (!queue.isEmpty()) {
            Location currentNode = queue.poll();

            Collection<Location> neighbors = locator.neighbours(currentNode, map);
            for (Location neighbor : neighbors) {
                if (!visited.contains(neighbor) && map[currentNode.row()][currentNode.column()] != "x") {
                    map[neighbor.row()][neighbor.column()] = String.valueOf(Integer.parseInt(map[currentNode.row()][currentNode.column()]) + 1);
                    visited.add(neighbor);
                    queue.add(neighbor);
                    /*if(currentNode.row() == playerLocation.row() && currentNode.column() == playerLocation.column()){
                        return map;
                    }*/
                }
            }
        }

        return map;
    }

    //bfs implementation of finding distance to each coin 
    public ArrayList<ItemInfo> bfs(String[][] map,
                                   Location location,
                                   Collection<Response.StateLocations.ItemLocation> itemLocations,
                                   boolean goldSearch) {

        if (goldSearch)
            itemLocations = itemLocations.stream().filter(x -> x.entity() instanceof Item.Gold).toList();
        else
            itemLocations = itemLocations.stream().filter(x -> x.entity() instanceof Item.Health).toList();

        Queue<Location> queue = new LinkedList<>();
        Set<Location> visited = new HashSet<>();
        ArrayList<ItemInfo> itemInfo = new ArrayList<>();
        ArrayList<ItemLocation> itemInfoList = new ArrayList<>();
        for (Response.StateLocations.ItemLocation itemLocation : itemLocations) {
            itemInfoList.add(itemLocation);
        }

        map[location.row()][location.column()] = "0";

        queue.add(location);
        visited.add(location);

        while (!queue.isEmpty()) {
            Location currentNode = queue.poll();

            Collection<Location> neighbors = locator.neighbours(currentNode, map);
            for (Location neighbor : neighbors) {
                if (!visited.contains(neighbor) && map[currentNode.row()][currentNode.column()] != "x") {
                    Integer newDistance = Integer.parseInt(map[currentNode.row()][currentNode.column()]) + 1;
                    map[neighbor.row()][neighbor.column()] = String.valueOf(newDistance);
                    visited.add(neighbor);
                    queue.add(neighbor);
                    for (Response.StateLocations.ItemLocation itemLocation : itemInfoList){
                        if (itemLocation.location().row() == currentNode.row() && itemLocation.location().column() == currentNode.column()){
                            ItemInfo foundItem = new ItemInfo(currentNode, newDistance);
                            foundItem.isGold = goldSearch;
                            itemInfo.add(foundItem);
                        }
                    }   
                }
            }
        }

        return itemInfo;
    }

    //bfs untill finding the first coin
    public ItemInfo bfsInterrupted(String[][] map,
                                   Location location,
                                   Collection<Response.StateLocations.ItemLocation> itemLocations) {
        Queue<Location> queue = new LinkedList<>();
        Set<Location> visited = new HashSet<>();

        map[location.row()][location.column()] = "0";

        queue.add(location);
        visited.add(location);


        while (!queue.isEmpty()) {
            Location currentNode = queue.poll();

            Collection<Location> neighbors = locator.neighbours(currentNode, map);
            for (Location neighbor : neighbors) {
                if (!visited.contains(neighbor) && map[currentNode.row()][currentNode.column()] != "x") {
                    Integer newDistance = Integer.parseInt(map[currentNode.row()][currentNode.column()]) + 1;
                    map[neighbor.row()][neighbor.column()] = String.valueOf(newDistance);
                    visited.add(neighbor);
                    queue.add(neighbor);
                    for (Response.StateLocations.ItemLocation itemLocation : itemLocations){
                        if (itemLocation.location().row() == currentNode.row() && itemLocation.location().column() == currentNode.column()){
                            return new ItemInfo(currentNode, Integer.parseInt(map[currentNode.row()][currentNode.column()]));
                        }
                    }
                }
            }
        }

        return new ItemInfo(new Location(0, 0), 0);
    }

    
}
