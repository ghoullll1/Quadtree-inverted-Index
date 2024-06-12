package structure;

import config.ParameterTest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    public Quadtree(double xMin, double yMin, double xMax, double yMax) {
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

    @Override
    public String toString() {
        return "Quadtree{" +
                "root=" + root +
                '}';
    }
}