package org.chornobyl.hamdash.domain

/**
 * Publicly known amateur repeater technology. Kept to open, legal amateur-radio
 * modes only — never extend this with military or restricted-system identifiers.
 */
enum class RepeaterMode(val label: String) {
    FM("FM"),
    DMR("DMR"),
    DSTAR("D-STAR"),
    C4FM("C4FM"),
    OTHER("Other");

    companion object {
        fun fromRaw(raw: String?): RepeaterMode =
            entries.firstOrNull { it.name.equals(raw, ignoreCase = true) } ?: OTHER
    }
}
