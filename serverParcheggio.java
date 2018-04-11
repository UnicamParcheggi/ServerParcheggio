
public class serverParcheggio {

	private static final String FILENAME = "parking.config";

	public static void main(String[] args) {
		BluecoveServer btServer;
		Thread serverMainThread;

		Config conf = new Config(FILENAME);

		if (!conf.load()) {
			System.out.println("\nErrore di lettura delle configurazioni.");
			return;
		}

		System.out.println("\nAvvio autenticazione parcheggio (id = " + conf.getId() + ").");

		connectionToNode con = new connectionToNode(conf.getUrl());
		String token = con.authenticateParcheggio(conf.getId(), conf.getPassword());

		if (con.resultIsError()) {
			System.out.println(token);
			System.out.println("\nServer chiuso.");
			return;
		}

		System.out.println("\nAutenticazione avvenuta con successo.");

		try {
			btServer = new BluecoveServer(conf.getUrl(), token);
		} catch (Exception e) {
			System.out.println("Server bluetooth non avviabile.\nApplicazione interrotta.");
			e.printStackTrace();
			return;
		}

		serverMainThread = new Thread(btServer);
		serverMainThread.start();
	}

}
