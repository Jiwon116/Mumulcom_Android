package com.example.mumulcom

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.mumulcom.databinding.ActivityCommentBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

class CommentActivity : AppCompatActivity() ,CommentsForReplyView, UploadCommentView {// end of class

    lateinit var binding : ActivityCommentBinding
    var replyIdx : Long = -1

    private lateinit var imageViewPagerAdapter: ImageViewPagerAdapter
    private lateinit var commentsForReplyAdapter: CommentsForReplyAdapter

    // 앨범 이미지 관련 변수
    private lateinit var resultLauncher : ActivityResultLauncher<Intent>

    // bitmap 변수
    private  var path : Bitmap? = null
    // multipart 관련 변수
    private  var multibody : MultipartBody.Part? = null

    private val uploadCommentService = UploadCommentService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val intent = intent
        replyIdx=intent.getLongExtra("replyIdx",-1) // 보고있는 답변 idx 가져옴.
        val profileImage=intent.getStringExtra("profileImage")
        val nickname=intent.getStringExtra("nickname")
        val createdAt=intent.getStringExtra("createdAt")
        val content=intent.getStringExtra("content")
        val replyUrl=intent.getStringExtra("replyUrl")
        val replyImageResult  = intent.getStringArrayListExtra("replyImgResult")
        Log.d("commentActivity",replyImageResult.toString())
        Log.d("commentActivity","replyIdx:"+replyIdx)

        Glide.with(this).load(profileImage).into(binding.profileIv)
        binding.nickNameTv.text = nickname
        binding.createdAtTv.text = createdAt
        binding.contentTv.text = content
        if(replyUrl!=null){
            binding.replyUrl.text = replyUrl
        }else{
            binding.replyUrl.visibility = View.GONE
        }
        if(replyImageResult?.size==0){
            binding.itemLl3.visibility = View.GONE
        }else{
            imageViewPagerAdapter = ImageViewPagerAdapter(this)
            imageViewPagerAdapter.addQuestions(replyImageResult as ArrayList<String>)
            binding.viewPager.adapter = imageViewPagerAdapter
            binding.indicator.setViewPager(binding.viewPager)

        }


        getCommentsForReply() // 답변에 대한 댓글들 가져오기
        initRecyclerView() // recyclerview <-> adapter 연결

        uploadCommentService.setUploadCommentView(this)


        // 사진 추가 기능
        binding.addPhotoIv.setOnClickListener {
            addPhoto() // 사진 추가 함수
        }

        binding.refreshLayout.setOnRefreshListener { // 페이지 reload
            getCommentsForReply() // 답변에 대한 댓글들 가져오기
            initRecyclerView()

            binding.refreshLayout.isRefreshing = false
        }


        // 댓글 전송 기능
        binding.commentSendTv.setOnClickListener {
            val comment = binding.commentEditText.text.toString()
            Log.d("apple",path.toString())
            // todo 사진과 댓글내용 서버에 전달.
            if(path!=null){ // 이미지가 추가됬을때 -> 이미지 & 댓글 전달
                val uploadBitmap = Bitmap.createScaledBitmap(path!!,500,400,true)
                val stream = ByteArrayOutputStream()
                uploadBitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
                val byteArray = stream.toByteArray()
                val sendImage = byteArray.toRequestBody("image/*".toMediaTypeOrNull())
                multibody = MultipartBody.Part.createFormData("images","image.jpeg",sendImage)

                if(comment==""){
                    Toast.makeText(this,"댓글을 입력해주세요",Toast.LENGTH_SHORT).show()
                }else{
                    uploadCommentService.getUploadComment(getJwt(this),
                        CommentSend(replyIdx, getUserIdx(this),comment),multibody)

                    Handler(Looper.getMainLooper()).postDelayed({
                        getCommentsForReply() // 댓글 가져오는 api 호출
                        initRecyclerView()

                    },500)

                    binding.commentEditText.text.clear()
                    binding.imageLinearLayout.visibility = View.GONE
                    path = null

                }

            }else{ // 이미가 추가 안됬을때 -> 댓글만 전달.

                if(comment==""){
                    Toast.makeText(this,"댓글을 입력해주세요",Toast.LENGTH_SHORT).show()
                }else{

                    uploadCommentService.getUploadComment(getJwt(this),
                        CommentSend(replyIdx, getUserIdx(this),comment),null)

                    Handler(Looper.getMainLooper()).postDelayed({
                        getCommentsForReply() // 댓글 가져오는 api 호출
                        initRecyclerView()

                    },500)

                    binding.commentEditText.text.clear()

                }
            }

        }



