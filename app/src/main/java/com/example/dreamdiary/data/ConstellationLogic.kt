package com.example.dreamdiary.data

object ConstellationLogic {

    /** Количество созвездий: групп из 2 и более снов, связанных общими тегами (напрямую или через другие сны). */
    fun countGroups(tagsPerDream: List<List<String>>): Int {
        val n = tagsPerDream.size
        val parent = IntArray(n) { it }

        fun find(x: Int): Int {
            var root = x
            while (parent[root] != root) root = parent[root]
            var cur = x
            while (parent[cur] != root) {
                val next = parent[cur]
                parent[cur] = root
                cur = next
            }
            return root
        }

        for (i in 0 until n) {
            for (j in i + 1 until n) {
                if (tagsPerDream[i].any { it in tagsPerDream[j] }) {
                    parent[find(i)] = find(j)
                }
            }
        }

        val sizes = HashMap<Int, Int>()
        for (i in 0 until n) {
            val root = find(i)
            sizes[root] = (sizes[root] ?: 0) + 1
        }
        return sizes.values.count { it >= 2 }
    }
}
