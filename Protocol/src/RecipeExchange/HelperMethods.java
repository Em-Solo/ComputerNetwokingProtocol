package RecipeExchange;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

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

    public int indexOf(byte[] buffer, byte t) {

        for (int i = 0; i < buffer.length; i++) {
            if (buffer[i] == t) {
                return i;
            }
        }

        return buffer.length;

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

        this.errorPacketSend(socket, receivedPacket.getAddress(), receivedPacket.getPort());

    }

    public void errorPacketSend(DatagramSocket socket, InetAddress address, int port) {

        try {
            byte[] errorBuffer = headerSetup(0, 7, 1, 1);

            errorBuffer[4] = checksum(errorBuffer).byteValue();

            DatagramPacket errorPacket = new DatagramPacket(
                    errorBuffer,
                    errorBuffer.length,
                    address,
                    port
            );

            socket.send(errorPacket);

        } catch ( IOException e ) {
            System.out.println(
                    "Communication error while sending error packet. "
            );
        }

    }

    public boolean ackPacketSend(DatagramSocket socket, DatagramPacket packet) {
        byte[] packetBuffer = packet.getData();

        Byte messageNumber = packetBuffer[0];
        Byte progOfMessage = packetBuffer[3];
        byte[] ackBuffer = this.headerSetup(messageNumber.intValue(), 0, 1, progOfMessage.intValue());

        //calculating and then setting the checksum in the buffer
        ackBuffer[4] = this.checksum(ackBuffer).byteValue();

        DatagramPacket ackPacket = new DatagramPacket(
                ackBuffer,
                ackBuffer.length,
                packet.getAddress(),
                packet.getPort()
        );

        try {
            socket.send(ackPacket);
            return true;

        } catch (IOException e) {
            System.err.println("ACK packet sending : Communication error with server/client, on trying to send ACK");
            e.printStackTrace();

        }
        return false;
    }



    public String receiveMultiplePackets(DatagramSocket socket, byte[] receivingBuffer, DatagramPacket initialPacket) {

        byte[] initialPacketBuffer = initialPacket.getData();

        StringBuilder sb = new StringBuilder();
        String partOfData = null;

        byte[] dataOfInitialPacket = Arrays.copyOfRange(initialPacketBuffer, 8, initialPacketBuffer.length);

        partOfData = new String(
                dataOfInitialPacket, 0, this.indexOf(dataOfInitialPacket, (byte) 0x0), StandardCharsets.UTF_8
        );

        sb.append(partOfData);

        DatagramPacket latestPacket = initialPacket;

        //send ack
        boolean sent = false;
        for (int i = 1; i<=5; i++) {
            sent = this.ackPacketSend(socket, latestPacket);
            if (sent) {
                break;
            }
        }
        if (sent == false) {
            return "continue";
        }


        Byte numOfPartsByteNum = initialPacketBuffer[2];
        Integer numOfParts = numOfPartsByteNum.intValue();

        Integer counter = 2;

        while (counter <= numOfParts) {

            int oldCounter = counter;

            for (int i = 1; i <= 5; i++) {

                try {

                    //emptying buffer size
                    receivingBuffer = new byte[receivingBuffer.length];
                    //receiving list of recipes
                    DatagramPacket incomingPartPacket = new DatagramPacket(
                            receivingBuffer,
                            receivingBuffer.length,
                            initialPacket.getAddress(),
                            initialPacket.getPort()
                    );

                    try {
                        socket.receive(incomingPartPacket);
                    } catch (SocketTimeoutException e) {
                        System.out.println("Receiving Packet Parts: Timeout, packet was not received after timeout time passed, receiving parts");

                        sent = false;
                        for (int j = 1; i<=5; i++) {
                            sent = this.ackPacketSend(socket, latestPacket);
                            if (sent) {
                                break;
                            }
                        }
                        if (!sent) {
                            return "continue";
                        }

                        continue;

                    }

                    latestPacket = incomingPartPacket;

                    byte[] receivedPartBuffer = incomingPartPacket.getData();

                    if (this.checkChecksum(receivedPartBuffer)) {
                        System.out.println("Receiving Packet Parts: Checksum matched");

                        if (receivedPartBuffer[1] == 7) {
                            System.out.println("Receiving Packet Parts: Received error packet from sender, receiving parts, restarting");
                            return "error";
                        }


                        byte [] dataOfPacket = Arrays.copyOfRange(receivedPartBuffer, 8, receivedPartBuffer.length);

                        partOfData = new String(
                                dataOfPacket, 0, indexOf(dataOfPacket, (byte) 0x0), StandardCharsets.UTF_8
                        );

                        sb.append(partOfData);

                        System.out.println("Receiving Packet Parts: Part: " + counter + " received from Sender");

                        counter++;

                        //sending ACK
                        sent = false;
                        for (i = 1; i<=5; i++) {
                            sent = this.ackPacketSend(socket, latestPacket);
                            if (sent) {
                                break;
                            }
                        }
                        if (sent == false) {
                            return "continue";
                        }

                        break;

                    } else {
                        System.out.println("Receiving Packet Parts: Checksum doesnt match");

                        sent = false;
                        for (i = 1; i<=5; i++) {
                            sent = this.ackPacketSend(socket, latestPacket);
                            if (sent) {
                                break;
                            }
                        }
                        if (sent == false) {
                            return "continue";
                        }

                        continue;

                    }


                } catch (IOException e) {
                    System.err.println("Packet Part Receiving: Communication error with sender at part receiving, either on send or receive");
                    e.printStackTrace();

                    sent = false;
                    for (i = 1; i<=5; i++) {
                        sent = this.ackPacketSend(socket, latestPacket);
                        if (sent) {
                            break;
                        }
                    }
                    if (sent == false) {
                        return "continue";
                    }

                    continue;

                }
            }

            if (counter == oldCounter) {
                return "error";
            }

        }

        return sb.toString();

    }


    public boolean sendMultiplePackets(DatagramSocket socket, byte[] receivingBuffer, InetAddress sendToAddress, int sendToPort, byte[] thingsToSend, int fullSizeOfTargetBuffer, Integer messNum, Integer messType) {

        int sizeOfTargetBuffer = fullSizeOfTargetBuffer - 8;

        int numOfFullParts = thingsToSend.length / sizeOfTargetBuffer;

        int remainingBytes = thingsToSend.length % sizeOfTargetBuffer;

        int totalParts = 0;

        if (remainingBytes == 0) {

            totalParts = numOfFullParts;

        } else {

            totalParts = numOfFullParts + 1;

        }


        int partCounter = 1;

        boolean old = false;

        while (partCounter <= numOfFullParts) {

            try {

                byte[] header = this.headerSetup(messNum, messType, totalParts, partCounter);

                byte[] partOfData = Arrays.copyOfRange(thingsToSend, ((partCounter - 1) * sizeOfTargetBuffer), (partCounter * sizeOfTargetBuffer));

                byte[] partPacketBuffer = this.byteArrayConc2(header, partOfData);

                //calculating checksum
                partPacketBuffer[4] = this.checksum(partPacketBuffer).byteValue();



                DatagramPacket sendPacket = new DatagramPacket(
                        partPacketBuffer,
                        partPacketBuffer.length,
                        sendToAddress,
                        sendToPort
                );


                if (!old) {
                    socket.send(sendPacket);
                }


                //emptying buffer size
                receivingBuffer = new byte[receivingBuffer.length];
                //receiving list of recipes
                DatagramPacket incomingAckPacket = new DatagramPacket(
                        receivingBuffer,
                        receivingBuffer.length,
                        sendToAddress,
                        sendToPort
                );

                try {
                    socket.receive(incomingAckPacket);
                } catch (SocketTimeoutException e) {
                    System.out.println("Sending part packets: Timeout, ACK packet was not received after timeout time passed, receiving ack");
                    this.errorPacketSend(socket, incomingAckPacket);
                    return false;
                }


                if (this.checkChecksum(incomingAckPacket.getData())) {
                    System.out.println("Sending part packets: Checksum matched for ack packet: " + partCounter);


                    if (incomingAckPacket.getData()[3] == partCounter) {
                        System.out.println("Sending part packets: ACK " + partCounter + " received, proceeding to send the next part");
                        partCounter++;
                        old = false;
                    } else {
                        old = true;
                    }


                } else {
                    System.out.println("Sending part packets: Checksum doesnt match for Ack : " + partCounter);
                    this.errorPacketSend(socket,incomingAckPacket);
                    return false;
                }


            } catch (IOException e) {
                System.err.println("Sending part packets: Communication error with receiving side at packet part sending step");
                e.printStackTrace();
                this.errorPacketSend(socket, sendToAddress, sendToPort);
                return false;
            }



        }

        if (remainingBytes != 0) {

            try {

                byte[] header = this.headerSetup(messNum, messType, totalParts, partCounter);

                byte[] partOfData = Arrays.copyOfRange(thingsToSend, ((partCounter - 1) * sizeOfTargetBuffer), thingsToSend.length );

                byte[] partPacketBuffer = this.byteArrayConc2(header, partOfData);

                //calculating checksum
                partPacketBuffer[4] = this.checksum(partPacketBuffer).byteValue();

                DatagramPacket sendPacket = new DatagramPacket(
                        partPacketBuffer,
                        partPacketBuffer.length,
                        sendToAddress,
                        sendToPort
                );

                socket.send(sendPacket);

            } catch (IOException e) {
                System.err.println("Sending part packets: Communication error with receiving side at packet part sending step");
                e.printStackTrace();
                this.errorPacketSend(socket, sendToAddress, sendToPort);
                return false;
            }

        }

        return true;

    }

}
