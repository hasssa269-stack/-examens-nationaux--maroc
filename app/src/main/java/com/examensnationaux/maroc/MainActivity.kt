package com.examensnationaux.maroc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Green = Color(0xFF087F5B)
private val Dark = Color(0xFF142D28)
private val Background = Color(0xFFF4F7F5)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BacApp()
        }
    }
}

@Composable
fun BacApp() {
    var search by remember { mutableStateOf("") }

    val subjects = listOf(
        "العلوم الفيزيائية",
        "الرياضيات",
        "علوم الحياة والأرض",
        "الفلسفة",
        "اللغة العربية",
        "اللغة الفرنسية",
        "اللغة الإنجليزية",
        "الاقتصاد العام والإحصاء",
        "المحاسبة والرياضيات المالية",
        "التاريخ والجغرافيا"
    )

    val filtered = subjects.filter {
        it.contains(search, ignoreCase = true)
    }

    Scaffold(
        containerColor = Background
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Dark,
                            RoundedCornerShape(22.dp)
                        )
                        .padding(22.dp)
                ) {
                    Text(
                        "🇲🇦 امتحانات وطنية",
                        color = Color.White,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "مع التصحيح جميع الشعب",
                        color = Color(0xFFD5EBDD),
                        fontSize = 17.sp
                    )

                    Spacer(Modifier.height(18.dp))

                    Text(
                        "استعد للباكالوريا بثقة!",
                        color = Color.White,
                        fontSize = 15.sp
                    )
                }
            }

            item {
                Text(
                    "ابحث عن المادة",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = Dark
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("اكتب اسم المادة...")
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )
            }

            item {
                Text(
                    "المواد الدراسية",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Dark
                )
            }

            items(filtered) { subject ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "📘",
                            fontSize = 25.sp
                        )

                        Spacer(Modifier.width(14.dp))

                        Text(
                            subject,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Dark
                        )

                        Spacer(Modifier.weight(1f))

                        Text(
                            "›",
                            color = Green,
                            fontSize = 25.sp
                        )
                    }
                }
            }

            item {
                Text(
                    "جميع الحقوق محفوظة © 2026",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}
