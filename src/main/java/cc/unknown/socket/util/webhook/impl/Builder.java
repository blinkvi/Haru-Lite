package cc.unknown.socket.util.webhook.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import cc.unknown.socket.util.webhook.DiscordEmbed;
import cc.unknown.socket.util.webhook.DiscordMessage;

public class Builder {

    private final DiscordMessage message = new DiscordMessage();

    public Builder withUsername(String username) {
        message.username = username;
        return this;
    }

    public Builder withAvatar(String avatar_url) {
        message.avatar_url = avatar_url;
        return this;
    }

    public Builder withContent(String content) {
        message.content = content;
        return this;
    }

    public Builder withTextToSpeech(boolean tts) {
        message.tts = tts;
        return this;
    }

    public Builder addEmbed(DiscordEmbed embed) {
        return addEmbeds(embed);
    }

    public Builder addEmbeds(DiscordEmbed... embeds) {
        if (message.embeds == null)
            message.embeds = new ArrayList<>(3);

        Collections.addAll(message.embeds, embeds);
        return this;
    }

    public Builder addFile(File file) {
        return addFiles(file);
    }

    public Builder addFiles(File... files) {
        if (message.files == null)
            message.files = new ArrayList<>(3);

        Collections.addAll(message.files, files);
        return this;
    }

    public DiscordMessage build() {
        return message;
    }
}