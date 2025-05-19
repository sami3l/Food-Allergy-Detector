package com.example.foodalergy.ui.evaluate

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.foodalergy.R
import com.example.foodalergy.data.model.*
import com.example.foodalergy.data.network.RetrofitClient
import com.google.zxing.integration.android.IntentIntegrator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EvaluationActivity : AppCompatActivity() {

    private lateinit var editTextAllergies: EditText
    private lateinit var btnScan: Button
    private lateinit var textScanResult: TextView
    private lateinit var btnEvaluate: Button
    private lateinit var textEvaluationResult: TextView
    private lateinit var btnDeleteScan: Button

    private var lastScanId: String? = null
    private var scannedProduct: ProductResponse? = null

    private val CAMERA_PERMISSION_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_evaluation)
        title = "Évaluation des allergies"

        editTextAllergies = findViewById(R.id.editTextAllergies)
        btnScan = findViewById(R.id.btnScan)
        textScanResult = findViewById(R.id.textScanResult)
        btnEvaluate = findViewById(R.id.btnEvaluate)
        textEvaluationResult = findViewById(R.id.textEvaluationResult)
        btnDeleteScan = findViewById(R.id.btnDeleteScan)

        checkCameraPermission()

        btnScan.setOnClickListener { startQRScanner() }

        btnEvaluate.setOnClickListener { evaluateRisk() }

        btnDeleteScan.setOnClickListener {
            lastScanId?.let { deleteScan(it) }
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        }
    }

    private fun startQRScanner() {
        val integrator = IntentIntegrator(this)
        integrator.setPrompt("Scanner un QR code de produit")
        integrator.setOrientationLocked(false)
        integrator.setBeepEnabled(true)
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null && result.contents != null) {
            val scanId = result.contents
            lastScanId = scanId
            getProductFromScan(scanId)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun getProductFromScan(scanId: String) {
        textScanResult.text = "Chargement du produit..."

        RetrofitClient.scanApi.getScan(scanId).enqueue(object : Callback<ProductResponse> {
            override fun onResponse(call: Call<ProductResponse>, response: Response<ProductResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    scannedProduct = response.body()
                    textScanResult.text = "Produit : ${scannedProduct?.name ?: "Inconnu"}"
                    btnDeleteScan.visibility = Button.VISIBLE
                } else {
                    textScanResult.text = "Produit introuvable"
                }
            }

            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                textScanResult.text = "Erreur : ${t.message}"
            }
        })
    }

    private fun evaluateRisk() {
        val username = getUsernameFromPreferences()

        if (username.isEmpty()) {
            textEvaluationResult.text = "Nom d'utilisateur non trouvé"
            return
        }

        fetchUserId(username) { userId ->
            if (userId == null) {
                textEvaluationResult.text = "Impossible de récupérer l'ID utilisateur"
                return@fetchUserId
            }

            val request = EvaluateRequest(

                productText = scannedProduct?.description ?: "",
                barcode = scannedProduct?.barcode ?: "",
                productName = scannedProduct?.name ?: ""
            )

            RetrofitClient.scanApi.evaluate(request).enqueue(object : Callback<EvaluateResponse> {
                override fun onResponse(call: Call<EvaluateResponse>, response: Response<EvaluateResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val result = response.body()
                        textEvaluationResult.text =
                            "Statut : ${result?.status}\nRisque : ${result?.level}\nDétails : ${result?.details}"
                    } else {
                        textEvaluationResult.text = "Erreur d’évaluation"
                    }
                }

                override fun onFailure(call: Call<EvaluateResponse>, t: Throwable) {
                    textEvaluationResult.text = "Échec : ${t.message}"
                }
            })
        }
    }

    private fun fetchUserId(username: String, callback: (String?) -> Unit) {
        RetrofitClient.authApi.getUserByUsername(username)
            .enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful && response.body() != null) {
                        callback(response.body()!!.id)
                    } else {
                        callback(null)
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    callback(null)
                }
            })
    }

    private fun getUsernameFromPreferences(): String {
        val prefs: SharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)
        return prefs.getString("username", "") ?: ""
    }

    private fun deleteScan(scanId: String) {
        RetrofitClient.scanApi.deleteScan(scanId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@EvaluationActivity, "Scan supprimé", Toast.LENGTH_SHORT).show()
                    btnDeleteScan.visibility = Button.GONE
                    textScanResult.text = ""
                    scannedProduct = null
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@EvaluationActivity, "Erreur : ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
