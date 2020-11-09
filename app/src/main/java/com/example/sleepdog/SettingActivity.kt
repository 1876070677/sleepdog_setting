package com.example.sleepdog

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.sleepdog.Retrofit.RetrofitManager
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {
    val TAG : String = "MainActivity"
    private val RESQUE_CODE = 0; //갤러리에 진입할때 정상 진입을 알려주기 위한 임의로 정한 변수

    //모든 변수 선언하면서 공백으로 초기화
    var username : String = ""
    var dogname : String = ""
    var happy : String =""
    var kind : String = ""
    var gender : String = ""
    var weight : Int = 0
    var realPath : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        val items = resources.getStringArray(R.array.breeds) //견종 어레이를 불러온다.
        //어레이 어뎁터를 이용해서 스피너와 연결
        val kindAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        dog_kind.setAdapter(kindAdapter)

        //이미지 뷰 클릭시 갤러리를 열기 전에 권한 요청을 먼저 한다
        profile.setOnClickListener {
            checkPerGalery()
        }

        //성별 버튼 클릭시 Gender변수에 성별 값 저장
        btn_female.setOnClickListener {
            btn_female.setSelected(true)
            btn_male.setSelected(false)
            gender = "G"
        }
        btn_male.setOnClickListener {
            btn_male.setSelected(true)
            btn_female.setSelected(false)
            gender = "M"
        }

        dog_kind.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                when(p2) {
                    0-> {kind = ""}//견종 어레이에서 디폴트값 선택되어있는경우 아무것도 하지 않는다

                    else ->{ //그외경우에 어떤게 선택되어졌는지 String으로 불러와서 Kind에 저장
                        kind = dog_kind.getSelectedItem().toString()
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        btn_save.setOnClickListener {

            //trim을 이용해서 좌우공백이 있을시 제거해준다
            dogname = dog_name.text.toString().trim()
            username = user.text.toString().trim()

            //입력해야 할 부분이 하나라도 비었을 경우
            if (username.equals("") || dogname.equals("") || et_year.text.toString().trim()
                    .equals("") || et_month.text.toString().trim()
                    .equals("") || et_date.text.toString().trim()
                    .equals("") || kind.equals("") || gender == null || dog_weight.text.toString()
                    .trim().equals("")
            ) {

                //그중에서도 년도, 월, 일 부분은 일정 글자수가 채워지지 않으면 형식에 맞지 않으므로
                //저장되지 않게 한다
                if (et_year.text.toString().trim().length < 4 || et_month.text.toString()
                        .trim().length < 2 || et_date.text.toString().trim().length < 2
                ) {
                    Toast.makeText(this, "날짜형식이 맞지 않습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    //그 외 경우에는 입력하지 않은 부분이 있다고 출력
                    Toast.makeText(this, "입력하지 않은 부분이 있습니다.", Toast.LENGTH_SHORT).show()
                }
            } else {
                //날짜 형식이 안맞을 경우 Null이 되면서 api에 아무것도 들어가지 않는다.예를들어 월은 1월에서 12월 밖에 없는데 month에 14가 들어가있으면
                //api가 모든값을 null로 반환해버린다
                happy = et_year.text.toString() + "-" + et_month.text.toString() + "-" + et_date.text.toString()
                //edittext에 받은걸 Int형으로 바꿔준다
                weight = Integer.parseInt(dog_weight.text.toString())

                //레트로핏 매니저의 postToDo에 바로 접근
                RetrofitManager.instance.postToDo(dogname, happy, kind, gender, weight)
                Log.d(TAG, "finish post method and return")
                //api에 포스트 이후 sharedpreference에 저장
                saveData()
                finish()
            }
        }
    }
    fun openGallery() {
        val gallery : Intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, RESQUE_CODE)
    }

    //tedPermission
    //PermissionListener 형식을 상속받는 객체 생성
    var permissioncheckGalery : PermissionListener = object : PermissionListener {
        //권한허가시 실행할 내용
        override fun onPermissionGranted() {
            openGallery()
        }

        //권한 거부시 실행 할 내용
        override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
            Toast.makeText(applicationContext, "권한이 거부되었습니다\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    //이 함수가 호출될 때 특정 권한을 묻는다.(권한은 여러개 지정가능)
    fun checkPerGalery() {
        //마미멜로(안드로이드 6.0) 이상일 때 권한을 묻는다, 그 이하는 물어보지 않아도 된다
        if(Build.VERSION.SDK_INT >= 23) {
            TedPermission.with(this)
                .setPermissionListener(permissioncheckGalery)
                .setRationaleMessage("이미지를 다루기 위해서는 접근권한이 필요합니다.")
                .setDeniedMessage("앱에서 요구하는 권한설정이 필요합니다...\n [설정]>[권한]에서 사용으로 활성화해주세요")
                .setPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                .check()
        }
        //권한을 안물어도 되는 경우 실행할 함수
        else {
            openGallery()
        }
    }

    //갤러리에 진입하고 이미지의 Uri값을 받아서 이미지뷰에 넣어준다
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == RESQUE_CODE) {
            var currentUri: Uri? = data?.data
            profile.setImageURI(currentUri)
            //null 체크
            currentUri?.let {
                it
            }?: return

            realPath = getRealPathFromURI(currentUri)
            //이미지를 최종 로드하면 플러스모양 추가버튼을 안보이게 설정
            btn_add.visibility = View.INVISIBLE
        }
    }

    private fun saveData() {
        //저장 or 불러오기 할때 키값이 pref, MODE_PRIVATE는 앱 안에서는 공유되는 정보임을 명시
        val pref = getSharedPreferences("info", Context.MODE_PRIVATE)
        val edit = pref.edit() //수정모드
        //1번째 인자가 key값, 두번째가 저장되는 값
        edit.putString("realPath", realPath)
        edit.putString("userName", username)
        edit.putString("dogName", dogname)
        edit.putString("dogHappy", happy)
        edit.putString("dogKind", kind)
        edit.putString("dogGender", gender)
        edit.putInt("dogWeight", weight)
        edit.apply()

        /*
        String 데이터 불러오기 할경우
        val pref = getSharedPreference("info", MODE_PRIVATE)

        키값이 없을 경우 2번째 인자에 있는 "" 으로 대신한다는 의미
        String temp = pref.getString( key, "")
         */
    }

    //uri를 이용해서 절대경로 구하기
    fun getRealPathFromURI(contentUri : Uri) : String {
        var proj : Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        var c : Cursor? = contentResolver.query(contentUri, proj, null, null, null)

        c?.let {
            it
        }?: return ""
        var index = c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        c?.moveToFirst()

        var result = c.getString(index)

        return result
    }

    //뒤로가기 버튼 눌렀을 때 이루어지는 효과
    override fun onBackPressed() {
        Toast.makeText(applicationContext, "뒤로가기", Toast.LENGTH_SHORT).show()
        super.finish()
    }
}