package com.examensnationaux.maroc

import android.os.Bundle
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { BacApp() }
    }
}

data class Exam(
    val subject: String,
    val year: String,
    val session: String,
    val examUrl: String,
    val correctionUrl: String
)

@Composable
fun BacApp() {
    var selectedUrl by remember { mutableStateOf<String?>(null) }
    var title by remember { mutableStateOf("") }

    val exam = Exam(
        subject = "الرياضيات",
        year = "2024",
        session = "الدورة العادية",
        examUrl = "https://levelspace.ma/exams/2bac/mathematiques/national-2024-normale-sciences-sujet.pdf",
        correctionUrl = "https://levelspace.ma/exams/2bac/mathematiques/national-2024-normale-sciences-corrige.pdf"
    )

    MaterialTheme {
        if (selectedUrl != null) {
            PdfReader(
                url = selectedUrl!!,
                title = title,
                onBack = { selectedUrl = null }
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Text(
                    "🇲🇦 امتحانات وطنية",
                    style = MaterialTheme.typography.headlineMedium
                )

                Text("مع التصحيح جميع الشعب")

                Spacer(Modifier.height(25.dp))

                Text(
                    "الامتحانات الوطنية",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(Modifier.height(15.dp))

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp)
                    ) {
                        Text(
                            exam.subject,
                            style = MaterialTheme.typography.titleLarge
                        )

                        Text("الثانية باكالوريا • ${exam.year}")
                        Text(exam.session)

                        Spacer(Modifier.height(15.dp))

                        Button(
                            onClick = {
                                title = "امتحان الرياضيات 2024"
                                selectedUrl = exam.examUrl
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("📘 فتح الامتحان")
                        }

                        OutlinedButton(
                            onClick = {
                                title = "تصحيح الرياضيات 2024"
                                selectedUrl = exam.correctionUrl
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("✅ فتح التصحيح")
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                Text("المحتوى الأولي للتجربة")
                Text("سنضيف باقي الشعب والمواد والسنوات تدريجياً.")
            }
        }
    }
}

data class RenderResult(
    val bitmap: Bitmap,
    val count: Int
)

@Composable
fun PdfReader(
    url: String,
    title: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    var bitmap by remember(url) { mutableStateOf<Bitmap?>(null) }
    var page by remember(url) { mutableIntStateOf(0) }
    var total by remember(url) { mutableIntStateOf(0) }
    var loading by remember(url) { mutableStateOf(true) }
    var error by remember(url) { mutableStateOf("") }

    LaunchedEffect(url, page) {
        loading = true
        error = ""

        try {
            val result = withContext(Dispatchers.IO) {
                val file = File(
                    context.cacheDir,
                    "exam_${url.hashCode()}.pdf"
                )

                if (!file.exists() || file.length() == 0L) {
                    val connection =
                        URL(url).openConnection() as HttpURLConnection

                    connection.connectTimeout = 20000
                    connection.readTimeout = 30000
                    connection.instanceFollowRedirects = true

                    try {
                        connection.inputStream.use { input ->
                            file.outputStream().use { output ->
                                input.copyTo(output)
                            }
                        }
                    } finally {
                        connection.disconnect()
                    }
                }

                val descriptor = ParcelFileDescriptor.open(
                    file,
                    ParcelFileDescriptor.MODE_READ_ONLY
                )

                try {
                    val renderer = PdfRenderer(descriptor)

                    try {
                        val count = renderer.pageCount
                        val index = page.coerceIn(0, count - 1)
                        val pdfPage = renderer.openPage(index)

                        try {
                            val image = Bitmap.createBitmap(
                                pdfPage.width * 2,
                                pdfPage.height * 2,
                                Bitmap.Config.ARGB_8888
                            )

                            pdfPage.render(
                                image,
                                null,
                                null,
                                PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                            )

                            RenderResult(image, count)
                        } finally {
                            pdfPage.close()
                        }
                    } finally {
                        renderer.close()
                    }
                } finally {
                    descriptor.close()
                }
            }

            bitmap = result.bitmap
            total = result.count

        } catch (e: Exception) {
            error = "تعذر فتح الملف. تحقق من الإنترنت وحاول مجدداً."
        }

        loading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = onBack) {
                Text("رجوع")
            }

            Text(title)
        }

        if (loading) {
            CircularProgressIndicator()
            Text("جاري تحميل PDF...")
        }

        if (error.isNotEmpty()) {
            Text(error, color = MaterialTheme.colorScheme.error)
            Button(onClick = {
                page = 0
                bitmap = null
            }) {
                Text("إعادة المحاولة")
            }
        }

        bitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = title,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                contentScale = ContentScale.FillWidth
            )
        }

        if (total > 0) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    enabled = page > 0 && !loading,
                    onClick = { page-- }
                ) {
                    Text("السابق")
                }

                Text("${page + 1} / $total")

                Button(
                    enabled = page < total - 1 && !loading,
                    onClick = { page++ }
                ) {
                    Text("التالي")
                }
            }
        }
    }
}
