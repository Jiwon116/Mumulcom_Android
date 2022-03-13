package com.example.mumulcom

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.media.DrmInitData
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.example.mumulcom.databinding.QuestionAnswerItemBinding
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

import java.io.File


class RepliesForQuestionAdapter(val context: Context,var adopt:String,var writer:Boolean):RecyclerView.Adapter<RepliesForQuestionAdapter.ViewHolder>(),
    LikeReplyView, CommentsForReplyView, UploadCommentView, AdoptReplyView {


    private val replyList = ArrayList<Reply>()

    private var isWriter : Boolean = false // 조회한 사람이 이 글을 작성한 작성자인지 확인

    private var isAdopted : String ="N" // 채택하기
    private var isClickAdoptButton : Boolean = false // 채택하기 버튼 눌렀는지 확인

    private var isLike : Boolean = false // 좋아요
    private var isCommentClick : Boolean = false // comment 이미지를 클릭했는지 여부 확인
    private lateinit var imageViewPagerAdapter: ImageViewPagerAdapter
    private  var replyIdx : Long = -1
    private lateinit var commentsForReplyAdapter: CommentsForReplyAdapter
    private lateinit var comment : String // 댓글 작성 내용 저장할 변수

   // private var imgUrl : String? = null






    // 클릭 인터페이스 정의
    interface RepliesItemClickListener{
        fun onRemoveAnswerButton(isClicked:Boolean)
        //   fun onClickAdoptButton(isClicked:Boolean)
        fun onAccessAlbum()
        fun getImageFile(): Bitmap
    }


    // 리스너 객체를 전달받는 함수랑 리스너 객체 저장 변수
    private lateinit var repliesItemClickListener : RepliesItemClickListener

    fun setRepliesClickListener(repliesItemClickListener: RepliesItemClickListener){
        this.repliesItemClickListener = repliesItemClickListener
    }


     fun String?.toPlainRequestBody() = requireNotNull(this).toRequestBody("text/plain".toMediaTypeOrNull())


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: QuestionAnswerItemBinding =  QuestionAnswerItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        val uploadCommentService = UploadCommentService()
        uploadCommentService.setUploadCommentView(this)

        isAdopted = adopt // 이 글 자체가 채택이 된 답변이 있는지를 확인하는 변수  ( Y or N )
        isWriter =  writer // 조회한 사람이 이 글을 작성한 작성자인지 확인 (Ture or False )

        Log.d("adopt",isAdopted)
        Log.d("writer",isWriter.toString())

        binding.addPhotoIv.setOnClickListener { // 사진 추가 버튼 클릭.
             repliesItemClickListener.onAccessAlbum()
        }

        binding.uploadCommentTv.setOnClickListener { // 게시 버튼 누름.
            Log.d("umc","게시를 누름.")

           val bitmap =repliesItemClickListener.getImageFile()
           val uploadbitmap = Bitmap.createScaledBitmap(bitmap,300,300,true)
            val stream = ByteArrayOutputStream()
            uploadbitmap.compress(Bitmap.CompressFormat.PNG,100,stream)
            val byteArray = stream.toByteArray()
            val sendimage = byteArray.toRequestBody("image/*".toMediaTypeOrNull())
            val multibody : MultipartBody.Part =
                MultipartBody.Part.createFormData("images","image",sendimage)


            val replyIdxRequestBody : RequestBody = replyIdx.toString().toPlainRequestBody()
            val textHashMap = hashMapOf<String,RequestBody>()
//            textHashMap["replyIdx"] =
//            textHashMap["userIdx"] =
//            textHashMap["content"] =


            comment =binding.commentEditText.text.toString() // 입력한 댓글을 가져옴
            if(comment==""){
                Toast.makeText(context,"댓글을 입력해주세요",Toast.LENGTH_SHORT).show()

            }else{
                //  api 에 연결해서 넘겨줌.

             //   uploadCommentService.getUploadComment(getJwt(context), ReplyRequest(replyIdx, getUserIdx(context),comment),)

                Handler(Looper.getMainLooper()).postDelayed({
                    getCommentsForReply() // 댓글 가져오는 api 호출
                    commentsForReplyAdapter = CommentsForReplyAdapter(context) // recyclerView adapter 연결
                    binding.commentRecyclerView.adapter = commentsForReplyAdapter
                    binding.commentRecyclerView.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
                    //    commentsForReplyAdapter.notifyDataSetChanged()
                },500)


                binding.commentEditText.text.clear()

            }


        }




        return ViewHolder(binding)
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(replyList[position])

        // 좋아요 처리
        holder.binding.itemLikeIv.setOnClickListener {
            isLike = !isLike
            if(isLike){
                holder.binding.itemLikeIv.setImageResource(R.drawable.ic_liked)
                setLikeReply() // 답변에 대한 좋아요 처리

                Handler(Looper.getMainLooper()).postDelayed({

                },500)

            }else{
                holder.binding.itemLikeIv.setImageResource(R.drawable.ic_like)
                setLikeReply() // 답변에 대한 좋아요 처리

                Handler(Looper.getMainLooper()).postDelayed({


                },500)

            }
        }
        // 채택하기 처리
        holder.binding.selectAnswerIv.setOnClickListener {
            //  채택하는 api 호출
            val adoptReplyService = AdoptReplyService()
            adoptReplyService.setAdoptReplyView(this)
            Log.d("house",replyIdx.toString())
            adoptReplyService.getAdoptReply(getJwt(context), getUserIdx(context),replyIdx)
            isClickAdoptButton = true
            //  답변 api 재호출 (QuestionDetailActivity)
            //  repliesItemClickListener.onClickAdoptButton(isClickAdoptButton)
            holder.binding.selectAnswerIv.setImageResource(R.drawable.ic_adopt_reply_ok)
        }

        // 댓글 처리
        holder.binding.commentIv.setOnClickListener {
            isCommentClick=!isCommentClick
            if(isCommentClick){
                holder.binding.commentIv.setImageResource(R.drawable.ic_message_select)
                holder.binding.itemCommentTv.setTextColor(Color.parseColor("#F7B77C"))
                holder.binding.commentLinearLayout.visibility = View.VISIBLE // 댓글창 염

                // api 연결
                getCommentsForReply() // 댓글 가져오는 api 호출
                commentsForReplyAdapter = CommentsForReplyAdapter(context) // recyclerView adapter 연결
                holder.binding.commentRecyclerView.adapter = commentsForReplyAdapter
                holder.binding.commentRecyclerView.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)




            }else{
                holder.binding.commentIv.setImageResource(R.drawable.ic_message)
                holder.binding.itemCommentTv.setTextColor(Color.parseColor("#000000"))
                holder.binding.commentLinearLayout.visibility = View.GONE // 댓글창 닫음.
            }
            //  답변하기 버튼 gone or visible 변경 (QuestionDetailActivity)
            repliesItemClickListener.onRemoveAnswerButton(isCommentClick)


        }




    }// end of onBindViewHolder


    private fun getCommentsForReply(){
        val commentsForReplyService = CommentsForReplyService()
        commentsForReplyService.setCommentsForReplyView(this)
        Log.d("replyIdx:---",replyIdx.toString())
        //  commentsForReplyService.getCommentsForReply(46)
        commentsForReplyService.getCommentsForReply(replyIdx)
    }

    override fun getItemCount(): Int {
        return replyList.size
    }




    @SuppressLint("NotifyDataSetChanged")
    fun addQuestions(replies:ArrayList<Reply> ){
        this.replyList.clear()
        this.replyList.addAll(replies)

        notifyDataSetChanged()
    }



    inner class ViewHolder(val binding:QuestionAnswerItemBinding):RecyclerView.ViewHolder(binding.root){

        fun bind(reply: Reply){
            Glide.with(context).load(reply.profileImgUrl).into(binding.profileIv) // 프로필 이미지
            binding.nickNameTv.text = reply.nickname // 닉네임
            binding.createdAtTv.text = reply.createdAt // 작성 날짜


            if(reply.replyUrl==null){
                binding.replyUrl.visibility = View.GONE
   //             binding.replyUrl.text = "참고 링크 : 없음."// 참고 링크
            }else{
                binding.replyUrl.text = "참고 링크 : "+reply.replyUrl // 참고 링크
            }

            binding.contentTv.text = reply.content // 답변 내용
            binding.itemCommentTv.text = reply.reReplyCount.toString() // 대댓글수
            binding.itemLikeTv.text = reply.likeCount.toString() // 좋아요 수

            // 답변 이미지 viewpager 연결
            imageViewPagerAdapter = ImageViewPagerAdapter(context)
            if(reply.replyImgUrl.size==0){
                binding.itemLl3.visibility = View.GONE
            }else{
                imageViewPagerAdapter.addQuestions(reply.replyImgUrl)
                binding.viewPager.adapter = imageViewPagerAdapter
                binding.indicator.setViewPager(binding.viewPager)
            }

            if(reply.isLiked=="Y"){
                isLike= true
                binding.itemLikeIv.setImageResource(R.drawable.ic_liked)
            }

            replyIdx =reply.replyIdx // 답변에 대한 고유 번호 저장. -> 서버에 넘겨서 댓글 받아옴.
            Log.d("replyIdx",replyIdx.toString())

            // --------------- 채택하기 처리 -------------------------------

            if(isAdopted=="Y"){
                if(reply.status=="Y")   {
                    binding.selectAnswerIv.visibility = View.VISIBLE
                    binding.selectAnswerIv.setImageResource(R.drawable.ic_adopt_reply_ok)
                }
                if(reply.status=="N"){
                    binding.selectAnswerIv.visibility = View.GONE
                }
            }

            if(isWriter){ // 글 작성자가 조회중
                if(isAdopted=="Y"){
                    if(reply.status=="Y"){
                        binding.selectAnswerIv.visibility = View.VISIBLE
                        binding.selectAnswerIv.setImageResource(R.drawable.ic_adopt_reply_ok)
                        binding.selectAnswerIv.isEnabled = false
                    }
                    if(reply.status=="N"){
                        binding.selectAnswerIv.visibility = View.GONE
                    }
                }else{
                    binding.selectAnswerIv.visibility = View.VISIBLE
                    binding.selectAnswerIv.setImageResource(R.drawable.ic_adopt_reply_not)
                }

            }else{ // 다른 사람이 조회중
                if(reply.status=="Y"){
                    binding.selectAnswerIv.visibility= View.VISIBLE
                    binding.selectAnswerIv.setImageResource(R.drawable.ic_adopt_reply_ok)
                    binding.selectAnswerIv.isEnabled = false
                }
            }



        }// end of bind()

    }


    private fun setLikeReply(){
        val likeReplyService = LikeReplyService()
        likeReplyService.setLikeReplyView(this)
        likeReplyService.getLikeReply(getJwt(context), LikeReplySend(replyIdx,getUserIdx(context)))
    }








    // --------------- LikeReplyView implement : 답변에 대한 좋아요 ----------------------
    override fun onGetLikeReplyLoading() {
        Log.d("답변에 대한 좋아요/API","로딩중...")
    }

    override fun onGetLikeReplySuccess(result: Like) {
        Log.d("likeTest","질문을 만든 유저 id : "+result.noticeTargetUserIdx) // 해당 질문을 작성한 유저 id
        Log.d("likeTest","좋아요 내용 :  "+result.noticeContent)  // 000 글을 좋아요 했습니다./취소 했습니다.
    }

    override fun onGetLikeReplyFailure(code: Int, message: String) {
        when(code){
            400-> Log.d("개념질문 상세페이지/API",message)
        }
    }



    // ---------------  CommentsForReplyView  implement : 답변에 대한 댓글 ----------------

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









    // ---------------  UploadComment  implement : 답변에 대한 댓글 달기 ----------------

    override fun onGetUploadCommentLoading() {
        Log.d("답변에 댓글달기/API","로딩중...")
    }

    override fun onGetUploadCommentSuccess(result: Like) {
        Log.d("답변에 댓글달기/API","성공")
        Log.d("답변에 댓글달기/API",result.noticeContent)
        Toast.makeText(context,"댓글을 달았습니다.",Toast.LENGTH_SHORT).show()

    }

    override fun onGetUploadCommentFailure(code: Int, message: String) {
        when(code){
            400-> Log.d("답변에 댓글달기/API",message)
            2800 -> Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
        }
    }


    // -------------- AdoptReplyView implement  : 채택하기 ----------------

    override fun onGetAdoptReplyLoading() {
        Log.d("답변 채택하기/API","로딩중..")
    }

    override fun onGetAdoptReplySuccess(result: Adopt) {
        Log.d("답변 채택하기/API","성공")
        Log.d("답변 채택하기/API",result.noticeMessage)
        Toast.makeText(context,"해당 답변을 채택했습니다.",Toast.LENGTH_SHORT).show()

    }

    override fun onGetAdoptReplyFailure(code: Int, message: String) {
        when(code){
            400-> Log.d("답변 채택하기/API",message)
        }
    }


}