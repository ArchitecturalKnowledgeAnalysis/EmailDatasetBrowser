package nl.andrewl.emaildatasetbrowser.util;

import java.util.regex.Pattern;

public final class HTMLHelper {

    public static final String TAG_START = "<\\w+((\\s+\\w+(\\s*=\\s*(?:\".*?\"|'.*?'|[^'\">\\s]+))?)+\\s*|\\s*)>";
    public static final String TAG_END = "</\\w+>";
    public static final String TAG_SELF_CLOSING = "<\\w+((\\s+\\w+(\\s*=\\s*(?:\".*?\"|'.*?'|[^'\">\\s]+))?)+\\s*|\\s*)/>";
    public static final String HTML_ENTITY = "&[a-zA-Z][a-zA-Z0-9]+;";
    public static final Pattern htmlPattern = Pattern
            .compile("(" + TAG_START + ".*" + TAG_END + ")|(" + TAG_SELF_CLOSING + ")|(" + HTML_ENTITY + ")",
                    Pattern.DOTALL);

    private HTMLHelper() {
    }

    public static boolean isHtml(String htmlString) {
        boolean isHTML = false;
        if (htmlString != null) {
            isHTML = htmlPattern.matcher(htmlString).find();
        }
        return isHTML;
    }
}
