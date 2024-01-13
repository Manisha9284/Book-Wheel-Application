package com.bookwheelapp.helper;

public class HtmlUtil {
    public static String stripHtmlTags(String html) {
        if (html == null) {
            return "";
        }
        return html.replaceAll("<[^>]+>", "").replaceAll("&nbsp;", " ");
    }
}

