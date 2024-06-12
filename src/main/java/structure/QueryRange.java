package structure;

import config.ParameterTest;
import lombok.Data;

import java.io.Serializable;
import java.util.Random;

/**
 * @author: hehe
 * @create: 2024-06-12 16:28
 * @Description:
 */
@Data
public class QueryRange implements Serializable {
    private static final long serialVersionUID = 1L;
    double x1,x2,y1,y2;

    public QueryRange() {
        Random random = new Random();
        this.x1 = (ParameterTest.xMax - ParameterTest.xMin)*random.nextDouble()  + ParameterTest.xMin;
        this.x2 = (ParameterTest.xMax - x1)*random.nextDouble()  + x1;
        this.y1 = (ParameterTest.yMax - ParameterTest.yMin) * random.nextDouble()+ ParameterTest.yMin;
        this.y2 = (ParameterTest.yMax - y1) * random.nextDouble()+y1;
    }

    @Override
    public String toString() {
        return "QueryRange{" +
                "x1=" + x1 +
                ", x2=" + x2 +
                ", y1=" + y1 +
                ", y2=" + y2 +
                '}';
    }
}
