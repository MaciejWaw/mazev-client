package example.domain;

import java.util.Collection;
import java.lang.Math;

import example.domain.Response;
import example.domain.game.Direction;
import example.domain.game.Location;
import example.domain.game.Player;
import example.domain.game.Item;

public class MapAnalyzer {


    public String isItem(Collection<Response.StateLocations.ItemLocation> itemLocations , int row, int column){
        Location location;
        for(Response.StateLocations.ItemLocation itemLocation :itemLocations){
            location = itemLocation.location();
            if(location.row() == row && location.column() == column){
            /*     switch (itemLocation.entity()) {
                    case Item.Gold ignored -> {
                        return "G";
                    }
                    case  Item.Health ignored -> {
                        return "H";
                    }
                } */
                if(itemLocation.entity() instanceof Item.Gold){
                    return "G";
                }
                else if(itemLocation.entity() instanceof Item.Health){
                    return "H";
                }
                else return "G";
            }
        }
        return " ";
    }
    
    public String isCharacter(Collection<Response.StateLocations.PlayerLocation> playerLocation ,int row, int column, Player me){
        Location location;
        for(Response.StateLocations.PlayerLocation character : playerLocation){
            location = character.location();
            if(location.row() == row && location.column() == column){
                if(character.entity().equals(me)){
                    return "O";
                }
                else if(character.entity() instanceof Player.Dragon){
                    return "D";
                }
                else return "P";
            }
        }
        return " ";
    }

    public String[][] drawDragon(String[][] map, int row, int column, Collection<Response.StateLocations.PlayerLocation> playerLocation){
        int length = map.length;
        int width = map[0].length;
        int dragonSize = 1;

        for(Response.StateLocations.PlayerLocation character : playerLocation){
            Location location = character.location();
            if(location.row() == row && location.column() == column){
                Player entity = character.entity();
                if(entity instanceof Player.Dragon dragon)
                switch (dragon.size()) {
                    case Player.Dragon.Size.Small -> {
                        dragonSize = 1;
                    }
                    case  Player.Dragon.Size.Medium -> {
                        dragonSize = 2;
                    }
                    case  Player.Dragon.Size.Large -> {
                        dragonSize = 3;
                    }
                }
            }
        }

        

        for (int i = -dragonSize; i <= dragonSize; i++) {
            for (int j = -dragonSize; j <= dragonSize; j++) {
                //xxx    
                //xxx
                //xxx
                //if dragons have square field around them
                //if cell is instide of a map
                // if(i < length && i >= 0 && j < width && j >= 0 ){
                //     map[i][j] = "x";
                // }


                // x     
                //xxx
                // x
                //if dragons have 'cross' field arond them
                //if cell is instide of a map and distance from dragon is not greater than dragonSize
                if(i < length && i >= 0 && j < width && j >= 0 && Math.abs(i) + Math.abs(j) <= dragonSize){
                    map[i][j] = "x";
                }
            }
        }
        return map;
    }

    public void drawMapDebug(String[][] map){
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                System.out.print("Row = " + i + " Column = " + j + "Value = " + map[i][j]);
                }
            System.out.println();
        }
    }

    public void drawMap(String[][] map){
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                System.out.print(map[i][j]);
                }
            System.out.println();
        }
    }

    public <T> T[][] deepCopy(T[][] matrix) {
        return java.util.Arrays.stream(matrix).map(el -> el.clone()).toArray($ -> matrix.clone());
    }
}
