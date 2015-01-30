package org.moparscape.elysium.entity;

import org.moparscape.elysium.net.codec.decoder.message.WalkMessage;
import org.moparscape.elysium.net.codec.decoder.message.WalkTargetMessage;

/**
 * Created by IntelliJ IDEA.
 *
 * @author lothy
 */
public final class Path {

    private final int startX, startY;

    private final byte[] waypointXOffsets, waypointYOffsets;

    public Path(WalkMessage message) {
        this.startX = message.getStartX();
        this.startY = message.getStartY();
        this.waypointXOffsets = message.getXOffsets();
        this.waypointYOffsets = message.getYOffsets();
    }

    public Path(WalkTargetMessage message) {
        this.startX = message.getStartX();
        this.startY = message.getStartY();
        this.waypointXOffsets = message.getXOffsets();
        this.waypointYOffsets = message.getYOffsets();
    }

    public Path(int startX, int startY, byte[] waypointXOffsets, byte[] waypointYOffsets) {
        this.startX = startX;
        this.startY = startY;
        this.waypointXOffsets = waypointXOffsets;
        this.waypointYOffsets = waypointYOffsets;
    }

    public int getPathLength() {
        return waypointXOffsets == null ? 0 : waypointXOffsets.length;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    /**
     * Gets the X coord of the given waypoint
     */
    public int getWaypointX(int wayPoint) {
        return startX + getWaypointXoffset(wayPoint);
    }

    /**
     * Gets the X offset of the given waypoint
     */
    public byte getWaypointXoffset(int wayPoint) {
        return wayPoint >= getPathLength() ? 0 : waypointXOffsets[wayPoint];
    }

    /**
     * Gets the Y coord of the given waypoint
     */
    public int getWaypointY(int wayPoint) {
        return startY + getWaypointYoffset(wayPoint);
    }

    /**
     * Gets the Y offset of the given waypoint
     */
    public byte getWaypointYoffset(int wayPoint) {
        return wayPoint >= getPathLength() ? 0 : waypointYOffsets[wayPoint];
    }
}
