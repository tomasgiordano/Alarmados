package com.example.alarmados

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.camera2.CameraManager
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Vibrator
import android.view.View
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*

var flag = true
var flagMovimiento = 0

@Suppress("DEPRECATION")
class HomeActivity : AppCompatActivity(), SensorEventListener {

    lateinit var sensor: Sensor
    private var sensorManager: SensorManager? = null
    lateinit var vibrator: Vibrator
    lateinit var flash : CameraManager
    lateinit var cameraId : String
    var counter = 0

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //Setup

        val bundle = intent.extras
        val email = bundle?.getString("email")
        val password = bundle?.getString("password")
        setup(email ?: "", password ?: "")
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setup(email: String, password: String) {
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        flash = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraId = flash.getCameraIdList()[0]

        emailTextView.text = email
        logOutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            stop()
        }

        imagenPowerOn.setOnClickListener {
            if (flag) {
                sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
                sensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
                buttonDesactivar.visibility = View.VISIBLE
                editTextPassword.visibility = View.VISIBLE
                logOutButton.visibility = View.INVISIBLE
                imagenPowerOn.setImageResource(R.drawable.powerrojo)
                estadoAlarma2.text = "Introduzca su contraseña para desactivarla."
                estadoAlarma.text = "¡ALARMA ACTIVADA!"
                flag = !flag
                start()
            }
        }

        buttonDesactivar.setOnClickListener {
            if (editTextPassword.text.toString().equals(password)) {
                buttonDesactivar.visibility = View.INVISIBLE
                logOutButton.visibility = View.VISIBLE
                editTextPassword.visibility = View.INVISIBLE
                editTextPassword.setText("")
                imagenPowerOn.setImageResource(R.drawable.powernegro)
                estadoAlarma2.text = "Presione el boton para activar la alarma contra robo."
                estadoAlarma.text = "ALARMA DESACTIVADA"
                flash.setTorchMode(cameraId,false)
                flag = !flag
                stop()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getAccelerometer(event: SensorEvent) {
        // Movement
        val xVal = event.values[0]
        val yVal = event.values[1]
        val zVal = event.values[2]

        val accelerationSquareRoot = (xVal * xVal + yVal * yVal + zVal * zVal) / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH)
        if (event != null) {
            if (xVal<-5 && xVal>-7 && flagMovimiento!=0) {
                sound1()
                flagMovimiento=0
            } else if (xVal>5 && xVal<7 && flagMovimiento!=1) {
                sound2()
                flagMovimiento=1
            }
            else if(yVal>8 && xVal>0 && xVal<1 && yVal<9 && flagMovimiento!=2)
            {
                sound3()
                flash.setTorchMode(cameraId,true)
                fun startTimeCounter(view: View) {
                    object : CountDownTimer(5000,1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            counter++
                        }
                        override fun onFinish() {
                            flash.setTorchMode(cameraId,false)
                        }
                    }.start()
                }
                flagMovimiento=2
            }
            else if(xVal>8 && xVal<10 && yVal>0 && yVal<1 && flagMovimiento!=3 && flagMovimiento!=0)
            {
                sound4()
                vibrator.vibrate(5000)
                flagMovimiento=3
            }
            if (flagMovimiento == 3) {
                flagMovimiento = 0
            }
        }
    }

    override fun onAccuracyChanged(s: Sensor?, i: Int) {
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onSensorChanged(event: SensorEvent?) {
        if (event!!.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event)
        }
    }


    private fun sound1() {
        val mp1 = MediaPlayer.create(this, R.raw.audioalerta1)
        mp1.start()
    }

    private fun sound2() {
        val mp2 = MediaPlayer.create(this, R.raw.audioalerta2)
        mp2.start()
    }

    private fun sound3(){
        val mp3 = MediaPlayer.create(this, R.raw.audioalerta3)
            mp3.start()
    }

    private fun sound4(){
        val mp4 = MediaPlayer.create(this, R.raw.audioalerta4)
        mp4.start()
    }

    fun start() {
        if(sensorManager!=null&&sensor!=null) {
            sensorManager!!.registerListener(
                this, sensor,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        }
    }

    fun stop() {
        if(sensorManager!=null&&sensor!=null){
        sensorManager!!.unregisterListener(this, sensor)
        }
    }

    override fun onPause() {
        super.onPause()
        if(sensorManager!=null&&sensor!=null) {
            sensorManager!!.unregisterListener(this)
        }
    }

    override fun onResume() {
        super.onResume()
        if(sensorManager!=null&&sensor!=null) {
            sensorManager!!.registerListener(
                this,
                sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }
}