package RecipeExchange;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Header {

    public Header () {

    }

    public byte[] intToBytes( final int i ) {
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.putInt(i);
        return bb.array();
    }

    public Integer checksum (byte[] buffer) {
        Integer lrc = 0;

        for (byte b : buffer) {
            lrc = ( lrc + b ) & 0xFF;
        }

        lrc = (((lrc ^ 0xFF) + 1) & 0xFF);

        return lrc;
    }

    byte messageNumber = 0;

    public byte[] headerSetup ( Integer messageType, Integer numOfParts, Integer progOfMessage  ) {
        byte[] header = new byte[8];

        header[0] = this.messageNumber;
        this.messageNumber++;

        header[1] = messageType.byteValue();
        header[2] = numOfParts.byteValue();
        header[3] = progOfMessage.byteValue();

        //checksum set to zero initially
        header[4] = 0;

        header[5] = 0;
        header[6] = 0;
        header[7] = 0;

        return header;
    }
}
