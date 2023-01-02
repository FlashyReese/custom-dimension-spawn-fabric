package me.flashyreese.mods.customdimensionspawn.model;

import net.minecraft.util.Identifier;

import java.util.List;

public class DimensionSpawnPoint {
    private Identifier dimensionId;
    private Identifier dimensionRespawn;
    private List<CoordinateRange> coordinateRangeList;
    private List<Identifier> biomeList;
    private double positionX;
    private double positionY;
    private double positionZ;
    private float yaw;
    private float pitch;

    public Identifier getDimensionId() {
        return dimensionId;
    }

    public Identifier getDimensionRespawn() {
        return dimensionRespawn;
    }

    public List<CoordinateRange> getCoordinateRangeList() {
        return coordinateRangeList;
    }

    public List<Identifier> getBiomeList() {
        return biomeList;
    }

    public double getPositionX() {
        return positionX;
    }

    public double getPositionY() {
        return positionY;
    }

    public double getPositionZ() {
        return positionZ;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }
}
