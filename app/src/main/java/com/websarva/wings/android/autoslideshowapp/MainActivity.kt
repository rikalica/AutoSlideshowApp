package com.websarva.wings.android.autoslideshowapp

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(),View.OnClickListener {

    private val PERMISSIONS_REQUEST_CODE = 100
    private var mTimer: Timer? = null
    private var mHandler = Handler()
    var cursor: Cursor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo()
            } else {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE)
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo()
        }

        //val resolver = contentResolver
        //cursor = resolver.query(
            //MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
           // null, // 項目(null = 全項目)
           // null, // フィルタ条件(null = フィルタなし)
           // null, // フィルタ用パラメータ
           // null // ソート (null ソートなし)
        //)
        //if(cursor!!.moveToFirst()){
        //    next_button.setOnClickListener(this)
        //    back_button.setOnClickListener(this)
        //    play_button.setOnClickListener(this)
        //}
    }


    override fun onClick(v: View) {
        Log.d("UI_PARTS", "ボタンをタップしました")
        if (v.id == R.id.next_button) {
            Log.d("UI_PARTS", "進むボタンをタップしました")

            if (cursor!!.moveToNext()) {
                getImage()
            } else {
                cursor!!.moveToFirst()
                getImage()
            }
        } else if (v.id == R.id.back_button) {
            Log.d("UI_PARTS", "戻るボタンをタップしました")

            if (cursor!!.moveToPrevious()) {
                getImage()
            } else {
                cursor!!.moveToLast()
                getImage()
            }
        } else if (v.id == R.id.play_button) {
            Log.d("UI_PARTS", "再生/停止ボタンをタップしました")
            if(mTimer == null) {
                play_button.text = "停止"
                next_button.setEnabled(false)
                back_button.setEnabled(false)
                mTimer = Timer()
                mTimer!!.schedule(object : TimerTask() {
                    override fun run() {
                        mHandler.post {
                            Log.d("UI_PARTS", "IF文")
                            if (cursor!!.moveToNext()) {
                                getImage()
                            } else {
                                cursor!!.moveToFirst()
                                getImage()
                            }
                        }
                    }
                }, 2000, 2000)
            } else {
                play_button.text = "再生"
                next_button.setEnabled(true)
                back_button.setEnabled(true)
                mTimer!!.cancel()
                mTimer = null
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("ANDROID", "許可された")
                    getContentsInfo()
                } else {
                    Log.d("ANDROID", "許可されなかった")
                }
        }
    }

    private fun getContentsInfo() {
        // 画像の情報を取得する
        val resolver = contentResolver
        cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
            null, // 項目(null = 全項目)
            null, // フィルタ条件(null = フィルタなし)
            null, // フィルタ用パラメータ
            null // ソート (null ソートなし)
        )

        if (null != cursor && cursor!!.getCount() >= 1) {
            if (cursor!!.moveToFirst()) {
                next_button.setOnClickListener(this)
                back_button.setOnClickListener(this)
                play_button.setOnClickListener(this)
            }
        }
    }

    private fun  getImage() {
        val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
        val id = cursor!!.getLong(fieldIndex)
        val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
        Log.d("ANDROID", "URI : " + imageUri.toString())
        imageView.setImageURI(imageUri)
    }
}
