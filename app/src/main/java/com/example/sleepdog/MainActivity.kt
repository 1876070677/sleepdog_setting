package com.example.sleepdog

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.LogoutResponseCallback
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var TAG: String = "로그"

    //블루투스 권한을 물어볼때 구별할 수 있는 값
    private var REQUEST_CODE_ENABLE_BT: Int = 1

    //블루투스 어댑터
    lateinit var mBluetoothAdapter: BluetoothAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "MainActivity-onCreate() called")

        logout.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("로그아웃 하시겠습니까?")
            builder.setPositiveButton("확인") { dialogInterface, i ->
                UserManagement.getInstance().requestLogout(object : LogoutResponseCallback() {
                    override fun onCompleteLogout() {
                        val intent = Intent(applicationContext, LoginActivity::class.java)
                        startActivity(intent)
                    }
                })
                dialogInterface.dismiss()
            }
            builder.setNegativeButton("취소") { dialogInterface, i ->
                dialogInterface.dismiss()
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()

        }

        //db에 저장되있는 데이터 로드
        loaddata()

        //블루투스 어뎁터를 초기화
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        //블루투스 스위치 구현
        switch_bluetooth.setOnCheckedChangeListener { compoundButton, onSwitch ->
            checkBluetooth()

            if (onSwitch) {
                if (mBluetoothAdapter.isEnabled) {
                } else {
                    val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    startActivityForResult(intent, REQUEST_CODE_ENABLE_BT)
                }
            } else {
                Toast.makeText(this, "블루투스 사용을 권장합니다.", Toast.LENGTH_SHORT).show()
                mBluetoothAdapter.disable()
            }
        }
    }

    fun onSleepConfirmButtonClicked(view: View) {
        val intent = Intent(this, HealthActivity::class.java)
        startActivity(intent)
    }

    fun onModifiedButtonClicked(view: View) {
        val intent2 = Intent(this, SettingActivity::class.java)
        startActivity(intent2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_ENABLE_BT ->
                if (resultCode == Activity.RESULT_OK) {

                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Toast.makeText(this, "블루투스 사용을 권장합니다.", Toast.LENGTH_SHORT).show()
                    switch_bluetooth.setChecked(false)
                }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun checkBluetooth() {
        //블루투스기능을 지원가능한 기기인지 확인
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "블루투스를 지원하지 않는 기기입니다.", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "블루투스를 사용할 수 있습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loaddata() {
        val pref = getSharedPreferences("info", Context.MODE_PRIVATE)

        tv_user.setText(pref.getString("userName", ""))
        tv_dog.setText(pref.getString("dogName", ""))
        tv_gender.setText(pref.getString("dogGender", ""))
        tv_kind.setText(pref.getString("dogKind", ""))
        tv_happy.setText(pref.getString("dogHappy", ""))
        tv_weight.setText(pref.getInt("dogWeight", 0).toString())
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "Resume / 확인용 로그")
        loaddata()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
