package web.finder;

import org.jsoup.select.NodeVisitor;

/**
 * Visitor interface.
 * <p>
 * This interface provides two methods, {@code head} and {@code tail}. The head method is called when the node is first
 * seen, and the tail method when all of the node's children have been visited. As an example, head can be used to
 * create a start tag for a node, and tail to create the end tag.
 * </p>
 *
 * @see NodeVisitor
 */
public interface Visitor<E> {
    void head(E e, int depth);

    void tail(E e, int depth);
}
