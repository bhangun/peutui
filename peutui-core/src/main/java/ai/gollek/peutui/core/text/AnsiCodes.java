package ai.gollek.peutui.core.text;

import java.util.regex.Pattern;

/**
 * Constants and small helpers for building/recognizing ANSI SGR (Select
 * Graphic Rendition) escape sequences. Kept dependency-free so it can be
 * reused by any renderer or terminal backend.
 */
public final class AnsiCodes {

    /** Matches a single CSI escape sequence, e.g. {@code \u001b[38;5;12m}. */
    public static final Pattern CSI_SEQUENCE = Pattern.compile("\u001b\\[[0-9;]*[a-zA-Z]");

    public static final String RESET = "\u001b[0m";
    public static final String BOLD = "\u001b[1m";
    public static final String DIM = "\u001b[2m";
    public static final String ITALIC = "\u001b[3m";
    public static final String UNDERLINE = "\u001b[4m";
    public static final String INVERSE = "\u001b[7m";
    public static final String STRIKETHROUGH = "\u001b[9m";

    private AnsiCodes() {
    }

    public static String fg256(int colorIndex) {
        return "\u001b[38;5;" + colorIndex + "m";
    }

    public static String bg256(int colorIndex) {
        return "\u001b[48;5;" + colorIndex + "m";
    }

    public static String fgRgb(int r, int g, int b) {
        return "\u001b[38;2;" + r + ";" + g + ";" + b + "m";
    }

    public static String bgRgb(int r, int g, int b) {
        return "\u001b[48;2;" + r + ";" + g + ";" + b + "m";
    }

    /** Strips all CSI/SGR escape sequences from the given text. */
    public static String strip(String text) {
        return CSI_SEQUENCE.matcher(text).replaceAll("");
    }

    /**
     * If {@code text} starts with a CSI sequence at {@code index}, returns its
     * length in chars; otherwise returns 0.
     */
    public static int matchLength(CharSequence text, int index) {
        var matcher = CSI_SEQUENCE.matcher(text);
        if (matcher.find(index) && matcher.start() == index) {
            return matcher.end() - matcher.start();
        }
        return 0;
    }
}
