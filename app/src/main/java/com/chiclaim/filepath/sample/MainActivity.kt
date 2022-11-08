package com.chiclaim.filepath.sample

import android.R.attr
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.chiclaim.filepath.FilePath
import com.huantansheng.easyphotos.EasyPhotos
import com.huantansheng.easyphotos.models.album.entity.Photo
import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode


@SuppressLint("SetTextI18n")
class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_IMAGE = 101
        private const val REQUEST_CODE_FILE = 102
    }

    private var tvResult: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvResult = findViewById(R.id.text_result)
        println(externalCacheDir?.mkdirs())
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (RESULT_OK != resultCode || data == null) return
        if (requestCode == REQUEST_CODE_IMAGE) {
            val resultData = data.getParcelableArrayListExtra<Photo>(EasyPhotos.RESULT_PHOTOS)
            for (photo in resultData!!) {
                Log.e("onActivityResult", photo.toString())
            }
            if (resultData.size == 1) {
                val photo = resultData[0]
                val path = FilePath.parseUri(this, photo.uri)
                tvResult?.append("\nEasyPhotos info:\n")
                tvResult?.append("uri=${photo.uri}\n")
                tvResult?.append("path=$path\n")
                if (path != null) tvResult?.append("file size=${File(path).length()}\n")
            }
        } else if (requestCode == REQUEST_CODE_FILE) {
            val uri = data.data
            if (uri != null) {
                tvResult?.append("\nSelected File info:\n")
                val path = FilePath.parseUri(this, uri)
                tvResult?.append("uri=$uri\n")
                tvResult?.append("path=$path\n")
                if (path != null) tvResult?.append("file size=${File(path).length()}\n")
            }
        }

    }

    /**
     * 用于测试，一般需要放在子线程
     */
    private fun getImageBound(filePath: String?): String {
        val op = BitmapFactory.Options()
        op.inJustDecodeBounds = true
        op.inSampleSize = 1
        BitmapFactory.decodeFile(filePath, op)
        val ratio =
            if (op.outWidth > op.outHeight) op.outHeight.toDouble() / op.outWidth else op.outWidth.toDouble() / op.outHeight
        return "${op.outWidth}x${op.outHeight}, 宽高比：${
            BigDecimal(ratio.toString()).setScale(
                2,
                RoundingMode.DOWN
            )
        }"
    }

    fun selectFileBySystem(view: View) {
        var chooseFile = Intent(Intent.ACTION_GET_CONTENT)
        chooseFile.type = "*/*"
        chooseFile = Intent.createChooser(chooseFile, "Choose a file")
        startActivityForResult(chooseFile, REQUEST_CODE_FILE)
    }


    fun selectImage(view: View) {
        EasyPhotos.createAlbum(this, false, false, GlideEngine.getInstance())
            .start(REQUEST_CODE_IMAGE)
    }
}