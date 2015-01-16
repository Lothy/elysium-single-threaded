package org.moparscape.elysium.net.handler;

import org.moparscape.elysium.Server;
import org.moparscape.elysium.entity.DefaultEntityFactory;
import org.moparscape.elysium.entity.Player;
import org.moparscape.elysium.entity.UnregistrableSession;
import org.moparscape.elysium.entity.component.Credentials;
import org.moparscape.elysium.net.Packets;
import org.moparscape.elysium.net.Session;
import org.moparscape.elysium.net.codec.decoder.message.LoginMessage;
import org.moparscape.elysium.world.World;

/**
 * Created by IntelliJ IDEA.
 *
 * @author lothy
 */
public final class LoginMessageHandler extends MessageHandler<LoginMessage> {

    @Override
    public void handle(Session session, Player player, LoginMessage message) {
        if (message.isReconnecting()) {
            System.out.println("Reconnecting player -- rejecting them");

            UnregistrableSession us = new UnregistrableSession(session, false);
            Server.getInstance().queueUnregisterSession(us);
            return;
        }

        System.out.printf("Login message received! %d %d %s %s\n",
                message.getUid(), message.getVersion(),
                message.getUsername(), message.getPassword());

        Player p = DefaultEntityFactory.getInstance().newPlayer(session);
        Credentials creds = p.getCredentials();
        creds.setUsername(message.getUsername());
        creds.setPassword(message.getPassword());

        session.setPlayer(p);

        // TODO: Actually load a player and such

        Packets.sendLoginResponse(p, Packets.LoginResponse.SUCCESS);

        // This MUST happen before sending things such as the server and
        // world info to the client
        World.getInstance().registerPlayer(p);

        Packets.sendServerInfo(p);
        Packets.sendFatigue(p);
        Packets.sendWorldInfo(p);
        Packets.sendStats(p);
        Packets.sendLoginBox(p);

        session.setAllowedToDisconnect(false);
        p.setLoggedIn(true);
    }
}
