package protocol;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import client.DRDTChallengeClient;
import client.NetworkLayer;

/**
 * Entry point of the program. Starts the client and links the used MAC protocol.
 * 
 * @author Jaco ter Braak, Twente University
 * @version 05-12-2013
 */
public class Program {
    /*
     * Kevin Hetterscheid - s1490443
     * Ciske Harsema      - s1482149
     */

    // Change to your group number (use your student number)
    private static int groupId = 1490443;

    // Change to your group password (doesn't matter what it is,
    // as long as everyone in the group uses the same string)
    private static String password = "Welkom1";

    // Change to your protocol implementation
    private static IRDTProtocol protocolImpl = new CustomSender(); // EgoProtocol for receiver.

    // Challenge server address
    private static String serverAddress = "netsys.ewi.utwente.nl";

    // Challenge server port
    private static int serverPort = 8003;

    /*
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * DO NOT EDIT BELOW THIS LINE
     */
    public static void main(String[] args) {
        int group = groupId;

        if (args.length > 0) {
            group = Integer.parseInt(args[0]);
        }

        DRDTChallengeClient client = null;
        try {
            System.out.print("[FRAMEWORK] Starting client... ");

            // Create the client
            client = new DRDTChallengeClient(serverAddress, serverPort, group, password);

            System.out.println("Done.");

            System.out.println("[FRAMEWORK] Press Enter to start the simulation...");
            System.out
                    .println("[FRAMEWORK] (Simulation will also be started automatically if another client in the group issues the start command)");

            boolean startCommand = false;
            InputStream inputStream = new BufferedInputStream(System.in);
            while (!client.isSimulationStarted() && !client.isSimulationFinished()) {
                if (!startCommand && inputStream.available() > 0) {
                    client.requestStart();
                    startCommand = true;
                }
                Thread.sleep(10);
            }

            System.out.println("[FRAMEWORK] Simulation started!");
            System.out.println("[FRAMEWORK] Running protocol implementation...");

            protocolImpl.setNetworkLayer(new NetworkLayer(client));
            protocolImpl.run();

        } catch (IOException e) {
            System.out.println("[FRAMEWORK] Could not start the client, because: ");
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("[FRAMEWORK] Operation interrupted.");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("[FRAMEWORK] Unexpected Exception: ");
            e.printStackTrace();
        } finally {
            if (client != null) {
                System.out.print("[FRAMEWORK] Flushing output buffer... ");
                while (!client.isOutputBufferEmpty()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                System.out.println("Done.");
                System.out.print("[FRAMEWORK] Shutting down client... ");
                client.stop();
                System.out.println("Done.");
            }
            System.out.println("[FRAMEWORK] Terminating program.");
        }
    }
}
