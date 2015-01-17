package org.moparscape.elysium.net.codec.decoder.message;

import org.moparscape.elysium.net.codec.AbstractMessage;

/**
 * Created by IntelliJ IDEA.
 *
 * @author lothy
 */
public final class SpellNpcMessage extends AbstractMessage {

    private final int actionType;
    private final int actionVariable;

    public SpellNpcMessage(int actionVariable, int actionType) {
        this.actionVariable = actionVariable;
        this.actionType = actionType;
    }

    public int getActionType() {
        return actionType;
    }

    public int getActionVariable() {
        return actionVariable;
    }
}
