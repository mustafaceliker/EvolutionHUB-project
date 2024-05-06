package com.example.evolutionhub

//İhtiyacımız olan tüm importlar yapıldı.

import Hedef
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.example.evolutionhub.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale



// Main class'ı tanımlanırken BaseActivity miras alındı (Navigation)
class MainActivity : BaseActivity() {

    private val hedefList = mutableListOf<Hedef>()
    private lateinit var hedefAdapter: ArrayAdapter<String>
    private lateinit var kategoriAdapter: ArrayAdapter<String>
    private lateinit var selectedKategori: String
    private lateinit var database: DatabaseReference
    private lateinit var binding: ActivityMainBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutMain: RelativeLayout = findViewById(R.id.layout_main)
        val animationDrawable: AnimationDrawable = layoutMain.background as AnimationDrawable

        animationDrawable.setEnterFadeDuration(2000)
        animationDrawable.setExitFadeDuration(4000)
        animationDrawable.start()

        // BaseActivity içindeki setupBottomNavigationBar fonksiyonunu çağırdık. Böylece navigation view'i çalıştırmış olacağız.
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        setupBottomNavigationView(bottomNavigationView)

        database = FirebaseDatabase.getInstance().reference.child("hedefler")

        val hedefListView: ListView = findViewById(R.id.hedefListView)
        hedefAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        hedefListView.adapter = hedefAdapter

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            showHedefEkleDialog()
        }
        // Kategori spinner'ı ve adapter ile kategori filtreleme tanımı.
        val kategoriSpinner: Spinner = findViewById(R.id.kategoriSpinner)
        kategoriAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item)
        kategoriAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        kategoriSpinner.adapter = kategoriAdapter

        kategoriSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long) {
                selectedKategori = parentView.getItemAtPosition(position).toString()
                filterHedefListByCategory()
            }
            override fun onNothingSelected(parentView: AdapterView<*>) {
                // Burada bir şey yapmamıza gerek yok.
            }
        }

        // Kategorileri başlangıçta ekledik. (Daha fazla eklenebilir, proje işlevini yerine getiriyor.)
        kategoriAdapter.addAll("İş", "Kişisel", "Eğlence", "Diğer")

        hedefListView.setOnItemClickListener { _, _, position, _ ->
            val secilenHedef = hedefList[position]
            showHedefDetayDialog(secilenHedef)
        }

        hedefListView.setOnItemLongClickListener { _, _, position, _ ->
            hedefList.removeAt(position)
            hedefAdapter.notifyDataSetChanged()
            filterHedefListByCategory()  // Hedef silindiğinde kategoriye göre listeyi güncelle
            Toast.makeText(this, "Hedef başarıyla silindi!", Toast.LENGTH_SHORT).show()
            true

        } // Fab profile tuşu ile profili görüntüleyebiliyoruz.
        binding.fabProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

    }

    // Hedef eklememizi sağlayan dialog sayfasının komutları. Value olarak tanımlanan ve textten çekilen veriler database'e aktarılıyor.
    private fun showHedefEkleDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_hedef_ekle, null)
        builder.setView(dialogView)

        val isimEditText: TextInputEditText = dialogView.findViewById(R.id.isimEditText)
        val aciklamaEditText: TextInputEditText = dialogView.findViewById(R.id.aciklamaEditText)
        val baslangicTarihEditText: TextInputEditText = dialogView.findViewById(R.id.baslangicTarihEditText)
        val bitisTarihEditText: TextInputEditText = dialogView.findViewById(R.id.bitisTarihEditText)
        val kategoriSpinner: Spinner = dialogView.findViewById(R.id.kategoriSpinnerDialog)

        kategoriSpinner.adapter = kategoriAdapter

        baslangicTarihEditText.setOnClickListener {
            showDatePickerDialog(baslangicTarihEditText)
        }
        bitisTarihEditText.setOnClickListener {
            showDatePickerDialog(bitisTarihEditText)
        }

        builder.setPositiveButton("Ekle") { _, _ ->
            val isim = isimEditText.text.toString().trim()
            val aciklama = aciklamaEditText.text.toString().trim()
            val baslangicTarih = baslangicTarihEditText.text.toString().trim()
            val bitisTarih = bitisTarihEditText.text.toString().trim()

            // Doldurulmamış alan var mı diye kontrol eden karar bloğu.
            if (isim.isNotEmpty() && aciklama.isNotEmpty() && baslangicTarih.isNotEmpty() && bitisTarih.isNotEmpty()) {
                // Check if the end date is valid
                if (isBitisTarihiGecerli(baslangicTarih, bitisTarih)) {
                    val selectedKategori = kategoriSpinner.selectedItem.toString()

                    // Firebase'e hedef eklendi. Bir id alarak yapıyoruz.
                    val hedefId = database.push().key
                    val yeniHedef = Hedef(isim, aciklama, baslangicTarih, bitisTarih, selectedKategori, hedefId)
                    hedefList.add(yeniHedef)
                    hedefAdapter.add(isim)
                    filterHedefListByCategory()
                    database.child(hedefId!!).setValue(yeniHedef)

                    Toast.makeText(this, "Hedef başarıyla eklendi!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Bitiş Tarihi, Başlangıç Tarihinden önce olmalıdır!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Lütfen tüm bilgileri eksiksiz doldurun!", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("İptal") { _, _ -> }

        val dialog = builder.create()
        dialog.show()
    }

    // Tarihleri kontrol eden blok, bitiş tarihini başlangıç tarihinden önce girmeye izin vermiyor.
    private fun isBitisTarihiGecerli(baslangicTarih: String, bitisTarih: String): Boolean {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val startDate = dateFormat.parse(baslangicTarih)
        val endDate = dateFormat.parse(bitisTarih)

        return startDate?.before(endDate) ?: false
    }
    // Hedefin detay kısmında alt hedef eklemeyi sağladık. Ayrıca hedef detay sayfası yapmaktansa dialog ile işi çözdük. Tıklanılan hedefe göre açılan dialog ekranı.
    private fun showHedefDetayDialog(hedef: Hedef) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_hedef_detay, null)
        builder.setView(dialogView)

        val isimTextView: TextView = dialogView.findViewById(R.id.detayIsimTextView)
        val aciklamaTextView: TextView = dialogView.findViewById(R.id.detayAciklamaTextView)
        val baslangicTarihTextView: TextView = dialogView.findViewById(R.id.detayBaslangicTarihTextView)
        val bitisTarihTextView: TextView = dialogView.findViewById(R.id.detayBitisTarihTextView)
        val kalanSureTextView: TextView = dialogView.findViewById(R.id.kalanSureTextView)
        val progressBar: ProgressBar = dialogView.findViewById(R.id.progressBar)
        val altHedefListView: ListView = dialogView.findViewById(R.id.altHedefListView)

        isimTextView.text = "İsim: ${hedef.isim}"
        aciklamaTextView.text = "Açıklama: ${hedef.aciklama}"
        baslangicTarihTextView.text = "Başlangıç Tarihi: ${hedef.baslangicTarih}"
        bitisTarihTextView.text = "Bitiş Tarihi: ${hedef.bitisTarih}"

        // Bitiş tarihine kalan süreyi hesapladık ve gösterdik.
        val kalanSure = calculateRemainingDays(hedef.bitisTarih)
        kalanSureTextView.text = "Kalan Süre: $kalanSure gün"

        // Progress bar'ı güncelle
        progressBar.progress = kalanSure.toInt()

        val altHedefAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1)
        altHedefAdapter.addAll(hedef.altHedefler)
        altHedefListView.adapter = altHedefAdapter

        val altHedefEkleButton: FloatingActionButton = dialogView.findViewById(R.id.altHedefEkleButton)
        altHedefEkleButton.setOnClickListener {
            showAltHedefEkleDialog(hedef, altHedefAdapter)
        }

        // Hedef silme butonu.
        val hedefSilButton: Button = dialogView.findViewById(R.id.hedefSilButton)
        val dialog = builder.create()  // dialog nesnesini oluşturun

        hedefSilButton.setOnClickListener {
            val hedefId = hedef.hedefID

            // Firebase'den hedefi siliyoruz.
            database.child(hedefId!!).removeValue()

            hedefList.remove(hedef)
            hedefAdapter.remove(hedef.isim)
            hedefAdapter.notifyDataSetChanged()
            Toast.makeText(this, "Hedef başarıyla silindi!", Toast.LENGTH_SHORT).show()
            dialog.dismiss() // dialog.dismiss() kullanın
        }

        builder.setPositiveButton("Düzenle") { _, _ ->
            // Hedefi düzenleme işlemleri
        }

        builder.setNegativeButton("Kapat") { _, _ ->
            dialog.dismiss()
        }

        dialog.show()  // dialog'u göster
    }

    private fun calculateRemainingDays(bitisTarih: String): Long {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val currentDate = Calendar.getInstance().time
        val endDate = dateFormat.parse(bitisTarih)

        if (endDate != null) {
            val remainingMillis = endDate.time - currentDate.time
            return remainingMillis / (24 * 60 * 60 * 1000)
        }
        return 0
    }

    private fun showAltHedefEkleDialog(hedef: Hedef, altHedefAdapter: ArrayAdapter<String>) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_alt_hedef_ekle, null)
        builder.setView(dialogView)

        val altHedefEditText: EditText = dialogView.findViewById(R.id.altHedefEditText)
        val altHedefEkleButton: Button = dialogView.findViewById(R.id.altHedefEkleButton)
        val dialog = builder.create()

        altHedefEkleButton.setOnClickListener {
            val altHedef = altHedefEditText.text.toString().trim()
            if (altHedef.isNotEmpty()) {
                hedef.altHedefler.add(altHedef)
                hedef.altHedefDetay.add("Detay: $altHedef")
                altHedefAdapter.add(altHedef)
                altHedefAdapter.notifyDataSetChanged()
                Toast.makeText(this, "Alt Hedef başarıyla eklendi!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Alt Hedef boş olamaz!", Toast.LENGTH_SHORT).show()
            }
            database.child(hedef.hedefID!!).child("altHedefler").setValue(hedef.altHedefler)
        }

        builder.setNeutralButton("İptal") { _, _ -> dialog.dismiss() }

        dialog.show()
    }

    private fun showDatePickerDialog(editText: EditText) {
        val cal = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)

                val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                editText.setText(dateFormat.format(selectedDate.time))
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }
    // Kategoriye göre filtreleme.
    private fun filterHedefListByCategory() {
        hedefAdapter.clear()
        for (hedef in hedefList) {
            if (hedef.kategori == selectedKategori || selectedKategori == "Tümü") {
                hedefAdapter.add(hedef.isim)
            }
        }
        hedefAdapter.notifyDataSetChanged()
    }
}
