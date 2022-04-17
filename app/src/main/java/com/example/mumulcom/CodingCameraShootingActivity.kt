package com.example.mumulcom

import android.Manifest
import android.annotation.SuppressLint
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
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.mumulcom.databinding.ActivityCodingcamerashootingBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.*


class CodingCameraShootingActivity: AppCompatActivity() {

    lateinit var binding: ActivityCodingcamerashootingBinding

    //이미지 전송
    val CAMERA: Int = 100
    val GALLERY: Int = 101 // 갤러리 선택 시 인텐트로 보내는 값
    var imagePath=""
    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SimpleDateFormat")
    var imageDate: SimpleDateFormat = SimpleDateFormat("yyyyMMdd_HHmmss")

    var path: Bitmap? = null

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCodingcamerashootingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.camerashootingBackIb.setOnClickListener {
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

        binding.camerashootingCameraIv.setOnClickListener {
            intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)

            if (intent.resolveActivity(packageManager) != null) {
                var imageFile: File? = null
                try {
                    imageFile = createImageFile()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                if (imageFile != null) {
                    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    val imageUri = FileProvider.getUriForFile(
                        this,
                        "com.example.mumulcom.fileprovider",
                        imageFile
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                    startActivityForResult(takePictureIntent, CAMERA)
                }
            }
            binding.camerashootingCheckIb.visibility=View.VISIBLE
            binding.camerashootingturnIb.visibility=View.VISIBLE
            binding.camerashootingBnv.visibility=View.VISIBLE
        }

        binding.camerashootingGalleryIv.setOnClickListener {
            intent = Intent(Intent.ACTION_PICK)
            intent.type = MediaStore.Images.Media.CONTENT_TYPE
            intent.type = "image/*"
            startActivityForResult(intent, GALLERY)
            binding.camerashootingCheckIb.visibility=View.VISIBLE
            binding.camerashootingturnIb.visibility=View.VISIBLE
            binding.camerashootingBnv.visibility=View.VISIBLE
        }

        //이미지가 null값일때 체크버튼을 누르지 못함
        if (imagePath=="") {
            binding.camerashootingCheckIb.setOnClickListener {
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
//          1) data의 주소 사용하는 방법
                        imagePath =
                            data?.dataString!! // "content://media/external/images/media/7215"
                        data?.data?.let{ // 결과가 제대로 들어왔을때 (이미지 주소를 잘 가져왔을때) 실행
                                uri->
                            path = null // 앨범에서 가져올때마다 초기화
                            val inputStream = uri.let{
                                contentResolver.openInputStream(
                                    it
                                )
                            }
                            bitmap = BitmapFactory.decodeStream(inputStream)
                        }
                    }
                    if (imagePath.length > 0) {
                        Glide.with(this)
                            .load(bitmap)
                            .into(binding.ivPre)
                        binding.ivPre.visibility = View.VISIBLE
                        Log.d("gallery /ppp", bitmap.toString())
                    }
                }
                CAMERA -> {
                    val options = BitmapFactory.Options()
                    options.inSampleSize = 2 // 이미지 축소 정도. 원 크기에서 1/inSampleSize 로 축소됨
                    bitmap = BitmapFactory.decodeFile(imagePath, options)
                    binding.ivPre.visibility = View.VISIBLE
                }
            }
            //사진이 회전되므로 보여줄떈 imagepath, 넘겨줄땐 bitmap
            Glide.with(this)
                .load(imagePath)
                .into(binding.ivPre)
            Log.d("camara/ppp", bitmap.toString())
            Log.d("pathpath", imagePath)

            //삭제버튼
            binding.camerashootingturnIb.setOnClickListener {
                imagePath=""
                binding.ivPre.visibility=View.INVISIBLE
                binding.camerashootingCheckIb.setOnClickListener {
                    Toast.makeText(this, "이미지를 넣어주세요", Toast.LENGTH_SHORT).show()
                }
            }


            //이미지가 null값이 아니어야 체크버튼 클릭 가능(사진 다른 액티비티로 전송)
            if(imagePath!="") {
                binding.ivPre.visibility=View.VISIBLE
                binding.camerashootingCheckIb.setOnClickListener {
                    val uploadBitmap = Bitmap.createScaledBitmap(bitmap!!,500,400,true)
                    val stream = ByteArrayOutputStream()
                    uploadBitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
                    val byteArray = stream.toByteArray()
                    intent.putExtra("path", byteArray)
                    intent.putExtra("imagepath", imagePath)
                    setResult(RESULT_OK, intent);
                    finish()
                    Log.d("PUT/path", byteArray.toString())

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
        return file
    }

}