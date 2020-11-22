package cn.edu.sustech.cse.sqlab.leakdroid

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2020/11/22 23:20
 */
fun main(args: Array<String>) {
    val list = arrayOf(
            1..10,
    )

    println(list[0])

    val mergeList = list.flatMap {
        it.map {
            "No.$it"
        }
    }

    mergeList.forEach(::println)
}