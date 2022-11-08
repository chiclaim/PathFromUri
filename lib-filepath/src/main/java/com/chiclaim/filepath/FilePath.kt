package com.chiclaim.filepath

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils


/**
 *
 * Created by kumu@2dfire.com on 2022/11/2.
 */
class FilePath private constructor() {


    companion object {

        @JvmStatic
        fun parseUri(context: Context, uri: Uri): String? {
            try {
                println(uri.scheme)
                println(uri.authority)
                println(uri.path)
                println(uri.toString())
                println()
                println("docId:${DocumentsContract.isDocumentUri(context, uri)}")
                val resolver = context.applicationContext.contentResolver
                //resolver.query(uri)
                // content://media/external/images/media/5930


                println(uri.toString())
                // document

                // content

                when (uri.scheme) {
                    "file" -> return uri.path
                    "content" -> {
                        if ("com.android.externalstorage.documents" == uri.authority) {
                            val docId = DocumentsContract.getDocumentId(uri)
                            if (TextUtils.isEmpty(docId)) return null
                            val split = docId.split(":").toTypedArray()
                            if (split.size >= 2) {
                                // 存储的根目录
                                if ("primary".equals(split[0], ignoreCase = true)) {
                                    return Environment.getExternalStorageDirectory()
                                        .toString() + "/" + split[1]
                                }
                            }
                        }
                        // start Intent(Intent.ACTION_GET_CONTENT)
                        // content://com.android.providers.media.documents/document/image%3A6318
                        else if ("com.android.providers.media.documents" == uri.authority) {
                            val docId = DocumentsContract.getDocumentId(uri)
                            println("docId=$docId")
                            if (TextUtils.isEmpty(docId)) {
                                return null
                            }
                            val split = docId.split(":").toTypedArray()
                            if (split.size >= 2) {
                                println("${docId}--${split[1]}")
                                val contentUri: Uri = when (split[0]) {
                                    "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                                    "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                                    "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                                    // TODO content://com.android.providers.media.documents/document/document:6328
                                    else -> null
                                } ?: return null

                                uri.path
                                return getDataColumnValue(
                                    context,
                                    contentUri,
                                    "${MediaStore.Images.Media._ID}=?",
                                    arrayOf<String?>(split[1])
                                )
                            }
                        } else {
                            // /storage/emulated/0/DCIM/Screenshots/
                            return getDataColumnValue(context, uri, null, null)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }


        private fun getDataColumnValue(
            context: Context,
            uri: Uri,
            selection: String?,
            selectionArgs: Array<String?>?
        ): String? {
            val column = MediaStore.Images.Media.DATA
            context.contentResolver.query(uri, arrayOf(column), selection, selectionArgs, null)
                ?.use {
                    if (it.moveToFirst()) {
                        val index = it.getColumnIndex(column)
                        if (index != -1) return it.getString(index)
                    }
                }
            return null
        }
    }
}