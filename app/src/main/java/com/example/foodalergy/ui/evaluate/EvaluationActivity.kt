package com.example.foodalergy.ui.evaluate

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.example.foodalergy.R
import com.example.foodalergy.data.model.*
import com.example.foodalergy.data.network.RetrofitClient
import com.example.foodalergy.data.store.UserSessionManager
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
    private lateinit var progressBar: ProgressBar
    private lateinit var imageViewProduct: ImageView

    // Manual Entry
    private lateinit var btnManualEntry: Button
    private lateinit var manualEntryLayout: LinearLayout
    private lateinit var editProductName: EditText
    private lateinit var editBarcode: EditText
    private lateinit var editProductText: EditText

    private var isManualMode = false
    private var lastScanId: String? = null
    private var scannedProduct: ProductResponse? = null

    private val CAMERA_PERMISSION_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_evaluation)
        title = "Évaluation des allergies"

        // Init views
        editTextAllergies = findViewById(R.id.editTextAllergies)
        btnScan = findViewById(R.id.btnScan)
        textScanResult = findViewById(R.id.textScanResult)
        btnEvaluate = findViewById(R.id.btnEvaluate)
        textEvaluationResult = findViewById(R.id.textEvaluationResult)
        btnDeleteScan = findViewById(R.id.btnDeleteScan)
        progressBar = findViewById(R.id.progressBar)
        imageViewProduct = findViewById(R.id.imageViewProduct)

        // Manual
        btnManualEntry = findViewById(R.id.btnManualEntry)
        manualEntryLayout = findViewById(R.id.manualEntryLayout)
        editProductName = findViewById(R.id.editProductName)
        editBarcode = findViewById(R.id.editBarcode)
        editProductText = findViewById(R.id.editProductText)

        checkCameraPermission()

        btnScan.setOnClickListener { startQRScanner() }
        btnEvaluate.setOnClickListener { evaluateRisk() }
        btnDeleteScan.setOnClickListener { lastScanId?.let { deleteScan(it) } }

        btnManualEntry.setOnClickListener {
            isManualMode = !isManualMode
            manualEntryLayout.visibility = if (isManualMode) View.VISIBLE else View.GONE
            btnManualEntry.text = if (isManualMode) "Cancel Manual Entry" else getString(R.string.enter_product_manually)
            if (!isManualMode) {
                editProductName.text.clear()
                editBarcode.text.clear()
                editProductText.text.clear()
            }
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
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
        imageViewProduct.setImageDrawable(null)

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
        val userId = UserSessionManager(this).getUserId()
        if (userId.isEmpty()) {
            textEvaluationResult.text = "ID utilisateur introuvable dans la session"
            return
        }

        val request = if (isManualMode) {
            EvaluateRequest(
                productText = editProductText.text.toString(),
                barcode = editBarcode.text.toString(),
                productName = editProductName.text.toString()
            )
        } else {
            EvaluateRequest(
                productText = scannedProduct?.description ?: "",
                barcode = scannedProduct?.barcode ?: "",
                productName = scannedProduct?.name ?: ""
            )
        }

        // UI loading state
        btnEvaluate.isEnabled = false
        progressBar.visibility = View.VISIBLE
        textEvaluationResult.text = "Évaluation en cours..."

        RetrofitClient.scanApi.evaluate(request).enqueue(object : Callback<EvaluateResponse> {
            override fun onResponse(call: Call<EvaluateResponse>, response: Response<EvaluateResponse>) {
                btnEvaluate.isEnabled = true
                progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()
                    textEvaluationResult.text = """
                        🧾 Produit : ${result?.productName}
                        ⚠️ Risque : ${result?.riskLevel}
                        🧪 Source : ${result?.source}
                        🚨 Allergènes détectés : ${result?.allergens?.joinToString(", ") ?: "Aucun"}
                    """.trimIndent()
                } else {
                    textEvaluationResult.text = "Erreur d’évaluation"
                }
            }

            override fun onFailure(call: Call<EvaluateResponse>, t: Throwable) {
                btnEvaluate.isEnabled = true
                progressBar.visibility = View.GONE
                textEvaluationResult.text = "Échec : ${t.message}"
            }
        })
    }

    private fun deleteScan(scanId: String) {
        RetrofitClient.scanApi.deleteScan(scanId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@EvaluationActivity, "Scan supprimé", Toast.LENGTH_SHORT).show()
                    btnDeleteScan.visibility = Button.GONE
                    textScanResult.text = ""
                    scannedProduct = null
                    imageViewProduct.setImageDrawable(null)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@EvaluationActivity, "Erreur : ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
