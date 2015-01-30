package org.moparscape.elysium.io;

import org.moparscape.elysium.def.GameObjectLoc;
import org.moparscape.elysium.def.ItemLoc;
import org.moparscape.elysium.def.NPCLoc;
import org.moparscape.elysium.entity.*;
import org.moparscape.elysium.util.DataConversions;
import org.moparscape.elysium.util.PersistenceManager;
import org.moparscape.elysium.world.World;

import java.io.BufferedInputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by IntelliJ IDEA.
 *
 * @author lothy
 */
public final class WorldLoader {

    private ZipFile tileArchive;
//	private ZipOutputStream out;

    private void loadSection(int sectionX, int sectionY, int height, World world, int bigX, int bigY) {
        Sector s = null;
        try {
            String filename = "h" + height + "x" + sectionX + "y" + sectionY;
            ZipEntry e = tileArchive.getEntry(filename);
            if (e == null) {
                throw new Exception("Missing tile: " + filename);
            }
            ByteBuffer data = DataConversions.streamToBuffer(new BufferedInputStream(tileArchive.getInputStream(e)));
            s = Sector.unpack(data);
//			s = modifyAndSave(filename, s, bigX, bigY);
        } catch (Exception e) {
            System.out.println(e);
        }

        for (int y = 0; y < Sector.HEIGHT; y++) {
            for (int x = 0; x < Sector.WIDTH; x++) {
                int bx = bigX + x;
                int by = bigY + y;
                if (!world.withinWorld(bx, by)) {
                    continue;
                }
                /** start of shit **/
                if ((s.getTile(x, y).groundOverlay & 0xff) == 250) {
                    s.getTile(x, y).groundOverlay = (byte) 2;
                }
                /** break in shit **/
                int groundOverlay = s.getTile(x, y).groundOverlay & 0xFF;
                if (groundOverlay > 0 && DefinitionHandler.getTileDef(groundOverlay - 1).getObjectType() != 0) {
                    world.getTileValue(bx, by).mapValue |= 0x40; // 64
                }

                int verticalWall = s.getTile(x, y).verticalWall & 0xFF;
                if (verticalWall > 0 && DefinitionHandler.getDoorDef(verticalWall - 1).getUnknown() == 0 && DefinitionHandler.getDoorDef(verticalWall - 1).getDoorType() != 0) {
                    world.getTileValue(bx, by).mapValue |= 1; // 1
                    world.getTileValue(bx, by - 1).mapValue |= 4; // 4
                }

                int horizontalWall = s.getTile(x, y).horizontalWall & 0xFF;
                if (horizontalWall > 0 && DefinitionHandler.getDoorDef(horizontalWall - 1).getUnknown() == 0 && DefinitionHandler.getDoorDef(horizontalWall - 1).getDoorType() != 0) {
                    world.getTileValue(bx, by).mapValue |= 2; // 2
                    world.getTileValue(bx - 1, by).mapValue |= 8; // 8
                }

                int diagonalWalls = s.getTile(x, y).diagonalWalls;
                if (diagonalWalls > 0 && diagonalWalls < 12000 && DefinitionHandler.getDoorDef(diagonalWalls - 1).getUnknown() == 0 && DefinitionHandler.getDoorDef(diagonalWalls - 1).getDoorType() != 0) {
                    world.getTileValue(bx, by).mapValue |= 0x20; // 32
                }
                if (diagonalWalls > 12000 && diagonalWalls < 24000 && DefinitionHandler.getDoorDef(diagonalWalls - 12001).getUnknown() == 0 && DefinitionHandler.getDoorDef(diagonalWalls - 12001).getDoorType() != 0) {
                    world.getTileValue(bx, by).mapValue |= 0x10; // 16
                }
                /** end of shit **/
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void loadWorld(World world) {
        try {
            tileArchive = new ZipFile(new File(System.getProperty("elysium.conf.directory"), "data/Landscape.rscd"));
//			out = new ZipOutputStream(new FileOutputStream(new File(Config.CONF_DIR, "data/new_Landscape.rscd")));
//			out.setLevel(9);
        } catch (Exception e) {
            System.out.println(e);
        }

        for (int lvl = 0; lvl < 4; lvl++) {
            int wildX = 2304;
            int wildY = 1776 - (lvl * 944);
            for (int sx = 0; sx < 1000; sx += 48) {
                for (int sy = 0; sy < 1000; sy += 48) {
                    int x = (sx + wildX) / 48;
                    int y = (sy + (lvl * 944) + wildY) / 48;
                    loadSection(x, y, lvl, world, sx, sy + (944 * lvl));
                }
            }
        }

//		try { out.close(); } catch(Exception e) { Logger.error(e); }

        for (GameObjectLoc gameObject : (List<GameObjectLoc>) PersistenceManager.load("locs/GameObjectLoc.xml")) {
            world.registerGameObject(new GameObject(gameObject));
        }
        for (ItemLoc loc : (List<ItemLoc>) PersistenceManager.load("locs/ItemLoc.xml")) {
            world.registerItem(new Item(loc));
        }
        for (NPCLoc loc : (List<NPCLoc>) PersistenceManager.load("locs/NpcLoc.xml")) {
            world.registerNpc(new Npc(loc));
        }
        for (Shop shop : (List<Shop>) PersistenceManager.load("locs/Shops.xml")) {
            world.registerShop(shop);
        }

        System.gc();
    }
}
