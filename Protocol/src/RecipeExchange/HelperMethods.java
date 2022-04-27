package RecipeExchange;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class HelperMethods {

    public HelperMethods() {

    }

    //https://stackoverflow.com/questions/26930066/bytebuffer-switch-endianness
    //https://javadeveloperzone.com/java-basic/java-convert-int-to-byte-array/
    public byte[] intToBytes(int i) {
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.putInt(i);
        return bb.array();
    }

    public int bytesToInt(byte[] b) {
        ByteBuffer bb = ByteBuffer.wrap(b);
        bb.order(ByteOrder.BIG_ENDIAN);
        return bb.getInt();
    }

    public byte[] byteArrayConc2(byte[] arr1, byte[] arr2) {
        byte[] arr3 = new byte[arr1.length + arr2.length];
        System.arraycopy(arr1, 0, arr3, 0, arr1.length);
        System.arraycopy(arr2, 0, arr3, arr1.length, arr2.length);
        return arr3;
    }

    public Integer checksum (byte[] buffer) {
        Integer lrc = 0;

        for (byte b : buffer) {
            lrc = ( lrc + b ) & 0xFF;
        }

        lrc = (((lrc ^ 0xFF) + 1) & 0xFF);

        return lrc;
    }

    public boolean checkChecksum (byte[] receivedBuffer) {

        byte receivedChecksum = receivedBuffer[4];

        receivedBuffer[4] = 0;

        Integer reCalculatedChecksum = this.checksum(receivedBuffer);

        if (receivedChecksum == reCalculatedChecksum.byteValue())
            return true;

        return  false;
    }

    public byte[] headerSetup (Integer messageNumber, Integer messageType, Integer numOfParts, Integer progOfMessage  ) {
        byte[] header = new byte[8];

        header[0] = messageNumber.byteValue();

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

    public void errorPacketSend(DatagramSocket socket, DatagramPacket receivedPacket) {

        try {
            byte[] errorBuffer = headerSetup(0, 7, 1, 1);

            errorBuffer[4] = checksum(errorBuffer).byteValue();

            DatagramPacket errorPacket = new DatagramPacket(
                    errorBuffer,
                    errorBuffer.length,
                    receivedPacket.getAddress(),
                    receivedPacket.getPort()
            );

            socket.send(errorPacket);

        } catch ( IOException e ) {
            System.out.println(
                    "Communication error while sending error packet. "
            );
        }

    }
}
