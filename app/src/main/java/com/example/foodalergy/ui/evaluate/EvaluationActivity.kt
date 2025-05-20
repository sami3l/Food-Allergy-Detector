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
import com.bumptech.glide.Glide
import com.example.foodalergy.R
import com.example.foodalergy.data.model.ProductInfoResponse
import com.example.foodalergy.data.model.ProductResponse
import com.example.foodalergy.data.network.RetrofitClient
import com.google.zxing.integration.android.IntentIntegrator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EvaluationActivity : AppCompatActivity() {

    private lateinit var btnScan: Button
    private lateinit var textScanResult: TextView
    private lateinit var btnEvaluate: Button
    private lateinit var textEvaluationResult: TextView
    private lateinit var btnDeleteScan: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var imageViewProduct: ImageView

    private lateinit var btnManualEntry: Button
    private lateinit var manualEntryLayout: LinearLayout
    private lateinit var editProductName: EditText
    private lateinit var editBarcode: EditText
    private lateinit var editProductText: EditText

    private var isManualMode = false
    private var lastScanId: String? = null
    private var scannedProduct: ProductResponse? = null
    private var currentProductInfo: ProductInfoResponse? = null

    private val CAMERA_PERMISSION_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_evaluation)
        title = "Évaluation des allergies"

        btnScan = findViewById(R.id.btnScan)
        textScanResult = findViewById(R.id.textScanResult)
        btnEvaluate = findViewById(R.id.btnEvaluate)
        textEvaluationResult = findViewById(R.id.textEvaluationResult)
        btnDeleteScan = findViewById(R.id.btnDeleteScan)
        progressBar = findViewById(R.id.progressBar)
        imageViewProduct = findViewById(R.id.imageViewProduct)

        btnManualEntry = findViewById(R.id.btnManualEntry)
        manualEntryLayout = findViewById(R.id.manualEntryLayout)
        editProductName = findViewById(R.id.editProductName)
        editBarcode = findViewById(R.id.editBarcode)
        editProductText = findViewById(R.id.editProductText)

        checkCameraPermission()

        btnScan.setOnClickListener { startQRScanner() }
        btnEvaluate.setOnClickListener { evaluateRisk() }
        btnDeleteScan.setOnClickListener {
            lastScanId?.let { deleteScan(it) }
            clearScanData()
        }

        btnManualEntry.setOnClickListener {
            isManualMode = !isManualMode
            manualEntryLayout.visibility = if (isManualMode) View.VISIBLE else View.GONE
            btnManualEntry.text =
                if (isManualMode) "Cancel Manual Entry" else getString(R.string.enter_product_manually)
            if (!isManualMode) {
                editProductName.text.clear()
                editBarcode.text.clear()
                editProductText.text.clear()
                currentProductInfo = null
            }
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
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
            val barcode = result.contents.trim().replace("\"", "")
            lastScanId = barcode
            textScanResult.text = "Scan détecté : $barcode"
            getProductFromScan(barcode)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun getProductFromScan(barcode: String) {
        textScanResult.text = "Chargement du produit..."
        imageViewProduct.setImageDrawable(null)
        progressBar.visibility = View.VISIBLE
        btnEvaluate.isEnabled = false

        RetrofitClient.scanApi.getProductInfo(barcode).enqueue(object : Callback<ProductInfoResponse> {
            override fun onResponse(call: Call<ProductInfoResponse>, response: Response<ProductInfoResponse>) {
                btnEvaluate.isEnabled = true
                progressBar.visibility = View.GONE

                if (response.isSuccessful && response.body() != null) {
                    val product = response.body()!!
                    currentProductInfo = product
                    scannedProduct = ProductResponse(
                        id = product.barcode ?: "",
                        name = product.productName,
                        barcode = barcode,
                        description = product.ingredients ?: ""
                    )
                    displayProductInfo(product)
                    btnDeleteScan.visibility = View.VISIBLE
                } else {
                    textScanResult.text = "Produit introuvable"
                }
            }

            override fun onFailure(call: Call<ProductInfoResponse>, t: Throwable) {
                btnEvaluate.isEnabled = true
                progressBar.visibility = View.GONE
                textScanResult.text = "Erreur réseau : ${t.message}"
            }
        })
    }

    private fun displayProductInfo(product: ProductInfoResponse) {
        textScanResult.text = """
            🧾 Produit : ${product.productName ?: "Non spécifié"}
            📄 Ingrédients : ${product.ingredients ?: "Non spécifiés"}
            🚨 Allergènes : ${product.allergens?.joinToString(", ") ?: "Aucun"}
        """.trimIndent()

        if (!product.imageUrl.isNullOrEmpty()) {
            Glide.with(this@EvaluationActivity)
                .load(product.imageUrl)
                .into(imageViewProduct)
        } else {
            imageViewProduct.setImageDrawable(null)
        }
    }

    private fun evaluateRisk() {
        if (!isManualMode && currentProductInfo != null) {
            displayEvaluationResult(currentProductInfo!!)
            return
        }

        val barcode = if (isManualMode) {
            editBarcode.text.toString().trim()
        } else {
            scannedProduct?.barcode ?: ""
        }

        if (barcode.isEmpty()) {
            textEvaluationResult.text = "Code-barres introuvable"
            return
        }

        btnEvaluate.isEnabled = false
        progressBar.visibility = View.VISIBLE
        textEvaluationResult.text = "Chargement des infos produit..."

        RetrofitClient.scanApi.getProductInfo(barcode).enqueue(object : Callback<ProductInfoResponse> {
            override fun onResponse(call: Call<ProductInfoResponse>, response: Response<ProductInfoResponse>) {
                btnEvaluate.isEnabled = true
                progressBar.visibility = View.GONE

                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()!!
                    currentProductInfo = result
                    displayEvaluationResult(result)
                } else {
                    textEvaluationResult.text = "Erreur lors de la récupération"
                }
            }

            override fun onFailure(call: Call<ProductInfoResponse>, t: Throwable) {
                btnEvaluate.isEnabled = true
                progressBar.visibility = View.GONE
                textEvaluationResult.text = "Erreur réseau : ${t.message}"
            }
        })
    }

    private fun displayEvaluationResult(product: ProductInfoResponse) {
        textEvaluationResult.text = """
            🧾 Produit : ${product.productName ?: "Non spécifié"}
            📄 Ingrédients : ${product.ingredients ?: "Non spécifiés"}
            🚨 Allergènes : ${product.allergens?.joinToString(", ") ?: "Aucun"}
        """.trimIndent()

        if (!product.imageUrl.isNullOrEmpty()) {
            Glide.with(this@EvaluationActivity)
                .load(product.imageUrl)
                .into(imageViewProduct)
        }
    }

    private fun deleteScan(scanId: String) {
        RetrofitClient.scanApi.deleteScan(scanId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@EvaluationActivity, "Scan supprimé", Toast.LENGTH_SHORT).show()
                    clearScanData()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@EvaluationActivity, "Erreur : ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun clearScanData() {
        btnDeleteScan.visibility = View.GONE
        textScanResult.text = ""
        textEvaluationResult.text = ""
        scannedProduct = null
        currentProductInfo = null
        lastScanId = null
        imageViewProduct.setImageDrawable(null)
    }
}