        binding.backIv.setOnClickListener {
            finish()
        }

        // 이미지 삭제 버튼 클릭
        binding.deleteCommentImage.setOnClickListener {
            binding.imageLinearLayout.visibility = View.GONE
            path = null
        }


        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result->
            if(result.resultCode== Activity.RESULT_OK){
                result.data?.data?.let{ // 결과가 제대로 들어왔을때 (이미지 주소를 잘 가져왔을때) 실행
                    uri->
                    path = null // 앨범에서 가져올때마다 초기화
                    val inputStream = uri.let{
                        contentResolver.openInputStream(
                            it
                        )
                    }
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream!!.close()
                    path = bitmap // 전역변수에 bitmap 값 저장.
                }
                if(result.data?.data==null){
                    Toast.makeText(this,"사진을 가져오지 못했습니다.",Toast.LENGTH_SHORT).show()
                    binding.imageLinearLayout.visibility = View.GONE
                }else{
                    binding.imageLinearLayout.visibility = View.VISIBLE
                    Glide.with(this).load(result.data!!.data).into(binding.commentImageView)
                }

            }else return@registerForActivityResult
        }

    }// end of onCreate


    private fun initRecyclerView(){
        commentsForReplyAdapter= CommentsForReplyAdapter(this)
        binding.commentRecyclerView.adapter = commentsForReplyAdapter
        binding.commentRecyclerView.layoutManager = LinearLayoutManager(this)

    }

    private fun addPhoto(){
        when{
            ContextCompat.checkSelfPermission(
                this@CommentActivity,android.Manifest.permission.READ_EXTERNAL_STORAGE
            )== PackageManager.PERMISSION_GRANTED->{
                navigatePhoto() // 엘범에서 사진 선택
            }
            shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE)->{
                // 교육용 팝업 확인후 권한 팝업 띄움
                showPermissionContextPopup()
            }
            else->{
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1010)
            }
        }
    }
    private fun navigatePhoto() { // 갤러리에서 사진 가져오는 함수
        // SAF 기능 구현
        val intent = Intent(Intent.ACTION_GET_CONTENT) // ACTION_GET_CONTENT 은 SAF 기능을 실행시켜 content 를 가져온다.
        intent.type = "image/*"
        resultLauncher.launch(intent)

    }

    private fun showPermissionContextPopup(){
        AlertDialog.Builder(this)
            .setTitle("권한 팝업")
            .setMessage("앨범 접근에 대한 권한을 허락해주세요.")
            .setPositiveButton("동의") { _, _ ->
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1010)
            }
            .create()
            .show()
    }











    // -------- 댓글 관련 --------------------

    private fun getCommentsForReply(){
        val commentsForReplyService = CommentsForReplyService()
        commentsForReplyService.setCommentsForReplyView(this)
        commentsForReplyService.getCommentsForReply(replyIdx)

    }

    override fun onGetCommentsLoading() {
        Log.d("답변에 대한 댓글 가져오기/API","로딩중...")
    }

    override fun onGetCommentsSuccess(result: ArrayList<Comment>) {
        Log.d("답변에 대한 댓글 가져오기/API","성공")
        commentsForReplyAdapter.addComments(result)
    }

    override fun onGetCommentsFailure(code: Int, message: String) {
        when(code){
            400-> Log.d("개념질문 상세페이지/API",message)
        }
    }

    // ---------  댓글 업로드 관련 -------------------

    override fun onGetUploadCommentLoading() {
        Log.d("답변에 댓글달기/API","로딩중...")
    }

    override fun onGetUploadCommentSuccess(result: Like) {
        Log.d("답변에 댓글달기/API","성공")
        Log.d("답변에 댓글달기/API",result.noticeContent)
        Toast.makeText(this,"댓글을 달았습니다.",Toast.LENGTH_SHORT).show()

    }

    override fun onGetUploadCommentFailure(code: Int, message: String) {
        when(code){
            400-> Log.d("답변에 댓글달기/API",message)
            2800 -> Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
        }
    }


}//end of class
