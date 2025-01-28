package example.domain.strategy;

import java.util.ArrayList;

import example.domain.Response.StateLocations.ItemLocation;
import example.domain.game.Location;

public class ItemInfo {
    public Location itemLocation;
    public int distance;
    public boolean isGold;

    public ItemInfo(Location itemLocation, int distance) {
        this.itemLocation = itemLocation;
        this.distance = distance;
        this.isGold = false;
    }
    
    public boolean positionEquals(ItemInfo other){
        return this.itemLocation.row() == other.itemLocation.row() && this.itemLocation.column() == other.itemLocation.column();
    }

    public boolean itemInArray(ArrayList<ItemInfo> itemInfos){
        for(ItemInfo itemInfo : itemInfos){
            if(this.positionEquals(itemInfo)){
                return true;
            }
        }
        return false;
    }
    @Override
    public String toString() {
        return "ItemInfo {" +
                "itemLocation=" + (itemLocation != null ? 
                                    "(row=" + itemLocation.row() + ", column=" + itemLocation.column() + ")" : "null") +
                ", distance=" + distance +
                '}';
    }
}
