@file:OptIn(ExperimentalForeignApi::class)
@file:Suppress("ktlint:standard:no-wildcard-imports")

package interop

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.cValue
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.useContents
import my.simple.math.*
import kotlin.test.Test
import kotlin.test.assertEquals

class CInterOpDefTest {
    @Test
    fun `test custom c library basic`() {
        // 直接调用 C 函数
        val sum = add_integers(10, 20)
        assertEquals(30, sum)
    }

    @Test
    fun `test custom c library struct`() =
        memScoped {
            // 使用 CValue (值类型) 方式传递结构体
            // 这种方式不需要 alloc 指针，Kotlin 会自动处理值传递

            val p1 =
                cValue<Point2D> {
                    x = 10
                    y = 20
                }

            val p2 =
                cValue<Point2D> {
                    x = 5
                    y = 5
                }

            // 调用 C 函数：Point2D add_points(Point2D a, Point2D b);
            val result = add_points(p1, p2)

            // 验证结果
            // useContents 是读取 CValue 内部数据的辅助函数
            result.useContents {
                assertEquals(15, x)
                assertEquals(25, y)
            }
        }
}
