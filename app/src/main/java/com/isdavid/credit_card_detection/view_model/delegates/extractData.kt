package com.isdavid.credit_card_detection.view_model.delegates

import java.util.regex.Pattern

fun extractData(input: String) = digestString(input, patterns)

enum class PatternType(val label: String) {
    CREDIT_CARD_NUMBER("creditCardNumber"),
    DATE("date"),
    SECURITY_NUMBER("securityNumber");
}

private val patterns = mapOf(
    PatternType.CREDIT_CARD_NUMBER.label to listOf(
        "\\d{16}",
        /**
         * This regular expression matches credit card numbers in various formats.
         * It ensures the credit card number is composed of exactly four groups of
         * four digits, separated by spaces, hyphens, or periods, optionally surrounded by spaces.
         *
         * Examples:
         * 1. "1234 1233 1234 1234"
         * 2. "1234-1233-1234-1234"
         * 3. "1234 -1233- 1234-1234"
         * 4. "1234.1233.1234.1234"
         * 5. "1234 . 1233 . 1234 . 1234"
         */
        "\\b(?:\\d{4}(?:\\s+|(?:\\s*(?:-|.)\\s*))){3}\\d{4}\\b",
    ),

    /**
     * This regular expression matches dates in the format of `MM-YY`, `MM-YYYY`, `MM/YY`, or `MM/YYYY`.
     *
     * The pattern:
     * - Ensures the match is a standalone date using word boundaries (`\b`).
     * - Matches months from `01` to `12` using `(0[1-9]|1[0-2])`.
     * - Allows either a hyphen (`-`) or a forward slash (`/`) as the separator using `[-/]`.
     * - Matches a two-digit or four-digit year using `(\\d{2}|\\d{4})`.
     *
     * Examples of valid dates:
     * 1. "01-21"
     * 2. "12/2021"
     * 3. "03-99"
     * 4. "10/1999"
     */
    PatternType.DATE.label to listOf("\\b(0[1-9]|1[0-2])[-/](\\d{2}|\\d{4})\\b"),

    /**
     * This regular expression matches CVV numbers that are either 3 or 4 digits long.
     *
     * The pattern:
     * - Ensures the match is a standalone number using word boundaries (`\b`).
     * - Matches exactly 3 or 4 digits using `\\d{3,4}`.
     *
     * Examples of valid CVV numbers:
     * 1. "123"
     * 2. "1234"
     */
    PatternType.SECURITY_NUMBER.label to listOf("\\b\\d{3,4}\\b"),
)

fun digestString(input: String, patterns: Map<String, List<String>>): Map<String, List<String>> {
    var remainingString = input
    val matches = mutableMapOf<String, MutableList<String>>()

    patterns.forEach { (label, patternList) ->
        patternList.forEach { pattern ->
            val regex = Pattern.compile(pattern)
            val matcher = regex.matcher(remainingString)

            while (matcher.find()) {
                val match = matcher.group()
                remainingString = remainingString.replaceFirst(match, "")
                matches.computeIfAbsent(label) { mutableListOf() }.add(match)
            }
        }
    }

    return matches
}

fun consolidateMaps(vararg maps: Map<String, List<String>>) = consolidateMaps(maps.toList())

fun consolidateMaps(mapList: List<Map<String, List<String>>>): Map<String, List<String>> {
    val consolidatedMap = mutableMapOf<String, MutableList<String>>()

    mapList.forEach { map ->
        map.forEach { (key, value) ->
            consolidatedMap.computeIfAbsent(key) { mutableListOf() }.addAll(value)
        }
    }

    return consolidatedMap
}





