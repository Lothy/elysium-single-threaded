package org.moparscape.elysium.world;

import org.moparscape.elysium.entity.GameObject;
import org.moparscape.elysium.entity.Item;
import org.moparscape.elysium.entity.Npc;
import org.moparscape.elysium.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author lothy
 */
public final class Region {

    private static final int REGION_SIZE = 64;

    private static final int LOWER_BOUND = (REGION_SIZE / 2) - 1;

    private static final int HORIZONTAL_REGIONS = (World.MAX_WIDTH / REGION_SIZE) + 1;

    private static final int VERTICAL_REGIONS = (World.MAX_HEIGHT / REGION_SIZE) + 1;

    private static final Region[][] regions = new Region[HORIZONTAL_REGIONS][VERTICAL_REGIONS];
    private final List<Item> items = new ArrayList<>();
    private final List<Npc> npcs = new ArrayList<>();
    private final List<GameObject> objects = new ArrayList<>();
    private final List<Player> players = new ArrayList<>();

    static {
        for (int x = 0; x < HORIZONTAL_REGIONS; x++) {
            for (int y = 0; y < VERTICAL_REGIONS; y++) {
                regions[x][y] = new Region();
            }
        }
    }

    public static Region getRegion(Point p) {
        return getRegion(p.getX(), p.getY());
    }

    private static Region getRegion(int x, int y) {
        int regionX = x / REGION_SIZE;
        int regionY = y / REGION_SIZE;

        return regions[regionX][regionY];
    }

    public static Iterable<Item> getViewableItems(Point p, int radius) {
        Region[] regions = getViewableRegions(p);
        List<Item> items = new LinkedList<Item>();

        for (Region r : regions) {
            for (Item i : r.getItems()) {
                if (!i.isRemoved() && p.withinRange(i.getLocation(), radius)) {
                    items.add(i);
                }
            }
        }

        return items;
    }

    public static Iterable<Npc> getViewableNpcs(Point p, int radius) {
        Region[] regions = getViewableRegions(p);
        List<Npc> npcs = new LinkedList<Npc>();

        for (Region r : regions) {
            for (Npc n : r.getNpcs()) {
                if (p.withinRange(n.getLocation(), radius)) {
                    npcs.add(n);
                }
            }
        }

        return npcs;
    }

    public static Iterable<GameObject> getViewableObjects(Point p, int radius) {
        Region[] regions = getViewableRegions(p);
        List<GameObject> objects = new LinkedList<GameObject>();

        for (Region r : regions) {
            for (GameObject go : r.getObjects()) {
                if (!go.isRemoved() && p.withinRange(go.getLocation(), radius)) {
                    objects.add(go);
                }
            }
        }

        return objects;
    }

    public static Iterable<Player> getViewablePlayers(Point p, int radius) {
        Region[] regions = getViewableRegions(p);
        List<Player> players = new LinkedList<Player>();

        for (Region r : regions) {
            for (Player player : r.getPlayers()) {
                if (player.isLoggedIn() && p.withinRange(player.getLocation(), radius)) {
                    players.add(player);
                }
            }
        }

        return players;
    }

    public static Iterable<Player> getViewablePlayers(Player player, int radius) {
        Point loc = player.getLocation();
        Region[] regions = getViewableRegions(loc);
        List<Player> players = new LinkedList<Player>();

        for (Region r : regions) {
            for (Player p : r.getPlayers()) {
                if (p != player && p.isLoggedIn() && loc.withinRange(p.getLocation(), radius)) {
                    players.add(p);
                }
            }
        }

        return players;
    }

    private static Region[] getViewableRegions(int x, int y) {
        Region[] neighbours = new Region[4];
        int regionX = x / REGION_SIZE;
        int regionY = y / REGION_SIZE;
        neighbours[0] = regions[regionX][regionY];

        int relX = x % REGION_SIZE;
        int relY = y % REGION_SIZE;

        if (relX <= LOWER_BOUND) {
            if (relY <= LOWER_BOUND) {
                neighbours[1] = regions[regionX - 1][regionY];
                neighbours[2] = regions[regionX - 1][regionY - 1];
                neighbours[3] = regions[regionX][regionY - 1];
            } else {
                neighbours[1] = regions[regionX - 1][regionY];
                neighbours[2] = regions[regionX - 1][regionY + 1];
                neighbours[3] = regions[regionX][regionY + 1];
            }
        } else {
            if (relY <= LOWER_BOUND) {
                neighbours[1] = regions[regionX + 1][regionY];
                neighbours[2] = regions[regionX + 1][regionY - 1];
                neighbours[3] = regions[regionX][regionY - 1];
            } else {
                neighbours[1] = regions[regionX + 1][regionY];
                neighbours[2] = regions[regionX + 1][regionY + 1];
                neighbours[3] = regions[regionX][regionY + 1];
            }
        }

        return neighbours;
    }

    public static Region[] getViewableRegions(Point p) {
        return getViewableRegions(p.getX(), p.getY());
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public void addNpc(Npc npc) {
        npcs.add(npc);
    }

    public void addObject(GameObject go) {
        objects.add(go);
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public Item getItem(int itemId, Point location) {
        for (Item i : items) {
            if (i.getId() == itemId && i.getLocation().equals(location)) {
                return i;
            }
        }

        return null;
    }

    public Iterable<Item> getItems() {
        return Collections.unmodifiableCollection(items);
    }

    public Npc getNpc() {
        throw new UnsupportedOperationException();
    }

    public Iterable<Npc> getNpcs() {
        return Collections.unmodifiableCollection(npcs);
    }

    public GameObject getObject() {
        throw new UnsupportedOperationException();
    }

    public Iterable<GameObject> getObjects() {
        return Collections.unmodifiableCollection(objects);
    }

    public Player getPlayer() {
        throw new UnsupportedOperationException();
    }

    public Iterable<Player> getPlayers() {
        return Collections.unmodifiableCollection(players);
    }

    public void removeItem(Item item) {
        items.remove(item);
    }

    public void removeNpc(Npc npc) {
        npcs.remove(npc);
    }

    public void removeObject(GameObject go) {
        objects.remove(go);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(2000);
        sb.append("Players:\n");
        for (Player p : players) {
            sb.append("\t").append(p).append("\n");
        }

        sb.append("\nItems:\n");
        for (Item i : items) {
            sb.append("\t").append(i).append("\n");
        }

        return sb.toString();
    }
}
