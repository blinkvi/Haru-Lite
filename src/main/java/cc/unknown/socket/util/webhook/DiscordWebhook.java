package cc.unknown.socket.util.webhook;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import cc.unknown.socket.util.webhook.impl.*;

public class DiscordWebhook {

	public final String url;
    public String content;
    public String username;
    public String avatarUrl;
    public boolean tts;
    public List<EmbedObject> embeds = new ArrayList<>();

    public DiscordWebhook(String url) {
        this.url = url;
    }

	public void addEmbed(EmbedObject embed) {
        this.embeds.add(embed);
    }

    public void execute() throws IOException {
        if (this.content == null && this.embeds.isEmpty()) {
            throw new IllegalArgumentException("Set content or add at least one EmbedObject");
        }

        JSONObject json = new JSONObject();

        json.put("content", this.content);
        json.put("username", this.username);
        json.put("avatar_url", this.avatarUrl);
        json.put("tts", this.tts);

        if (!this.embeds.isEmpty()) {
            List<JSONObject> embedObjects = new ArrayList<>();

            for (EmbedObject embed : this.embeds) {
                JSONObject jsonEmbed = new JSONObject();

                jsonEmbed.put("title", embed.title);
                jsonEmbed.put("description", embed.description);
                jsonEmbed.put("url", embed.url);

                if (embed.color != null) {
                    Color color = embed.color;
                    int rgb = color.getRed();
                    rgb = (rgb << 8) + color.getGreen();
                    rgb = (rgb << 8) + color.getBlue();

                    jsonEmbed.put("color", rgb);
                }

                Footer footer = embed.footer;
                Image image = embed.image;
                Thumbnail thumbnail = embed.thumbnail;
                Author author = embed.author;
                List<Field> fields = embed.fields;

                if (footer != null) {
                    JSONObject jsonFooter = new JSONObject();

                    jsonFooter.put("text", footer.text);
                    jsonFooter.put("icon_url", footer.iconUrl);
                    jsonEmbed.put("footer", jsonFooter);
                }

                if (image != null) {
                    JSONObject jsonImage = new JSONObject();

                    jsonImage.put("url", image.url);
                    jsonEmbed.put("image", jsonImage);
                }

                if (thumbnail != null) {
                    JSONObject jsonThumbnail = new JSONObject();

                    jsonThumbnail.put("url", thumbnail.url);
                    jsonEmbed.put("thumbnail", jsonThumbnail);
                }

                if (author != null) {
                    JSONObject jsonAuthor = new JSONObject();

                    jsonAuthor.put("name", author.name);
                    jsonAuthor.put("url", author.url);
                    jsonAuthor.put("icon_url", author.iconUrl);
                    jsonEmbed.put("author", jsonAuthor);
                }

                List<JSONObject> jsonFields = new ArrayList<>();
                for (Field field : fields) {
                    JSONObject jsonField = new JSONObject();

                    jsonField.put("name", field.name);
                    jsonField.put("value", field.value);
                    jsonField.put("inline", field.inline);

                    jsonFields.add(jsonField);
                }

                jsonEmbed.put("fields", jsonFields.toArray());
                embedObjects.add(jsonEmbed);
            }

            json.put("embeds", embedObjects.toArray());
        }

        URL url = new URL(this.url);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.addRequestProperty("Content-Type", "application/json");
        connection.addRequestProperty("User-Agent", "Mozilla/5.0 (X11; U; Linux i686) Gecko/20071127 Firefox/2.0.0.11");
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");

        OutputStream stream = connection.getOutputStream();
        stream.write(json.toString().getBytes(StandardCharsets.UTF_8));
        stream.flush();
        stream.close();

        connection.getInputStream().close(); //I'm not sure why but it doesn't work without getting the InputStream
        connection.disconnect();
    }
}