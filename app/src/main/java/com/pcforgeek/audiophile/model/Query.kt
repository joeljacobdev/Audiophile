package com.pcforgeek.audiophile.model

import android.net.Uri

class Query(
    val uri: Uri,
    val projection: Array<String>,
    val selection: String?,
    val args: Array<String>,
    val sort: String?
) {

    class Builder {
        private var uri: Uri? = null
        private var projection: Array<String> = emptyArray()
        private var selection: String? = null
        private var args: Array<String> = emptyArray()
        private var sort: String? = null


        fun setUri(uri: Uri): Builder {
            this.uri = uri
            return this@Builder
        }

        fun setProjection(projection: Array<String>): Builder {
            this.projection = projection
            return this@Builder
        }

        fun setSelection(selection: String): Builder {
            this.selection = selection
            return this@Builder
        }

        fun setArgs(args: Array<String>): Builder {
            this.args = args
            return this@Builder
        }

        fun setSort(sort: String): Builder {
            this.sort = sort
            return this@Builder
        }

        fun build(): Query {
            return Query(uri!!, projection, selection, args, sort)
        }
    }
}