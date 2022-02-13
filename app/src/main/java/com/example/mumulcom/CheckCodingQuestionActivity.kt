package com.example.mumulcom

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.mumulcom.databinding.ActivityCheckcodingquestionBinding
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File


//, CheckCodingQuestionView
class CheckCodingQuestionActivity:AppCompatActivity(), CheckCodingQuestionView {

    lateinit var binding: ActivityCheckcodingquestionBinding

    private lateinit var checkCodingService: CheckCodingService
    private var images: MutableList<String>? = null  // defalut값은 null, 건너뛰기 가능
    private var jwt: String = ""
    private var userIdx: Long = 1
    private lateinit var title:String
    private lateinit var currentError:String
    private lateinit var myCodingSkill:String
    private lateinit var codeQuestionUrl:String
    private var bigCategoryIdx:Long=1
    private var smallCategoryIdx:Long=1
    private var bigCategory: String? = null    // 선택한 상위 카테고리
    private var smallCategory: String? = null  // 선택한 하위 카테고리

    private var albumDatas=ArrayList<Album>()//뷰페이저
    private lateinit var checkCodingQuestionView: CheckCodingQuestionView

    var realUri: Uri? = null

    // 스피너 어댑터
    private lateinit var bigCategoryAdapter: ArrayAdapter<String>
    private lateinit var smallCategoryAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCheckcodingquestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // jwt = getJwt(this)
        // userIdx = getUserIdx(this)

        jwt = "eyJ0eXBlIjoiand0IiwiYWxnIjoiSFMyNTYifQ.eyJ1c2VySWR4Ijo3NSwiaWF0IjoxNjQ0NjQ5NzQ1LCJleHAiOjE2NDYxMjA5NzR9.WHV5dJciYzL3AixOfySplBCa2E9Xpz_FjuTXH8egCpU"
        userIdx= 75
        Log.d("jwt", jwt)


        // 카테고리 초기화
        setupBigCategorySpinner()
        setupBigCategorySpinnerHandler()

        // 화면 배경 누르면 키보드 사라지기
        binding.checkcodingquestionBackIv.setOnClickListener {
            CloseKeyboard()
        }

        binding.checkcodingquestionBackIv.setOnClickListener {
            startActivity(Intent(this, QuestionCategoryActivity::class.java))
        }

        binding.checkcodingquestionPlusIv.setOnClickListener {
            startActivity(Intent(this, CodingCameraShootingActivity::class.java))
        }

        binding.checkcodingquestionQuestionIv.setOnClickListener {
            checkCodingQuestioning()

        }
        checkCodingService = CheckCodingService()
        checkCodingService.setcheckcodingquestionView(this)

//        albumDatas.apply {
//            add(Album(R.drawable.ic_image_plus))
//            add(Album(R.drawable.ic_image_plus))
//            add(Album(R.drawable.ic_image_plus))
//            add(Album(R.drawable.ic_image_plus))
//            add(Album(R.drawable.ic_image_plus))
//        }



        val ViewPagerAdapter=ViewPagerAdapter(albumDatas)
        binding.checkcodingquestionVp.adapter=ViewPagerAdapter// 뷰페이저 어댑터 생성
        binding.checkcodingquestionVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL // 방향을 가로로
        binding.checkcodingIndicator.setViewPager(binding.checkcodingquestionVp)
        binding.checkcodingIndicator.createIndicators(5,0);
    }

//    data class ListData(var image: Int){
//
//    }

    //formdata
    fun sendAddRequest() {
        val gson = Gson()
        val data = gson.toJson(getCoding())
        Log.d("TAG-JSON", data.toString())
        val codeQuestionReqBody = data.toString().toRequestBody("dddddddssdfsdfsfdsfsdf".toMediaTypeOrNull())
        val codeQuestionReq = MultipartBody.Part.createFormData("codeQuestionReq", codeQuestionReqBody.toString())

        val fileBody = images.toString().toRequestBody("image/jpeg".toMediaTypeOrNull())
        val images = MultipartBody.Part.createFormData("images","images.png", fileBody)

        checkCodingService.checkCodingQuestion(jwt, images, codeQuestionReq)   // images, codeQuestionReq
    }

