import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.*;

public class connectionToNode {
	public final static int INGRESSO = 1;
	public final static int USCITA = 2;

	HttpURLConnection conn = null;
	String url, token;
	boolean error;

	public connectionToNode(String stringaUrl) {
		this.url = stringaUrl;
		this.token = null;
	}
	
	public connectionToNode(String stringaUrl, String token) {
		this.token = token;
		this.url = stringaUrl;
	}

	public String authenticateParcheggio(int id, String key) {
		String result;
		int codice;
		error = true;

		try {
			URL url = new URL(this.url + "/parcheggio/auth");
			conn = (HttpURLConnection) url.openConnection();

			if (conn != null) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("id", id);
				jsonObj.put("key", key);

				conn.setDoOutput(true);
				conn.setDoInput(true);
				conn.setUseCaches(false);
				conn.setRequestProperty("Content-Type", "application/json");
				conn.setRequestProperty("Accept", "application/json");
				conn.setRequestMethod("POST");

				DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
				dos.writeBytes(jsonObj.toString());

				codice = conn.getResponseCode();

				BufferedReader br;

				if (codice != 200)
					br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
				else
					br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

				StringBuilder sb = new StringBuilder();
				String line;

				while ((line = br.readLine()) != null)
					sb.append(line + "\n");

				result = sb.toString();

				conn.disconnect();
			} else
				return "Connessione con il server non riuscita, impossibile autenticarsi.";
		} catch (Exception e) {
			return "Riscontrati errori durante la connessione con il server, impossibile autenticarsi.";
		}

		if (codice == 200) {
			JSONObject response;
			try {
				response = new JSONObject(result);
				result = response.getString("token");
			} catch (JSONException e) {
				return "Riscontrati errori durante l'elaborazione della risposta, impossibile autenticarsi.";
			}

			error = false;
		} else {
			JSONObject response;
			try {
				response = new JSONObject(result);
				JSONObject info = response.getJSONObject("error");
				result = info.getString("info");
			} catch (JSONException e) {
				return "Riscontrati errori durante l'elaborazione della risposta, impossibile autenticarsi.";
			}
		}

		return result;
	}

	public String inviaCode(String qrCode, String path, int direzione) {
		String result;
		int codice;
		error = true;

		if (token == null)
			return "Impossibile inviare un codice senza un token di autenticazione.";
		
		try {
			URL url = new URL(this.url + path);
			conn = (HttpURLConnection) url.openConnection();

			if (conn != null) {
				String strPostData = qrCode;
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("token", token);
				jsonObj.put("QRCODE", strPostData);

				conn.setDoOutput(true);
				conn.setDoInput(true);
				conn.setUseCaches(false);
				conn.setRequestProperty("Content-Type", "application/json");
				conn.setRequestProperty("Accept", "application/json");
				conn.setRequestMethod("POST");

				DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
				dos.writeBytes(jsonObj.toString());

				codice = conn.getResponseCode();

				BufferedReader br;

				if (codice != 200)
					br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
				else {
					br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					error = false;
				}

				StringBuilder sb = new StringBuilder();
				String line;

				while ((line = br.readLine()) != null)
					sb.append(line + "\n");

				result = sb.toString();

				conn.disconnect();
			} else
				return "Connessione con il server non riuscita.";
		} catch (Exception e) {
			error = true;
			return "Riscontrati errori durante la connessione con il server.";
		}

		if (direzione == INGRESSO)
			return estraiRispostaIngresso(result, error);
		else if (direzione == USCITA)
			return estraiRispostaUscita(result, error);
		else
			return "Direzione errata.";
	}

	public boolean resultIsError() {
		return error;
	}

	private String estraiRispostaIngresso(String data, boolean resultIsError) {
		String result = "";
		String type;

		if (resultIsError)
			type = "error";
		else
			type = "successful";

		try {
			JSONObject response = new JSONObject(data);
			JSONObject info = response.getJSONObject(type);
			result = info.getString("info");

			if (!resultIsError)
				result = result + "|" + response.getInt("id");

		} catch (Exception e) {
			result = "Impossibile leggere la risposta del server.";
		}
		return result;
	}

	private String estraiRispostaUscita(String data, boolean resultIsError) {
		String result = "";
		String type;

		if (resultIsError)
			type = "error";
		else
			type = "successful";

		try {
			JSONObject response = new JSONObject(data);
			JSONObject info = response.getJSONObject(type);
			result = info.getString("info");

			if (!resultIsError)
				result = result + "|" + response.getInt("minuti");

		} catch (Exception e) {
			result = "Impossibile leggere la risposta del server.";
		}
		return result;
	}
}