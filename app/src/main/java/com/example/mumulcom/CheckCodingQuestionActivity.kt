package com.example.mumulcom

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.viewpager2.widget.ViewPager2
import com.example.mumulcom.databinding.ActivityCheckcodingquestionBinding
import com.example.test.ViewPagerAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.util.*


//, CheckCodingQuestionView
class CheckCodingQuestionActivity:AppCompatActivity(), CheckCodingQuestionView {

    lateinit var binding: ActivityCheckcodingquestionBinding

    private var images = arrayListOf<String>()
    var photoList = arrayListOf<Photo>()
    private var jwt: String = ""
    private var userIdx: Long = 0
    private lateinit var title: String
    private lateinit var currentError: String
    private lateinit var myCodingSkill: String
    private lateinit var codeQuestionUrl: String
    private var bigCategoryIdx: Long = 0
    private var smallCategoryIdx: Long? = null

    private var bigCategory: String? = null    // 선택한 상위 카테고리
    private var smallCategory: String? = null  // 선택한 하위 카테고리

    //뷰페이저+파이어스토리지
    lateinit var viewPagerAdapter: ViewPagerAdapter
    lateinit var storage: FirebaseStorage
    lateinit var firestore: FirebaseFirestore//파이어스토리지
    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>//이동(카메라 앨범)
    var count=0//이미지 수
    var selectImage:Uri?=null
    val CAMERA: Int = 100
    val GALLERY: Int = 101

    // 스피너 어댑터
    private lateinit var bigCategoryAdapter: ArrayAdapter<String>
    private lateinit var smallCategoryAdapter: ArrayAdapter<String>

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCheckcodingquestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        jwt = getJwt(this)
        userIdx = getUserIdx(this)

        storage = FirebaseStorage.getInstance()
        firestore = FirebaseFirestore.getInstance()
        // 카테고리 초기화
        setupBigCategorySpinner()
        setupBigCategorySpinnerHandler()


        // 화면 배경 누르면 키보드 사라지기
        binding.codingBack.setOnClickListener {
            CloseKeyboard()
        }

        //startActivityresult대신
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            }

        //편집버튼 누르면 이미지 편집
//        binding.checkcodingquestionEditIv.setOnClickListener {
//            setResult(RESULT_OK, intent)
//            finish()
//        }

