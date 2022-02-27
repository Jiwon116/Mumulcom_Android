package com.example.mumulcom

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import com.example.mumulcom.databinding.ActivityCodingcamerashootingBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.IOException
import java.util.*


class CodingCameraShootingActivity: AppCompatActivity() {

    lateinit var binding: ActivityCodingcamerashootingBinding

    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>//이동(카메라,앨범)

    //파이어스토리지
    val IMAGE_PICK=1111
    var selectImage:Uri?=null
    lateinit var storage:FirebaseStorage
    lateinit var firestore:FirebaseFirestore

    //권한
    val FLAG_PERM_STORAGE = 99
    val STORAGE_PERMISSION = arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCodingcamerashootingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //이미지 선택 안하고 싶을 수 있으니
        binding.camerashootingBackIb.setOnClickListener {
            onBackPressed()
        }


        //삭제버튼+이미지 삭제하고 체크 버튼 누르면 토스트 메세지 띄우기
        binding.camerashootingturnIb.setOnClickListener {
            binding.ivPre.visibility=View.INVISIBLE
            binding.camerashootingCheckIb.onThrottleClick {
                Toast.makeText(this, "이미지를 삽입해주세요.", Toast.LENGTH_SHORT).show()
            }
        }


        storage = FirebaseStorage.getInstance()
        firestore = FirebaseFirestore.getInstance()

// 화면이 만들어 지면서 정장소 권한을 체크
        // 권한이 승인되어 있으면 카메라를 호출하는 메소드를 실행
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

    private fun setViews(){
        //카메라 버튼 클릭
//        binding.camerashootingCameraIv.setOnClickListener {
//            //카메라 호출 메소드
//            openCamera()
//        }

        //갤러리 버튼 클릭
        binding.camerashootingGalleryIv.setOnClickListener {
            var intent = Intent(Intent.ACTION_PICK) //선택하면 무언가를 띄움. 묵시적 호출
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK)
            binding.camerashootingCheckIb.visibility=View.VISIBLE
            binding.camerashootingturnIb.visibility=View.VISIBLE
            binding.camerashootingBnv.visibility=View.VISIBLE
            //체크버튼 누르면 이미지 전송(파이어베이스)
            binding.camerashootingCheckIb.onThrottleClick {
                if (selectImage != null) {
                    var fileName =
                        SimpleDateFormat("yyyyMMddHHmmss").format(Date()) // 파일명이 겹치면 안되기 떄문에 시년월일분초 지정
                    storage.getReference().child("image").child(fileName)
                        .putFile(selectImage!!)//어디에 업로드할지 지정
                        .addOnSuccessListener { taskSnapshot -> // 업로드 정보를 담는다
                            taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { it ->
                                var imageUrl = it.toString()
                                var photo = Photo(imageUrl)
                                firestore.collection("coding-images")
                                    .document().set(photo)
                                    .addOnSuccessListener {
                                        finish()
                                    }
                                Log.d("PHOTO/imageUrl", imageUrl)
                                Log.d("PHOTO/photo", photo.toString())
                                val intent = Intent(
                                    this,
                                    CheckCodingQuestionActivity()::class.java
                                )//이미지 넘겨주기
                                intent.putExtra("PHOTO/PUT/imageUrl", imageUrl)
                                Log.d(
                                    "PHOTO/PUT/imageUrl",
                                    intent.putExtra("PHOTO/PUT/imageUrl", imageUrl).toString()
                                )
                                setResult(RESULT_OK, intent);
                                finish()//전 액티비티로 전달
                            }
                        }

                }
            }
        }

    }


    //미리보기 이미지
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            selectImage = data?.data
            if (selectImage != null) {
                // 사진 가져오기
                val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(selectImage!!))
                // 사진의 회전 정보 가져오기
                val orientation = getOrientationOfImage(selectImage!!).toFloat()
                // 이미지 회전하기
                val newBitmap = getRotatedBitmap(bitmap, orientation)
                // 회전된 이미지로 imaView 설정
                binding.ivPre.setImageBitmap(newBitmap)
                binding.ivPre.visibility=View.VISIBLE
            }
            else
                binding.ivPre.setImageURI(selectImage)
            binding.ivPre.visibility=View.VISIBLE
        }
    }

    //checkPermission() 에서 ActivityCompat.requestPermissions 을 호출한 다음 사용자가 권한 허용여부를 선택하면 해당 메소드로 값이 전달
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
//            FLAG_PERM_CAMERA ->{
//                for(grant in grantResults){
//                    if(grant != PackageManager.PERMISSION_GRANTED){
//                        Toast.makeText(this,"카메라 권한을 승인해야지만 카메라를 사용할 수 있습니다.",Toast.LENGTH_SHORT).show()
//                        return
//                    }
//                }
//                openCamera()
//            }
        }
    }

    // 중복클릭
    fun View.onThrottleClick(action: (v: View) -> Unit) {
        val listener = View.OnClickListener { action(it) }
        val interval: Long = 1000//1분
        setOnClickListener(OnThrottleClickListener(listener, interval))
    }

    // 이미지 회전 정보 가져오기
    @RequiresApi(Build.VERSION_CODES.N)
    private fun getOrientationOfImage(uri: Uri): Int {
        // uri -> inputStream
        val inputStream = contentResolver.openInputStream(uri)
        val exif: ExifInterface? = try {
            ExifInterface(inputStream!!)
        } catch (e: IOException) {
            e.printStackTrace()
            return -1
        }
        inputStream.close()

        // 회전된 각도 알아내기
        val orientation = exif?.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        if (orientation != -1) {
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> return 90
                ExifInterface.ORIENTATION_ROTATE_180 -> return 180
                ExifInterface.ORIENTATION_ROTATE_270 -> return 270
            }
        }
        return -90
    }

    // 이미지 회전하기
    @Throws(Exception::class)
    private fun getRotatedBitmap(bitmap: Bitmap?, degrees: Float): Bitmap? {
        if (bitmap == null) return null
        if (degrees == 90F) return bitmap
        val m = Matrix()
        m.setRotate(degrees, bitmap.width.toFloat() /2, bitmap.height.toFloat() /2)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, m, true)
        Log.d("bitmap", return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, m, true))
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
}