package com.example.mumulcom

import android.animation.ValueAnimator
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mumulcom.databinding.ActivityConceptcamerashootingBinding
import java.text.SimpleDateFormat
import android.graphics.Matrix
import android.media.ExifInterface
import com.example.mumulcom.databinding.ActivityCodingcamerashootingBinding


class CodingCameraShootingActivity: AppCompatActivity() {

    lateinit var binding: ActivityCodingcamerashootingBinding

    private lateinit var getResultText: ActivityResultLauncher<Intent>
    private var realUri:Uri?=null

    //Manifest 에서 설정한 권한을 가지고 온다.
    val CAMERA_PERMISSION = arrayOf(android.Manifest.permission.CAMERA)
    val STORAGE_PERMISSION = arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

    //권한 플래그값 정의
    val FLAG_PERM_CAMERA = 98
    val FLAG_PERM_STORAGE = 99

    //카메라와 갤러리를 호출하는 플래그
    val FLAG_REQ_CAMERA = 101
    val FLAG_REA_GALLERY = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCodingcamerashootingBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.camerashootingBackIb.setOnClickListener {
            startActivity(Intent(this, QuestionCategoryActivity::class.java))
        }


        // 화면이 만들어 지면서 정장소 권한을 체크 합니다.
        // 권한이 승인되어 있으면 카메라를 호출하는 메소드를 실행합니다.
        if (checkPermission(STORAGE_PERMISSION, FLAG_PERM_STORAGE)) {
            setViews()
            val imageView=binding.ivPre
            val currentRotation=imageView.rotation
            val currentImageViewHeight = imageView.height
            val displayMetrics = DisplayMetrics()
            val deviceWidth=displayMetrics.widthPixels
            val deviceHeight=displayMetrics.heightPixels



            val  heightGap= if (currentImageViewHeight>deviceWidth){
                deviceWidth-currentImageViewHeight
            }else{
                deviceHeight - currentImageViewHeight
            }

            if (currentRotation%90==0.toFloat()){

                ValueAnimator.ofFloat(0f, 1f).apply {
                    duration=500
                    addUpdateListener {
                        val animatedValue = it.animatedValue as Float
                        imageView.run{
                            layoutParams.height=
                                currentImageViewHeight+(heightGap * animatedValue)
                                    .toInt()
                            rotation=currentRotation+90*animatedValue
                            requestLayout()
                        }
                    }
                }.start()
            }
        }

    }

    private fun setViews() {
        //카메라 버튼 클릭
        binding.camerashootingCameraIv.setOnClickListener {
            //카메라 호출 메소드
            openCamera()
        }

        //갤러리 버튼 클릭
        binding.camerashootingGalleryIv.setOnClickListener {
            //카메라 호출 메소드
            openGallery()
        }
    }

    fun openGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type=MediaStore.Images.Media.CONTENT_TYPE
        startActivityForResult(intent, FLAG_REA_GALLERY)
    }


    private fun openCamera() {
        //카메라 권한이 있는지 확인
        if(checkPermission(CAMERA_PERMISSION,FLAG_PERM_CAMERA)) {
//            //권한이 있으면 카메라를 실행시킵니다.
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            createImageUri(newFileName(), "image/jpg")?.let { uri ->
                realUri = uri
                // MediaStore.EXTRA_OUTPUT을 Key로 하여 Uri를 넘겨주면
                // 일반적인 Camera App은 이를 받아 내가 지정한 경로에 사진을 찍어서 저장시킨다.
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                startActivityForResult(intent, FLAG_REQ_CAMERA)
            }
        }
    }
    //사진 각도 반환 함수
    fun exifOrientationToDegrees(exifOrientation: Int): Int {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270
        }
        return 0
    }

    // 이미지 회전 함수
    private fun rotate(bitmap: Bitmap, degree: Int) : Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix,true)
    }

    private fun newFileName(): String {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss")
        val filename = sdf.format(System.currentTimeMillis())
        return "$filename.jpg"
    }

    private fun createImageUri(filename: String, mimeType: String): Uri? {
        var values = ContentValues()
        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename)
        values.put(MediaStore.Images.Media.MIME_TYPE, mimeType)
        return this.contentResolver.insert(EXTERNAL_CONTENT_URI, values)
    }


    //권한이 있는지 체크하는 메소드
    fun checkPermission(permissions:Array<out String>,flag:Int):Boolean{
        //안드로이드 버전이 마쉬멜로우 이상일때
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            for(permission in permissions){
                //만약 권한이 승인되어 있지 않다면 권한승인 요청을 사용에 화면에 호출합니다.
                if(ContextCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this,permissions,flag)
                    return false
                }
            }
        }
        return true
    }

    //checkPermission() 에서 ActivityCompat.requestPermissions 을 호출한 다음 사용자가 권한 허용여부를 선택하면 해당 메소드로 값이 전달 됩니다.
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            FLAG_PERM_STORAGE ->{
                for(grant in grantResults){
                    if(grant != PackageManager.PERMISSION_GRANTED){
                        //권한이 승인되지 않았다면 return 을 사용하여 메소드를 종료시켜 줍니다
                        Toast.makeText(this,"저장소 권한을 승인해야지만 앱을 사용할 수 있습니다..",Toast.LENGTH_SHORT).show()
                        finish()
                        return
                    }
                }
                //카메라 호출 메소드
                setViews()
            }
            FLAG_PERM_CAMERA ->{
                for(grant in grantResults){
                    if(grant != PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this,"카메라 권한을 승인해야지만 카메라를 사용할 수 있습니다.",Toast.LENGTH_SHORT).show()
                        return
                    }
                }
                openCamera()
            }
        }
    }

//    var realUri: Uri? = null
//    var data: Uri?=null

    //startActivityForResult 을 사용한 다음 돌아오는 결과값을 해당 메소드로 호출합니다.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                FLAG_REQ_CAMERA -> {
                    if (resultCode == RESULT_OK) {
                        realUri?.let { uri ->
                            binding.ivPre.setImageURI(uri)
                            binding.ivPre.visibility = View.VISIBLE

                            intent.putExtra("uri", uri.toString())
                            binding.camerashootingturnIb.setOnClickListener {
                                binding.ivPre.visibility = View.GONE
                            }
                            Log.d("u1", uri.toString())
                            //체크버튼
                            binding.camerashootingCheckIb.setOnClickListener {
                                val intent=Intent(this, CheckCodingQuestionActivity::class.java)
                                intent.putExtra("uri", uri)
                                startActivity(intent)
                                finish()
                                Log.d("u3", uri.toString())
                            }
                        }

                    }
                }
//                data?.data?
                FLAG_REA_GALLERY -> {
                    data?.data?.let { uri ->
                        binding.ivPre.setImageURI(uri)
                        binding.ivPre.visibility = View.VISIBLE
                        intent.putExtra("uri", uri.toString())
                        binding.camerashootingturnIb.setOnClickListener {
                            binding.ivPre.visibility = View.GONE
                        }
                        Log.d("u2", uri.toString())
                        //체크버튼
                        binding.camerashootingCheckIb.setOnClickListener {
                            val intent=Intent(this, CheckCodingQuestionActivity::class.java)
                            intent.putExtra("realUri", uri)
                            startActivity(intent)
                            finish()
                            Log.d("u3", uri.toString())
                        }
                    }
                }
            }
        }
    }



}