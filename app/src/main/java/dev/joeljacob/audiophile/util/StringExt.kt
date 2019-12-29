package dev.joeljacob.audiophile.util

fun String?.containsCaseInsensitive(other: String?) =
    if (this == null && other == null) {
        true
    } else if (this != null && other != null) {
        toLowerCase().contains(other.toLowerCase())
    } else {
        false
    }