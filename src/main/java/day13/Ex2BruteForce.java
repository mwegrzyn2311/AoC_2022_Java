package day13;

import common.FileParser;
import common.TreeNode;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static java.util.Objects.isNull;

public class Ex2BruteForce {
    public static void main(String[] args) throws Exception {
        List<String> input = FileParser.inputToStrList("/input13.txt");

        List<TreeNode<Integer>> trees = new LinkedList<>();

        for (int i = 0; i < input.size(); i += 3) {
            trees.add(intTreeFromString(input.get(i)));
            trees.add(intTreeFromString(input.get(i + 1)));
        }
        TreeNode<Integer> firstDivider = intTreeFromString("[[2]]");
        TreeNode<Integer> secondDivider = intTreeFromString("[[6]]");
        trees.add(firstDivider);
        trees.add(secondDivider);

        long start = System.currentTimeMillis();
        trees.sort((t1, t2) -> -childrenSorted(t1, t2));
        long end = System.currentTimeMillis();
        System.out.printf("Brute force time = %dms.%n", end - start);

        int firstDividerIdx = trees.indexOf(firstDivider) + 1;
        int secondDividerIdx = trees.indexOf(secondDivider) + 1;

        int decoderKey = firstDividerIdx * secondDividerIdx;
        System.out.printf("Res = %d * %d = %d.%n", firstDividerIdx, secondDividerIdx, decoderKey);
    }

    public static int childrenSorted(TreeNode<Integer> left, TreeNode<Integer> right) {
        List<TreeNode<Integer>> lChildren = left.getChildren();
        List<TreeNode<Integer>> rChildren = right.getChildren();
        if (left.isLeaf())
            lChildren = singletonList(left);
        if (right.isLeaf())
            rChildren = singletonList(right);

        for (int i = 0; i < lChildren.size() && i < rChildren.size(); ++i) {
            TreeNode<Integer> lChild = lChildren.get(i);
            TreeNode<Integer> rChild = rChildren.get(i);
            if (lChild.isLeaf() && rChild.isLeaf()) {
                if (isNull(lChild.getValue()) || isNull(rChild.getValue())) {
                    continue;
                } else if (lChild.getValue().equals(rChild.getValue())) {
                    continue;
                } else if (lChild.getValue() > rChild.getValue()) {
                    return -1;
                } else {
                    return 1;
                }
            }
            int childrenRes = childrenSorted(lChild, rChild);
            if (childrenRes != 0)
                return childrenRes;
        }

        return rChildren.size() - lChildren.size();
    }

    public static boolean treePairSorted(Pair<TreeNode<Integer>, TreeNode<Integer>> treePair) {
        System.out.println("======");
        System.out.printf("Left = %s.%n", treePair.getLeft().toString());
        System.out.printf("Left = %s.%n", treePair.getRight().toString());
        return childrenSorted(treePair.getLeft(), treePair.getRight()) > 0;
    }

    public static TreeNode<Integer> intTreeFromString(String line) throws Exception {
        TreeNode<Integer> root = new TreeNode<>(null, null);
        TreeNode<Integer> currentNode = root;
        String currentNumChar = "";
        for (char c : line.toCharArray()) {
            if (c == '[') {
                currentNode = currentNode.addEmptyChild();
            } else if (c == ',' || c == ']') {
                if (!currentNumChar.equals("")) {
                    int value = Integer.parseInt(currentNumChar);
                    currentNode.addChild(value);
                    currentNumChar = "";
                }
                if (c == ']') {
                    if (currentNode.getChildren().isEmpty())
                        currentNode.addEmptyChild();
                    currentNode = currentNode.getParent();
                }
            } else if (Character.isDigit(c)) {
                currentNumChar += c;
            } else {
                throw new Exception(format("%c is not handled input char", c));
            }
        }
        return root;
    }
}
