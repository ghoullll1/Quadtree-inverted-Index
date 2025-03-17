package utils;

import java.lang.instrument.Instrumentation;

/**
 * @author: hehe
 * @create: 2024-09-18 20:03
 * @Description:
 */
public class ObjectSizeCalculator {
    private static Instrumentation instrumentation;

    public static void premain(String agentArgs, Instrumentation inst) {
        instrumentation = inst;
    }

    public static long getObjectSize(Object obj) {
        return instrumentation.getObjectSize(obj);
    }
}
