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
    private double x1,x2,y1,y2;
    private double MIN_AREA = 0.0;
    private double MAX_AREA = 64800.0;

    public QueryRange() {
        Random random = new Random();
        this.x1 = (ParameterTest.xMax - ParameterTest.xMin)*random.nextDouble()  + ParameterTest.xMin;
        this.x2 = (ParameterTest.xMax - x1)*random.nextDouble()  + x1;
        this.y1 = (ParameterTest.yMax - ParameterTest.yMin) * random.nextDouble()+ ParameterTest.yMin;
        this.y2 = (ParameterTest.yMax - y1) * random.nextDouble()+y1;
    }
    public QueryRange(double MIN_AREA,double MAX_AREA) {
        this.MIN_AREA = MIN_AREA;
        this.MAX_AREA = MAX_AREA;
        Random random = new Random();
        double area;
        do {
            this.x1 = (ParameterTest.xMax - ParameterTest.xMin) * random.nextDouble() + ParameterTest.xMin;
            this.x2 = (ParameterTest.xMax - x1) * random.nextDouble() + x1;
            this.y1 = (ParameterTest.yMax - ParameterTest.yMin) * random.nextDouble() + ParameterTest.yMin;
            this.y2 = (ParameterTest.yMax - y1) * random.nextDouble() + y1;
            area = Math.abs((x2 - x1) * (y2 - y1));;
        } while (area < MIN_AREA || area > MAX_AREA);
    }

    public QueryRange(double targetArea) {
        Random random = new Random();
        int width;
        double height;
        do{
            this.x1 = (ParameterTest.xMax - ParameterTest.xMin) * random.nextDouble() + ParameterTest.xMin;
            this.y1 = (ParameterTest.yMax - ParameterTest.yMin) * random.nextDouble() + ParameterTest.yMin;
            width=random.nextInt(180-(int)this.x1);
            height=targetArea/width;
        }while (height>90-(int)this.y1);
        this.x2=this.x1+width;
        this.y2=this.y1+height;
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
