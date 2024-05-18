package org.example.utils

/**
 * rounds up division first by second
 * @param first the Int number
 * @param second the Int number
 * @return the Int number that first / second rounded up, if it not divides entirely
 */
fun roundUp(first: Int, second: Int): Int {
    return (first - 1 + second) / second
}

/**
 * complements a number to a higher multiple after rounding up
 * @param first the Int number
 * @param second the int number
 * @return the Int number that is a higher multiple number
 */
fun fit(first: Int, second: Int): Int {
    return roundUp(first, second) * second
}