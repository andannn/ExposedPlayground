#ifndef SIMPLEMATH_H
#define SIMPLEMATH_H

#ifdef __cplusplus
extern "C" {
#endif

typedef struct {
    int x;
    int y;
} Point2D;

// 函数声明
int add_integers(int a, int b);
Point2D add_points(Point2D a, Point2D b);

#ifdef __cplusplus
}
#endif

#endif
