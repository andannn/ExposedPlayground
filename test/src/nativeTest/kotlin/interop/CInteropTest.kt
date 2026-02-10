@file:OptIn(ExperimentalForeignApi::class)
@file:Suppress("ktlint:standard:no-wildcard-imports")

package interop

import kotlinx.cinterop.*
import platform.posix.*
import kotlin.test.*

class CInteropTest {
    @Test
    fun `getpid test`() {
        val pid = getpid()
        println("Current Process ID: $pid")
    }

    @Test
    fun `assign value to variable and get it`() =
        memScoped {
            // C: int a;
            // KN: 在当前作用域分配一块存 int 的内存
            val aVar = alloc<IntVar>()

            // C: a = 100;
            // KN: 设置这块内存的值
            aVar.value = 100
            assertEquals(100, aVar.value)

            // C: int* p = &a;
            // KN: 获取这块内存的地址 (指针)
            val p: CPointer<IntVar> = aVar.ptr

            // C: *p = 200; (修改指针指向的内容)
            // KN: 通过指针修改值。pointed 表示“指针指向的东西”
            p.pointed.value = 200

            assertEquals(200, aVar.value)
        }

    @Test
    fun `arrary pointer test`() =
        memScoped {
            // C: int arr[5];
            val arrPtr = allocArray<IntVar>(5)

            // C: arr[0] = 10; arr[1] = 20;
            // KN: 直接像操作 Kotlin 数组一样操作指针
            arrPtr[0] = 10
            arrPtr[1] = 20

            // 指针算术 (Pointer Arithmetic)
            // 这里的 plus(1) 相当于 C 的 (ptr + 1)
            // 并不是地址 + 1 字节，而是地址 + sizeof(int)
            val secondElementPtr = arrPtr.plus(1)
            assertEquals(20, secondElementPtr?.pointed?.value)
        }

    @Test
    fun `c style string test`() =
        memScoped {
            val kotlinString = "ABC"
            // 1. Kotlin String -> C Pointer
            // cstr 会在 memScoped 内存池中分配并复制字符串
            val cStringRef: CValuesRef<ByteVar> = kotlinString.cstr
            val cPointer = cStringRef.getPointer(this)
            assertEquals(
                'A',
                cPointer.pointed.value
                    .toInt()
                    .toChar(),
            )
            assertEquals(
                'B',
                (cPointer.plus(1))
                    ?.pointed
                    ?.value
                    ?.toInt()
                    ?.toChar(),
            )
            assertEquals(
                'C',
                (cPointer.plus(2))
                    ?.pointed
                    ?.value
                    ?.toInt()
                    ?.toChar(),
            )

            // \0
            assertEquals(
                0x00,
                (cPointer.plus(3))
                    ?.pointed
                    ?.value,
            )
        }

    @Test
    fun `void pointer test`() =
        memScoped {
            val intValue = alloc<IntVar>()
            intValue.value = 12345

            // 1. 转为 void* (COpaquePointer)
            val voidPtr: COpaquePointer = intValue.ptr

            // 2. 强转回 int*
            // interpretCPointer<T>(rawPtr) 是底层的转换
            // 更常用的方式是对指针对象调用 reinterpret
            val intPtrAgain = voidPtr.reinterpret<IntVar>()

            assertEquals(12345, intPtrAgain.pointed.value)
        }

    @Test
    fun `struct access test - date time`() =
        memScoped {
            // 1. 分配一个 time_t (通常是 Long) 来存时间戳
            val timeHolder = alloc<time_tVar>()
            time(timeHolder.ptr)
            println("current time: ${timeHolder.value}")

            val tmPtr: CPointer<tm>? = localtime(timeHolder.ptr)

            assertNotNull(tmPtr, "Localtime should not return null")

            // 3. 访问结构体成员
            // C: tmPtr->tm_year
            // KN: tmPtr.pointed.tm_year
            val year = tmPtr.pointed.tm_year + 1900
            val month = tmPtr.pointed.tm_mon + 1
            println("Date from C: $year-$month-${tmPtr.pointed.tm_mday}")
        }

    @Test
    fun `function pointer test - qsort`() =
        memScoped {
            // 1. 准备数据：乱序的 Int 数组
            val count = 5
            val numbers = allocArrayOf(50f, 20f, 40f, 10f, 30f)

            // 2. 定义比较器函数 (Comparator)
            // C签名: int compare(const void *a, const void *b)
            // staticCFunction <参数1, 参数2, 返回值> { ... }
            val comparator =
                staticCFunction<COpaquePointer?, COpaquePointer?, Int> { a, b ->
                    // a 和 b 都是 void*，需要强转为 int*
                    val intA = a!!.reinterpret<IntVar>().pointed.value
                    val intB = b!!.reinterpret<IntVar>().pointed.value
                    // 返回比较结果: A - B (升序)
                    intA - intB
                }

            // 3. 调用 C 的 qsort
            // void qsort(void *base, size_t nmemb, size_t size, int (*compar)(const void *, const void *));
            qsort(
                numbers,
                count.toULong(), // 数组长度
                sizeOf<IntVar>().toULong(), // 每个元素的大小
                comparator, // 传入我们的 Kotlin 函数指针
            )

            // 4. 验证结果
            assertEquals(10f, numbers[0])
            assertEquals(20f, numbers[1])
            assertEquals(30f, numbers[2])
            assertEquals(40f, numbers[3])
            assertEquals(50f, numbers[4])
        }
}
