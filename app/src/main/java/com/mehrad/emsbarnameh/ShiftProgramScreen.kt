package com.mehrad.emsbarnameh

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import java.io.File

@Composable
fun ShiftProgramScreen() {
    val context = LocalContext.current
    val people = remember { mutableStateListOf<PersonShift>() }

    var name by remember { mutableStateOf("") }
    var mofazafi by remember { mutableStateOf("") }
    var ezafeKar by remember { mutableStateOf("") }
    var month by remember { mutableStateOf("خرداد") }
    var year by remember { mutableStateOf("1405") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Text("شیفت‌نامه", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("نام") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = mofazafi, onValueChange = { mofazafi = it }, label = { Text("موظفی") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = ezafeKar, onValueChange = { ezafeKar = it }, label = { Text("اضافه‌کار") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = month, onValueChange = { month = it }, label = { Text("ماه") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = year, onValueChange = { year = it }, label = { Text("سال") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = {
            val m = mofazafi.toIntOrNull() ?: 0
            val e = ezafeKar.toIntOrNull() ?: 0

            if (name.isNotBlank()) {
                people.add(PersonShift(name, m, e))
                name = ""
                mofazafi = ""
                ezafeKar = ""
            }
        }) { Text("ثبت شیفت") }

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = { saveMonthData(context, people, month, year) }) {
            Text("ذخیره ماه جاری")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = {
            val loaded = loadMonthData(context, month, year)
            people.clear()
            people.addAll(loaded)
        }) { Text("بارگذاری ماه انتخابی") }

        Spacer(modifier = Modifier.height(24.dp))

        people.forEach { person ->
            Text("${person.name} → موظفی: ${person.mofazafi} | اضافه‌کار: ${person.ezafeKar} | تعداد شیفت: ${person.totalShifts}")
            Row { repeat(person.totalShifts) { Text("⭐") } }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

fun saveMonthData(context: Context, people: List<PersonShift>, month: String, year: String) {
    val gson = Gson()
    val json = gson.toJson(people)
    val file = File(context.filesDir, "shifts_${month}_${year}.json")
    file.writeText(json)
}

fun loadMonthData(context: Context, month: String, year: String): List<PersonShift> {
    val file = File(context.filesDir, "shifts_${month}_${year}.json")
    if (!file.exists()) return emptyList()

    val json = file.readText()
    val gson = Gson()
    return gson.fromJson(json, Array<PersonShift>::class.java).toList()
}

data class PersonShift(
    val name: String,
    var mofazafi: Int = 0,
    var ezafeKar: Int = 0
) {
    val totalShifts: Int
        get() = mofazafi + ezafeKar
}
