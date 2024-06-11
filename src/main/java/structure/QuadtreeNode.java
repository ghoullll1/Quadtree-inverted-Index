package structure;

import config.Parameter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.*;

/**
 * @author: hehe
 * @create: 2024-06-05 20:32
 * @Description:
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
public class QuadtreeNode {
    private double xMin, yMin, xMax, yMax;
    private List<Point> points;
    private boolean divided;
    private QuadtreeNode[] children;
    private Map<Integer, Integer> datasetCounts;
    private int layer = 0;


    @Override
    public String toString() {
        return "QuadtreeNode{" +
                "xMin=" + xMin +
                ", yMin=" + yMin +
                ", xMax=" + xMax +
                ", yMax=" + yMax +
                ", divided=" + divided +
                ", children=" + Arrays.toString(children) +
                ", datasetCounts=" + datasetCounts +
                ", layer=" + layer +
                '}';
    }

    public QuadtreeNode(double xMin, double yMin, double xMax, double yMax, int layer) {
        this.xMin = xMin;
        this.yMin = yMin;
        this.xMax = xMax;
        this.yMax = yMax;
        this.points = new ArrayList<>();
        this.divided = false;
        this.datasetCounts = new HashMap<>();
        this.layer = layer;
    }

    private void subdivide() {
        double xMid = (xMin + xMax) / 2;
        double yMid = (yMin + yMax) / 2;

        children = new QuadtreeNode[4];
        children[0] = new QuadtreeNode(xMin, yMin, xMid, yMid, layer + 1); // SW
        children[1] = new QuadtreeNode(xMid, yMin, xMax, yMid, layer + 1); // SE
        children[2] = new QuadtreeNode(xMin, yMid, xMid, yMax, layer + 1); // NW
        children[3] = new QuadtreeNode(xMid, yMid, xMax, yMax, layer + 1); // NE

        divided = true;
    }

    public void insert(Point point) {
        if (layer <= Parameter.quadTreeMaxLayer) {
            if (contains(point)) {
                datasetCounts.put(point.getDatasetId(), datasetCounts.getOrDefault(point.getDatasetId(), 0) + 1);
                if (layer+1 < Parameter.quadTreeMaxLayer&&!divided) {
                    subdivide();
                }
                if(children!=null){
                    for (QuadtreeNode child : children) {
                        child.insert(point);
                    }
                }
            }
        }
    }


    private boolean contains(Point point) {
        return (point.x >= xMin && point.x <= xMax && point.y >= yMin && point.y <= yMax);
    }

    public void query(double xMin, double yMin, double xMax, double yMax, List<QuadtreeNode> found) {
        if(isContainedByRange(xMin, yMin, xMax, yMax)||(this.layer+1==Parameter.quadTreeMaxLayer)&&intersects(xMin, yMin, xMax, yMax)){
            found.add(this);
        }else {
            if (divided) {
                for (QuadtreeNode child : children) {
                    child.query(xMin, yMin, xMax, yMax, found);
                }
            }
        }
    }

    private boolean intersects(double xMin, double yMin, double xMax, double yMax) {
        return !(xMax < this.xMin || xMin > this.xMax || yMax < this.yMin || yMin > this.yMax);
    }

    public Map<Integer, Integer> getDatasetCounts() {
        return datasetCounts;
    }

    private boolean isContainedByRange(double xMin, double yMin, double xMax, double yMax) {
        return this.xMin >= xMin && this.yMin >= yMin && this.xMax <= xMax && this.yMax <= yMax;
    }

    /**
     * 查询指定范围内数据集点数最多的数据集
     *
     * @param xMin
     * @param yMin
     * @param xMax
     * @param yMax
     * @return
     */
    public Map<Integer, Integer> queryMostPoints(double xMin, double yMin, double xMax, double yMax) {
        Map<Integer, Integer> counts = new HashMap<>();
        List<QuadtreeNode> nodes = new ArrayList<>();
        query(xMin, yMin, xMax, yMax, nodes);

        for (QuadtreeNode node : nodes) {
            Map<Integer, Integer> nodeCounts = node.getDatasetCounts();
            for (Map.Entry<Integer, Integer> entry : nodeCounts.entrySet()) {
                counts.put(entry.getKey(), counts.getOrDefault(entry.getKey(), 0) + entry.getValue());
            }
        }

        return counts;
    }
}
