package structure;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author: hehe
 * @create: 2024-06-05 10:51
 * @Description:
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
public class Point {
    double x, y;

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                ", datasetId=" + datasetId +
                '}';
    }

    int datasetId;

    public Point(double x, double y, int datasetId) {
        this.x = x;
        this.y = y;
        this.datasetId = datasetId;
    }
}