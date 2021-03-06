package org.moparscape.elysium.net.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.moparscape.elysium.Server;
import org.moparscape.elysium.entity.UnregistrableSession;
import org.moparscape.elysium.net.Session;

/**
 * Created by IntelliJ IDEA.
 *
 * @author lothy
 */
public final class ElysiumConnectionHandler extends SimpleChannelInboundHandler<Message> {

    private Session session;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        this.session = new Session(ctx.channel());

        Server server = Server.getInstance();
        server.queueRegisterSession(session);

        System.out.println("Channel connected");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("Channel inactive");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();

        // Only apply the penalty if the exception was thrown after
        // the player successfully logged in. If they aren't logged
        // in yet then just boot them immediately.
        boolean applyPenalty = session.getPlayer() != null;
        UnregistrableSession us = new UnregistrableSession(session, applyPenalty);
        Server.getInstance().queueUnregisterSession(us);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Message message) {
        session.messageReceived(message);
    }
}
