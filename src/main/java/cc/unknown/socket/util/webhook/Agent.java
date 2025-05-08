package cc.unknown.socket.util.webhook;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.Gson;

public final class Agent {
	private static final Gson GSON = new Gson();
	private final String url;

	public Agent(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void execute(DiscordMessage message) {
		if (message.content == null && message.files == null && message.embeds == null)
			throw new RuntimeException("Discord message can't contain no information.");

		new Thread(() -> {
			HttpURLConnection connection = null;
			try {
				URL webhookUrl = new URL(url);
				connection = (HttpURLConnection) webhookUrl.openConnection();
				connection.setDoOutput(true);
				connection.setRequestMethod("POST");

				// Add any necessary headers here (e.g., Authorization, User-Agent)
				connection.setRequestProperty("User-Agent", "Mozilla/5.0");

				if (message.files != null && !message.files.isEmpty()) {
					String boundary = "===" + System.currentTimeMillis() + "===";
					connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

					try (OutputStream outputStream = connection.getOutputStream()) {
						writeMultipartData(outputStream, boundary, message);
					}
				} else {
					connection.setRequestProperty("Content-Type", "application/json");
					try (OutputStream outputStream = connection.getOutputStream()) {
						byte[] input = GSON.toJson(message).getBytes("utf-8");
						outputStream.write(input, 0, input.length);
					}
				}

				int responseCode = connection.getResponseCode();
				if (responseCode != HttpURLConnection.HTTP_OK) {
					System.err.println("HTTP error code: " + responseCode);
					if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
						System.err.println("Access is forbidden. Check your permissions and URL.");
					}
				} else {
					//System.out.println("Request successful.");
				}

			} catch (IOException ex) {
				ex.printStackTrace();
			} finally {
				if (connection != null) {
					connection.disconnect();
				}
			}
		}).start();
	}

	private void writeMultipartData(OutputStream outputStream, String boundary, DiscordMessage message) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("--").append(boundary).append("\r\n");
		sb.append("Content-Disposition: form-data; name=\"payload_json\"\r\n");
		sb.append("Content-Type: application/json; charset=UTF-8\r\n\r\n");
		sb.append(GSON.toJson(message)).append("\r\n");

		for (int i = 0; i < message.files.size(); i++) {
			File file = message.files.get(i);
			if (file.exists() && file.isFile()) {
				Path filePath = Paths.get(file.getAbsolutePath());
				String mimeType = Files.probeContentType(filePath);

				sb.append("--").append(boundary).append("\r\n");
				sb.append("Content-Disposition: form-data; name=\"file").append(i).append("\"; filename=\"")
						.append(file.getName()).append("\"\r\n");
				sb.append("Content-Type: ").append(mimeType != null ? mimeType : "application/octet-stream").append("\r\n\r\n");

				outputStream.write(sb.toString().getBytes("UTF-8"));
				Files.copy(filePath, outputStream);
				outputStream.write("\r\n".getBytes("UTF-8"));
				sb.setLength(0); // Clear the string builder for the next part
			} else {
				System.err.println("File [" + file.getAbsolutePath() + "] doesn't exist. It is skipped.");
			}
		}
		sb.append("--").append(boundary).append("--\r\n");
		outputStream.write(sb.toString().getBytes("UTF-8"));
	}
}