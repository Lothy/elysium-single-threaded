package org.moparscape.elysium.net.handler;

import org.moparscape.elysium.entity.Player;
import org.moparscape.elysium.net.Session;
import org.moparscape.elysium.net.codec.decoder.message.TradeDeclineMessage;

/**
 * Created by IntelliJ IDEA.
 *
 * @author lothy
 */
public final class TradeDeclineMessageHandler extends MessageHandler<TradeDeclineMessage> {
    @Override
    public boolean handle(Session session, Player player, TradeDeclineMessage message) {
        //To change body of implemented methods use File | Settings | File Templates.
        return true;
    }
}
