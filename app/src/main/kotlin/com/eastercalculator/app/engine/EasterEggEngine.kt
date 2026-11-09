package com.eastercalculator.app.engine

import com.eastercalculator.app.model.EasterEgg
import com.eastercalculator.app.model.Rarity
import java.util.Calendar
import kotlin.math.abs
import kotlin.random.Random

class EasterEggEngine(
    private val random: Random = Random.Default
) {

    val currentSeasonId: String
    val currentDailySecret: Int
    val currentMonthlySecret: Int

    init {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_YEAR)

        currentSeasonId = String.format(java.util.Locale.US, "%04d-%02d", year, month)
        currentDailySecret = ((day * 7919 + year) % 8999) + 1000
        currentMonthlySecret = ((month * 104729 + year) % 89999) + 10000
    }

    fun getDailyRiddle(): String {
        val num = currentDailySecret
        val sumDigits = num.toString().map { it.digitToInt() }.sum()
        val isEven = num % 2 == 0
        val lowerBound = (num / 1000) * 1000
        val upperBound = lowerBound + 1000
        val lastDigit = num % 10
        val parityText = if (isEven) "çift" else "tek"
        return "4 basamaklı, $parityText bir sayı. $lowerBound ile $upperBound arasında saklı; rakamları toplamı $sumDigits ve son rakamı $lastDigit!"
    }

    fun getMonthlyRiddle(): String {
        val num = currentMonthlySecret
        val sumDigits = num.toString().map { it.digitToInt() }.sum()
        val isEven = num % 2 == 0
        val lowerBound = (num / 10000) * 10000
        val upperBound = lowerBound + 10000
        val parityText = if (isEven) "çift" else "tek"
        return "5 basamaklı kozmik bir $parityText sayı! $lowerBound ile $upperBound arasında saklanıyor; tüm basamaklarının toplamı tam $sumDigits."
    }

    fun getAllEasterEggs(): List<EasterEgg> {
        return listOf(
            // === TANRI PARÇACIKLARI (20/1 - 100.000/1 Rastgele Oran • 50 - 100,000 Puan) ===
            EasterEgg(
                id = "god_higgs_boson",
                emoji = "🧬",
                title = "Higgs Bozonu: Tanrı Parçacığı",
                description = "Evrene kütle kazandıran mutlak parçacık! İmkansıza yakın rastlantısallığı yakaladın!",
                rarity = Rarity.GOD_PARTICLE,
                hint = "Her hesaplamada tamamen rastgele 100.000/1 olasılıkla ortaya çıkar.",
                triggerDescription = "Rastgele Yakalama Oranı: 100.000/1",
                customPoints = 100000,
                oneInChance = 100000
            ),
            EasterEgg(
                id = "god_singularity_core",
                emoji = "🌀",
                title = "Tekillik Özü",
                description = "Uzay-zaman dokusunun tek bir noktaya çöktüğü kozmik çekirdek!",
                rarity = Rarity.GOD_PARTICLE,
                hint = "Her hesaplamada tamamen rastgele 50.000/1 olasılıkla ortaya çıkar.",
                triggerDescription = "Rastgele Yakalama Oranı: 50.000/1",
                customPoints = 50000,
                oneInChance = 50000
            ),
            EasterEgg(
                id = "god_tachyon_storm",
                emoji = "⚡",
                title = "Takyon Fırtınası",
                description = "Işık hızını aşan varsayımsal parçacıklar hesap makinenin içinden geçti!",
                rarity = Rarity.GOD_PARTICLE,
                hint = "Her hesaplamada tamamen rastgele 10.000/1 olasılıkla ortaya çıkar.",
                triggerDescription = "Rastgele Yakalama Oranı: 10.000/1",
                customPoints = 25000,
                oneInChance = 10000
            ),
            EasterEgg(
                id = "god_dark_matter",
                emoji = "🌑",
                title = "Karanlık Madde Kristali",
                description = "Görünmez evrenin gizemli kütlesi işleminin sonucunda kristalleşti!",
                rarity = Rarity.GOD_PARTICLE,
                hint = "Her hesaplamada tamamen rastgele 2.500/1 olasılıkla ortaya çıkar.",
                triggerDescription = "Rastgele Yakalama Oranı: 2.500/1",
                customPoints = 7500,
                oneInChance = 2500
            ),
            EasterEgg(
                id = "god_antimatter_spark",
                emoji = "💥",
                title = "Antimadde Çekirdeği",
                description = "Madde ve antimaddenin saf enerji çarpışması!",
                rarity = Rarity.GOD_PARTICLE,
                hint = "Her hesaplamada tamamen rastgele 500/1 olasılıkla ortaya çıkar.",
                triggerDescription = "Rastgele Yakalama Oranı: 500/1",
                customPoints = 2500,
                oneInChance = 500
            ),
            EasterEgg(
                id = "god_neutrino_wave",
                emoji = "✨",
                title = "Nötrino Sıçraması",
                description = "Milyarlarca hayalet parçacıktan biri tam senin işleminle etkileşime girdi!",
                rarity = Rarity.GOD_PARTICLE,
                hint = "Her hesaplamada tamamen rastgele 100/1 olasılıkla ortaya çıkar.",
                triggerDescription = "Rastgele Yakalama Oranı: 100/1",
                customPoints = 500,
                oneInChance = 100
            ),
            EasterEgg(
                id = "god_quantum_fluctuation",
                emoji = "⚛️",
                title = "Kuantum Dalgalanması",
                description = "Boşlukta aniden beliren rastgele atom altı enerji dalgası!",
                rarity = Rarity.GOD_PARTICLE,
                hint = "Her hesaplamada tamamen rastgele 20/1 olasılıkla ortaya çıkar.",
                triggerDescription = "Rastgele Yakalama Oranı: 20/1",
                customPoints = 50,
                oneInChance = 20
            ),

            // === MYTHIC (%3 Oran - 5,000 Puan) ===
            EasterEgg(
                id = "mythic_golden_egg",
                emoji = "🐣",
                title = "Altın Paskalya Yumurtası",
                description = "Tüm gizemlerin başlangıcı! En nadir ve en yüksek puanlı yumurta.",
                rarity = Rarity.MYTHIC,
                hint = "Milyonluk kozmik bir hayal kur ya da kumarhane şansının altı kez parıldadığı o devasa sayıya ulaş.",
                triggerDescription = "Bir milyonun ya da altı adet şanslı yeddinin sırrı."
            ),
            EasterEgg(
                id = "mythic_celestial_supernova",
                emoji = "🌟",
                title = "Kozmik Süpernova",
                description = "Evrenin en güçlü patlaması ekranına yağıyor!",
                rarity = Rarity.MYTHIC,
                hint = "Doğanın kutsal Altın Oranını on binlerle genişlet ya da yan yana altı birliğin kozmik hizalanmasını bul.",
                triggerDescription = "Kutsal Altın Oran (Fi) sabitinin 10.000 katı veya ardışık 6 tane bir."
            ),
            EasterEgg(
                id = "mythic_monthly_secret",
                emoji = "🏆",
                title = "Aylık Gizem Kupası",
                description = "Bu aya özel rastgele üretilen gizli anahtarı keşfettin!",
                rarity = Rarity.MYTHIC,
                hint = getMonthlyRiddle(),
                triggerDescription = "Bu ayın kozmik matematik bilmecesini çöz."
            ),

            // === EPIC (%7 Oran - 1,500 Puan) ===
            EasterEgg(
                id = "epic_black_hole",
                emoji = "🌌",
                title = "Kozmik Karadelik",
                description = "Evrenin kurallarını büktün! Sıfıra bölmek bir karadelik açtı.",
                rarity = Rarity.EPIC,
                hint = "Matematiğin en büyük yasağını çiğne: Sayıları hiçliğin ta kendisine bölmeyi dene...",
                triggerDescription = "Matematiksel tekillik: Bir sayıyı sıfıra bölme cüreti."
            ),
            EasterEgg(
                id = "epic_dragon",
                emoji = "🐉",
                title = "Kızıl Ejderha",
                description = "Alev püskürten kadim ejderha hesap makinesini ele geçirdi!",
                rarity = Rarity.EPIC,
                hint = "Dokuzların gazabı: Beş basamaklı en büyük tek sayının ejderha alevini yakala.",
                triggerDescription = "En büyük 5 basamaklı tek sayının ejderha ateşi."
            ),
            EasterEgg(
                id = "epic_chronos",
                emoji = "⏳",
                title = "Zamanın Efendisi",
                description = "Zaman döngüsünü kırdın!",
                rarity = Rarity.EPIC,
                hint = "Zaman çizgisinde tam da şu an içinde yaşadığımız yılı bir işlem sonucu olarak üret.",
                triggerDescription = "İçinde yaşadığımız şimdiki zamanın yılı."
            ),
            EasterEgg(
                id = "epic_wizard_ladder",
                emoji = "🧙‍♂️",
                title = "Büyücü Merdiveni",
                description = "Sıralı sayıların mistik harmonisi!",
                rarity = Rarity.EPIC,
                hint = "Büyücünün kulesine çıkan veya inen 5 basamaklı kusursuz basamak merdivenini inşa et.",
                triggerDescription = "1'den 5'e yükselen veya alçalan ardışık basamaklar."
            ),

            // === RARE (%15 Oran - 600 Puan) ===
            EasterEgg(
                id = "rare_daily_secret",
                emoji = "🎯",
                title = "Günün Gizli Kodu",
                description = "Bugünün rastgele belirlenen gizli hedefini vurdun!",
                rarity = Rarity.RARE,
                hint = getDailyRiddle(),
                triggerDescription = "Bugünün matematiksel gizem hedefini yakala."
            ),
            EasterEgg(
                id = "rare_leet_hacker",
                emoji = "🤖",
                title = "1337 Elite Hacker",
                description = "Sisteme sızıldı! Klasik hacker kodu aktif.",
                rarity = Rarity.RARE,
                hint = "Eski siber dünyanın ve hacker kültürünün efsanevi 'Elite' (L-E-E-T) şifresini hesapla.",
                triggerDescription = "Siber alemin kutsal 'LEET' kodu."
            ),
            EasterEgg(
                id = "rare_pi_infinite",
                emoji = "🥧",
                title = "Pi Sonsuzluğu",
                description = "Dairenin kadim sırrı ekranı kaplıyor.",
                rarity = Rarity.RARE,
                hint = "Bir çemberin çevresini çapına böldüğünde evrenin en meşhur irrasyonel sabitini keşfedersin.",
                triggerDescription = "Dairenin kadim sırrı: Pi sayısının ilk basamakları."
            ),
            EasterEgg(
                id = "rare_unicorn",
                emoji = "🦄",
                title = "Tekboynuz Mucizesi",
                description = "Gökkuşağı diyarından gelen saf enerji!",
                rarity = Rarity.RARE,
                hint = "Uzak Doğu kültüründe sonsuz bolluk ve bereket getiren dörtlü sekizlerin mucizesi...",
                triggerDescription = "Bolluk getiren dört basamaklı bereket sayısı."
            ),
            EasterEgg(
                id = "rare_area_51",
                emoji = "🛸",
                title = "51. Bölge Uçan Dairesi",
                description = "Dünya dışı sinyaller tespit edildi!",
                rarity = Rarity.RARE,
                hint = "Nevada çölündeki en gizli dünya dışı askeri araştırma bölgesinin numarasını kendisiyle çarp.",
                triggerDescription = "Gizli UFO üssünün karesi."
            ),

            // === UNCOMMON (%25 Oran - 250 Puan) ===
            EasterEgg(
                id = "uncommon_rocket",
                emoji = "🚀",
                title = "Aya Yolculuk",
                description = "Roketler ateşlendi! Ay'a ilk ayak basılan yıl.",
                rarity = Rarity.UNCOMMON,
                hint = "Apollo 11'in 'İnsan için küçük, insanlık için dev bir adım' attığı tarihi yılı hesapla.",
                triggerDescription = "İnsanoğlunun Ay yüzeyine ilk ayak bastığı tarihi yıl."
            ),
            EasterEgg(
                id = "uncommon_diamond",
                emoji = "💎",
                title = "Kusursuz Elmas",
                description = "Göz kamaştırıcı pırlanta yağmuru!",
                rarity = Rarity.UNCOMMON,
                hint = "Metrik sistemin 'kilo' öneki: Tam bir binlik cevhere ulaş.",
                triggerDescription = "Dört basamaklı ilk kusursuz binlik kilometre taşı."
            ),
            EasterEgg(
                id = "uncommon_d20",
                emoji = "🎲",
                title = "Kritik Vuruş (Natural 20)",
                description = "Zarlar senin için en yüksek sayıyı attı!",
                rarity = Rarity.UNCOMMON,
                hint = "Masaüstü RPG oyunlarında en kritik vuruşu (Natural Hit) sağlayan 20 yüzlü zarın zirvesi.",
                triggerDescription = "D&D masasında kritik vuruş sağlayan en büyük zar sonucu."
            ),
            EasterEgg(
                id = "uncommon_retro_game",
                emoji = "👾",
                title = "Retro 2048",
                description = "Klasik 2048 karelerinin nostaljisi!",
                rarity = Rarity.UNCOMMON,
                hint = "Kareleri kaydırıp birleştirdiğin retro bulmaca oyununda 2'nin 11. kuvvetine ulaş.",
                triggerDescription = "Popüler retro kaydırma oyununun nihai hedefi."
            ),
            EasterEgg(
                id = "uncommon_crystal_ball",
                emoji = "🔮",
                title = "Evrenin Anlamı",
                description = "Otostopçunun Galaksi Rehberi'ne göre tüm soruların cevabı.",
                rarity = Rarity.UNCOMMON,
                hint = "Otostopçunun Galaksi Rehberi'ne göre 'Hayat, evren ve her şeye dair nihai cevap'...",
                triggerDescription = "Derin Düşünce bilgisayarının bulduğu nihai cevap."
            ),

            // === COMMON (%50 Oran - 100 Puan) ===
            EasterEgg(
                id = "common_jackpot",
                emoji = "🎰",
                title = "Şanslı 777",
                description = "Casino zilleri çalıyor! Şans kapını çaldı.",
                rarity = Rarity.COMMON,
                hint = "Kumarhane slot makinelerinin en büyük ikramiyesini saçan üçlü şans sayısı...",
                triggerDescription = "Slot makinelerinin üçlü ikramiye sembolü."
            ),
            EasterEgg(
                id = "common_clover",
                emoji = "🍀",
                title = "Dört Yapraklı Yonca",
                description = "Bugün senin şanslı günün!",
                rarity = Rarity.COMMON,
                hint = "Çimlerin arasında nadiren denk gelinen o şanslı yoncanın yaprak sayısı...",
                triggerDescription = "En klasik uğur getiren yonca yaprağı sayısı."
            ),
            EasterEgg(
                id = "common_pizza",
                emoji = "🍕",
                title = "Pizza Dilimi",
                description = "360 derecelik pizzayı 8 arkadaş eşit paylaştı!",
                rarity = Rarity.COMMON,
                hint = "Tam dairesel bir pizzayı 8 arkadaş eşit paylaştığında bir dilimin merkez açısı...",
                triggerDescription = "360 derecenin sekizde bir dilimi."
            ),
            EasterEgg(
                id = "common_coffee_404",
                emoji = "☕",
                title = "Hata 404: Kahve Bulunamadı",
                description = "Biraz kahve molası verme zamanı geldi.",
                rarity = Rarity.COMMON,
                hint = "İnternette arayıp da bulamadığın kayıp web sayfalarının evrensel protokol kodu...",
                triggerDescription = "İnternet dünyasının en meşhur sayfa bulunamadı hatası."
            ),
            EasterEgg(
                id = "common_gravity_apple",
                emoji = "🍎",
                title = "Newton'un Elması",
                description = "Kafana bir elma düştü ve yerçekimini keşfettin!",
                rarity = Rarity.COMMON,
                hint = "Newton'un başına düşen elmanın yeryüzüne doğru hızlandığı o meşhur ivme sabiti...",
                triggerDescription = "Dünya'nın standart yerçekimi ivmesi."
            )
        )
    }

    fun rollGodParticle(
        unlockedEggIds: Set<String> = emptySet(),
        luckBoostMultiplier: Int = 1
    ): EasterEgg? {
        val godParticles = getAllEasterEggs()
            .filter { it.rarity == Rarity.GOD_PARTICLE && it.id !in unlockedEggIds }
            .sortedByDescending { it.points }

        for (particle in godParticles) {
            val baseChance = particle.oneInChance ?: continue
            val effectiveChance = (baseChance / luckBoostMultiplier.coerceAtLeast(1)).coerceAtLeast(1)
            if (random.nextInt(effectiveChance) == 0) {
                return particle
            }
        }
        return null
    }

    fun checkEasterEgg(
        expression: String,
        numericResult: Double?,
        isDivisionByZero: Boolean,
        unlockedEggIds: Set<String> = emptySet(),
        enableRandomGodParticles: Boolean = true,
        luckBoostMultiplier: Int = 1
    ): EasterEgg? {
        val deterministicEgg = findDeterministicEgg(expression, numericResult, isDivisionByZero)
        if (deterministicEgg != null && deterministicEgg.id !in unlockedEggIds) {
            return deterministicEgg
        }

        if (enableRandomGodParticles && numericResult != null && !numericResult.isNaN() && !numericResult.isInfinite()) {
            val rolledGodParticle = rollGodParticle(unlockedEggIds, luckBoostMultiplier)
            if (rolledGodParticle != null) {
                return rolledGodParticle
            }
        }

        return deterministicEgg
    }

    private fun findDeterministicEgg(
        expression: String,
        numericResult: Double?,
        isDivisionByZero: Boolean
    ): EasterEgg? {
        val eggs = getAllEasterEggs().associateBy { it.id }

        // 1. Division by zero -> Black Hole
        if (isDivisionByZero || expression.contains("/0") || expression.contains("÷0")) {
            return eggs["epic_black_hole"]
        }

        if (numericResult == null) return null

        val roundedLong = try { numericResult.toLong() } catch (_: Exception) { 0L }
        val absVal = abs(numericResult)
        val isWhole = numericResult == roundedLong.toDouble()

        // 2. Check Monthly & Daily randomized secrets
        if (isWhole && roundedLong == currentMonthlySecret.toLong()) {
            return eggs["mythic_monthly_secret"]
        }
        if (isWhole && roundedLong == currentDailySecret.toLong()) {
            return eggs["rare_daily_secret"]
        }

        // 3. Mythic checks
        if (isWhole && (roundedLong == 1000000L || roundedLong == 777777L)) {
            return eggs["mythic_golden_egg"]
        }
        if (isWhole && (roundedLong == 16180L || roundedLong == 111111L)) {
            return eggs["mythic_celestial_supernova"]
        }

        // 4. Epic checks
        if (isWhole && roundedLong == 99999L) {
            return eggs["epic_dragon"]
        }
        if (isWhole && roundedLong == 2026L) {
            return eggs["epic_chronos"]
        }
        if (isWhole && (roundedLong == 12345L || roundedLong == 54321L)) {
            return eggs["epic_wizard_ladder"]
        }

        // 5. Rare checks
        if (isWhole && roundedLong == 1337L) {
            return eggs["rare_leet_hacker"]
        }
        if (abs(absVal - 3.1415) < 0.001 || abs(absVal - 3.14) < 0.005) {
            return eggs["rare_pi_infinite"]
        }
        if (isWhole && roundedLong == 8888L) {
            return eggs["rare_unicorn"]
        }
        if (isWhole && (roundedLong == 2601L || expression.contains("51*51") || expression.contains("51×51"))) {
            return eggs["rare_area_51"]
        }

        // 6. Uncommon checks
        if (isWhole && roundedLong == 1969L) {
            return eggs["uncommon_rocket"]
        }
        if (isWhole && roundedLong == 1000L) {
            return eggs["uncommon_diamond"]
        }
        if (isWhole && roundedLong == 20L) {
            return eggs["uncommon_d20"]
        }
        if (isWhole && roundedLong == 2048L) {
            return eggs["uncommon_retro_game"]
        }
        if (isWhole && roundedLong == 42L) {
            return eggs["uncommon_crystal_ball"]
        }

        // 7. Common checks
        if (isWhole && roundedLong == 777L) {
            return eggs["common_jackpot"]
        }
        if (isWhole && roundedLong == 4L) {
            return eggs["common_clover"]
        }
        if (isWhole && roundedLong == 45L) {
            return eggs["common_pizza"]
        }
        if (isWhole && roundedLong == 404L) {
            return eggs["common_coffee_404"]
        }
        if (abs(absVal - 9.81) < 0.02 || abs(absVal - 9.8) < 0.02) {
            return eggs["common_gravity_apple"]
        }

        return null
    }
}

