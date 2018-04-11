import javax.bluetooth.*;
import javax.microedition.io.*;

public class BluecoveServer implements Runnable {
	LocalDevice local = null;
	StreamConnectionNotifier notifier;
	String urlremote;
	String token;

	public BluecoveServer(String urlremote, String token) throws Exception {
		this.urlremote = urlremote;
		this.token = token;
		local = LocalDevice.getLocalDevice();
		local.setDiscoverable(DiscoveryAgent.GIAC);
		
		UUID uuid = new UUID("0000110100001000800000805f9b34fb", false);
		String url = "btspp://localhost:" + uuid.toString() + ";name=ParkingUnicam;authenticate=false;encrypt=false;";
		notifier = (StreamConnectionNotifier) Connector.open(url);
		
		System.out.println("\nIndirizzo BT locale: " + local.getBluetoothAddress() + "\nUUID servizio: " + uuid.toString());
	}

	@Override
	public void run() {
		StreamConnection connection = null;
		System.out.println("\nServer RFCOMM avviato.\n");

		while (true) {
			try {
				connection = notifier.acceptAndOpen();
				new Thread(new AndroiClientBtThread(connection, urlremote, token)).start();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
	}

}
