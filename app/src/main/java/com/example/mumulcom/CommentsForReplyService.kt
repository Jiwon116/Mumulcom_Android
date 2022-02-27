package com.example.mumulcom

import retrofit2.Call
import retrofit2.Response


// 답변에 대한 댓글을 가져오는 api
class CommentsForReplyService {
    private lateinit var commentsForReplyView: CommentsForReplyView

    // 외부 접근
    fun setCommentsForReplyView(commentsForReplyView: CommentsForReplyView){
        this.commentsForReplyView = commentsForReplyView
    }

    // 서버 연동
    fun getCommentsForReply(replyIdx:Long){
        val commentsForReplyService = getRetrofit().create(QuestionRetrofitInterface::class.java)

        commentsForReplyView.onGetCommentsLoading()

        commentsForReplyService.getCommentsForReply(replyIdx)
            .enqueue(object : retrofit2.Callback<CommentsForReplyResponse>{
                override fun onResponse(call: Call<CommentsForReplyResponse>, response: Response<CommentsForReplyResponse>) {

                    val resp = response.body()!!
                    when(resp.code){
                        1000->{
                            commentsForReplyView.onGetCommentsSuccess(resp.result)
                        }else-> commentsForReplyView.onGetCommentsFailure(resp.code,resp.message)
                    }

                }

                override fun onFailure(call: Call<CommentsForReplyResponse>, t: Throwable) {
                    commentsForReplyView.onGetCommentsFailure(400,"네트워크 오류가 발생했습니다.")
                }

            })
    }


}