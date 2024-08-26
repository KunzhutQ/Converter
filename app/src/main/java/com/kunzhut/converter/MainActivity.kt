package com.kunzhut.converter

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.StringReader


class MainActivity : ComponentActivity() {

    private var choice : Int=0
    private var USDRate : Float=0f
    private var EURORate : Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        actionBar!!.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mainlayout)
        val button: Button = findViewById(R.id.button1)
        val edittext: EditText = findViewById(R.id.textedit)
        val textview1: TextView = findViewById(R.id.textview1)
        val spinner: Spinner = findViewById(R.id.spinner1)
        val arradap =
            ArrayAdapter(this, R.layout.selecteditem, resources.getStringArray(R.array.spinnertext))
        arradap.setDropDownViewResource(R.layout.spinnerlayout)
        spinner.adapter = arradap

        val connm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork= connm.activeNetwork

        if(activeNetwork!=null) {
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    choice=position
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            val thread = Thread{

                val client = OkHttpClient()
                    val request1 = Request.Builder().url("https://api.nbrb.by/exrates/rates/431?periodicity=0").build()
                    val request2 = Request.Builder().url("https://api.nbrb.by/exrates/rates/451?periodicity=0").build()

                    val resp1: Response = client.newCall(request1).execute()
                    val jsonobj1 = JSONObject(resp1.body!!.string())
                    USDRate = jsonobj1.get("Cur_OfficialRate").toString().toFloat()
                    resp1.close()


                    val resp2: Response = client.newCall(request2).execute()
                    val jsonobj2 = JSONObject(resp2.body!!.string())
                    EURORate = jsonobj2.get("Cur_OfficialRate").toString().toFloat()
                    resp2.close()
            }
            thread.start()

            button.setOnClickListener {
                try {
                    val getMoney = edittext.text.toString().toFloat()

               textview1.text = when (choice) {
                    0 -> getString(R.string.USD, (getMoney / USDRate).toString())
                    1 -> getString(R.string.BYN, (getMoney * USDRate).toString())
                    2 -> getString(R.string.EURO, (getMoney / EURORate).toString())
                    3 -> getString(R.string.BYN, (getMoney * EURORate).toString())
                    4 -> getString(R.string.EURO, (getMoney * USDRate / EURORate).toString())
                    5 -> getString(R.string.USD, (getMoney * EURORate / USDRate).toString())
                    else -> ""
                }

                }catch (a : Exception){
                    Toast.makeText(this, "Введите значение", Toast.LENGTH_LONG).show()
                }
            }
        }else{
            Toast.makeText(this, "Для работы приложения требуется\n " +
                    "интернет-соединение", Toast.LENGTH_LONG).show()
        }

    }

    override fun attachBaseContext(newBase: Context?) {
        val conf = Configuration(newBase!!.resources.configuration)
        conf.fontScale=1.0f
        applyOverrideConfiguration(conf)
        super.attachBaseContext(newBase)
    }

}