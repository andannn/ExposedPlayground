#ifndef SIMPLEMATH_H
#define SIMPLEMATH_H

typedef struct {
    int x;
    int y;
} Point2D;

// 函数声明
int add_integers(int a, int b);
Point2D add_points(Point2D a, Point2D b);

#endif
