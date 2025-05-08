package cc.unknown.socket.util.webhook;

import java.awt.Color;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.TimeZone;

import cc.unknown.socket.util.webhook.impl.*;

public class Builder {

    public final DiscordEmbed embed = new DiscordEmbed();

    public Builder withTitle(String title) {
        embed.title = title;
        return this;
    }

    public Builder withTitleUrl(String url) {
        embed.url = url;
        return this;
    }

    public Builder withDescription(String description) {
        embed.description = description;
        return this;
    }

    public Builder withColor(Color color) {
        embed.color = (((color.getRed() << 8) + color.getGreen()) << 8) + color.getBlue();
        return this;
    }

    public Builder withTimestamp(Calendar calendar) {
        embed.timestamp = OffsetDateTime.ofInstant(calendar.toInstant(), calendar.getTimeZone().toZoneId()).toString();
        ;
        return this;
    }

    public Builder withTimestamp(long timeInMillis) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.setTimeInMillis(timeInMillis);
        return withTimestamp(calendar);
    }

    public Builder withAuthor(AuthorEmbed author) {
        embed.author = author;
        return this;
    }

    public Builder withImage(ImageEmbed image) {
        embed.image = image;
        return this;
    }

    public Builder withThumbnail(ThumbnailEmbed thumbnail) {
        embed.thumbnail = thumbnail;
        return this;
    }

    public Builder withFooter(FooterEmbed footer) {
        embed.footer = footer;
        return this;
    }

    public Builder addField(FieldEmbed field) {
        return addFields(field);
    }

    public Builder addFields(FieldEmbed... fields) {
        if (embed.fields == null)
            embed.fields = new ArrayList<>(6);

        Collections.addAll(embed.fields, fields);
        return this;
    }
}