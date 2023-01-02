package me.flashyreese.mods.customdimensionspawn.model;

import net.minecraft.util.math.Vec3d;

public class CoordinateRange {
    private double startX;
    private double startY;
    private double startZ;
    private double endX;
    private double endY;
    private double endZ;

    public boolean isWithinRange(Vec3d vec3d) {
        return this.isWithinRange(vec3d.x, vec3d.y, vec3d.z);
    }

    public boolean isWithinRange(double x, double y, double z) {
        // Check if x is within the range
        boolean xWithinRange = (x >= startX && x <= endX);

        // Check if y is within the range
        boolean yWithinRange = (y >= startY && y <= endY);

        // Check if z is within the range
        boolean zWithinRange = (z >= startZ && z <= endZ);

        // Return true if all coordinates are within range, false otherwise
        return xWithinRange && yWithinRange && zWithinRange;
    }

    public double getStartX() {
        return startX;
    }

    public double getStartY() {
        return startY;
    }

    public double getStartZ() {
        return startZ;
    }

    public double getEndX() {
        return endX;
    }

    public double getEndY() {
        return endY;
    }

    public double getEndZ() {
        return endZ;
    }
}
