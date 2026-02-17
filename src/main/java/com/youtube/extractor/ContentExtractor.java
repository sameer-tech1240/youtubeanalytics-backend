package com.youtube.extractor;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ContentExtractor {

    @Value("${youtube.api.key}")
    private String youtubeApiKey;

    @Autowired
    private RestTemplate restTemplate;

    /* ================= ENTRY POINT ================= */

    public String extractText(String url) {

        // YouTube video
        if (isYoutube(url)) {
            return extractYoutubeVideoContext(url);
        }

        // Direct image file
        if (isImage(url)) {
            return """
                   Content Type: Image

                   Note:
                   This link points to an image.
                   No direct textual description of the image content is available.
                   """;
        }

        // Website / article
        return extractWebsiteText(url);
    }

    /* ================= YOUTUBE ================= */

    private String extractYoutubeVideoContext(String url) {

        String videoId = extractVideoId(url);
        if (videoId == null) {
            throw new RuntimeException("Invalid YouTube URL");
        }

        String apiUrl =
                "https://www.googleapis.com/youtube/v3/videos"
                        + "?part=snippet"
                        + "&id=" + videoId
                        + "&key=" + youtubeApiKey;

        String response = restTemplate.getForObject(apiUrl, String.class);
        JSONObject json = new JSONObject(response);

        if (json.getJSONArray("items").isEmpty()) {
            throw new RuntimeException("YouTube video not found");
        }

        JSONObject snippet = json
                .getJSONArray("items")
                .getJSONObject(0)
                .getJSONObject("snippet");

        String title = snippet.getString("title");
        String description = snippet.optString("description", "");
        String channel = snippet.getString("channelTitle");

        // ONLY FACTUAL CONTEXT — NO INSTRUCTIONS
        return """
               Content Type: YouTube Educational Video

               Channel / Instructor:
               """ + channel + """

               Video Title:
               """ + title + """

               Video Description:
               """ + description;
    }

    /* ================= HELPERS ================= */

    private boolean isYoutube(String url) {
        return url.contains("youtube.com/watch") || url.contains("youtu.be");
    }

    private boolean isImage(String url) {
        return url.endsWith(".jpg")
                || url.endsWith(".jpeg")
                || url.endsWith(".png")
                || url.endsWith(".webp");
    }

    private String extractVideoId(String url) {

        if (url.contains("v=")) {
            String id = url.substring(url.indexOf("v=") + 2);
            return id.contains("&") ? id.substring(0, id.indexOf("&")) : id;
        }

        if (url.contains("youtu.be/")) {
            return url.substring(url.lastIndexOf("/") + 1);
        }

        return null;
    }

    /* ================= WEBSITE ================= */

    private String extractWebsiteText(String url) {
        try {
            String text = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .get()
                    .body()
                    .text();

            // Avoid AI overload
            return text.length() > 8000
                    ? text.substring(0, 8000)
                    : text;

        } catch (Exception e) {
            throw new RuntimeException("Unable to extract website content");
        }
    }
}
