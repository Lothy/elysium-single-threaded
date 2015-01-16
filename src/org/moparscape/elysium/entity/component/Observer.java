package org.moparscape.elysium.entity.component;

import org.moparscape.elysium.entity.*;
import org.moparscape.elysium.util.StatefulEntityCollection;
import org.moparscape.elysium.world.Point;
import org.moparscape.elysium.world.Region;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author lothy
 */
public final class Observer extends AbstractComponent {

    private final List<Bubble> bubbles = new ArrayList<>();
    private final Map<Integer, Integer> knownPlayerAppearanceIds = new HashMap<>();
    private final List<Npc> npcHitUpdates = new ArrayList<>();
    private final List<Player> playerHitUpdates = new ArrayList<>();
    private final List<Projectile> projectiles = new ArrayList<>();
    private final StatefulEntityCollection<Item> watchedItems = new StatefulEntityCollection<>();
    private final StatefulEntityCollection<Npc> watchedNpcs = new StatefulEntityCollection<>();
    private final StatefulEntityCollection<GameObject> watchedObjects = new StatefulEntityCollection<>();
    private final StatefulEntityCollection<Player> watchedPlayers = new StatefulEntityCollection<>();
    private Player owner;

    private Sprite sprite;

    public Observer(Player owner, Sprite sprite) {
        this.owner = owner;
        this.sprite = sprite;
    }

    public void addPlayerAppearanceIds(int[] indices, int[] appearanceIds) {
        for (int i = 0; i < indices.length; i++) {
            knownPlayerAppearanceIds.put(indices[i], appearanceIds[i]);
        }
    }

    public void clearDisplayLists() {
        projectiles.clear();
        playerHitUpdates.clear();
        npcHitUpdates.clear();
        bubbles.clear();
    }

    public List<Bubble> getBubblesNeedingDisplayed() {
        return bubbles;
    }

    public List<Npc> getNpcHitUpdates() {
        return npcHitUpdates;
    }

    public List<Player> getPlayerAppearanceUpdates() {
        Set<Player> newEntities = watchedPlayers.getNewEntities();
        Set<Player> knownEntities = watchedPlayers.getKnownEntities();
        List<Player> needingUpdates = new ArrayList<>(newEntities.size() + knownEntities.size());

        needingUpdates.addAll(watchedPlayers.getNewEntities());
        if (sprite.appearanceChanged()) {
            needingUpdates.add(owner);
        }

        for (Player p : watchedPlayers.getKnownEntities()) {
            if (needsAppearanceUpdateFor(p)) {
                needingUpdates.add(p);
            }
        }

        return needingUpdates;
    }

    public List<Player> getPlayerHitUpdates() {
        return playerHitUpdates;
    }

    public List<Projectile> getProjectilesNeedingDisplayed() {
        return projectiles;
    }

    public StatefulEntityCollection<Item> getWatchedItems() {
        return watchedItems;
    }

    public StatefulEntityCollection<Npc> getWatchedNpcs() {
        return watchedNpcs;
    }

    public StatefulEntityCollection<GameObject> getWatchedObjects() {
        return watchedObjects;
    }

    public StatefulEntityCollection<Player> getWatchedPlayers() {
        return watchedPlayers;
    }

    private boolean needsAppearanceUpdateFor(Player target) {
        int targetIndex = target.getIndex();
        Sprite targetSprite = target.getSprite();
        if (knownPlayerAppearanceIds.containsKey(targetIndex)) {
            int knownAppearanceId = knownPlayerAppearanceIds.get(targetIndex);
            if (knownAppearanceId != targetSprite.getAppearanceId()) {
                knownPlayerAppearanceIds.put(targetIndex, targetSprite.getAppearanceId());
                return true;
            }
        } else {
            knownPlayerAppearanceIds.put(targetIndex, targetSprite.getAppearanceId());
            return true;
        }

        return false;
    }

    @Override
    public void resolveDependencies(Map<Class<? extends Component>, Component> components) {
        this.sprite = Sprite.class.cast(components.get(Sprite.class));
    }

    public void revalidateWatchedEntities() {
        revalidateWatchedPlayers();
        revalidateWatchedObjects();
        revalidateWatchedItems();
        revalidateWatchedNpcs();
    }

    private void revalidateWatchedItems() {
        Point loc = owner.getLocation();
        for (Item i : watchedItems.getKnownEntities()) {
            if (!loc.withinRange(i.getLocation(), 16) || i.isRemoved() || !i.isVisibleTo(owner)) {
                watchedItems.remove(i);
            }
        }
    }

    private void revalidateWatchedNpcs() {
        Point loc = owner.getLocation();
        for (Npc n : watchedNpcs.getKnownEntities()) {
            if (!loc.withinRange(n.getLocation(), 16) || n.isRemoved()) {
                watchedNpcs.remove(n);
            }
        }
    }

    private void revalidateWatchedObjects() {
        Point loc = owner.getLocation();
        for (GameObject o : watchedObjects.getKnownEntities()) {
            if (!loc.withinRange(o.getLocation(), 21) || o.isRemoved()) {
                watchedObjects.remove(o);
            }
        }
    }

    private void revalidateWatchedPlayers() {
        Point loc = owner.getLocation();
        for (Player p : watchedPlayers.getKnownEntities()) {
            if (!loc.withinRange(p.getLocation(), 16) || !p.isLoggedIn()) {
                watchedPlayers.remove(p);
                knownPlayerAppearanceIds.remove(p.getIndex());
            }
        }
    }

    public void setOwner(Player player) {
        if (owner != null) {
            throw new IllegalStateException("Observer's player is already set");
        }
        this.owner = player;
    }

    public void updateEntityLists() {
        watchedPlayers.update();
        watchedObjects.update();
        watchedItems.update();
        watchedNpcs.update();
    }

    public void updateWatchedEntities() {
        updateWatchedPlayers();
        updateWatchedObjects();
        updateWatchedItems();
        updateWatchedNpcs();
    }

    private void updateWatchedItems() {
        Iterable<Item> items = Region.getViewableItems(owner.getLocation(), 16);

        for (Item item : items) {
            if (!watchedItems.contains(item) && item.isVisibleTo(owner)) {
                watchedItems.add(item);
            }
        }
    }

    private void updateWatchedNpcs() {
        Iterable<Npc> npcs = Region.getViewableNpcs(owner.getLocation(), 16);

        for (Npc npc : npcs) {
            if (!watchedNpcs.contains(npc) || watchedNpcs.isRemoving(npc)) {
                watchedNpcs.add(npc);
            }
        }
    }

    private void updateWatchedObjects() {
        Iterable<GameObject> objects = Region.getViewableObjects(owner.getLocation(), 21);

        for (GameObject go : objects) {
            if (!watchedObjects.contains(go)) {
                watchedObjects.add(go);
            }
        }
    }

    private void updateWatchedPlayers() {
        Iterable<Player> players = Region.getViewablePlayers(owner, 16);

        for (Player player : players) {
            if (!watchedPlayers.contains(player) || watchedPlayers.isRemoving(player)) {
                watchedPlayers.add(player);
            }
        }
    }
}
