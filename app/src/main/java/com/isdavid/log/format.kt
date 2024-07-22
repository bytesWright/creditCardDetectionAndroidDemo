package com.isdavid.log


fun formatStringsIntoColumns(strings: List<String>, columns: Int, indentation: Int = 0): String {
    if (strings.isEmpty() || columns <= 0) return ""

    val columnWidths = IntArray(columns) { 0 }
    val output = StringBuilder()

    // Calculate maximum width needed for each column
    strings.forEachIndexed { index, string ->
        val columnIndex = index % columns
        columnWidths[columnIndex] = maxOf(columnWidths[columnIndex], string.length)
    }

    // Generate the indentation string
    val indentString = " ".repeat(indentation)

    // Format strings into columns
    strings.forEachIndexed { index, string ->
        val columnIndex = index % columns
        // Apply indentation to the start of each new line
        if (columnIndex == 0) output.append(indentString)
        output.append(string.padEnd(columnWidths[columnIndex] + 2))  // +2 for spacing between columns
        if (columnIndex == columns - 1) output.appendln()  // New line at the end of a row
    }

    // Handle case where the last line might not have all columns
    if (strings.size % columns != 0) output.appendln()

    return output.toString()
}