package fr.itspower.teamfortress.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class BukkitUtility
{
  public static int[] swords = { 268, 283, 272, 267, 276 };
  private static Random rand = new Random();
  private static String nms_version = "v1.5.2";
  
  static
  {
    String bukkitVersion = Bukkit.getVersion();
    String cleanedVersion = bukkitVersion.split(java.util.regex.Pattern.quote("(MC:"))[1].split(java.util.regex.Pattern.quote(")"))[0].trim();
    nms_version = "v" + cleanedVersion;
  }
  
  public static Location fromString(String loc)
  {
    loc = loc.substring(loc.indexOf("{") + 1);
    loc = loc.substring(loc.indexOf("{") + 1);
    String worldName = loc.substring(loc.indexOf("=") + 1, loc.indexOf("}"));
    loc = loc.substring(loc.indexOf(",") + 1);
    String xCoord = loc.substring(loc.indexOf("=") + 1, loc.indexOf(","));
    loc = loc.substring(loc.indexOf(",") + 1);
    String yCoord = loc.substring(loc.indexOf("=") + 1, loc.indexOf(","));
    loc = loc.substring(loc.indexOf(",") + 1);
    String zCoord = loc.substring(loc.indexOf("=") + 1, loc.indexOf(","));
    loc = loc.substring(loc.indexOf(",") + 1);
    String pitch = loc.substring(loc.indexOf("=") + 1, loc.indexOf(","));
    loc = loc.substring(loc.indexOf(",") + 1);
    String yaw = loc.substring(loc.indexOf("=") + 1, loc.indexOf("}"));
    return new Location(Bukkit.getWorld(worldName), Double.parseDouble(xCoord), Double.parseDouble(yCoord), Double.parseDouble(zCoord), Float.parseFloat(yaw), Float.parseFloat(pitch));
  }
  
  public static List<Entity> getNearbyEntities(Location loc, double x, double y, double z)
  {
    List<Entity> entities = new ArrayList<Entity>();
    for (Entity entity : loc.getWorld().getEntities())
    {
      Location entLoc = entity.getLocation();
      boolean nearX = Math.abs(entLoc.getX() - loc.getX()) <= x;
      boolean nearY = Math.abs(entLoc.getY() - loc.getY()) <= y;
      boolean nearZ = Math.abs(entLoc.getZ() - loc.getZ()) <= z;
      if ((nearX) && (nearY) && (nearZ)) {
        entities.add(entity);
      }
    }
    return entities;
  }
  
  public static Location floorLivingEntity(LivingEntity entity)
  {
    Location eyeLoc = entity.getEyeLocation().clone();
    double eyeHeight = entity.getEyeHeight();
    Location floor = eyeLoc.clone().subtract(0.0D, Math.floor(eyeHeight) + 0.5D, 0.0D);
    for (int y = eyeLoc.getBlockY(); y > 0; y--)
    {
      Location loc = new Location(floor.getWorld(), floor.getX(), y, floor.getZ(), floor.getYaw(), floor.getPitch());
      if (!loc.getBlock().isEmpty())
      {
        floor = loc;
        break;
      }
    }
    return eyeLoc.clone().subtract(0.0D, eyeLoc.getY() - floor.getY() - 2.0D * eyeHeight, 0.0D);
  }
  
  public static Block getHighestEmptyBlockUnder(Location loc)
  {
    for (int y = loc.getBlockY(); y > 0; y--)
    {
      Location floor = new Location(loc.getWorld(), loc.getX(), y, loc.getZ(), loc.getYaw(), loc.getPitch());
      Block block = floor.getBlock();
      if (!block.isEmpty()) {
        return block;
      }
    }
    return loc.getBlock();
  }
  
  public static Location getNearbyLocation(Location loc, int minXdif, int maxXdif, int minYdif, int maxYdif, int minZdif, int maxZdif)
  {
    int modX = difInRandDirection(maxXdif, minXdif);
    int modY = difInRandDirection(maxXdif, minXdif);
    int modZ = difInRandDirection(maxXdif, minXdif);
    return loc.clone().add(modX, modY, modZ);
  }
  
  private static int difInRandDirection(int max, int min)
  {
    try
    {
      return (rand.nextBoolean() ? 1 : -1) * (rand.nextInt(Math.abs(max - min)) + min);
    }
    catch (IllegalArgumentException localIllegalArgumentException) {}
    return 0;
  }
  
  public static String getNMSVersionSlug()
  {
    return nms_version;
  }
  
  public static Block getSecondChest(Block b)
  {
    BlockFace[] faces = { BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST };
    BlockFace[] arrayOfBlockFace1;
    int j = (arrayOfBlockFace1 = faces).length;
    for (int i = 0; i < j; i++)
    {
      BlockFace face = arrayOfBlockFace1[i];
      Block bl = b.getRelative(face);
      if (((bl.getState() instanceof Chest)) || ((bl.getState() instanceof DoubleChest))) {
        return bl;
      }
    }
    return null;
  }
  
  public static boolean isDoubleChest(Block block)
  {
    if ((block == null) || (!(block.getState() instanceof Chest))) {
      return false;
    }
    Chest chest = (Chest)block.getState();
    return chest.getInventory().getContents().length == 54;
  }
  
  public static boolean locationMatch(Location loc1, Location loc2)
  {
    boolean nearX = Math.floor(loc1.getBlockX()) == Math.floor(loc2.getBlockX());
    boolean nearY = Math.floor(loc1.getBlockY()) == Math.floor(loc2.getBlockY());
    boolean nearZ = Math.floor(loc1.getBlockZ()) == Math.floor(loc2.getBlockZ());
    return (nearX) && (nearY) && (nearZ);
  }
  
  public static boolean locationMatch(Location loc1, Location loc2, int distance)
  {
    return (Math.abs(loc1.getX() - loc2.getX()) <= distance) && (Math.abs(loc1.getY() - loc2.getY()) <= distance) && (Math.abs(loc1.getZ() - loc2.getZ()) <= distance);
  }
  
  public static boolean locationMatchExact(Location loc1, Location loc2)
  {
    return locationMatchExact(loc1, loc2, 0.0D);
  }
  
  public static boolean locationMatchExact(Location loc1, Location loc2, double distance)
  {
    return loc1.distanceSquared(loc2) <= Math.pow(distance, 2.0D);
  }
}