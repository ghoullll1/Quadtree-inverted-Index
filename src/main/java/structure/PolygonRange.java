package structure;

import lombok.Data;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * @author: hehe
 * @create: 2024-06-25 14:54
 * @Description:
 */
@Data
public class PolygonRange {

    public static List<Point2D.Double> generateRandomPolygon(int numVertices) {
        Random random = new Random();
        List<Point2D.Double> points = new ArrayList<>();

        // 随机生成顶点坐标
        for (int i = 0; i < numVertices; i++) {
            double x = -180 + (360 * random.nextDouble());
            double y = -90 + (180 * random.nextDouble());
            points.add(new Point2D.Double(x, y));
        }

        // 确保顶点按顺时针或逆时针顺序排列
        orderPointsClockwise(points);

        return points;
    }

    // 按顺时针顺序排列点
    private static void orderPointsClockwise(List<Point2D.Double> points) {
        // 计算质心
        Point2D.Double center = new Point2D.Double(0, 0);
        for (Point2D.Double point : points) {
            center.x += point.x;
            center.y += point.y;
        }
        center.x /= points.size();
        center.y /= points.size();

        // 按相对于质心的角度排序
        points.sort(new Comparator<Point2D.Double>() {
            @Override
            public int compare(Point2D.Double p1, Point2D.Double p2) {
                double angle1 = Math.atan2(p1.y - center.y, p1.x - center.x);
                double angle2 = Math.atan2(p2.y - center.y, p2.x - center.x);
                return Double.compare(angle1, angle2);
            }
        });
    }
}