//5개 이하일때만 추가가능
        if (count<5){
            //추가버튼
            binding.checkcodingquestionPlusIv.setOnClickListener {
                val intent =
                    Intent(this, CodingCameraShootingActivity::class.java)
                activityResultLauncher.launch(intent)
//                finish()
            }
        }


        //질문하기등록 및 데이터 삭제
        binding.checkcodingquestionQuestionIv.setOnClickListener {
            checkcodingif()
        }

        //뒤로가기 버튼
        binding.checkcodingquestionBackIv.setOnClickListener {
            startActivity(Intent(this, QuestionCategoryActivity::class.java))
            finish()
        }

    }


    private fun getCoding(): CheckCoding {  // view에서 받은 값들

        title=binding.checkcodingquestionTitleTextEt.text.toString()
        currentError=binding.checkcodingquestionStopPartTextEt.text.toString()
        myCodingSkill=binding.checkcodingquestionCodingLevelTextEt.text.toString()
        codeQuestionUrl=binding.checkcodingquestionErrorCodeTextEt.text.toString()
        bigCategoryIdx=binding.checkcodingquestionBigCategorySp.selectedItemPosition.toLong()+1



        Log.d("images", images.toString())
        Log.d("userIdx : ", userIdx.toString())
        Log.d("title : ", title)
        Log.d("currentError : ", currentError)
        Log.d("myCodingSkill : ", myCodingSkill)
        Log.d("codeQuestionUrl : ", codeQuestionUrl)
        Log.d("bigCategoryIdx : ", bigCategoryIdx.toString())
        Log.d("smallCategoryIdx :", smallCategoryIdx.toString())
        return CheckCoding(images, userIdx, currentError, myCodingSkill, bigCategoryIdx, smallCategoryIdx, title, codeQuestionUrl)
    }


    //api서버
    private fun checkCodingQuestion() {

        val checkCodingQuestionService=CheckCodingQuestionService()

        checkCodingQuestionService.setcheckcodingquestionView(this)
//원래는 getJwt(this)
        checkCodingQuestionService.checkCodingQuestion(getJwt(this), getCoding())
        Log.d("CHECKCODING/API","Hello")


    }

    private fun checkcodingif(){
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

        binding.checkcodingquestionQuestionIv.setImageResource(R.drawable.ic_click_question)

        //승인 버튼 눌러야 api전송
        val builder = AlertDialog.Builder(this).create()
        val dialogView = layoutInflater.inflate(R.layout.dialog_question, null)

        builder?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        builder?.setCancelable(false)
        builder?.setCanceledOnTouchOutside(false)

        val approve = dialogView.findViewById<Button>(R.id.dialog_approve_btn)
        approve.setOnClickListener {
            checkCodingQuestion()
            builder.dismiss()
        }

        val cancle = dialogView.findViewById<Button>(R.id.dialog_cancel_btn)
        cancle.setOnClickListener {
            builder.dismiss()
        }

        builder.setView(dialogView)
        builder.show()


    }

    override fun onCheckCodingQuestionLoading() {
        Toast.makeText(this, "잠시만 기다려주세요", Toast.LENGTH_SHORT).show()
    }

    override fun onCheckCodingQuestionFailure(code: Int, message: String) {
        Toast.makeText(this, "질문 올리기 실패", Toast.LENGTH_SHORT).show()
    }

    override fun onCheckCodingQuestionSuccess(result: String) {
        Toast.makeText(this, "질문 올리기 성공", Toast.LENGTH_SHORT).show()
        finish()
    }

    // 키보드 사라지는 함수
    fun CloseKeyboard() {
        var view = this.currentFocus

        if(view != null) {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    //카메라 앨범 이미지 가져오기
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            var imagePath = data?.getStringExtra("path")!!

            photoList.apply {
                add(Photo(imagePath))
                Log.d("SEND/path", imagePath)
                count++
                Log.d("path/count", count.toString())
            }
            Log.d("GETGET", photoList.toString())

            //편집되는 부분은 아직
//            if (imagePath.length>0){
//                binding.checkcodingquestionPlusIv.visibility=View.INVISIBLE
//                binding.checkcodingquestionEditIv.visibility=View.VISIBLE
//            }


            //리스트에 추가
//            firestore.collection("coding-images").addSnapshotListener {
//                    querySnapshot, FirebaseFIrestoreException ->
//                if(querySnapshot!=null){
//                    for(dc in querySnapshot.documentChanges){
//                        if(dc.type== DocumentChange.Type.ADDED){
//                            var photo = dc.document.toObject(Photo::class.java)
////                            photoList.add(photo)//url추가(이미지)
//                            images.add(imagePath)
//                            count++
////                            binding.checkcodingIndicator.createIndicators(count, 0)
//                        }
//                    }
//                    viewPagerAdapter.notifyDataSetChanged()
//                }
//            }
//            Log.d("gege/images", images.toString())
//            Log.d("gege/photolist", photoList.toString())

            if (resultCode == CAMERA){
                var imageFile: File? = null
                //set되는 부분
                if (imageFile != null) {
                    var fileName =
                        SimpleDateFormat("yyyyMMddHHmmss").format(Date()) // 파일명이 겹치면 안되기 떄문에 시년월일분초 지정
                    storage.getReference().child("image").child(fileName)
                        .putFile(imageFile.toUri())//어디에 업로드할지 지정
                        .addOnSuccessListener { taskSnapshot -> // 업로드 정보를 담는다
                            taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { it ->
                                var imageUrl = it.toString()
                                var photo = Photo(imageUrl)
                                firestore.collection("coding-images")
                                    .document().set(photo)
                                    .addOnSuccessListener {
                                    }
                                Log.d("gege/imageUrl", imageUrl)
                                Log.d("gege/photo", photo.toString())
                                images.add(imageUrl)
                            }
                        }

                }
            }


                //set되는 부분
                if (imagePath != null) {
                    var fileName =
                        SimpleDateFormat("yyyyMMddHHmmss").format(Date()) // 파일명이 겹치면 안되기 떄문에 시년월일분초 지정
                    storage.getReference().child("image").child(fileName).putFile(imagePath.toUri())
                        //어디에 업로드할지 지정
                        .addOnSuccessListener { taskSnapshot -> // 업로드 정보를 담는다
                            taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { it ->
                                var imageUrl = it.toString()
                                var photo = Photo(imageUrl)
                                firestore.collection("coding-images")
                                    .document().set(photo)
                                    .addOnSuccessListener {
                                    }
                                Log.d("gege/imageUrl", imageUrl)
                                Log.d("gege/photo", photo.toString())
                                images.add(imageUrl)

                            }
                        }
                }

            //이미지가 5개부터는 추가 불
            if (count>=5){
                //추가버튼
                binding.checkcodingquestionPlusIv.setOnClickListener {
                    Toast.makeText(this, "이미지는 최대 5개까지 넣을 수 있습니다", Toast.LENGTH_SHORT).show()
                }
            }

            // 뷰페이저 어댑터 생성
            viewPagerAdapter = ViewPagerAdapter(this, photoList)
            binding.checkcodingquestionVp.adapter = viewPagerAdapter
            binding.checkcodingquestionVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            binding.checkcodingIndicator.setViewPager(binding.checkcodingquestionVp)
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
                    smallCategoryIdx = null
                    if (bigCategory=="앱") {
                        smallCategoryIdx =
                            binding.checkcodingquestionSmallCategorySp.selectedItemPosition.toLong() + 1
                        Log.d(ContentValues.TAG, "하위 카테고리 넘버 확인: $smallCategoryIdx")

                    }
                    smallCategoryIdx = null
                    if (bigCategory=="웹") {
                        smallCategoryIdx =
                            binding.checkcodingquestionSmallCategorySp.selectedItemPosition.toLong() + 3
                        Log.d(ContentValues.TAG, "하위 카테고리 넘버 확인: $smallCategoryIdx")
                    }
                    smallCategoryIdx = null
                    if (bigCategory=="서버") {
                        smallCategoryIdx =
                            binding.checkcodingquestionSmallCategorySp.selectedItemPosition.toLong() + 6
                        Log.d(ContentValues.TAG, "하위 카테고리 넘버 확인: $smallCategoryIdx")
                    }
                    smallCategoryIdx = null
                    if (bigCategory=="프로그래밍 언어") {
                        smallCategoryIdx =
                            binding.checkcodingquestionSmallCategorySp.selectedItemPosition.toLong() + 8
                        Log.d(ContentValues.TAG, "하위 카테고리 넘버 확인: $smallCategoryIdx")
                    }
                    else{//기타, 클릭
                        smallCategoryIdx = null
                        if (bigCategory=="앱") {
                            smallCategoryIdx =
                                binding.checkcodingquestionSmallCategorySp.selectedItemPosition.toLong() + 1
                            Log.d(ContentValues.TAG, "하위 카테고리 넘버 확인: $smallCategoryIdx")
                        }
                        smallCategoryIdx = null
                        if (bigCategory=="웹") {
                            smallCategoryIdx =
                                binding.checkcodingquestionSmallCategorySp.selectedItemPosition.toLong() + 3
                            Log.d(ContentValues.TAG, "하위 카테고리 넘버 확인: $smallCategoryIdx")
                        }
                        smallCategoryIdx = null
                        if (bigCategory=="서버") {
                            smallCategoryIdx =
                                binding.checkcodingquestionSmallCategorySp.selectedItemPosition.toLong() + 6
                            Log.d(ContentValues.TAG, "하위 카테고리 넘버 확인: $smallCategoryIdx")
                        }
                        smallCategoryIdx = null
                        if (bigCategory=="프로그래밍 언어") {
                            smallCategoryIdx =
                                binding.checkcodingquestionSmallCategorySp.selectedItemPosition.toLong() + 8
                            Log.d(ContentValues.TAG, "하위 카테고리 넘버 확인: $smallCategoryIdx")
                        }
                    }
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