//    fun sendAddRequest() {
//        //val data = gson.toJson(getCoding())
//        val codeQuestionReqBody = getCoding().toString().toRequestBody("text/plain".toMediaTypeOrNull())
//        //val codeQuestionReqBody = RequestBody.create(MediaType.parse("application/json"), data)
//        val codeQuestionReq = MultipartBody.Part.createFormData("codeQuestionReq", codeQuestionReqBody)
//
//        val fileBody = images.toString().toRequestBody("image/jpeg".toMediaTypeOrNull())
//        //val fileBody = "".toRequestBody("".toMediaTypeOrNull())
//        val images = MultipartBody.Part.createFormData("images","images.png", fileBody)
//        Log.d("file", fileBody.toString())
//        Log.d("codeQuestionReq", codeQuestionReq.toString())
//
////        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_image_plus)
////        val stream = ByteArrayOutputStream()
////        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
////        val filebody = stream.toByteArray()
//
//
//        Log.d("userIdx : ", userIdx.toString())
//        Log.d("title : ", title)
//        Log.d("currentError : ", currentError)
//        Log.d("myCodingSkill : ", myCodingSkill)
//        Log.d("codeQuestionUrl : ", codeQuestionUrl)
//        Log.d("bigCategoryIdx : ", bigCategoryIdx.toString())
//        Log.d("smallCategoryIdx :", smallCategoryIdx.toString())
//        checkCodingService.checkCodingQuestion(jwt, images, codeQuestionReq)   // images, codeQuestionReq
//    }



    private fun getCoding(): CheckCoding {  // view에서 받은 값들

//        val images:String= binding.checkcodingquestionVp.toString()
        title=binding.checkcodingquestionTitleTextEt.text.toString()
        currentError=binding.checkcodingquestionStopPartTextEt.text.toString()
        myCodingSkill=binding.checkcodingquestionCodingLevelTextEt.text.toString()
        codeQuestionUrl=binding.checkcodingquestionErrorCodeTextEt.text.toString()
        bigCategoryIdx=binding.checkcodingquestionBigCategorySp.selectedItemPosition.toLong()+1
        smallCategoryIdx=binding.checkcodingquestionSmallCategorySp.selectedItemPosition.toLong()+1

//        Log.d("images : ", images)
        /* Log.d("userIdx : ", userIdx.toString())
         Log.d("title : ", title)
         Log.d("currentError : ", currentError)
         Log.d("myCodingSkill : ", myCodingSkill)
         Log.d("codeQuestionUrl : ", codeQuestionUrl)*/
//        Log.d("bigCategoryIdx : ", bigCategoryIdx.toString())
//        Log.d("smallCategoryIdx :", smallCategoryIdx.toString())

        return CheckCoding(userIdx, title, currentError, myCodingSkill, codeQuestionUrl, bigCategoryIdx, smallCategoryIdx)
    }




    private fun checkCodingQuestioning(
    ) {
        if(binding.checkcodingquestionSmallCategorySp.isEnabled()==false){
            Toast.makeText(this, "카테고리를 선택해주세요.", Toast.LENGTH_SHORT).show()

            return
        }
        if (binding.checkcodingquestionTitleTextEt.text.isEmpty()) {

            Toast.makeText(this, "제목을 작성해주세요.", Toast.LENGTH_SHORT).show()

            return
        }
        if (binding.checkcodingquestionStopPartTextEt.text.isEmpty()) {

            Toast.makeText(this, "현재 막힌 부분을 작성해주세요.", Toast.LENGTH_SHORT).show()

            return
        }

        sendAddRequest()
        Log.d("CHECKCODING/API-MAIN", "메인")
    }

    //응답
    override fun onCheckCodingQuestionLoading() {
        Toast.makeText(this, "잠시만 기다려주세요", Toast.LENGTH_LONG).show()
    }

    override fun onCheckCodingQuestionSuccess(result: String) {
        Toast.makeText(this, "성공", Toast.LENGTH_LONG).show()
        startActivity(Intent(this, QuestionBoardActivity::class.java))
    }

    override fun onCheckCodingQuestionFailure(code:Int, message: String) {
        when(code){
            400 -> Toast.makeText(this, "네트워크 연결을 실패했습니다", Toast.LENGTH_LONG).show()
        }
    }

