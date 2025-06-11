package com.example.fliplearn_final.util

fun customCompare(str1: String, str2: String): Int {
    val alphabet = "abcdefghijklmnopqrstuvwxyzабвгґдеєжзиіїйклмнопрстуфхцчшщьюя"
    val s1 = str1.lowercase()
    val s2 = str2.lowercase()

    val minLength = minOf(s1.length, s2.length)
    for (i in 0 until minLength) {
        val c1 = s1[i]
        val c2 = s2[i]

        val index1 = alphabet.indexOf(c1).takeIf { it >= 0 } ?: c1.code
        val index2 = alphabet.indexOf(c2).takeIf { it >= 0 } ?: c2.code

        if (index1 != index2) {
            return index1 - index2
        }
    }
    return s1.length - s2.length
}
