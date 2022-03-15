package com.example.mumulcom

import android.Manifest
import android.R.attr
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.mumulcom.databinding.ActivityAnswercamerashootingBinding
import java.io.File
import java.io.IOException
import java.lang.String
import java.util.*
import kotlin.Exception
import kotlin.Int
import kotlin.Throws
import kotlin.arrayOf


class AnswerCameraShootingActivity: AppCompatActivity() {

    lateinit var binding: ActivityAnswercamerashootingBinding

    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>//이동(카메라,앨범)

    //이미지 전송
    val CAMERA: Int = 100
    val GALLERY: Int = 101 // 갤러리 선택 시 인텐트로 보내는 값
    var imagePath = ""
    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SimpleDateFormat")
    var imageDate: SimpleDateFormat = SimpleDateFormat("yyyyMMdd_HHmmss")
    lateinit var photoAdapter:PhotoAdapter//리사이클러뷰

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnswercamerashootingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.answercameraCamerashootingBackIb.setOnClickListener {
            onBackPressed()
            finish()
        }


        //        권한 체크
        val hasCamPerm =
            checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        val hasWritePerm =
            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        if (!hasCamPerm || !hasWritePerm) // 권한 없을 시  권한설정 요청
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1
            )

        onClick()

    }



    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("NonConstantResourceId", "QueryPermissionsNeeded")
    fun onClick() {

        binding.answercameraCamerashootingCameraIv.setOnClickListener {
            intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)

            if (intent.resolveActivity(packageManager) != null) {
                var imageFile: File? = null
                try {
                    imageFile = createImageFile()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                if (imageFile != null) {
                    val imageUri = FileProvider.getUriForFile(
                        applicationContext,
                        "com.example.mumulcom.fileprovider",
                        imageFile
                    )
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                    startActivityForResult(intent, CAMERA) // final int CAMERA = 100;
                }
            }
            binding.answercameraCamerashootingCheckIb.visibility=View.VISIBLE
            binding.answercameraCamerashootingturnIb.visibility=View.VISIBLE
            binding.answercameraCamerashootingBnv.visibility=View.VISIBLE
        }

        binding.answercameraCamerashootingGalleryIv.setOnClickListener {
            intent = Intent(Intent.ACTION_PICK)
            intent.type = MediaStore.Images.Media.CONTENT_TYPE
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            intent.type = "image/*"
            startActivityForResult(intent, GALLERY)


            binding.answercameraCamerashootingCheckIb.visibility=View.VISIBLE
            binding.answercameraCamerashootingturnIb.visibility=View.VISIBLE
            binding.answercameraCamerashootingBnv.visibility=View.VISIBLE
        }

        //이미지가 null값일때 체크버튼을 누르지 못함
        if (imagePath=="") {
            binding.answercameraCamerashootingCheckIb.setOnClickListener {
                Toast.makeText(this, "이미지를 넣어주세요", Toast.LENGTH_SHORT).show()
            }
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) { // 결과가 있을 경우
            var bitmap: Bitmap? = null
            when (requestCode) {
                GALLERY -> {
                    if (requestCode == GALLERY) { // 갤러리 선택한 경우

//				1) data의 주소 사용하는 방법
                        imagePath = data?.dataString!! // "content://media/external/images/media/7215"

                    }
                    if (imagePath.length > 0) {
                        Glide.with(this)
                            .load(imagePath)
                            .into(binding.ivPre)
                        binding.ivPre.visibility=View.VISIBLE
                    }
                }
                CAMERA -> {
                    val options = BitmapFactory.Options()
                    options.inSampleSize = 2 // 이미지 축소 정도. 원 크기에서 1/inSampleSize 로 축소됨
                    bitmap = BitmapFactory.decodeFile(imagePath, options)
                    binding.ivPre.visibility = View.VISIBLE
                }
            }
//            binding.ivPre.setImageBitmap(bitmap)
            Glide.with(this)
                .load(imagePath)
                .into(binding.ivPre)//글라이드 사용하면 이미지 회전을 막을 수 있음
            Log.d("pathpath", imagePath)


            //삭제버튼
            binding.answercameraCamerashootingturnIb.setOnClickListener {
                imagePath=""
                binding.ivPre.visibility=View.INVISIBLE
                binding.answercameraCamerashootingCheckIb.setOnClickListener {
                    Toast.makeText(this, "이미지를 넣어주세요", Toast.LENGTH_SHORT).show()
                }
            }

            //이미지가 null값이 아니어야 체크버튼 클릭 가능
            if(imagePath!="") {
                binding.ivPre.visibility = View.VISIBLE
                binding.answercameraCamerashootingCheckIb.setOnClickListener {
                    intent.putExtra("path", imagePath)
                    setResult(RESULT_OK, intent);
                    finish()
                    Log.d("PUT/path", imagePath)
                }
            }
        }
    }
        @RequiresApi(Build.VERSION_CODES.N)
        @SuppressLint("SimpleDateFormat")
        @Throws(IOException::class)
        fun createImageFile(): File? {
//	이미지 파일 생성
//	SimpleDateFormat imageDate = new SimpleDateFormat("yyyyMMdd_HHmmss");
            val timeStamp = imageDate.format(Date()) // 파일명 중복을 피하기 위한 "yyyyMMdd_HHmmss"꼴의 timeStamp
            val fileName = "IMAGE_$timeStamp" // 이미지 파일 명
            val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val file = File.createTempFile(fileName, ".jpg", storageDir) // 이미지 파일 생성
            imagePath = file.absolutePath // 파일 절대경로 저장하기, String
            imagePath=file.path
            Log.d("file//",file.path)
            imagePath= file.toUri().toString()
            Log.d("file//",file.toUri().toString())
            imagePath= file.toURI().toString()
            Log.d("file//",file.toURI().toString())
            return file
        }
}