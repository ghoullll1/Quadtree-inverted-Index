package utils;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 自定义比较器，按文件名中数字大小升序排序
 *
 * @author 蓝色的三明治
 * @date 2024/04/09
 */
public class NumericNameComparator implements Comparator<String> {
    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+");

    @Override
    public int compare(String fileName1, String fileName2) {
        Matcher matcher1 = NUMBER_PATTERN.matcher(fileName1);
        Matcher matcher2 = NUMBER_PATTERN.matcher(fileName2);

        while (matcher1.find() && matcher2.find()) {
            String numStr1 = matcher1.group();
            String numStr2 = matcher2.group();

            // 将数字字符串转换为整数比较
            int num1 = Integer.parseInt(numStr1);
            int num2 = Integer.parseInt(numStr2);

            // 数字不同，直接比较
            if (num1 != num2) {
                return Integer.compare(num1, num2);
            }

            // 数字相同，继续比较下一个数字
        }

        // 文件名中的数字部分相同或没有数字，按文件名全字符串比较
        return fileName1.compareTo(fileName2);
    }
}
