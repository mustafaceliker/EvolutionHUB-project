package com.example.evolutionhub


// Analiz sayfası için MikePhil Android Charts kütüphanesini kullandım gerekli implementleri gradle'a da yaptım. (çok uğraştırdı bu yüzden kullanıcı verilerini çekmeyi yetiştiremedim.)
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.widget.RelativeLayout
import com.example.evolutionhub.databinding.ActivityAnalysisBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.bottomnavigation.BottomNavigationView


class AnalysisActivity : BaseActivity() {

    private lateinit var binding: ActivityAnalysisBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalysisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutAnalysis: RelativeLayout = findViewById(R.id.layout_analysis)
        val animationDrawable: AnimationDrawable = layoutAnalysis.background as AnimationDrawable

        animationDrawable.setEnterFadeDuration(2000)
        animationDrawable.setExitFadeDuration(4000)
        animationDrawable.start()

        // BaseActivity içindeki setupBottomNavigationBar fonksiyonunu çağırdık. Böylece navigation view'i çalıştırmış olacağız.
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        setupBottomNavigationView(bottomNavigationView)

        // Kullanıcının hedeflerini aldık.
        val userTargets = getUserTargets()

        // Grafikleri güncelledik.
        updateLineChart(binding.lineChart, userTargets)
        updatePieChart(binding.pieChart, userTargets)
    }

    private fun getUserTargets(): List<Float> {
        // Kullanıcının hedeflerini dinamik olarak al.
        // Bu örnekte rastgele veri kullanılmıştır. Gerçek verileri kullanıcıdan veya başka bir kaynaktan almalısınız.
        return List(4) { (0..100).random().toFloat() }
    }

    private fun updateLineChart(lineChart: LineChart, userTargets: List<Float>) {
        // LineChart'ı güncelle
        val entries = userTargets.mapIndexed { index, value ->
            Entry(index.toFloat() + 1, value)
        }

        val dataSet = LineDataSet(entries, "Hedef Ulaşma Oranları")
        dataSet.color = Color.rgb(102, 51, 153)
        dataSet.setCircleColor(Color.rgb(102, 51, 153))
        dataSet.valueTextColor = Color.BLACK

        val data = LineData(dataSet)
        lineChart.data = data
        lineChart.description.isEnabled = false
        lineChart.legend.isEnabled = false
        lineChart.setPinchZoom(false)
        lineChart.setScaleEnabled(false)

        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)

        val yAxis = lineChart.axisLeft
        yAxis.setDrawGridLines(false)

        lineChart.invalidate()
    }

    private fun updatePieChart(pieChart: PieChart, userTargets: List<Float>) {
        // PieChart'ı güncelle
        val completed = userTargets.count { it >= 50 }
        val notCompleted = userTargets.size - completed

        val entries = listOf(
            PieEntry(completed.toFloat(), "Başarıyla Tamamlanan"),
            PieEntry(notCompleted.toFloat(), "Tamamlanmayan")
        )

        val dataSet = PieDataSet(entries, "Hedef Durumu")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.valueTextColor = Color.WHITE

        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.description.isEnabled = false
        pieChart.legend.isEnabled = true
        pieChart.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        pieChart.legend.verticalAlignment = Legend.LegendVerticalAlignment.CENTER
        pieChart.legend.orientation = Legend.LegendOrientation.VERTICAL
        pieChart.legend.setDrawInside(false)
        pieChart.setUsePercentValues(true)
        pieChart.setEntryLabelColor(Color.WHITE)
        pieChart.setEntryLabelTextSize(12f)

        pieChart.invalidate()

    }
}
