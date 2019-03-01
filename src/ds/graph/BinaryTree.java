/**
 * BinaryTree.java
 *
 */
package ds.graph;

import java.util.function.Predicate;
import org.apache.commons.math3.util.Pair;

/**
 *
 * @author Martin Groß
 */
public class BinaryTree<D> {

    private D data;
    private BinaryTree<D> leftChild;
    private BinaryTree<D> rightChild;

    public BinaryTree(D data) {
        this.data = data;
    }

    public BinaryTree(D data, BinaryTree<D> leftChild, BinaryTree<D> rightChild) {
        this.data = data;
        this.leftChild = leftChild;
        this.rightChild = rightChild;
    }

    public BinaryTree<D> find(Predicate<D> predicate) {
        BinaryTree<D> result = null;
        if (predicate.test(data)) {
            return this;
        }
        if (leftChild != null) {
            result = leftChild.find(predicate);
            if (result != null) {
                return result;
            }
        }
        if (rightChild != null) {
            result = rightChild.find(predicate);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    public boolean test(Predicate<D> predicate) {
        return predicate.test(data) || (leftChild != null && leftChild.test(predicate)) || (rightChild != null && rightChild.test(predicate));
    }

    public D getData() {
        return data;
    }

    public void setData(D data) {
        this.data = data;
    }

    public BinaryTree<D> getLeftChild() {
        return leftChild;
    }

    public void setLeftChild(BinaryTree<D> leftChild) {
        this.leftChild = leftChild;
    }

    public BinaryTree<D> getRightChild() {
        return rightChild;
    }

    public void setRightChild(BinaryTree<D> rightChild) {
        this.rightChild = rightChild;
    }
}
