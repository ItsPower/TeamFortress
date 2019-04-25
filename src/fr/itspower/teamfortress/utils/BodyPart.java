package fr.itspower.teamfortress.utils;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public enum BodyPart {
    HEAD, BODY, LEFT_ARM, RIGHT_ARM, LEFT_LEG, RIGHT_LEG;

    public void setPose(ArmorStand stand, Location target) {
        EulerAngle ea;
        Location origin;
        double initYaw;
        double yaw;
        double pitch;
       
        if (this == HEAD) {
            origin = stand.getLocation(); //our original location (Point A)
            initYaw = origin.getYaw();
            Vector tgt = target.toVector(); //our target location (Point B)
            origin.setDirection(tgt.subtract(origin.toVector())); //set the origin's direction to be the direction vector between point A and B.
            yaw = origin.getYaw() - initYaw;
            pitch = origin.getPitch();
            if (yaw < -180) {
                yaw = yaw + 360;
            } else if (yaw >= 180) {
                yaw -= 360;
            }
            ea = new EulerAngle(Math.toRadians(pitch), Math.toRadians(yaw), 0);
            setPose(stand, ea);
        } else {
            origin = stand.getLocation();
            if (this == LEFT_ARM || this == RIGHT_ARM) {
                origin = origin.add(0, 1.4, 0);
            } else if (this == LEFT_LEG || this == RIGHT_LEG) {
                origin = origin.add(0, 0.8, 0);
            }
            initYaw = origin.getYaw();
            Vector tgt = target.toVector(); //our target location (Point B)
            origin.setDirection(tgt.subtract(origin.toVector())); //set the origin's direction to be the direction vector between point A and B.
            yaw = origin.getYaw() - initYaw;
            pitch = origin.getPitch();
            pitch -= 90;
           
            ea = new EulerAngle(Math.toRadians(pitch), Math.toRadians(yaw), 0);
            setPose(stand, ea);
        }

    }

    public void setPose(ArmorStand stand, EulerAngle angle) {
        switch (this) {
            case HEAD:
                stand.setHeadPose(angle);
                break;
            case BODY:
                stand.setBodyPose(angle);
                break;
            case LEFT_ARM:
                stand.setLeftArmPose(angle);
                break;
            case LEFT_LEG:
                stand.setLeftLegPose(angle);
                break;
            case RIGHT_ARM:
                stand.setRightArmPose(angle);
                break;
            case RIGHT_LEG:
                stand.setRightLegPose(angle);
                break;
        }
    }
}