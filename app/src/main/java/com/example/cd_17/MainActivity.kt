package com.example.cd_17

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultContracts
import androidx.activity.result.contracts.ActivityResultContracts.GetContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.lifecycleScope
import com.example.cd_17.ui.theme.CD17Theme
import org.pytorch.Tensor
import org.pytorch.torchvision.TensorImageUtils
import java.io.ByteArrayOutputStream

class MainActivity : ComponentActivity() {
    private lateinit var objectDetector: ObjectDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        objectDetector = ObjectDetector(this)

        // Set up activity result for selecting an image
        val getContent = registerForActivityResult(GetContent()) { uri ->
            uri?.let {
                val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(it))
                detectObjects(bitmap)
            }
        }

        setContent {
            CD17Theme {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Button(onClick = { getContent.launch("image/*") }) {
                        Text(text = "Select Image")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    BasicText(text = "Object Detection Results")

                    // Display the image preview
                    val bitmap = // You will need a bitmap to display here
                        bitmap?.let {
                            Image(bitmap = it.asImageBitmap(), contentDescription = "Selected Image")
                        }
                }
            }
        }
    }

    private fun detectObjects(bitmap: Bitmap) {
        lifecycleScope.launchWhenCreated {
            val outputTensor = objectDetector.runModel(bitmap)
            val detectedObjects = outputTensor.dataAsFloatArray.joinToString()
            Log.d("ObjectDetection", "Detected Objects: $detectedObjects")
            Toast.makeText(this@MainActivity, "Objects Detected: $detectedObjects", Toast.LENGTH_LONG).show()
        }
    }
}
