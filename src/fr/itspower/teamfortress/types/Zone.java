package fr.itspower.teamfortress.types;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Zone {
	
	private final int minX, minY, minZ, maxX, maxY, maxZ;
	
	private ArrayList<Location> blocs;
	
	public Zone(Location c1, Location c2) {
		this(c1.getBlockX(), c1.getBlockY(), c1.getBlockZ(), c2.getBlockX(), c2.getBlockY(), c2.getBlockZ());
	}
	
	public Zone(int x1, int y1, int z1, int x2, int y2, int z2) {
		if (x1 < x2 || x1 == x2) {
			minX = x1;
			maxX = x2;
		} else {
			minX = x2;
			maxX = x1;
		}
		
		if (y1 < y2 || y1 == y2) {
			minY = y1 < 0 ? 0 : y1;
			maxY = y2 > 255 ? 255 : y2;
		} else {
			minY = y2 < 0 ? 0 : y1;
			maxY = y1 > 255 ? 255 : y2;
		}
		
		if (z1 < z2 || z1 == z2) {	
			minZ = z1;
			maxZ = z2;
		} else {
			minZ = z2;
			maxZ = z1;
		}
		Bukkit.broadcastMessage("§azone: "+toString()+" START");
		blocs = new ArrayList<Location>();
		for(int x = minX; x <= maxX; x++) {
			Bukkit.broadcastMessage("§a"+x);
			for(int y = minY; y <= maxY; y++) {
				Bukkit.broadcastMessage("§5"+y);
				for(int z = minZ; z <= maxZ; z++) {
					Bukkit.broadcastMessage("§4"+z);
					//if(x==minX && x==maxX && z==minZ && z==maxZ) {
					/*if(!Main.w.getBlockAt(x,y,z).getType().isSolid()) {
						blocs.add(new Location(Main.w,x,y,z));
					}*/
				}
			}
		}
		Bukkit.broadcastMessage("§azone: "+toString()+" END");
	}
	
	public String toString() {
		return minX+","+minY+","+minZ+"|"+maxX+","+maxY+","+maxZ;
	}
	
	public boolean isInArea(Location l) {
		return isInArea(l.getBlockX(), l.getBlockY(), l.getBlockZ());
	}
	
	public boolean isInArea(int x, int y, int z) {
		return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
	}

	public ArrayList<Location> getContours() {
		return blocs;
	}
}
