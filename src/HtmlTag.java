import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class HtmlTag {

    protected final String element;
    protected final boolean openTag;

    public HtmlTag(String element, boolean isOpenTag) {
        if (element == null) {
            throw new IllegalArgumentException("El elemento no puede ser null");
        }
        this.element = element.toLowerCase();
        this.openTag = isOpenTag;
    }
    public String getElement() {
        return element;
    }

    public boolean isOpenTag() {
        return openTag && !isSelfClosing();
    }

    public boolean matches(HtmlTag other) {
        return other != null
                && element.equalsIgnoreCase(other.element)
                && openTag != other.openTag;
    }

    public boolean isSelfClosing() {
        return SELF_CLOSING_TAGS.contains(element);
    }
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof HtmlTag) {
            HtmlTag other = (HtmlTag) obj;
            return element.equals(other.element)
                    && openTag == other.openTag;
        }
        return false;
    }
    @Override
    public String toString() {
        if (element.equals("!--")) {
            return openTag ? "<!--" : "-->";
        }
        return "<" + (openTag ? "" : "/") + element + ">";
    }
    protected static final Set<String> SELF_CLOSING_TAGS = new HashSet<>(
            Arrays.asList("!doctype", "!--", "?xml", "xml", "area", "base",
                    "basefont", "br", "col", "frame", "hr", "img",
                    "input", "link", "meta", "param"));

    protected static final String WHITESPACE = " \f\n\r\t";
    public static Queue<HtmlTag> tokenize(String text) {
        StringBuffer buf = new StringBuffer(text);
        Queue<HtmlTag> queue = new LinkedList<>();

        HtmlTag nextTag = nextTag(buf);
        while (nextTag != null) {
            queue.add(nextTag);
            nextTag = nextTag(buf);
        }
        return queue;
    }
    protected static HtmlTag nextTag(StringBuffer buf) {
        int openBracket = buf.indexOf("<");
        int closeBracket = buf.indexOf(">");
        if (openBracket >= 0 && closeBracket > openBracket) {
            int commentIndex = openBracket + 4;
            if (commentIndex <= buf.length()
                    && buf.substring(openBracket + 1, commentIndex).equals("!--")) {
                closeBracket = buf.indexOf("-->", commentIndex);
                if (closeBracket < 0) {
                    return null;
                } else {
                    buf.insert(commentIndex, " ");
                    closeBracket += 3;
                }
            }

            String element = buf.substring(openBracket + 1, closeBracket).trim();
            for (int i = 0; i < WHITESPACE.length(); i++) {
                int attributeIndex = element.indexOf(WHITESPACE.charAt(i));
                if (attributeIndex >= 0) {
                    element = element.substring(0, attributeIndex);
                }
            }

            boolean isOpenTag = true;
            int checkForClosing = element.indexOf("/");
            if (checkForClosing == 0) {
                isOpenTag = false;
                element = element.substring(1);
            }
            element = element.replaceAll("[^a-zA-Z0-9!-]+", "");

            buf.delete(0, closeBracket + 1);
            return new HtmlTag(element, isOpenTag);
        } else {
            return null;
        }
    }
}
