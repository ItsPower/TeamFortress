package fr.itspower.teamfortress.utils;


import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
 
public class AABB {
   
    private Vector min, max;
    
    public AABB(Vector min, Vector max) {
        this(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ());
    }
   
    public AABB(double x1, double y1, double z1, double x2, double y2, double z2) {
        this.min = new Vector(Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2));
        this.max = new Vector(Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2));
    }
   
    private AABB(Entity e) {
        this.min = e.getLocation().toVector().add(new Vector(-0.4, 0, -0.4));
        this.max = e.getLocation().toVector().add(new Vector(0.4, 2, 0.4));
    }
    
    private AABB(Entity e, double taille) {
        this.min = e.getLocation().toVector().add(new Vector(-taille, -0.5, -taille));
        this.max = e.getLocation().toVector().add(new Vector(taille, 2.5, taille));
    }
   
    public static AABB from(Entity e) {
        return new AABB(e);
    }

    public static AABB from(Entity e, double taille) {
        return new AABB(e, taille);
    }
   
    public Vector getMin() {
        return min;
    }
   
    public Vector getMax() {
        return max;
    }
   
    public double min(int i) {
        switch (i) {
            case 0:
                return min.getX();
            case 1:
                return min.getY();
            case 2:
                return min.getZ();
            default:
                return 0;
        }
    }
   
    public double max(int i) {
        switch (i) {
            case 0:
                return max.getX();
            case 1:
                return max.getY();
            case 2:
                return max.getZ();
            default:
                return 0;
        }
    }
    public void show() {
    	
    }
    
    public boolean collides(Ray ray, double tmin, double tmax) {
        for (int i = 0; i < 3; i++) {
            double d = 1 / ray.direction(i);
            double t0 = (min(i) - ray.origin(i)) * d;
            double t1 = (max(i) - ray.origin(i)) * d;
            if (d < 0) {
                double t = t0;
                t0 = t1;
                t1 = t;
            }
            tmin = t0 > tmin ? t0 : tmin;
            tmax = t1 < tmax ? t1 : tmax;
            if (tmax <= tmin) return false;
        }
        return true;
    }

    public double collidesD(Ray ray, double tmin, double tmax) {
        for (int i = 0; i < 3; i++) {
            double d = 1 / ray.direction(i);
            double t0 = (min(i) - ray.origin(i)) * d;
            double t1 = (max(i) - ray.origin(i)) * d;
            if (d < 0) {
                double t = t0;
                t0 = t1;
                t1 = t;
            }
            tmin = t0 > tmin ? t0 : tmin;
            tmax = t1 < tmax ? t1 : tmax;
            if (tmax <= tmin) return -1;
        }
        return tmin;
    }
   
    public boolean contains(Location location) {
        if (location.getX() > max.getX()) return false;
        if (location.getY() > max.getY()) return false;
        if (location.getZ() > max.getZ()) return false;
        if (location.getX() < min.getX()) return false;
        if (location.getY() < min.getY()) return false;
        if (location.getZ() < min.getZ()) return false;
        return true;
    }

	public boolean isHeadShot(Location l) {
		if(l.getY() > min.getY()+1.5) return true;
		return false;
	}
}