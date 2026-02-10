#include "simplemath.h"

int add_integers(int a, int b) {
    return a + b;
}

Point2D add_points(Point2D a, Point2D b) {
    Point2D result;
    result.x = a.x + b.x;
    result.y = a.y + b.y;
    return result;
}
