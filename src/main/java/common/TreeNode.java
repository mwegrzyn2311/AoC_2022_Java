package common;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class TreeNode<T> {
    private final TreeNode<T> parent;
    private final List<TreeNode<T>> children = new LinkedList<>();
    private final T value;

    public TreeNode(T value, TreeNode<T> parent) {
        this.value = value;
        this.parent = parent;
    }

    public List<TreeNode<T>> getChildren() {
        return this.children.stream()
                .filter(child -> !child.isLeaf() || nonNull(child.getValue()))
                .collect(Collectors.toList());
    }

    public TreeNode<T> getParent() {
        return parent;
    }

    public T getValue() {
        return value;
    }

    public TreeNode<T> addEmptyChild() {
        TreeNode<T> child = new TreeNode<>(null, this);
        this.children.add(child);
        return child;
    }

    public void addChild(T childValue) {
        TreeNode<T> child = new TreeNode<>(childValue, this);
        this.children.add(child);
    }

    public TreeNode<T> getLeaf() {
        TreeNode<T> res = this;
        while (!res.isLeaf()) {
            res = res.getChildren().get(0);
        }
        return res;
    }

    public boolean isRoot() {
        return isNull(parent);
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    @Override
    public String toString() {
        if (isLeaf())
            return isNull(value) ? "" : String.valueOf(value);
        String childrenStr = children.stream()
                .map(TreeNode::toString)
                .collect(Collectors.joining(","));
        return "[" + childrenStr + "]";
    }
}
