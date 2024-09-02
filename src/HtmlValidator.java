import java.util.Queue;
import java.util.Stack;

public class HtmlValidator {

    public static Stack<HtmlTag> isValidHtml(Queue<HtmlTag> tags) {
        Stack<HtmlTag> stack = new Stack<>();

        while (!tags.isEmpty()) {
            HtmlTag currentTag = tags.poll();

            if (currentTag.isSelfClosing()) {

            } else if (currentTag.isOpenTag()) {

                stack.push(currentTag);
            } else {
                if (stack.isEmpty()) {

                    return null;
                }

                HtmlTag lastOpenTag = stack.peek();

                if (currentTag.matches(lastOpenTag)) {
                    stack.pop();
                } else {
                    return stack;
                }
            }
        }

        if (!stack.isEmpty()) {
            return stack;
        }

        return new Stack<>();
    }
}
