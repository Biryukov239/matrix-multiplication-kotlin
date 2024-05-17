package org.example.utils

object Round {
    fun roundUp(first: Int, second: Int): Int {
        return (first - 1 + second) / second
    }

    fun fit(first: Int, second: Int): Int {
        return roundUp(first, second) * second
    }
}