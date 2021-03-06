package org.moparscape.elysium.entity;

import org.moparscape.elysium.Server;
import org.moparscape.elysium.net.Session;
import org.moparscape.elysium.task.timed.AbstractTimedTask;

/**
 * Created by daniel on 16/01/2015.
 */
public final class UnregistrableSession extends AbstractTimedTask {

    private final Session session;

    public UnregistrableSession(Session session, boolean applyPenalty) {
        super(applyPenalty ?
                        Server.getInstance().getHighResolutionTimestamp() + 30000 :
                        Server.getInstance().getHighResolutionTimestamp(),
                0);

        if (session == null) throw new IllegalArgumentException("session");

        this.session = session;
    }

    public Session getSession() {
        return session;
    }

    public void run() {

    }
}
