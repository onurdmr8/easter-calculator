package com.eastercalculator.app.engine

import com.eastercalculator.app.model.EasterEgg
import com.eastercalculator.app.model.Rarity
import java.util.Calendar
import kotlin.math.abs

class EasterEggEngine {

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

    fun getAllEasterEggs(): List<EasterEgg> {
        return listOf(
            // === MYTHIC (%3 Oran - 5,000 Puan) ===
            EasterEgg(
                id = "mythic_golden_egg",
                emoji = "🐣",
                title = "Altın Paskalya Yumurtası",
                description = "Tüm gizemlerin başlangıcı! En nadir ve en yüksek puanlı yumurta.",
                rarity = Rarity.MYTHIC,
                hint = "Sonucu tam olarak 1,000,000 olan bir işlem yap veya 777777'ye ulaş!",
                triggerDescription = "Sonuç: 1,000,000 veya 777,777"
            ),
            EasterEgg(
                id = "mythic_celestial_supernova",
                emoji = "🌟",
                title = "Kozmik Süpernova",
                description = "Evrenin en güçlü patlaması ekranına yağıyor!",
                rarity = Rarity.MYTHIC,
                hint = "Kutsal Altın Oran'ın (16180) veya 111111'in sırrını çöz.",
                triggerDescription = "Sonuç: 16180 veya 111111"
            ),
            EasterEgg(
                id = "mythic_monthly_secret",
                emoji = "🏆",
                title = "Aylık Gizem Kupası",
                description = "Bu aya özel rastgele üretilen gizli anahtarı keşfettin!",
                rarity = Rarity.MYTHIC,
                hint = "Bu ayın gizli sayısı: $currentMonthlySecret. Bu sonuca ulaş!",
                triggerDescription = "Aylık Gizli Sonuç: $currentMonthlySecret"
            ),

            // === EPIC (%7 Oran - 1,500 Puan) ===
            EasterEgg(
                id = "epic_black_hole",
                emoji = "🌌",
                title = "Kozmik Karadelik",
                description = "Evrenin kurallarını büktün! Sıfıra bölmek bir karadelik açtı.",
                rarity = Rarity.EPIC,
                hint = "42 veya herhangi bir sayıyı sıfıra bölmeyi dene...",
                triggerDescription = "Sıfıra Bölme: ÷ 0"
            ),
            EasterEgg(
                id = "epic_dragon",
                emoji = "🐉",
                title = "Kızıl Ejderha",
                description = "Alev püskürten kadim ejderha hesap makinesini ele geçirdi!",
                rarity = Rarity.EPIC,
                hint = "Dokuzların gazabı: Sonucu tam 99999 olan bir işlem bul.",
                triggerDescription = "Sonuç: 99999"
            ),
            EasterEgg(
                id = "epic_chronos",
                emoji = "⏳",
                title = "Zamanın Efendisi",
                description = "Zaman döngüsünü kırdın!",
                rarity = Rarity.EPIC,
                hint = "İçinde bulunduğumuz yılı (2026) bir işlem sonucu olarak bul.",
                triggerDescription = "Sonuç: 2026"
            ),
            EasterEgg(
                id = "epic_wizard_ladder",
                emoji = "🧙‍♂️",
                title = "Büyücü Merdiveni",
                description = "Sıralı sayıların mistik harmonisi!",
                rarity = Rarity.EPIC,
                hint = "1'den 5'e sıralı bir sayıya ulaş (12345).",
                triggerDescription = "Sonuç: 12345 veya 54321"
            ),

            // === RARE (%15 Oran - 600 Puan) ===
            EasterEgg(
                id = "rare_daily_secret",
                emoji = "🎯",
                title = "Günün Gizli Kodu",
                description = "Bugünün rastgele belirlenen gizli hedefini vurdun!",
                rarity = Rarity.RARE,
                hint = "Günün gizli hedef sayısı: $currentDailySecret. Bu sayıya ulaş!",
                triggerDescription = "Günün Hedefi: $currentDailySecret"
            ),
            EasterEgg(
                id = "rare_leet_hacker",
                emoji = "🤖",
                title = "1337 Elite Hacker",
                description = "Sisteme sızıldı! Klasik hacker kodu aktif.",
                rarity = Rarity.RARE,
                hint = "Eski usül siber alemin en meşhur sayısını hesapla (1337).",
                triggerDescription = "Sonuç: 1337"
            ),
            EasterEgg(
                id = "rare_pi_infinite",
                emoji = "🥧",
                title = "Pi Sonsuzluğu",
                description = "Dairenin kadim sırrı ekranı kaplıyor.",
                rarity = Rarity.RARE,
                hint = "Pi sayısının ilk basamaklarına ulaş (3.14 veya 3.1415).",
                triggerDescription = "Sonuç: 3.14 veya 3.1415"
            ),
            EasterEgg(
                id = "rare_unicorn",
                emoji = "🦄",
                title = "Tekboynuz Mucizesi",
                description = "Gökkuşağı diyarından gelen saf enerji!",
                rarity = Rarity.RARE,
                hint = "Dört tane 8'in yan yana geldiği bir sonuç elde et (8888).",
                triggerDescription = "Sonuç: 8888"
            ),
            EasterEgg(
                id = "rare_area_51",
                emoji = "🛸",
                title = "51. Bölge Uçan Dairesi",
                description = "Dünya dışı sinyaller tespit edildi!",
                rarity = Rarity.RARE,
                hint = "51 x 51 işlemini çöz veya 2601 sonucuna ulaş.",
                triggerDescription = "İşlem: 51 × 51 veya Sonuç 2601"
            ),

            // === UNCOMMON (%25 Oran - 250 Puan) ===
            EasterEgg(
                id = "uncommon_rocket",
                emoji = "🚀",
                title = "Aya Yolculuk",
                description = "Roketler ateşlendi! Ay'a ilk ayak basılan yıl.",
                rarity = Rarity.UNCOMMON,
                hint = "Ay'a ilk insan ne zaman indi? (1969).",
                triggerDescription = "Sonuç: 1969"
            ),
            EasterEgg(
                id = "uncommon_diamond",
                emoji = "💎",
                title = "Kusursuz Elmas",
                description = "Göz kamaştırıcı pırlanta yağmuru!",
                rarity = Rarity.UNCOMMON,
                hint = "Binlik kilometre taşı: Sonucu tam 1000 yap.",
                triggerDescription = "Sonuç: 1000"
            ),
            EasterEgg(
                id = "uncommon_d20",
                emoji = "🎲",
                title = "Kritik Vuruş (Natural 20)",
                description = "Zarlar senin için en yüksek sayıyı attı!",
                rarity = Rarity.UNCOMMON,
                hint = "Masaüstü RPG oyunlarının en kritik zarı: 20.",
                triggerDescription = "Sonuç: 20"
            ),
            EasterEgg(
                id = "uncommon_retro_game",
                emoji = "👾",
                title = "Retro 2048",
                description = "Klasik 2048 karelerinin nostaljisi!",
                rarity = Rarity.UNCOMMON,
                hint = "İkinin 11. kuvveti olan o meşhur oyun sayısı: 2048.",
                triggerDescription = "Sonuç: 2048"
            ),
            EasterEgg(
                id = "uncommon_crystal_ball",
                emoji = "🔮",
                title = "Evrenin Anlamı",
                description = "Otostopçunun Galaksi Rehberi'ne göre tüm soruların cevabı.",
                rarity = Rarity.UNCOMMON,
                hint = "Hayat, evren ve her şeye dair nihai cevap: 42.",
                triggerDescription = "Sonuç: 42"
            ),

            // === COMMON (%50 Oran - 100 Puan) ===
            EasterEgg(
                id = "common_jackpot",
                emoji = "🎰",
                title = "Şanslı 777",
                description = "Casino zilleri çalıyor! Şans kapını çaldı.",
                rarity = Rarity.COMMON,
                hint = "Üç tane uğurlu 7'nin yan yana gelmesi (777).",
                triggerDescription = "Sonuç: 777"
            ),
            EasterEgg(
                id = "common_clover",
                emoji = "🍀",
                title = "Dört Yapraklı Yonca",
                description = "Bugün senin şanslı günün!",
                rarity = Rarity.COMMON,
                hint = "En basit şans sayısı: 4.",
                triggerDescription = "Sonuç: 4"
            ),
            EasterEgg(
                id = "common_pizza",
                emoji = "🍕",
                title = "Pizza Dilimi",
                description = "360 derecelik pizzayı 8 arkadaş eşit paylaştı!",
                rarity = Rarity.COMMON,
                hint = "360'ı 8'e böl (45).",
                triggerDescription = "Sonuç: 45"
            ),
            EasterEgg(
                id = "common_coffee_404",
                emoji = "☕",
                title = "Hata 404: Kahve Bulunamadı",
                description = "Biraz kahve molası verme zamanı geldi.",
                rarity = Rarity.COMMON,
                hint = "İnternetin en meşhur hata kodu (404).",
                triggerDescription = "Sonuç: 404"
            ),
            EasterEgg(
                id = "common_gravity_apple",
                emoji = "🍎",
                title = "Newton'un Elması",
                description = "Kafana bir elma düştü ve yerçekimini keşfettin!",
                rarity = Rarity.COMMON,
                hint = "Dünya'nın yerçekimi ivmesi: 9.8 veya 9.81.",
                triggerDescription = "Sonuç: 9.8 veya 9.81"
            )
        )
    }

    fun checkEasterEgg(
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
