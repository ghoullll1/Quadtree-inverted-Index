package config;

import java.util.Arrays;
import java.util.List;

/**
 * @author: hehe
 * @create: 2024-06-07 12:51
 * @Description:
 */
public class ParameterTest {
    public static double xMin = -180.0, yMin = -90.0, xMax = 180.0, yMax = 90.0;

    public static int datasetNum = 30000;
    public static int quadTreeMaxLayer = 12;

    public static List<String> name_datasets = Arrays.asList("public", "identifiable", "trackable");
    public static List<String> datasetsPath = Arrays.asList("D:\\Desktop\\experiments\\datasets\\public", "D:\\Desktop\\experiments\\datasets\\identifiable", "D:\\Desktop\\experiments\\datasets\\trackable");
    public static String datasetPath = "D:\\Desktop\\experiments\\datasets\\Datasets\\";
    public static String quadTreeIndexFilePath = "D:\\Desktop\\experiments\\index\\";
    public static String queryRangeFilePath = "D:\\Desktop\\experiments\\range\\";
    public static String resultAccuracyPath = "D:\\Desktop\\experiments\\result\\accuracy\\";
    public static String resultQueryTimePath = "D:\\Desktop\\experiments\\result\\queryTime\\";
}
