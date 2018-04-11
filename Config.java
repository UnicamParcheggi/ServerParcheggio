import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Config {
	private String file;
	private String url, password;
	private int id;

	public Config(String filename) {
		this.file = filename;
	}

	public boolean load() {
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {

			String line;

			if ((line = br.readLine()) != null)
				url = line;

			if ((line = br.readLine()) != null)
				id = Integer.parseInt(line);

			if ((line = br.readLine()) != null)
				password = line;

		} catch (IOException e) {
			return false;
		}

		return true;
	}

	public String getUrl() {
		return url;
	}

	public String getPassword() {
		return password;
	}

	public int getId() {
		return id;
	}
}
