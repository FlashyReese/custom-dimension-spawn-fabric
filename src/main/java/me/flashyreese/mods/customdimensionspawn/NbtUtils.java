package me.flashyreese.mods.customdimensionspawn;

import net.minecraft.nbt.NbtCompound;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class NbtUtils {
    public static boolean compareNbtCompound(NbtCompound condition, NbtCompound nbtCompound) {
        AtomicBoolean atomicBoolean = null;
        for (String key : condition.getKeys()) {
            if (nbtCompound.contains(key)) {
                int type = nbtCompound.getType(key);
                int conditionType = condition.getType(key);
                if (type == conditionType) { // Check type
                    boolean state = matchesTypeAndValue(condition, nbtCompound, type, key);
                    if (atomicBoolean == null) {
                        atomicBoolean = new AtomicBoolean(state);
                    } else {
                        atomicBoolean = new AtomicBoolean(atomicBoolean.get() && state);
                    }
                } else {
                    atomicBoolean = new AtomicBoolean(false);
                }
            } else {
                atomicBoolean = new AtomicBoolean(false);
            }
        }
        return atomicBoolean != null ? atomicBoolean.get() : false;
    }

    private static boolean matchesTypeAndValue(NbtCompound nbtCompound1, NbtCompound nbtCompound2, int type, String key) {
        return switch (type) {
            case 1 -> nbtCompound1.getByte(key) == nbtCompound2.getByte(key);
            case 2 -> nbtCompound1.getShort(key) == nbtCompound2.getShort(key);
            case 3 -> nbtCompound1.getInt(key) == nbtCompound2.getInt(key);
            case 4 -> nbtCompound1.getLong(key) == nbtCompound2.getLong(key);
            case 5 -> nbtCompound1.getFloat(key) == nbtCompound2.getFloat(key);
            case 6 -> nbtCompound1.getDouble(key) == nbtCompound2.getDouble(key);
            case 7 -> Arrays.equals(nbtCompound1.getByteArray(key), nbtCompound2.getByteArray(key));
            case 8 -> nbtCompound1.getString(key).equals(nbtCompound2.getString(key));
            case 9 -> // todo: fix 'type' of list we don't know the type of list
                    nbtCompound1.getList(key, type) == nbtCompound2.getList(key, type);
            case 10 -> compareNbtCompound(nbtCompound1.getCompound(key), nbtCompound2.getCompound(key));
            case 11 -> Arrays.equals(nbtCompound1.getIntArray(key), nbtCompound2.getIntArray(key));
            case 12 -> Arrays.equals(nbtCompound1.getLongArray(key), nbtCompound2.getLongArray(key));
            default -> nbtCompound1.get(key).equals(nbtCompound2.get(key));
        };
    }
}
