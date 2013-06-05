import java.net.*;
import java.io.*;

/**
 * This is NOT an example of a Lime application.  Instead it is designed to
 * test whether your hosts are configured properly with respect to multicast
 * in order to run Lime.
 *
 * The McastTest application tests whether a host has the ability to
 * send/receive multicast packets.  To test this, Integer packets are sent
 * every one second to address 230.0.0.1 port 6000.  Why this address/port
 * combination?  This is the default combination that Lime uses to
 * send/receive multicast packets.  When a packet is received, the transferred
 * integer is printed along with the source of the datagram.  The value
 * transmitted is essentially a sequence number.
 *
 * To run this program, compile McastTest.java.  Then execute 'java McastTest'
 * on the two (or more) hosts that you want to test.  The following output is
 * from a test done between the hosts crow.cs.rochester, murphy.cs.rochester,
 * and heart.cs.rochester.  The trace shown is from host
 * crow.cs.rochester.edu.  As you can see, the sends from crow are shown, as
 * are the periodic receives from murphy and heart.  Both murphy and heart
 * were started after crow had already begun sending.  While crow does receive
 * its own multicast packets, they are filtered by this application.

java McastTest
Setting up multicast receiver
Multicast receiver set up
sending multicast message
sending multicast message
sending multicast message
sending multicast message
sending multicast message
Received multicast packet: 0 from: murphy.cs.rochester.edu/192.5.53.117
Received multicast packet: 1 from: murphy.cs.rochester.edu/192.5.53.117
sending multicast message
Received multicast packet: 2 from: murphy.cs.rochester.edu/192.5.53.117
sending multicast message
Received multicast packet: 3 from: murphy.cs.rochester.edu/192.5.53.117
sending multicast message
Received multicast packet: 0 from: heart.cs.rochester.edu/192.5.53.109
Received multicast packet: 4 from: murphy.cs.rochester.edu/192.5.53.117
sending multicast message
Received multicast packet: 1 from: heart.cs.rochester.edu/192.5.53.109
Received multicast packet: 5 from: murphy.cs.rochester.edu/192.5.53.117
sending multicast message
...

 * @author <a href="mailto:murphy@cs.rochester.edu">Amy Murphy</a>
 * @version 1.0
 * @since 1.0 */
class McastTest {
  public static void main (String[] args) {
    int mcastPort = 6000;
    InetAddress mcastAddr = null;
    try {
      mcastAddr = InetAddress.getByName("230.0.0.1");
    } catch (UnknownHostException uhe) {
      System.out.println("Problems getting the symbolic multicast address");
      uhe.printStackTrace(); System.exit(1);
    }
    // start new thread to receive multicasts
    new Thread(new McastReceiver(mcastPort, mcastAddr), 
               "McastReceiver").start();
    // start new thread to send multicasts
    new Thread(new McastRepeater(mcastPort, mcastAddr), 
               "McastRepeater").start();
  }
}



/**
 * The McastReceiver receives Integer datagram packets on the host:port sent
 * during its construction.  The content and source of each received packet is
 * printed.  Multicast packets sent by this host are ignored.
 *
 * @author <a href="mailto:murphy@cs.rochester.edu">Amy Murphy</a>
 * @version 1.0
 * @since 1.0
 * @see Runnable */
class McastReceiver implements Runnable {
  int mcastPort = 0;
  InetAddress mcastAddr = null;
  InetAddress localHost = null;
  
  public McastReceiver(int port, InetAddress addr) {
    mcastPort = port;
    mcastAddr = addr;
    try {
      localHost = InetAddress.getLocalHost();
    } catch (UnknownHostException uhe) {
      System.out.println("Problems identifying local host");
      uhe.printStackTrace();  System.exit(1);
    }
  }

  public void run() {
    MulticastSocket mSocket = null;
    try {
      System.out.println("Setting up multicast receiver");
      mSocket = new MulticastSocket(mcastPort);
      mSocket.joinGroup(mcastAddr);
    } catch(IOException ioe) {
      System.out.println("Trouble opening multicast port");
      ioe.printStackTrace();      System.exit(1);
    }

    DatagramPacket packet;
    System.out.println("Multicast receiver set up ");
    while (true) {
      try {
        byte[] buf = new byte[1000];
        packet = new DatagramPacket(buf,buf.length);
        //System.out.println("McastReceiver: waiting for packet");
        mSocket.receive(packet);
        
        ByteArrayInputStream bistream = 
          new ByteArrayInputStream(packet.getData());
        ObjectInputStream ois = new ObjectInputStream(bistream);
        Integer value = (Integer) ois.readObject();

        // ignore packets from myself, print the rest
        if (!(packet.getAddress().equals(localHost))) {
          System.out.println("Received multicast packet: "+
                           value.intValue() + " from: " + packet.getAddress());
        } 
        ois.close();
        bistream.close();
      } catch(IOException ioe) {
        System.out.println("Trouble reading multicast message");
        ioe.printStackTrace();  System.exit(1);
      } catch (ClassNotFoundException cnfe) {
        System.out.println("Class missing while reading mcast packet");
        cnfe.printStackTrace(); System.exit(1);
      }
    }
  }
}


/**
 * Send a datagram message every 1 second to a multicast address:port.  The
 * datagram contains only and Integer sequence number.
 *
 * @author <a href="mailto:murphy@cs.rochester.edu">Amy Murphy</a>
 * @version 1.0
 * @since 1.0
 * @see Runnable */
class McastRepeater implements Runnable {

  private DatagramSocket dgramSocket = null;
  int mcastPort = 0;
  InetAddress mcastAddr = null;
  InetAddress localHost = null;

  
  McastRepeater (int port, InetAddress addr) {
    mcastPort = port;
    mcastAddr = addr;
    try {
      dgramSocket = new DatagramSocket();
    } catch (IOException ioe){
      System.out.println("problems creating the datagram socket.");
      ioe.printStackTrace(); System.exit(1);
    }
    try {
      localHost = InetAddress.getLocalHost();
    } catch (UnknownHostException uhe) {
      System.out.println("Problems identifying local host");
      uhe.printStackTrace();  System.exit(1);
    }

  }

  public void run() {
    DatagramPacket packet = null;
    int count = 0;

    // send multicast msg once per second 
    while (true) {
      // careate the packet to sned.
      try {
        // serialize the multicast message
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream (bos);
        out.writeObject(new Integer(count++));
        out.flush();
        out.close();
        
        // Create a datagram packet and send it
        packet = new DatagramPacket(bos.toByteArray(),
                                    bos.size(),
                                    mcastAddr,
                                    mcastPort);

        // send the packet
        dgramSocket.send(packet);
        System.out.println("sending multicast message");
        Thread.sleep(1000);
      } catch(InterruptedException ie) {
        ie.printStackTrace();
      } catch (IOException ioe) {
        System.out.println("error sending multicast");
        ioe.printStackTrace(); System.exit(1);
      }
    }

  }

}
