data class Hedef( // Hedeflere ait value'lar girildi.
    var isim: String,
    var aciklama: String,
    var baslangicTarih: String,
    var bitisTarih: String,
    var kategori: String,
    var hedefID: String? = null,
    var altHedefler: MutableList<String> = mutableListOf(), // altHedefler listesi
    var altHedefDetay: MutableList<String> = mutableListOf() // altHedeflerDetay
)