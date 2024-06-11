package config;

/**
 * @author: hehe
 * @create: 2024-06-07 12:51
 * @Description:
 */
public class Parameter {
    public static double xMin = -180, yMin = -90, xMax = 180, yMax = 90;

    public static int datasetNum = 1000;
    public static int quadTreeMaxLayer = 15;

    public static String datasetPath = "D:\\Desktop\\experiments\\datasets\\Datasets\\";
//    public static String quadTreeIndexFilePath = "D:\\Desktop\\experiments\\index\\quadtree_index.json";
    public static String quadTreeIndexFilePath = "D:\\Desktop\\experiments\\index\\";
    public static String resultAccuracyPath = "D:\\Desktop\\experiments\\result\\accuracy\\";
    public static String resultQueryTimePath = "D:\\Desktop\\experiments\\result\\queryTime\\";
}
