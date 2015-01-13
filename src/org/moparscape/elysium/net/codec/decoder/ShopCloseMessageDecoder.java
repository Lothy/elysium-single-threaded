package org.moparscape.elysium.net.codec.decoder;

import io.netty.buffer.ByteBuf;
import org.moparscape.elysium.net.codec.decoder.message.ShopCloseMessage;

/**
 * Created by IntelliJ IDEA.
 *
 * @author lothy
 */
public final class ShopCloseMessageDecoder extends AbstractMessageDecoder<ShopCloseMessage> {

    public ShopCloseMessageDecoder() {
        super(ShopCloseMessage.class, 253);
    }

    public ShopCloseMessage decode(ByteBuf buffer, int length) {
        throw new UnsupportedOperationException();
    }
}
