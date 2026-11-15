package com.brendigo.sublix.data

import java.net.URI

/**
 * Checks an explicitly user-entered HTTPS management URL before storing,
 * importing or exposing it as an ACTION_VIEW intent.
 *
 * This is syntax validation only: SubliX cannot verify the provider, domain
 * ownership or safety of remote content. No network request is made.
 */
object ManagementUrlPolicy {
    fun isValid(value: String): Boolean {
        if (value.isEmpty() || value.length > 512 || value != value.trim()) return false
        // Prevent newline/space injection into URLs and confusing backslash
        // separators that may be interpreted differently by browsers.
        if (value.any { it.isWhitespace() || it.isISOControl() || it == '\\' }) return false
        val url = try { URI(value) } catch (_: Exception) { return false }
        return url.isAbsolute &&
            url.scheme == "https" &&
            !url.host.isNullOrBlank() &&
            url.rawUserInfo == null &&
            (url.port == -1 || url.port in 1..65535)
    }

    fun isOptionalValid(value: String): Boolean =
        value.isBlank() || isValid(value)
}