//    private fun startBoard(){
//        getCoding()
//            fun onItemClick(checkCodingQuestion: CheckCodingQuestion) {
//                startQuestionBoardActivity(checkCodingQuestion)  // 이동
//            }
//    }
//       fun startQuestionBoardActivity(checkCodingQuestion: CheckCodingQuestion) {
//            val intent = Intent(this, QuestionBoardActivity::class.java)
//            intent.putExtra("title", checkCodingQuestion.title)
//            intent.putExtra("currentError", checkCodingQuestion.currentError)
//            intent.putExtra("myCodingSkill", checkCodingQuestion.myCodingSkill)
//            intent.putExtra("codeQuestionUrl", checkCodingQuestion.codeQuestionUrl)
//            intent.putExtra("bigCategoryName", checkCodingQuestion.bigCategoryName)
//            intent.putExtra("smallCategoryName", checkCodingQuestion.smallCategoryName)
//            startActivity(intent)
//        }


    // 키보드 사라지는 함수
    fun CloseKeyboard() {
        var view = this.currentFocus

        if(view != null) {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }









    /********************* 스피너 ********************/
    // dp 값을 px 값으로 변환해주는 함수
    private fun dipToPixels(dipValue: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dipValue,
            resources.displayMetrics
        )
    }

    // big category spinner에 arrayadapter 연결
    private fun setupBigCategorySpinner() {
        // 상위 카테고리들 (앱, 웹, 서버, 프로그래밍 언어, 기타)
        val bigCategoryArray = resources.getStringArray(R.array.big_category)
        bigCategoryAdapter = object : ArrayAdapter<String>(this, R.layout.item_big_category){
            @SuppressLint("CutPasteId")
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)
                if(position == count) {
                    // 마지막 포지션의 textView를 힌트용으로 사용
                    (v.findViewById<View>(R.id.item_big_category_tv) as TextView).text = ""
                    // 아이템의 마지막 값을 불러와 hint로 추가함
                    (v.findViewById<View>(R.id.item_big_category_tv) as TextView).hint = getItem(count)
                }
                return v
            }
            override fun getCount(): Int {
                // 마지막 아이템은 hint용이기 때문에 1을 빼줌
                return super.getCount() - 1
            }
        }
        // 아이템 추가
        bigCategoryAdapter.addAll(bigCategoryArray.toMutableList())
        // hint로 사용할 문구를 마지막 아이템에 추가
        bigCategoryAdapter.add("상위 선택")
        // 어댑터 연결
        binding.checkcodingquestionBigCategorySp.adapter = bigCategoryAdapter
        // 스피너 초기값을 마지막 아이템으로 설정
        binding.checkcodingquestionBigCategorySp.setSelection(bigCategoryAdapter.count)

        // droplist를 스피너와 간격을 두고 나오게 함 -> 아이템 크기 = 125px
        binding.checkcodingquestionBigCategorySp.dropDownVerticalOffset = dipToPixels(45f).toInt()
    }

    // 스피너 클릭 이벤트 핸들러
    private fun setupBigCategorySpinnerHandler() {
        binding.checkcodingquestionBigCategorySp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                binding.checkcodingquestionBigCategorySp.setBackgroundResource(R.drawable.bg_category_outline)
                binding.checkcodingquestionSmallCategorySp.isEnabled = true

                if (binding.checkcodingquestionBigCategorySp.getItemAtPosition(position).equals("앱")) {
                    selectedCategory(position)
                    binding.checkcodingquestionSmallCategorySp.visibility = View.VISIBLE
                    // 앱 하위 카테고리 (안드로이드, iOS)
                    val smallCategoryAppArray = resources.getStringArray(R.array.small_category_app)
                    setupSmallCategorySpinner(smallCategoryAppArray)
                    setupSmallCategorySpinnerHandler()
                }

                else if (binding.checkcodingquestionBigCategorySp.getItemAtPosition(position).equals("웹")) {
                    selectedCategory(position)
                    binding.checkcodingquestionSmallCategorySp.visibility = View.VISIBLE
                    // 웹 하위 카테고리 (HTML, CSS, React)
                    val smallCategoryWebArray = resources.getStringArray(R.array.small_category_web)
                    setupSmallCategorySpinner(smallCategoryWebArray)
                    setupSmallCategorySpinnerHandler()
                }

                else if (binding.checkcodingquestionBigCategorySp.getItemAtPosition(position).equals("서버")) {
                    selectedCategory(position)
                    binding.checkcodingquestionSmallCategorySp.visibility = View.VISIBLE
                    // 서버 하위 카테고리 (Node.js, Spring)
                    val smallCategoryServerArray = resources.getStringArray(R.array.small_category_server)
                    setupSmallCategorySpinner(smallCategoryServerArray)
                    setupSmallCategorySpinnerHandler()
                }

                else if (binding.checkcodingquestionBigCategorySp.getItemAtPosition(position).equals("프로그래밍 언어")) {
                    selectedCategory(position)
                    binding.checkcodingquestionSmallCategorySp.visibility = View.VISIBLE
                    // 프로그래밍 언어 하위 카테고리 (C, C++, JavaScript, Java, Python)
                    val smallCategoryProgramingArray = resources.getStringArray(R.array.small_category_programing)
                    setupSmallCategorySpinner(smallCategoryProgramingArray)
                    setupSmallCategorySpinnerHandler()
                }

                else if (binding.checkcodingquestionBigCategorySp.getItemAtPosition(position).equals("기타")) {
                    selectedCategory(position)
                    // 기타 선택 시 하위 선택 스피너 사라짐
                    binding.checkcodingquestionSmallCategorySp.visibility = View.GONE
                    // 하위 카테고리에 null 값
                    smallCategory = null
                }

                else if (binding.checkcodingquestionBigCategorySp.getItemAtPosition(position).equals("상위 선택")) {
                    bigCategory = null
                    Log.i(ContentValues.TAG, "상위 카테고리 확인: $bigCategory")
                    binding.checkcodingquestionSmallCategorySp.visibility = View.VISIBLE
                    // 하위 선택 뜨기
                    val smallCategoryArray = resources.getStringArray(R.array.small_category)
                    setupSmallCategorySpinner(smallCategoryArray)
                    setupSmallCategorySpinnerHandler()
                    // 하위 카테고리 스피너 사용 불가
                    binding.checkcodingquestionSmallCategorySp.isEnabled = false
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }

    private fun selectedCategory(position: Int) {
        // 상위 선택하면 배경 변경
        binding.checkcodingquestionBigCategorySp.setBackgroundResource(R.drawable.bg_category_selected)
        // bigCategory 변수에 상위 카테고리 저장하기
        bigCategory = binding.checkcodingquestionBigCategorySp.getItemAtPosition(position).toString()
        Log.i(ContentValues.TAG, "상위 카테고리 확인: $bigCategory")
    }



    // big category spinner에 arrayadapter 연결
    private fun setupSmallCategorySpinner(array: Array<String>) {
        smallCategoryAdapter = object : ArrayAdapter<String>(this, R.layout.item_small_category){
            @SuppressLint("CutPasteId")
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)
                if(position == count) {
                    // 마지막 포지션의 textView를 힌트용으로 사용
                    (v.findViewById<View>(R.id.item_small_category_tv) as TextView).text = ""
                    // 아이템의 마지막 값을 불러와 hint로 추가함
                    (v.findViewById<View>(R.id.item_small_category_tv) as TextView).hint = getItem(count)
                }
                return v
            }
            override fun getCount(): Int {
                // 마지막 아이템은 hint용이기 때문에 1을 빼줌
                return super.getCount() - 1
            }
        }
        // 아이템 추가
        smallCategoryAdapter.addAll(array.toMutableList())
        // hint로 사용할 문구를 마지막 아이템에 추가
        smallCategoryAdapter.add("하위 선택")
        // 어댑터 연결
        binding.checkcodingquestionSmallCategorySp.adapter = smallCategoryAdapter
        // 스피너 초기값을 마지막 아이템으로 설정
        binding.checkcodingquestionSmallCategorySp.setSelection(smallCategoryAdapter.count)

        // droplist를 스피너와 간격을 두고 나오게 함 -> 아이템 크기 = 125px
        binding.checkcodingquestionSmallCategorySp.dropDownVerticalOffset = dipToPixels(45f).toInt()
    }

    // 스피너 클릭 이벤트 핸들러
    private fun setupSmallCategorySpinnerHandler() {
        binding.checkcodingquestionSmallCategorySp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                binding.checkcodingquestionSmallCategorySp.setBackgroundResource(R.drawable.bg_category_outline)
                if (!binding.checkcodingquestionSmallCategorySp.getItemAtPosition(position).equals("하위 선택")) {
                    // 하위 카테고리 선택하면 배경 변경
                    binding.checkcodingquestionSmallCategorySp.setBackgroundResource(R.drawable.bg_category_selected)
                    // SmallCategory 변수에 하위 카테고리 저장하기
                    smallCategory = binding.checkcodingquestionSmallCategorySp.getItemAtPosition(position).toString()
                    Log.i(ContentValues.TAG, "하위 카테고리 확인: $smallCategory")
                } else {
                    smallCategory = null
                    Log.i(ContentValues.TAG, "하위 카테고리 확인: $smallCategory")
                    // Toast.makeText(context, "상위 카테고리를 선택해주세요!", Toast.LENGTH_SHORT).show()
                }

            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }

}