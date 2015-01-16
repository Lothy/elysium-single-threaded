package org.moparscape.elysium.net.handler;

import org.moparscape.elysium.entity.Player;
import org.moparscape.elysium.net.Session;
import org.moparscape.elysium.net.codec.decoder.message.ItemWieldMessage;

/**
 * Created by IntelliJ IDEA.
 *
 * @author lothy
 */
public final class ItemWieldMessageHandler extends MessageHandler<ItemWieldMessage> {

    @Override
    public boolean handle(Session session, Player player, ItemWieldMessage message) {
        System.out.println("Wield item at index " + message.getItemIndex());
        return true;
    }
}
