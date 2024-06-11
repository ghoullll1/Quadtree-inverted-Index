package structure;

/**
 * @author: hehe
 * @create: 2024-06-05 10:51
 * @Description:
 */
class Rectangle {
    double x, y, width, height;

    Rectangle(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    boolean contains(Point p) {
        return (p.x >= x && p.x < x + width && p.y >= y && p.y < y + height);
    }

    boolean intersects(Rectangle range) {
        return !(range.x > x + width || range.x + range.width < x || range.y > y + height || range.y + range.height < y);
    }
}