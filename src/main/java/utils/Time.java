package utils;

import lombok.Data;
import structure.Quadtree;

/**
 * @author: hehe
 * @create: 2024-06-07 10:23
 * @Description:
 */
@Data
public class Time {
    private long startTime;//执行开始时间
    private long endTime;//执行结束时间
    private double time;//执行花费时间（毫秒）
    private double nanoTime;//执行花费时间（毫秒）
    private double totalTime;//累计执行花费时间（毫秒）

    /**
     * 记录开始时间
     */
    public void start() {
        startTime = System.nanoTime();//获取开始时间
    }

    /**
     * 开始计算累计时间
     */
    public void startSum() {
        totalTime = 0;//累计时间置零
    }

    /**
     * 记录结束时间，并计算花费时间
     */
    public void end() {
        endTime = System.nanoTime();//获取结束时间
        time = (endTime - startTime) / (double) 1000000;//计算花费时间，System.nanoTime()获取的结果是纳秒，要换算为毫秒
        nanoTime = endTime - startTime;
        totalTime += time;//将当前执行时间加入到累计执行时间中
    }


}
