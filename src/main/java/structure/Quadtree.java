package structure;

import config.ParameterTest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import utils.Paillier;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author: hehe
 * @create: 2024-06-05 10:52
 * @Description:
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
public class Quadtree implements Serializable {
    private QuadtreeNode root;

    public static int maxLayer= ParameterTest.quadTreeMaxLayer;

    public Quadtree(float xMin, float yMin, float xMax, float yMax) {
        this.root = new QuadtreeNode(xMin, yMin, xMax, yMax, 0);
    }

    public void insert(Point point) {
        root.insert(point);
    }

    public List<QuadtreeNode> query(double xMin, double yMin, double xMax, double yMax) {
        List<QuadtreeNode> found = new ArrayList<>();
        root.query(xMin, yMin, xMax, yMax, found);
        return found;
    }

    public QuadtreeNode getRoot() {
        return root;
    }

    public List<Integer> queryTopKDataset(double xMin, double yMin, double xMax, double yMax,int topk){
        Map<Integer, Integer> counts = root.queryMostPoints(xMin, yMin, xMax, yMax);
        return counts.entrySet().stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                .limit(topk)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }


    /**
     *  从根节点开始遍历四叉树
     */
    public <T,R> void traverseTree() {
        traverse(root);  // 从根节点开始遍历
    }

    public void traverseTree(Function<BigInteger,BigInteger> function) {
        traverse(root,function);  // 从根节点开始遍历
    }


    /**
     * 从node节点开始遍历其所有节点
     * @param node
     */
    public <T,R> void traverse(QuadtreeNode node) {
        // 访问当前节点
        System.out.println(node);  // 或者其他你想要做的操作
        // 如果当前节点已经被分割，遍历它的子节点
        if (node.isDivided()) {
            for (QuadtreeNode child : node.getChildren()) {
                traverse(child);  // 递归遍历子节点
            }
        }
    }

    public void traverse(QuadtreeNode node, Function<BigInteger, BigInteger> function) {
        // 访问当前节点
        System.out.println(node);  // 或者其他你想要做的操作
        Map<Integer, Integer> datasetCounts = node.getDatasetCounts();
        for (Map.Entry<Integer, Integer> entry : datasetCounts.entrySet()) {
            Integer value = entry.getValue();
        }

        // 如果当前节点已经被分割，遍历它的子节点
        if (node.isDivided()) {
            for (QuadtreeNode child : node.getChildren()) {
                traverse(child);  // 递归遍历子节点
            }
        }
    }

    @Override
    public String toString() {
        return "Quadtree{" +
                "root=" + root +
                '}';
    }
}