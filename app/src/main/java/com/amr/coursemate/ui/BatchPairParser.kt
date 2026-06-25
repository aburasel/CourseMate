package com.amr.coursemate.ui

/**
 * Splits free-form input into Bangla↔Arabic pairs.
 *
 * The text is scanned into maximal runs of Bangla (Bengali) and Arabic script —
 * newlines, digits and punctuation between runs are ignored — then consecutive runs
 * of opposite scripts are paired. Order within a pair doesn't matter; the script of
 * each run decides which side it lands on. A run with no opposite-script neighbour is
 * kept with the other side blank.
 *
 * @return ordered list of (bangla, arabic) pairs, at most [max] entries.
 */
fun parseScriptPairs(input: String, max: Int = Int.MAX_VALUE): List<Pair<String, String>> {
    // Build ordered, script-typed chunks (true = Bangla, false = Arabic).
    val chunks = mutableListOf<Pair<Boolean, String>>()
    var currentScript: Boolean? = null
    val buffer = StringBuilder()

    fun flush() {
        val text = buffer.toString().trim()
        if (currentScript != null && text.isNotEmpty()) chunks.add(currentScript!! to text)
        buffer.setLength(0)
    }

    for (ch in input) {
        val code = ch.code
        val script = when {
            code in 0x0980..0x09FF -> true     // Bengali
            code in 0x0600..0x06FF ||          // Arabic
                code in 0x0750..0x077F ||      // Arabic Supplement
                code in 0xFB50..0xFDFF ||      // Arabic Presentation Forms-A
                code in 0xFE70..0xFEFF -> false // Arabic Presentation Forms-B
            else -> null                       // neutral: digits, spaces, punctuation
        }
        when {
            script == null -> buffer.append(ch)            // attaches to current run
            currentScript == null -> { currentScript = script; buffer.append(ch) }
            script == currentScript -> buffer.append(ch)
            else -> { flush(); currentScript = script; buffer.append(ch) }
        }
    }
    flush()

    val pairs = mutableListOf<Pair<String, String>>()
    var i = 0
    while (i < chunks.size && pairs.size < max) {
        val first = chunks[i]
        val second = chunks.getOrNull(i + 1)
        if (second != null && second.first != first.first) {
            val bangla = if (first.first) first.second else second.second
            val arabic = if (first.first) second.second else first.second
            pairs.add(bangla to arabic)
            i += 2
        } else {
            // Unpaired run — keep it with the opposite field left blank.
            if (first.first) pairs.add(first.second to "") else pairs.add("" to first.second)
            i += 1
        }
    }
    return pairs
}
