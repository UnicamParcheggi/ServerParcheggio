import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import javax.bluetooth.RemoteDevice;
import javax.microedition.io.StreamConnection;

public class AndroiClientBtThread implements Runnable {
	StreamConnection con = null;
	RemoteDevice rdev = null;
	connectionToNode connessione;

	public AndroiClientBtThread(StreamConnection connessione, String url, String token) {
		this.con = connessione;
		this.connessione = new connectionToNode(url, token);

		try {
			this.rdev = RemoteDevice.getRemoteDevice(con);
		} catch (IOException e) {
			rdev = null;
			System.out.println("Connessione accettata.\nImpossibile recuperare i dati del dispositivo remoto.\n");
			return;
		}

		System.out.println("Connessione con " + rdev.getBluetoothAddress() + " accettata.");
	}

	@Override
	public void run() {
		BufferedReader reader;
		BufferedWriter writer;
		String res;

		try {
			reader = new BufferedReader(new InputStreamReader(con.openInputStream()));
			writer = new BufferedWriter(new OutputStreamWriter(con.openOutputStream()));

			while (true) {
				res = reader.readLine();
				System.out.println("Comando ricevuto: " + res);

				if (res == null) {
					System.out.println("Errore di ricezione dati, chiusura thread.\n");
					return;
				}

				if (res.compareTo(Comandi.INGRESSO) == 0) {
					res = reader.readLine();

					System.out.println("Ricevuto codice ingresso: " + res);
					String response = connessione.inviaCode(res, "/parcheggio/entrataAutomobilista",
							connectionToNode.INGRESSO);

					if (connessione.resultIsError()) {
						System.out.println("Errore: " + response);
						response = Comandi.ERROR + "|" + response + "\n";
					} else {
						System.out.println(" -> Utente abilitato ad entrare nel parcheggio.\n");
						response = Comandi.SUCCESS + "|" + response + "\n";
					}

					writer.write(response);
					writer.flush();
					System.out.println("Thread terminato.\n");
					return;
				} else if (res.compareTo(Comandi.USCITA) == 0) {
					res = reader.readLine();

					System.out.println("Ricevuto codice uscita: " + res);
					String response = connessione.inviaCode(res, "/parcheggio/uscitaAutomobilista",
							connectionToNode.USCITA);

					if (connessione.resultIsError()) {
						System.out.println("Errore: " + response);
						response = Comandi.ERROR + "|" + response + "\n";
					} else {
						System.out.println("(pagamento avvenuto)\n -> Utente abilitato ad uscire dal parcheggio.\n");
						response = Comandi.SUCCESS + "|" + response + "\n";
					}

					writer.write(response);
					writer.flush();
					System.out.println("Thread terminato.\n");
					return;

				} else if (res.compareTo(Comandi.TERMINA) == 0) {
					con.close();
					return;
				} else {
					System.out.println("Errore: ricevuto comoando sconosciuto.\n");
					con.close();
					return;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
