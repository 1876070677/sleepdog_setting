package com.example.sleepdog


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kakao.auth.ApiErrorCode
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.LogoutResponseCallback
import com.kakao.usermgmt.callback.UnLinkResponseCallback
import com.kakao.util.exception.KakaoException
import kotlinx.android.synthetic.main.activity_login.*
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response


class LoginActivity : AppCompatActivity() {
    private var sessionCallback: SessionCallback? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.sleepdog.R.layout.activity_login)
        sessionCallback = SessionCallback()
        Session.getCurrentSession().addCallback(sessionCallback)
        Session.getCurrentSession().checkAndImplicitOpen()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data)
            return
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Session.getCurrentSession().removeCallback(sessionCallback)
    }

    private inner class SessionCallback : ISessionCallback {
        override fun onSessionOpened() {
            UserManagement.getInstance().me(object : MeV2ResponseCallback() {
                override fun onFailure(errorResult: ErrorResult) {
                    val result = errorResult.errorCode
                    if (result == ApiErrorCode.CLIENT_ERROR_CODE) {
                        Toast.makeText(
                            applicationContext,
                            "네트워크 연결이 불안정합니다. 다시 시도해 주세요.",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "로그인 도중 오류가 발생했습니다: " + errorResult.errorMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onSessionClosed(errorResult: ErrorResult) {
                    Toast.makeText(
                        applicationContext,
                        "세션이 닫혔습니다. 다시 시도해 주세요: " + errorResult.errorMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onSuccess(result: MeV2Response) {
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    Log.i("Log", "아이디 : ${result!!.id}")
                    Toast.makeText(applicationContext, "로그인 성공", Toast.LENGTH_SHORT).show()
                    //intent.putExtra("name", result.nickname)
                    //intent.putExtra("profile", result.profileImagePath)
                    startActivity(intent)
                    finish()
                }
            })
        }

        override fun onSessionOpenFailed(e: KakaoException) {
            Toast.makeText(
                applicationContext,
                "로그인 도중 오류가 발생했습니다. 인터넷 연결을 확인해주세요: $e",
                Toast.LENGTH_SHORT
            ).show()
        }



//class LoginActivity : AppCompatActivity() {
//
//    private var callback: SessionCallback = SessionCallback()
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_login)
//        Session.getCurrentSession().addCallback(callback);
//    }
//    @SuppressLint("MissingSuperCall")
//    override fun onDestroy() {
//        Session.getCurrentSession().removeCallback(callback);
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
//            Log.i("Log", "session get current session")
//            return
//        }
//        super.onActivityResult(requestCode, resultCode, data)
//>>>>>>> 1c9bed2f06305724bce4ecac2f43d2e284bf21e2
    }



}