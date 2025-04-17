package cc.unknown.socket.util;

import java.awt.Color;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import lombok.AllArgsConstructor;

public class Webhook {
	public final String url;
    public String content;
    public String username;
    public String avatarUrl;
    public boolean tts;
    public final List<EmbedObject> embeds = new ArrayList<>();

    public Webhook(String url) {
        this.url = url;
    }
    
    public void execute() {
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

                EmbedObject.Footer footer = embed.footer;
                EmbedObject.Image image = embed.image;
                EmbedObject.Thumbnail thumbnail = embed.thumbnail;
                EmbedObject.Author author = embed.author;
                List<EmbedObject.Field> fields = embed.fields;

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
                for (EmbedObject.Field field : fields) {
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

        try {
	        URL url = new URL(this.url);
	        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
	        connection.addRequestProperty("Content-Type", "application/json");
	        connection.addRequestProperty("User-Agent", "YourLocalLinuxUser");
	        connection.setDoOutput(true);
	        connection.setRequestMethod("POST");
	
	        OutputStream stream = connection.getOutputStream();
	        stream.write(json.toString().getBytes(StandardCharsets.UTF_8));
	        stream.flush();
	        stream.close();
	
	        connection.getInputStream().close();
	        connection.disconnect();
        } catch (Exception e) {
        	
        }
    }

    public static class EmbedObject {
        public String title;
        public String description;
        public String url;
        public Color color;

        public Footer footer;
        public Thumbnail thumbnail;
        public Image image;
        public Author author;
        public List<Field> fields = new ArrayList<>();

        public EmbedObject setTitle(String title) {
            this.title = title;
            return this;
        }

        public EmbedObject setDescription(String description) {
            this.description = description;
            return this;
        }

        public EmbedObject setUrl(String url) {
            this.url = url;
            return this;
        }

        public EmbedObject setColor(Color color) {
            this.color = color;
            return this;
        }

        public EmbedObject setFooter(String text, String icon) {
            this.footer = new Footer(text, icon);
            return this;
        }

        public EmbedObject setThumbnail(String url) {
            this.thumbnail = new Thumbnail(url);
            return this;
        }

        public EmbedObject setImage(String url) {
            this.image = new Image(url);
            return this;
        }

        public EmbedObject setAuthor(String name, String url, String icon) {
            this.author = new Author(name, url, icon);
            return this;
        }

        public EmbedObject addField(String name, String value, boolean inline) {
            this.fields.add(new Field(name, value, inline));
            return this;
        }

        @AllArgsConstructor
        public class Footer {
            public String text;
            public String iconUrl;
        }

        @AllArgsConstructor
        public class Thumbnail {
            public String url;
        }

        @AllArgsConstructor
        public class Image {
            public String url;
        }

        @AllArgsConstructor
        public class Author {
            public String name;
            public String url;
            public String iconUrl;
        }
        
        @AllArgsConstructor
        public class Field {
            public String name;
            public String value;
            public boolean inline;
        }
    }

    public class JSONObject {

        public final HashMap<String, Object> map = new HashMap<>();

        void put(String key, Object value) {
            if (value != null) {
                map.put(key, value);
            }
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            Set<Map.Entry<String, Object>> entrySet = map.entrySet();
            builder.append("{");

            int i = 0;
            for (Map.Entry<String, Object> entry : entrySet) {
                Object val = entry.getValue();
                builder.append(quote(entry.getKey())).append(":");

                if (val instanceof String) {
                    builder.append(quote(String.valueOf(val)));
                } else if (val instanceof Integer) {
                    builder.append(Integer.valueOf(String.valueOf(val)));
                } else if (val instanceof Boolean) {
                    builder.append(val);
                } else if (val instanceof JSONObject) {
                    builder.append(val.toString());
                } else if (val.getClass().isArray()) {
                    builder.append("[");
                    int len = Array.getLength(val);
                    for (int j = 0; j < len; j++) {
                        builder.append(Array.get(val, j).toString()).append(j != len - 1 ? "," : "");
                    }
                    builder.append("]");
                }

                builder.append(++i == entrySet.size() ? "}" : ",");
            }

            return builder.toString();
        }

        public String quote(String string) {
            return "\"" + string + "\"";
        }
    }
}