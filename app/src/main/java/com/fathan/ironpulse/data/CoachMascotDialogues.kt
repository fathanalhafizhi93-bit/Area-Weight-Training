package com.fathan.ironpulse.data

import com.fathan.ironpulse.model.AppLanguage
import com.fathan.ironpulse.model.DayTimePhase
import com.fathan.ironpulse.model.MascotExpression
import com.fathan.ironpulse.model.MascotMood
import com.fathan.ironpulse.model.MascotSpeechLine
import com.fathan.ironpulse.model.TimePhasePose

/**
 * Disciplined, focused, no-excuses Coach personality lines across every mood,
 * time phase (Morning / Afternoon / Night), and touch interaction.
 */
internal object CoachMascotDialogues {

    fun buildSpeechPool(ctx: MascotDialogueContext): List<MascotSpeechLine> {
        fun t(id: String, en: String) = ctx.lang.t(id, en)
        val n = ctx.n
        val totalSets = ctx.totalSets
        val completedSets = ctx.completedSets
        val progressPct = ctx.progressPct
        val dayLabel = ctx.dayLabel
        val remainingSets = (totalSets - completedSets).coerceAtLeast(0)

        if (ctx.mood == MascotMood.CLAP) {
            return listOf(
                MascotSpeechLine(
                    "c-clap-1",
                    t("Set dikunci$n.", "Set locked in$n."),
                    t("$completedSets/${totalSets.coerceAtLeast(1)} set terverifikasi. Atur napas 30 detik, bersiap set berikut.", "$completedSets/${totalSets.coerceAtLeast(1)} sets logged. Reset breath, prepare the next set."),
                    MascotExpression.DETERMINED
                ),
                MascotSpeechLine(
                    "c-clap-2",
                    t("Eksekusi rapi$n.", "Clean execution$n."),
                    t("Form terkendali. Jangan biarkan fokus lepas di sela istirahat.", "Form stayed tight. Keep your mind sharp through the rest window."),
                    MascotExpression.FOCUSED
                ),
                MascotSpeechLine(
                    "c-clap-3",
                    t("Satu langkah beres$n.", "Checkpoint cleared$n."),
                    t("Tempo sudah benar. Jaga standar yang sama pada repetisi berikutnya.", "Tempo was solid. Hold that exact standard on the next set."),
                    MascotExpression.PROUD
                ),
                MascotSpeechLine(
                    "c-clap-4",
                    t("Kerja bersih, lanjut$n.", "Sharp rep work$n."),
                    t("Hindari terburu-buru — kunci kontrol eksentrik di setiap pengulangan.", "Zero rushing — own the eccentric phase on every single rep."),
                    MascotExpression.SERIOUS
                ),
                MascotSpeechLine(
                    "c-clap-5",
                    t("$completedSets set beres$n.", "$completedSets sets banked$n."),
                    t("Konsistensi diukur dari disiplin pada set-set yang terasa berat.", "Discipline shows on the sets that test your focus."),
                    MascotExpression.DETERMINED
                ),
                MascotSpeechLine(
                    "c-clap-6",
                    t("Standar terjaga$n.", "Standard upheld$n."),
                    t("Pulihkan detak napas lewat hidung. Saat bel berbunyi, langsung eksekusi.", "Control breathing through the nose. Execute the second the timer ends."),
                    MascotExpression.CALM
                ),
                MascotSpeechLine(
                    "c-clap-7",
                    t("Repetisi penuh terverifikasi$n.", "Full range verified$n."),
                    t("Tidak ada separuh gerakan. Itu cara membangun kekuatan asli.", "No half reps. That is how real strength gets built."),
                    MascotExpression.PROUD
                ),
                MascotSpeechLine(
                    "c-clap-8",
                    t("Stabilitas inti terkunci$n.", "Core tension held$n."),
                    t("Beban bergerak lurus tanpa goyah. Lanjutkan ke set berikut dengan standar sama.", "Clean bar path with zero drift. Carry that standard forward."),
                    MascotExpression.FOCUSED
                ),
                MascotSpeechLine(
                    "c-clap-9",
                    t("Disiplin tempo terjaga$n.", "Cadence discipline locked$n."),
                    t("Tiga detik turun, dorongan tegas ke atas — otot bekerja penuh.", "Controlled descent, decisive drive — maximum muscle recruitment."),
                    MascotExpression.SERIOUS
                ),
                MascotSpeechLine(
                    "c-clap-10",
                    t("Angka tercatat bersih$n.", "Numbers banked cleanly$n."),
                    t("Satu set lagi masuk buku rekor hari ini. Jangan buang fokus di jeda.", "Another working set on the ledger. Stay locked during rest."),
                    MascotExpression.SMIRK
                )
            )
        }

        if (ctx.mood == MascotMood.MILESTONE) {
            return listOf(
                MascotSpeechLine(
                    "c-mile-1",
                    t("Separuh target lewat: $progressPct%$n.", "Halfway mark cleared: $progressPct%$n."),
                    t("$completedSets/$totalSets set selesai. Jangan turunkan intensitas di paruh akhir.", "$completedSets/$totalSets sets done. Do not drop standards in the second half."),
                    MascotExpression.DETERMINED
                ),
                MascotSpeechLine(
                    "c-mile-2",
                    t("Paruh kedua dimulai$n ($progressPct%).", "Second half starts now$n ($progressPct%)."),
                    t("Tinggal $remainingSets set lagi. Di sinilah karakter latihanmu diuji.", "$remainingSets sets left. This stretch separates routine from results."),
                    MascotExpression.SERIOUS
                ),
                MascotSpeechLine(
                    "c-mile-3",
                    t("Fokus tajam menuju finis$n.", "Eyes locked on completion$n."),
                    t("Kelelahan mulai datang — justru saat teknik harus paling rapi.", "Fatigue is creeping in — that is when technique matters most."),
                    MascotExpression.FOCUSED
                ),
                MascotSpeechLine(
                    "c-mile-4",
                    t("$progressPct% tuntas$n.", "$progressPct% complete$n."),
                    t("Jangan hitung sisanya dengan ragu. Tuntaskan satu set tiap satu waktu.", "No hesitation over what remains. Execute one set at a time."),
                    MascotExpression.DETERMINED
                ),
                MascotSpeechLine(
                    "c-mile-5",
                    t("Tutup dengan kuat$n.", "Finish with authority$n."),
                    t("Amankan $remainingSets set terakhir tanpa mengorbankan rentang gerak.", "Lock down the final $remainingSets sets with full range of motion."),
                    MascotExpression.ENERGETIC
                ),
                MascotSpeechLine(
                    "c-mile-6",
                    t("Momentum penuh terkendali$n.", "Momentum under command$n."),
                    t("Teguk air seperlunya, rapatkan grip, dan tuntaskan target hari ini.", "Brief sip of water, chalk the mindset, finish today’s assignment."),
                    MascotExpression.PROUD
                ),
                MascotSpeechLine(
                    "c-mile-7",
                    t("Zona penentu dimulai ($progressPct%)$n.", "Defining stretch open ($progressPct%)$n."),
                    t("Atlet biasa melambat di sini. Kamu percepat fokus dan selesaikan tugas.", "Average lifters coast here. You sharpen posture and finish."),
                    MascotExpression.DETERMINED
                ),
                MascotSpeechLine(
                    "c-mile-8",
                    t("Sisa $remainingSets set eksekusi$n.", "$remainingSets working sets left$n."),
                    t("Jaga napas perut di tiap repetisi penutup — jangan biarkan bahu runtuh.", "Hold abdominal pressure through the closing sets — no posture collapse."),
                    MascotExpression.SERIOUS
                ),
                MascotSpeechLine(
                    "c-mile-9",
                    t("Ambang akhir terlihat jelas$n.", "Final threshold in sight$n."),
                    t("Kamu sudah melewati separuh beban hari ini. Sekarang amankan semuanya.", "You cleared the climb; now lock down the descent cleanly."),
                    MascotExpression.SMIRK
                )
            )
        }

        if (ctx.mood == MascotMood.CELEBRATE) {
            return listOf(
                MascotSpeechLine(
                    "c-cel-1",
                    t("Misi $dayLabel selesai 100%$n.", "$dayLabel mission 100% cleared$n."),
                    t("Seluruh $totalSets set tuntas. Lakukan pendinginan, minum air, dan penuhi target protein.", "All $totalSets sets complete. Cool down, rehydrate, and hit your protein."),
                    MascotExpression.PROUD
                ),
                MascotSpeechLine(
                    "c-cel-2",
                    t("Disiplin terbayar$n.", "Discipline paid off$n."),
                    t("Sesi tuntas sesuai rencana. Catat beban hari ini sebagai standar minimum pekan depan.", "Plan executed in full. Today’s numbers are next week’s baseline."),
                    MascotExpression.DETERMINED
                ),
                MascotSpeechLine(
                    "c-cel-3",
                    t("Target hari ini tuntas$n.", "Assignment completed$n."),
                    t("Kerja keras selesai di gym; pemulihan dan tidur malam adalah tahap berikutnya.", "Gym work is done; structured recovery and sleep start right now."),
                    MascotExpression.PROUD
                ),
                MascotSpeechLine(
                    "c-cel-4",
                    t("100% selesai tanpa kompromi$n.", "100% done, zero compromises$n."),
                    t("Regangkan otot utama lima menit sebelum keluar agar mobilitas tetap terjaga.", "Stretch the target muscles for five minutes before leaving."),
                    MascotExpression.EXCITED
                ),
                MascotSpeechLine(
                    "c-cel-5",
                    t("Sesi bersih, hasil nyata$n.", "Clean session, earned progress$n."),
                    t("Hari ini masuk catatan resmi. Pertahankan ritme yang sama untuk sesi berikutnya.", "Today is banked. Bring this exact focus to your next scheduled day."),
                    MascotExpression.PROUD
                ),
                MascotSpeechLine(
                    "c-cel-6",
                    t("Penutup solid$n.", "Strong finish$n."),
                    t("Tidak ada set yang ditinggalkan. Pemulihan rapi mulai sekarang.", "Not a single set left behind. Begin proper recovery now."),
                    MascotExpression.CALM
                ),
                MascotSpeechLine(
                    "c-cel-7",
                    t("Protokol $dayLabel terverifikasi penuh$n.", "$dayLabel protocol fully verified$n."),
                    t("Setiap set selesai sesuai jadwal. Hormat untuk eksekusimu hari ini.", "Every working set cleared on cue. Respect for today’s execution."),
                    MascotExpression.SMIRK
                ),
                MascotSpeechLine(
                    "c-cel-8",
                    t("Standar baru terkunci$n.", "New baseline locked$n."),
                    t("Isi cairan, santap nutrisi berkualitas, dan lindungi jam tidurmu malam ini.", "Rehydrate, feed muscle repair, and protect tonight’s sleep window."),
                    MascotExpression.PROUD
                )
            )
        }

        if (ctx.mood == MascotMood.TIMER_ACTIVE) {
            return listOf(
                MascotSpeechLine(
                    "c-tmr-1",
                    if (!ctx.activeTimerExerciseName.isNullOrBlank())
                        t("Kunci posisi — ${ctx.activeTimerExerciseName}", "Hold alignment — ${ctx.activeTimerExerciseName}")
                    else t("Waktu berjalan. Tahan posisi$n.", "Clock running. Hold your line$n."),
                    t("Perut kencang, bahu stabil, napas teratur sampai peluit akhir berbunyi.", "Core braced, shoulders packed, steady breathing until the final buzzer."),
                    MascotExpression.FOCUSED
                ),
                MascotSpeechLine(
                    "c-tmr-2",
                    t("Jangan goyah sebelum bel$n.", "Do not break before the bell$n."),
                    t("Pikiran tetap tenang saat otot bekerja — selesaikan hitungan penuh.", "Keep your head steady under tension — finish every second on the clock."),
                    MascotExpression.DETERMINED
                ),
                MascotSpeechLine(
                    "c-tmr-3",
                    t("Kontrol penuh atas waktu$n.", "Own every second$n."),
                    t("Hembuskan napas pendek dan terukur. Jangan lepaskan postur.", "Short controlled exhales. Hold your posture tight."),
                    MascotExpression.SERIOUS
                ),
                MascotSpeechLine(
                    "c-tmr-4",
                    t("Fokus pada detik terakhir$n.", "Stay sharp to zero$n."),
                    t("Detik akhir membentuk ketahanan mental. Bertahan sampai waktu habis.", "The last seconds build mental grit. Hold until time expires."),
                    MascotExpression.DETERMINED
                ),
                MascotSpeechLine(
                    "c-tmr-5",
                    t("Stabilkan inti tubuh$n.", "Lock the core$n."),
                    t("Waktu terekam otomatis setelah selesai. Fokus penuh ke ketahanan.", "Duration logs automatically upon completion. Keep tension locked."),
                    MascotExpression.FOCUSED
                ),
                MascotSpeechLine(
                    "c-tmr-6",
                    t("Tahan tanpa alasan$n.", "Hold without excuses$n."),
                    t("Tatap lurus, jaga keseimbangan — tinggal beberapa detik menuju selesai.", "Eyes forward, steady balance — finish strong through the chime."),
                    MascotExpression.SERIOUS
                ),
                MascotSpeechLine(
                    "c-tmr-7",
                    t("Ketegangan stabil hingga nol$n.", "Steady tension to zero$n."),
                    t("Jangan turunkan pinggul atau bahu sebelum sinyal selesai berbunyi.", "Do not drop hips or shoulders before the finish tone sounds."),
                    MascotExpression.DETERMINED
                ),
                MascotSpeechLine(
                    "c-tmr-8",
                    t("Napas ritmis, pikiran dingin$n.", "Rhythmic breath, cold focus$n."),
                    t("Otot boleh membakar, tetapi kendali teknikmu tidak boleh retak.", "Muscles can burn, but your technical alignment never breaks."),
                    MascotExpression.FOCUSED
                )
            )
        }

        if (ctx.mood == MascotMood.REST) {
            val rem = ctx.restRemainingFormatted
            return listOf(
                MascotSpeechLine(
                    "c-rest-1",
                    if (!rem.isNullOrBlank()) t("Istirahat Terukur: $rem", "Timed Rest: $rem")
                    else t("Istirahat Terukur Aktif", "Timed Rest Running"),
                    t("Atur napas diafragma, turunkan detak jantung, bersiap untuk set berikutnya.", "Slow diaphragmatic breaths, lower heart rate, lock in for the next set."),
                    MascotExpression.CALM
                ),
                MascotSpeechLine(
                    "c-rest-2",
                    if (!rem.isNullOrBlank()) t("Jeda Pemulihan · $rem", "Recovery Window · $rem")
                    else t("Jeda Pemulihan Antar Set", "Between-Set Recovery"),
                    t("Jangan main ponsel terlalu lama. Fokus tetap pada target beban selanjutnya.", "Stay off distractions. Keep your mind on the next working set."),
                    MascotExpression.FOCUSED
                ),
                MascotSpeechLine(
                    "c-rest-3",
                    if (!rem.isNullOrBlank()) t("Siap dalam $rem", "Back on bar in $rem")
                    else t("Persiapan Set Berikut", "Next Set Prep"),
                    t("Bayangkan posisi awal dan kunci teknik sebelum menyentuh beban.", "Visualize your starting setup and bracing cue before touching the weight."),
                    MascotExpression.SERIOUS
                ),
                MascotSpeechLine(
                    "c-rest-4",
                    if (!rem.isNullOrBlank()) t("Pulih cepat · $rem", "Resetting power · $rem")
                    else t("Pulihkan tenaga otot", "Reset muscular output"),
                    t("Teguk air secukupnya dan kembalikan konsentrasi penuh.", "Quick sip of water and bring your focus right back to zero-drift."),
                    MascotExpression.CALM
                ),
                MascotSpeechLine(
                    "c-rest-5",
                    if (!rem.isNullOrBlank()) t("Disiplin jeda · $rem", "Rest discipline · $rem")
                    else t("Jaga ritme jeda", "Maintain rest cadence"),
                    t("Istirahat terlalu singkat mengurangi tenaga; terlalu lama mendinginkan otot. Patuhi jam.", "Too short cuts power; too long cools the muscle. Obey the clock."),
                    MascotExpression.FOCUSED
                ),
                MascotSpeechLine(
                    "c-rest-6",
                    if (!rem.isNullOrBlank()) t("Bersiap eksekusi · $rem", "Stand by · $rem")
                    else t("Siap eksekusi bersih", "Ready for clean execution"),
                    t("Begitu hitungan habis, langsung mulai repetisi pertama dengan tegas.", "The moment zero hits, step in and own rep number one."),
                    MascotExpression.DETERMINED
                ),
                MascotSpeechLine(
                    "c-rest-7",
                    if (!rem.isNullOrBlank()) t("Reset saraf · $rem", "Neural reset · $rem")
                    else t("Tenangkan sistem saraf", "Reset nervous system drive"),
                    t("Tarik napas 4 detik lewat hidung, hembuskan pelan untuk memulihkan ATP.", "Inhale 4s through the nose, long exhale to restore readiness."),
                    MascotExpression.CALM
                ),
                MascotSpeechLine(
                    "c-rest-8",
                    if (!rem.isNullOrBlank()) t("Kunci target berikut · $rem", "Lock next target · $rem")
                    else t("Tinjau target set berikut", "Review next set target"),
                    t("Pastikan beban dan target repetisi sudah jelas sebelum timer menyentuh nol.", "Confirm weight and target reps cleanly before the clock reaches zero."),
                    MascotExpression.SERIOUS
                )
            )
        }

        val coachMorningBanter = listOf(
            MascotSpeechLine("c-mb-1", t("Pagi adalah ujian disiplin pertama$n.", "Morning is discipline check #1$n."), t("Latihan atau siap kerja — jangan mulai hari dengan menunda alarm.", "Show up early and set the standard before distractions wake up."), MascotExpression.DETERMINED),
            MascotSpeechLine("c-mb-2", t("Mobilisasi sendi wajib sebelum beban$n.", "Joint prep before heavy loads$n."), t("Lima menit mobilitas bahu dan pinggul menyelamatkan teknikmu sebulan penuh.", "Five minutes on shoulders and hips protects every heavy lift."), MascotExpression.FOCUSED),
            MascotSpeechLine("c-mb-3", t("Isi 500 ml air setelah bangun$n.", "500 ml water right after waking$n."), t("Dehidrasi pagi menurunkan tenaga tanpa terasa. Segarkan sistem saraf dulu.", "Morning dehydration silently cuts strength. Rehydrate before lifting."), MascotExpression.SERIOUS),
            MascotSpeechLine("c-mb-4", t("Jangan andalkan motivasi pagi$n.", "Never rely on morning mood$n."), t("Motivasi naik-turun; sistem jadwal yang sudah kamu susun adalah peganganmu.", "Motivation fluctuates; your written schedule holds the line."), MascotExpression.DETERMINED),
            MascotSpeechLine("c-mb-5", t("Cek target beban pagi ini$n.", "Review today’s numbers first$n."), t("Ketahui angka pekan lalu sebelum menyentuh set kerja pertama.", "Know last week’s log before stepping under your first working set."), MascotExpression.CURIOUS),
            MascotSpeechLine("c-mb-6", t("Mulai bersih di pagi $dayLabel$n.", "Clean start on $dayLabel morning$n."), t("Selesaikan tanggung jawab latihan lebih awal supaya sisa harimu terkendali.", "Clear your training block early and carry momentum all day."), MascotExpression.PROUD),
            MascotSpeechLine("c-mb-7", t("Pemanasan progresif, bukan buru-buru$n.", "Progressive warm-up, zero rush$n."), t("Naikkan beban bertahap di dua set awal untuk menyalakan fokus otot.", "Ramp up over two warm-up sets to prime nervous system drive."), MascotExpression.FOCUSED),
            MascotSpeechLine("c-mb-8", t("Jaga postur sejak pagi$n.", "Posture check starts now$n."), t("Bahu terbuka, kepala tegak — bawa postur atletik ke setiap gerakan.", "Chest tall, shoulders stacked — carry athletic posture into every rep."), MascotExpression.SERIOUS),
            MascotSpeechLine("c-mb-9", t("Sarapan terukur, energi stabil$n.", "Structured fuel, steady output$n."), t("Jangan masuk sesi berat dengan perut kosong tanpa persiapan hidrasi.", "Never hit intense sets dehydrated or under-fueled."), MascotExpression.CALM),
            MascotSpeechLine("c-mb-10", t("Fokus pada satu set di depanmu$n.", "Focus on the set in front of you$n."), t("Eksekusi pagi hari menentukan kualitas seluruh minggu latihanmu.", "Morning execution dictates the tone for your entire training week."), MascotExpression.FOCUSED),
            MascotSpeechLine("c-mb-11", t("Standar teknik tidak pernah libur$n.", "Form standards never take a day off$n."), t("Meski masih pagi, rentang gerak penuh tetap wajib hukumnya.", "Early hour or not, full range of motion stays mandatory."), MascotExpression.SERIOUS),
            MascotSpeechLine("c-mb-12", t("Tutup ragu, buka sesi latihan$n.", "Drop hesitation, open the session$n."), t("Langkah paling berat adalah set pertama. Setelah itu fokus mengambil alih.", "Set one is the hardest hurdle. Focus takes over right after."), MascotExpression.DETERMINED),
            MascotSpeechLine("c-mb-13", t("Atur ritme pagi sejak menit pertama$n.", "Command the morning from minute one$n."), t("Lima menit pemanasan terarah mengaktifkan koneksi otot-otak secara instan.", "Five focused warm-up minutes dial in your mind-muscle connection."), MascotExpression.ENERGETIC),
            MascotSpeechLine("c-mb-14", t("Disiplin pagi membentuk ketenangan siang$n.", "Early discipline buys midday calm$n."), t("Selesaikan target berat sebelum dunia menuntut perhatianmu.", "Finish heavy work before the rest of the day competes for your focus."), MascotExpression.PROUD)
        )

        val coachAfternoonBanter = listOf(
            MascotSpeechLine("c-ab-1", t("Jam puncak tenaga fisik tiba$n.", "Peak strength window is open$n."), t("Suhu inti tubuh siang-sore berada di titik terbaik untuk beban maksimal.", "Afternoon core temperature primes peak force output — use it wisely."), MascotExpression.ENERGETIC),
            MascotSpeechLine("c-ab-2", t("Jangan biarkan lelah kerja jadi alasan$n.", "Leave work fatigue at the door$n."), t("Empat puluh lima menit latihan terfokus mengembalikan ketajaman mental.", "Forty-five focused minutes under load restores mental sharpness."), MascotExpression.DETERMINED),
            MascotSpeechLine("c-ab-3", t("Kualitas rep mengalahkan ego$n.", "Clean reps beat ego weight$n."), t("Jika ayunan badan mengambil alih gerakan, turunkan beban dan perbaiki kontrol.", "If momentum takes over the lift, drop weight and restore control."), MascotExpression.SERIOUS),
            MascotSpeechLine("c-ab-4", t("Hidrasi siang menentukan stamina$n.", "Midday hydration governs stamina$n."), t("Konsumsi cairan teratur sebelum merasa haus agar performa tidak anjlok.", "Hydrate ahead of thirst so output stays stable to the final set."), MascotExpression.FOCUSED),
            MascotSpeechLine("c-ab-5", t("Fokus di gym, bukan di notifikasi$n.", "Focus on iron, not notifications$n."), t("Istirahat antar set cukup 60–90 detik. Pertahankan kepadatan sesi.", "Keep rests tight at 60–90 seconds. Maintain session density."), MascotExpression.SERIOUS),
            MascotSpeechLine("c-ab-6", t("Progressive overload butuh catatan presisi$n.", "Overload demands precise logs$n."), t("Tambahkan 1 repetisi bersih atau 1–2 kg ketika target pekan lalu sudah mudah.", "Add 1 clean rep or small load bump when last week’s target feels controlled."), MascotExpression.FOCUSED),
            MascotSpeechLine("c-ab-7", t("Kendalikan napas di tengah set berat$n.", "Control breath under heavy load$n."), t("Tarik ke perut, kunci inti tubuh, dorong eksplosif dengan jalur gerak lurus.", "Breathe into the brace, lock your core, drive on a clean bar path."), MascotExpression.DETERMINED),
            MascotSpeechLine("c-ab-8", t("Siang yang disiplin membentuk hasil$n.", "Afternoon discipline builds physique$n."), t("Tidak ada latihan sia-sia selama tiap set dijalankan dengan niat penuh.", "No session is wasted when every set receives full intent."), MascotExpression.PROUD),
            MascotSpeechLine("c-ab-9", t("Keringat tanpa kontrol bukan latihan$n.", "Sweat without control is noise$n."), t("Pastikan setiap gerakan mengenai otot target, bukan sekadar bergerak cepat.", "Make sure target muscles carry the load, not sloppy speed."), MascotExpression.SERIOUS),
            MascotSpeechLine("c-ab-10", t("Evaluasi cepat gerakan $dayLabel$n.", "Quick audit on $dayLabel lifts$n."), t("Periksa catatan terakhir sebelum naik ke set kerja terberatmu.", "Verify previous numbers before climbing into your top working sets."), MascotExpression.CURIOUS),
            MascotSpeechLine("c-ab-11", t("Tuntaskan sebelum sore berakhir$n.", "Clear the work before dusk$n."), t("Selesaikan daftar set di bawah agar malammu bebas untuk pemulihan total.", "Finish today’s block now so tonight belongs 100% to recovery."), MascotExpression.DETERMINED),
            MascotSpeechLine("c-ab-12", t("Konsistensi menang telak dalam 90 hari$n.", "Consistency wins over 90 days$n."), t("Satu sesi mungkin kecil; akumulasi disiplin siang ini mengubah segalanya.", "One session looks small; ninety days of disciplined work changes everything."), MascotExpression.SMIRK),
            MascotSpeechLine("c-ab-13", t("Kunci scapula, lindungi bahumu$n.", "Pack the scapula, protect shoulders$n."), t("Bahu ditarik turun dan stabil memberi fondasi dorongan jauh lebih kuat.", "Depressed, stable shoulder blades create a stronger pressing platform."), MascotExpression.FOCUSED),
            MascotSpeechLine("c-ab-14", t("Jangan menawar target di tengah sesi$n.", "Never negotiate mid-session$n."), t("Susun rencana sebelum mulai, lalu patuhi sampai set terakhir tuntas.", "Plan before stepping in, then obey that plan to the last rep."), MascotExpression.SERIOUS)
        )

        val coachNightBanter = listOf(
            MascotSpeechLine("c-nb-1", t("Tidur 7–8 jam adalah protokol wajib$n.", "7–8 hours sleep is non-negotiable$n."), t("Otot tidak tumbuh saat di gym — otot memperbaiki diri ketika kamu tidur pulas.", "Muscle doesn’t grow under the bar — it rebuilds during deep sleep."), MascotExpression.CALM),
            MascotSpeechLine("c-nb-2", t("Evaluasi malam: jujur pada hasil hari ini$n.", "Night audit: review today honestly$n."), t("Sudah tuntas sesuai target atau belum? Catat pelajarannya, bersiap untuk besok.", "Did you hit today’s mark? Log the lesson and lock in tomorrow."), MascotExpression.SERIOUS),
            MascotSpeechLine("c-nb-3", t("Latihan malam harus tetap terukur$n.", "Late sessions demand strict control$n."), t("Jika baru latihan malam ini, kurangi durasi jeda dan lakukan pendinginan rapi.", "If lifting late tonight, keep rests crisp and finish with a real cool-down."), MascotExpression.FOCUSED),
            MascotSpeechLine("c-nb-4", t("Matikan stimulan & layar terang malam ini$n.", "Cut stimulants and bright screens$n."), t("Kualitas tidur malam menentukan tenaga angkatanmu besok pagi.", "Tonight’s sleep quality decides tomorrow morning’s strength ceiling."), MascotExpression.CALM),
            MascotSpeechLine("c-nb-5", t("Siapkan perlengkapan & alarm besok$n.", "Stage tomorrow’s gear & alarm$n."), t("Atlet disiplin tidak memutuskan di pagi hari — semuanya sudah disiapkan malam ini.", "Disciplined lifters don’t negotiate at dawn — stage everything tonight."), MascotExpression.DETERMINED),
            MascotSpeechLine("c-nb-6", t("Jangan begadang merusak kerja kerasmu$n.", "Don’t let late nights sabotage hard sets$n."), t("Satu jam kurang tidur memangkas fokus dan pemulihan hormon secara nyata.", "One lost hour of sleep measurably dents recovery and neural drive."), MascotExpression.SERIOUS),
            MascotSpeechLine("c-nb-7", t("Nutrisi malam: cukupkan protein harian$n.", "Evening nutrition: close protein target$n."), t("Asupan protein bersih sebelum tidur memberi bahan baku perbaikan jaringan.", "Clean evening protein supplies overnight muscle repair."), MascotExpression.CALM),
            MascotSpeechLine("c-nb-8", t("Pendinginan lima menit wajib sebelum tidur$n.", "Five-minute mobility before bed$n."), t("Lepaskan ketegangan punggung dan pinggul supaya tidur malam lebih dalam.", "Decompress hips and upper back so sleep drops straight into deep stage."), MascotExpression.COZY),
            MascotSpeechLine("c-nb-9", t("Tenang bukan berarti lengah$n.", "Calm never means complacent$n."), t("Nikmati malam dengan teratur, tidur tepat waktu, dan bangun lebih siap.", "Keep the evening structured, hit lights-out on time, wake up sharp."), MascotExpression.CALM),
            MascotSpeechLine("c-nb-10", t("Disiplin pemulihan sama beratnya dengan latihan$n.", "Recovery discipline equals training discipline$n."), t("Siapapun bisa memaksa angkat berat; hanya atlet serius yang disiplin beristirahat.", "Anyone can chase heavy weight; serious athletes master structured rest."), MascotExpression.PROUD),
            MascotSpeechLine("c-nb-11", t("Tutup hari $dayLabel dengan tuntas$n.", "Close $dayLabel with order$n."), t("Periksa jadwal besok sekilas lalu hentikan aktivitas layar.", "Confirm tomorrow’s schedule once, then step away from screens."), MascotExpression.CALM),
            MascotSpeechLine("c-nb-12", t("Hidrasi tipis sebelum istirahat$n.", "Light hydration before lights out$n."), t("Segelas kecil air membantu keseimbangan cairan tanpa mengganggu tidur.", "A modest sip keeps hydration balanced without interrupting sleep."), MascotExpression.COZY),
            MascotSpeechLine("c-nb-13", t("Jangan bawa stres hari ini ke bantal$n.", "Leave today’s noise off the pillow$n."), t("Tarik napas 4 detik, buang 6 detik — turunkan detak jantung sebelum tidur.", "Four-second inhale, six-second exhale — drop heart rate before sleep."), MascotExpression.SLEEPY),
            MascotSpeechLine("c-nb-14", t("Bangun besok dengan target jelas$n.", "Wake up tomorrow with clear orders$n."), t("Konsistensi malam hari menjaga rantai latihan tetap tak terputus.", "Evening discipline protects the chain so tomorrow executes automatically."), MascotExpression.DETERMINED)
        )

        if (ctx.timePhase == DayTimePhase.MORNING) {
            val poseLines = when (ctx.activeTimePose) {
                TimePhasePose.TEA -> listOf(
                    MascotSpeechLine("c-m-tea-1", t("Hidrasi hangat, fokus mulai naik$n.", "Warm hydration, sharp morning mind$n."), t("Isi cairan hangat secukupnya lalu langsung masuk ke daftar gerakan pagi.", "Warm fluids first, then step straight into morning mobility."), MascotExpression.CALM),
                    MascotSpeechLine("c-m-tea-2", t("Tenang tapi siaga di pagi $dayLabel$n.", "Calm and ready on $dayLabel morning$n."), t("Ketenangan pagi adalah fondasi teknik yang presisi.", "Morning composure translates directly into crisp bar control."), MascotExpression.FOCUSED),
                    MascotSpeechLine("c-m-tea-3", t("Jangan berlama-lama di cangkir$n.", "Do not linger too long$n."), t("Teguk selesai, sendi mulai dipanaskan dalam lima menit.", "Finish the cup, start mobilizing joints within five minutes."), MascotExpression.SERIOUS)
                )
                TimePhasePose.COFFEE -> listOf(
                    MascotSpeechLine("c-m-cof-1", t("Kafein masuk — fokus kunci target$n.", "Caffeine active — lock onto target$n."), t("Imbangi dengan air putih dan gunakan energi ini untuk set kerja bersih.", "Match it with water and channel that focus into clean working sets."), MascotExpression.ENERGETIC),
                    MascotSpeechLine("c-m-cof-2", t("Kesiapan sistem saraf optimal$n.", "Central drive switched on$n."), t("Mata tajam, rencana jelas. Eksekusi gerakan pertama sesuai urutan.", "Eyes sharp, plan locked. Execute exercise one cleanly."), MascotExpression.FOCUSED),
                    MascotSpeechLine("c-m-cof-3", t("Energi pagi tidak untuk disia-siakan$n.", "Morning drive is fuel, not hype$n."), t("Ubah dorongan kafein menjadi tempo repetisi yang terukur.", "Turn that boost into controlled rep speed, not sloppy rushing."), MascotExpression.DETERMINED)
                )
                TimePhasePose.STRETCH -> listOf(
                    MascotSpeechLine("c-m-str-1", t("Buka bahu & mobilitas sendi dulu$n.", "Open shoulders & joints first$n."), t("Sendi kaku menghasilkan gerak pincang. Panaskan dua menit sebelum angkat beban.", "Stiff joints compromise mechanics. Spend two minutes warming up right."), MascotExpression.FOCUSED),
                    MascotSpeechLine("c-m-str-2", t("Rentang gerak penuh dimulai di sini$n.", "Full range of motion starts here$n."), t("Putar pinggul, punggung atas, dan pergelangan sebelum set pertama.", "Mobilize hips, thoracic spine, and wrists before load one."), MascotExpression.SERIOUS),
                    MascotSpeechLine("c-m-str-3", t("Persiapan rapi mencegah cedera$n.", "Clean prep prevents setbacks$n."), t("Atlet yang bertahan lama selalu menghormati pemanasan.", "Lifters with longevity never skip structured prep."), MascotExpression.CALM)
                )
                TimePhasePose.WINK -> listOf(
                    MascotSpeechLine("c-m-wnk-1", t("Target hari ini bisa ditaklukkan$n.", "Today’s target is within reach$n."), t("Bukan soal keberuntungan — ini soal kesiapan dan eksekusi.", "Not luck — pure preparation and execution."), MascotExpression.SMIRK),
                    MascotSpeechLine("c-m-wnk-2", t("Satu set lebih baik dari pekan lalu$n.", "Beat last week by one clean notch$n."), t("Kejar progres terukur pada kontrol atau beban hari ini.", "Chase measurable progress in control or load today."), MascotExpression.DETERMINED),
                    MascotSpeechLine("c-m-wnk-3", t("Aku awasi standarnya hari ini$n.", "I’m watching the standard today$n."), t("Tunjukkan disiplin yang sama dari set pertama hingga penutup.", "Bring identical focus from warm-up to the final set."), MascotExpression.TEASING)
                )
                TimePhasePose.SUNRISE -> listOf(
                    MascotSpeechLine("c-m-sun-1", t("Fajar datang, jadwal memanggil$n.", "Dawn broke, schedule is waiting$n."), t("Bangun lebih awal memberi keunggulan disiplin atas seluruh harimu.", "Early discipline sets the pace for your entire day."), MascotExpression.PROUD),
                    MascotSpeechLine("c-m-sun-2", t("Cahaya pagi menyalakan ritme tubuh$n.", "Morning light sets circadian drive$n."), t("Dapatkan sinar pagi singkat lalu tuntaskan target gerakmu.", "Grab brief morning light and attack today’s session."), MascotExpression.ENERGETIC),
                    MascotSpeechLine("c-m-sun-3", t("Hari baru, standar tetap tinggi$n.", "New day, same high standard$n."), t("Jangan bawa kemalasan kemarin ke pagi $dayLabel ini.", "Leave yesterday’s fatigue behind on $dayLabel morning."), MascotExpression.DETERMINED)
                )
                TimePhasePose.PROTEIN -> listOf(
                    MascotSpeechLine("c-m-pro-1", t("Bahan bakar protein terukur$n.", "Protein target on schedule$n."), t("Latihan keras tanpa nutrisi memadai hanyalah setengah pekerjaan.", "Hard sets without protein fuel leave half your progress on the table."), MascotExpression.SERIOUS),
                    MascotSpeechLine("c-m-pro-2", t("Siapkan mesin sebelum kerja berat$n.", "Fuel the body before heavy work$n."), t("Protein cukup menjaga massa otot tetap solid sepanjang hari.", "Steady morning protein protects strength and lean recovery."), MascotExpression.FOCUSED),
                    MascotSpeechLine("c-m-pro-3", t("Disiplin dapur melengkapi disiplin gym$n.", "Kitchen discipline backs gym discipline$n."), t("Atur asupan dari pagi agar pemulihan malam tidak tertinggal.", "Lock in nutrition early so recovery never falls behind."), MascotExpression.PROUD)
                )
                TimePhasePose.SALUTE -> listOf(
                    MascotSpeechLine("c-m-sal-1", t("Siap di pos pagi$n. Fokus penuh.", "On post early$n. Full focus."), t("Jadwal $dayLabel menunggu eksekusi. Jangan tunda set pembuka.", "$dayLabel’s block awaits execution. Step into set one."), MascotExpression.DETERMINED),
                    MascotSpeechLine("c-m-sal-2", t("Hormat pada konsistensimu pagi ini$n.", "Respect for showing up on time$n."), t("Tepati janji pada dirimu sendiri sebelum dunia menuntut waktumu.", "Honor your training commitment before daily noise starts."), MascotExpression.PROUD),
                    MascotSpeechLine("c-m-sal-3", t("Barisan target siap dieksekusi$n.", "Targets lined up for execution$n."), t("Cek daftar gerakan, kunci beban target, dan mulai hitungannya.", "Check the lineup, confirm target loads, begin."), MascotExpression.FOCUSED)
                )
                TimePhasePose.SPARKLE -> listOf(
                    MascotSpeechLine("c-m-spk-1", t("Ketajaman mental level puncak pagi ini$n.", "Peak mental sharpness this morning$n."), t("Manfaatkan kejernihan ini untuk memecahkan rekor repetisi rapi.", "Channel this clarity into crisp, textbook reps."), MascotExpression.EXCITED),
                    MascotSpeechLine("c-m-spk-2", t("Fokus menyala — jangan kendor$n.", "Focus locked — hold intensity$n."), t("Kualitas awal pagi yang tajam selalu berbuah hasil nyata di riwayat.", "Sharp morning execution compounds fast in your history."), MascotExpression.DETERMINED),
                    MascotSpeechLine("c-m-spk-3", t("Energi bersih siap dipakai$n.", "Clean energy ready to deploy$n."), t("Arahkan seluruh dorongan ini ke teknik dan kontrol beban.", "Direct every ounce of energy into clean technique."), MascotExpression.ENERGETIC)
                )
                else -> listOf(
                    MascotSpeechLine(
                        "c-m-prk-1",
                        t("Pagi disiplin di $dayLabel$n.", "Disciplined $dayLabel morning$n."),
                        when {
                            ctx.mood == MascotMood.RESTDAY -> t("Hari istirahat terprogram. Lakukan mobilitas ringan, tetap jaga hidrasi dan protein.", "Scheduled rest day. Light mobility, strict hydration, hit protein.")
                            ctx.allCompleted -> t("Seluruh $totalSets set beres pagi ini. Standar eksekusi patut diapresiasi.", "All $totalSets sets cleared early. Textbook execution.")
                            else -> t("Rencana $dayLabel siap. Pemanasan lima menit, lalu langsung kerja bersih.", "$dayLabel plan is staged. Five-minute warm-up, then clean work.")
                        },
                        MascotExpression.FOCUSED
                    ),
                    MascotSpeechLine("c-m-prk-2", t("Tatap jadwal, mulai bergerak$n.", "Read the plan, begin work$n."), t("Tidak ada alasan menunda saat rencana sudah jelas di depan mata.", "Zero reason to delay when the plan is already laid out."), MascotExpression.DETERMINED),
                    MascotSpeechLine("c-m-prk-3", t("Coach memantau ritme pagimu$n.", "Coach tracking your morning pace$n."), t("Buka hari dengan sikap tegas dan langkah terstruktur.", "Open the day with poise and structured effort."), MascotExpression.SERIOUS)
                )
            }
            return poseLines + coachMorningBanter
        }

        if (ctx.timePhase == DayTimePhase.AFTERNOON) {
            val poseLines = when (ctx.activeTimePose) {
                TimePhasePose.HYDRATE -> listOf(
                    MascotSpeechLine("c-a-hyd-1", t("Cek hidrasi wajib siang ini$n.", "Mandatory midday hydration check$n."), t("Jangan tunggu tenggorokan kering. Penuhi cairan sebelum sesi utama.", "Never wait for thirst. Hydrate well before your working sets."), MascotExpression.SERIOUS),
                    MascotSpeechLine("c-a-hyd-2", t("Tenaga butuh air & elektrolit$n.", "Strength relies on fluids$n."), t("Kehilangan cairan sedikit saja memangkas daya ledak otot.", "Even slight dehydration drops explosive force output."), MascotExpression.FOCUSED),
                    MascotSpeechLine("c-a-hyd-3", t("Isi ulang botol minummu$n.", "Refill that water bottle$n."), t("Kembali ke lantai latihan dengan fokus jernih dan tubuh terhidrasi.", "Step back under load sharp and hydrated."), MascotExpression.CALM)
                )
                TimePhasePose.SHADES -> listOf(
                    MascotSpeechLine("c-a-shd-1", t("Tetap dingin di bawah tekanan beban$n.", "Stay composed under heavy load$n."), t("Ekspresi tenang, inti tubuh terkunci — selesaikan set tanpa panik.", "Calm expression, braced core — complete the set without panic."), MascotExpression.SMIRK),
                    MascotSpeechLine("c-a-shd-2", t("Siang tenang, fokus mematikan$n.", "Composed midday killer focus$n."), t("Jangan terpengaruh keramaian gym; jalankan programmu sendiri.", "Ignore gym noise; execute your own written progression."), MascotExpression.FOCUSED),
                    MascotSpeechLine("c-a-shd-3", t("Kontrol emosi menjaga teknik$n.", "Composure protects technique$n."), t("Dingin di kepala, panas di otot kerja.", "Cool head, focused tension in the target muscle."), MascotExpression.CALM)
                )
                TimePhasePose.FLEX -> listOf(
                    MascotSpeechLine("c-a-flx-1", t("Saatnya uji kekuatan terukur$n.", "Time to test true strength$n."), t("Siang hari suhu otot optimal — manfaatkan untuk set beban utama.", "Muscle temperature peaks midday — deliver clean power on top sets."), MascotExpression.ENERGETIC),
                    MascotSpeechLine("c-a-flx-2", t("Kunci teknik sebelum tambah pelat$n.", "Earn the weight with form$n."), t("Beban lebih berat hanya sah jika rentang gerak tetap utuh.", "Heavier weight only counts when range of motion stays complete."), MascotExpression.SERIOUS),
                    MascotSpeechLine("c-a-flx-3", t("Dorong penuh, tahan saat turun$n.", "Drive strong, control down$n."), t("Kontrol fase turun dua detik membangun otot jauh lebih efektif.", "Two-second controlled lowering builds real strength and size."), MascotExpression.DETERMINED)
                )
                TimePhasePose.MUSIC -> listOf(
                    MascotSpeechLine("c-a-mus-1", t("Pasang ritme kerja yang konstan$n.", "Lock into working tempo$n."), t("Biarkan tempo musik menjaga jeda istirahatmu tetap disiplin.", "Use the beat to keep rest intervals tight and transitions sharp."), MascotExpression.FOCUSED),
                    MascotSpeechLine("c-a-mus-2", t("Zona fokus aktif$n.", "Focus zone engaged$n."), t("Putus gangguan luar — tinggal kamu, barbel, dan hitungan repetisi.", "Shut out external noise — just you, the load, and clean reps."), MascotExpression.DETERMINED),
                    MascotSpeechLine("c-a-mus-3", t("Irama mantap menjaga daya tahan$n.", "Steady cadence holds stamina$n."), t("Jangan biarkan jeda melar lebih dari target waktu.", "Don’t let rest periods drift past your clock."), MascotExpression.SERIOUS)
                )
                TimePhasePose.BEAST -> listOf(
                    MascotSpeechLine("c-a-bst-1", t("Intensitas penuh, teknik tetap tajam$n.", "Full intensity, razor-sharp form$n."), t("Agresif saat mendorong, disiplin saat mengontrol kembali beban.", "Explosive on the press, disciplined on the return."), MascotExpression.EXCITED),
                    MascotSpeechLine("c-a-bst-2", t("Ini waktunya set penentu$n.", "Time for the defining sets$n."), t("Jangan sisakan keraguan pada dua repetisi terakhir.", "Leave zero hesitation on the final two reps."), MascotExpression.DETERMINED),
                    MascotSpeechLine("c-a-bst-3", t("Tunjukkan ketangguhanmu di $dayLabel$n.", "Prove your grit on $dayLabel$n."), t("Saat otot terbakar, justru di situlah adaptasi terjadi.", "When the muscle burns, hold posture — that is where adaptation lives."), MascotExpression.ENERGETIC)
                )
                TimePhasePose.SWEAT -> listOf(
                    MascotSpeechLine("c-a-swt-1", t("Keringat bukti sesi berjalan benar$n.", "Sweat confirms real work$n."), t("Lap keringat, atur napas lewat hidung, lanjut set berikut tepat waktu.", "Wipe the brow, reset nasal breathing, hit the next set on time."), MascotExpression.PROUD),
                    MascotSpeechLine("c-a-swt-2", t("Intensitas terjaga dengan baik$n.", "Strong intensity maintained$n."), t("Pastikan setiap set tetap rapi meski detak jantung naik.", "Keep mechanics clean even as heart rate climbs."), MascotExpression.FOCUSED),
                    MascotSpeechLine("c-a-swt-3", t("Kerja kerasmu tercatat nyata$n.", "Hard work stays on record$n."), t("Jangan berhenti di tengah jalan — selesaikan seluruh rencana hari ini.", "Don’t stall halfway — clear every scheduled movement."), MascotExpression.DETERMINED)
                )
                TimePhasePose.SMIRK -> listOf(
                    MascotSpeechLine("c-a-smk-1", t("Beban itu mulai terasa ringan?$n", "Load starting to feel light?$n"), t("Bagus. Jika seluruh rep bersih, saatnya tambah beban tipis pekan depan.", "Good. If every rep stayed strict, bump weight next week."), MascotExpression.SMIRK),
                    MascotSpeechLine("c-a-smk-2", t("Tidak ada alasan yang bisa mengalahkanmu$n.", "No excuse stopped you today$n."), t("Mayoritas menunda di jam segini — kamu memilih menuntaskan jadwal.", "Most skip at this hour — you showed up to execute."), MascotExpression.PROUD),
                    MascotSpeechLine("c-a-smk-3", t("Standar meningkat diam-diam$n.", "Quietly raising your baseline$n."), t("Terus catat progres dengan jujur; angka tidak pernah berbohong.", "Keep logging honestly; the numbers never lie."), MascotExpression.TEASING)
                )
                TimePhasePose.WHISTLE -> listOf(
                    MascotSpeechLine("c-a-whl-1", t("Peluit berbunyi — masuk posisi!$n", "Whistle’s blown — get set!$n."), t("Kurangi jeda ngobrol, langsung kunci posisi awal untuk set berikutnya.", "Cut chatter short and step into position for the next set."), MascotExpression.ENERGETIC),
                    MascotSpeechLine("c-a-whl-2", t("Jaga tempo sesi tetap padat$n.", "Keep session density tight$n."), t("Sesi 45 menit terfokus selalu mengalahkan dua jam penuh gangguan.", "Forty-five tight minutes beat two distracted hours every time."), MascotExpression.SERIOUS),
                    MascotSpeechLine("c-a-whl-3", t("Panggilan fokus siang hari$n.", "Midday focus call$n."), t("Luruskan punggung, kencangkan inti, eksekusi satu set bersih sekarang.", "Spine neutral, core braced, execute one crisp set right now."), MascotExpression.DETERMINED)
                )
                else -> listOf(
                    MascotSpeechLine("c-a-cur-1", t("Audit fokus siang $dayLabel$n.", "$dayLabel afternoon focus audit$n."), if (ctx.allCompleted) t("Target $dayLabel sudah 100%. Jaga nutrisi dan hidrasi sisa hari.", "$dayLabel is 100% cleared. Lock down nutrition and hydration now.") else t("Waktu masih cukup — tuntaskan set yang terjadwal tanpa menunda.", "Window is open — clear your scheduled sets without delay."), MascotExpression.CURIOUS),
                    MascotSpeechLine("c-a-cur-2", t("Coach mengecek ketepatan jadwalmu$n.", "Checking your schedule compliance$n."), t("Buka latihan di bawah dan pastikan setiap set terisi akurat.", "Open the workout block below and log every working set accurately."), MascotExpression.FOCUSED),
                    MascotSpeechLine("c-a-cur-3", t("Jangan biarkan sore lewat kosong$n.", "Don’t let the afternoon slip$n."), t("Disiplin bukan hanya saat bersemangat — terutama saat jadwal padat.", "Discipline isn’t only for easy days — it shines on busy afternoons."), MascotExpression.SERIOUS)
                )
            }
            return poseLines + coachAfternoonBanter
        }

        // NIGHT POSES (COACH)
        val nightPoseLines = when (ctx.activeTimePose) {
            TimePhasePose.SLEEP -> listOf(
                MascotSpeechLine("c-n-slp-1", t("Protokol tidur malam aktif$n.", "Night sleep protocol engaged$n."), if (ctx.allCompleted) t("Latihan sudah tuntas. Tidur berkualitas sekarang untuk memaksimalkan pemulihan otot.", "Work is done. Prioritize deep sleep now so recovery peaks.") else t("Jam malam semakin larut. Segera selesaikan atau rapikan istirahat untuk besok.", "Hour is late. Either wrap cleanly now or secure rest for tomorrow."), MascotExpression.SLEEPY),
                MascotSpeechLine("c-n-slp-2", t("Pemulihan adalah perintah, bukan pilihan$n.", "Recovery is an order, not optional$n."), t("Hentikan aktivitas berat dan tidur 7–8 jam penuh malam ini.", "Power down heavy distractions and lock in 7–8 hours of real sleep."), MascotExpression.CALM),
                MascotSpeechLine("c-n-slp-3", t("Simpan kekuatan untuk fajar$n.", "Bank strength for dawn$n."), t("Disiplin malam memastikan tubuh siap saat alarm berbunyi besok.", "Night discipline ensures your body answers tomorrow’s alarm at 100%."), MascotExpression.SLEEPY)
            )
            TimePhasePose.YAWN -> listOf(
                MascotSpeechLine("c-n-ywn-1", t("Jam malam. Cek kondisi tubuhmu$n.", "Late watch. Check readiness$n."), if (ctx.allCompleted) t("Semua set sudah aman tercatat. Matikan layar dan tidur tepat waktu.", "All sets logged cleanly. Step off screens and sleep on cue.") else t("Jika baru sempat latihan malam, jaga teknik tetap disiplin lalu segera pendinginan.", "If lifting late, keep strict form, cut junk volume, and cool down properly."), MascotExpression.SLEEPY),
                MascotSpeechLine("c-n-ywn-2", t("Jangan tukar jam tidur dengan begadang$n.", "Never trade sleep for scrolling$n."), t("Otot butuh malam yang tenang untuk memperbaiki serat yang lelah.", "Tired muscle fibers require quiet darkness to rebuild stronger."), MascotExpression.SERIOUS),
                MascotSpeechLine("c-n-ywn-3", t("Pengawasan malam Coach aktif$n.", "Night Coach watch active$n."), t("Tuntaskan urusan hari ini dengan cepat dan kembali ke jadwal tidur.", "Wrap today cleanly and get back on your sleep window."), MascotExpression.CALM)
            )
            TimePhasePose.STARGAZE -> listOf(
                MascotSpeechLine("c-n-stg-1", t("Evaluasi objektif malam $dayLabel$n.", "Objective night review for $dayLabel$n."), if (ctx.allCompleted) t("Target tuntas penuh. Banggalah sejenak, lalu fokus ke pemulihan.", "Full target met. Acknowledge the win, then lock in recovery.") else t("Evaluasi apa yang menghambat hari ini dan perbaiki sistem besok.", "Spot what slowed today down and fix the plan for tomorrow."), MascotExpression.CURIOUS),
                MascotSpeechLine("c-n-stg-2", t("Garis kemajuan dilihat dalam jangka panjang$n.", "Progress is measured over months$n."), t("Satu malam konsisten menjaga pola tidur jauh lebih bernilai daripada begadang.", "Steady night routines outlast short bursts of motivation."), MascotExpression.CALM),
                MascotSpeechLine("c-n-stg-3", t("Kunci alarm besok sebelum tidur$n.", "Lock tomorrow’s alarm tonight$n."), t("Persiapan malam menghapus keraguan pagi hari.", "Night preparation eliminates morning excuses."), MascotExpression.FOCUSED)
            )
            TimePhasePose.ZEN -> listOf(
                MascotSpeechLine("c-n-zen-1", t("Turunkan ketegangan sistem saraf$n.", "Down-regulate nervous system$n."), t("Tarik napas 4 detik, buang 6 detik — turunkan detak jantung ke mode pemulihan.", "Inhale 4s, exhale 6s — shift your nervous system into recovery mode."), MascotExpression.CALM),
                MascotSpeechLine("c-n-zen-2", t("Relaksasi terstruktur malam ini$n.", "Structured evening wind-down$n."), t("Otot yang rileks pulih lebih cepat daripada otot yang terus tegang oleh stres.", "Relaxed tissue repairs faster than stress-locked muscle."), MascotExpression.COZY),
                MascotSpeechLine("c-n-zen-3", t("Disiplin tenang menutup hari$n.", "Quiet discipline closes the day$n."), t("Kendalikan napas, tenangkan pikiran, siapkan tubuh untuk tidur pulas.", "Control breath, quiet mind, prepare for deep sleep."), MascotExpression.CALM)
            )
            TimePhasePose.COZY -> listOf(
                MascotSpeechLine("c-n-czy-1", t("Mode pemulihan malam terpantau$n.", "Night recovery stance held$n."), t("Pastikan ruangan sejuk, gelap, dan bebas gangguan sebelum tidur.", "Cool room, low light, zero phone scrolling before lights out."), MascotExpression.COZY),
                MascotSpeechLine("c-n-czy-2", t("Istirahat berkualitas itu terhormat$n.", "Earned rest is disciplined$n."), t("Setelah kerja bersih hari ini, tubuh berhak atas tidur tanpa gangguan.", "After clean work today, your body earned uninterrupted sleep."), MascotExpression.PROUD),
                MascotSpeechLine("c-n-czy-3", t("Jangan lupa hidrasi secukupnya$n.", "Modest night hydration$n."), t("Siapkan air minum untuk pagi hari di samping tempat tidur.", "Stage tomorrow’s morning water glass before turning in."), MascotExpression.CALM)
            )
            TimePhasePose.DREAM -> listOf(
                MascotSpeechLine("c-n-drm-1", t("Konsolidasi teknik saat tidur$n.", "Motor memory locks during sleep$n."), t("Gerakan rapi yang kamu latih hari ini dikunci oleh otak selama tidur dalam.", "Clean reps practiced today are wired permanently during deep sleep."), MascotExpression.CALM),
                MascotSpeechLine("c-n-drm-2", t("Target pekan depan sudah menanti$n.", "Next week’s targets are waiting$n."), t("Tidur cukup memastikan kamu bangun siap mengangkat standar.", "Solid sleep ensures you wake ready to raise the baseline."), MascotExpression.DETERMINED),
                MascotSpeechLine("c-n-drm-3", t("Tutup mata, pulihkan tenaga 100%$n.", "Close your eyes, recover 100%$n."), t("Besok sistem alarm akan menggerakkanmu tepat pada waktunya.", "Tomorrow’s alarm puts you back on schedule right on time."), MascotExpression.SLEEPY)
            )
            TimePhasePose.MOONLIT -> listOf(
                MascotSpeechLine("c-n-mnl-1", t("Patroli disiplin malam hari$n.", "Night watch discipline check$n."), t("Jika latihan di jam malam, utamakan kendali penuh tanpa memaksakan ego.", "If training under night skies, prioritize strict control over ego."), MascotExpression.FOCUSED),
                MascotSpeechLine("c-n-mnl-2", t("Malam sunyi menguji konsistensi$n.", "Quiet nights test consistency$n."), t("Tetap jalankan kebiasaan baik walau tidak ada yang melihat.", "Hold your habits sharp even when nobody is watching."), MascotExpression.SERIOUS),
                MascotSpeechLine("c-n-mnl-3", t("Pendinginan dan nutrisi malam aman?$n", "Cooldown and evening nutrition set?$n"), t("Pastikan kedua pilar itu selesai sebelum tidur malam ini.", "Confirm both pillars are handled before turning in tonight."), MascotExpression.CURIOUS)
            )
            TimePhasePose.RECHARGE -> listOf(
                MascotSpeechLine("c-n-rch-1", t("Pengisian ulang kapasitas penuh$n.", "Full capacity recharge underway$n."), t("Jangan potong durasi tidurmu jika ingin beban minggu depan naik.", "Do not shortchange sleep if you expect weights to climb next week."), MascotExpression.CALM),
                MascotSpeechLine("c-n-rch-2", t("Sistem pemulihan bekerja malam ini$n.", "Overnight rebuild protocol active$n."), t("Istirahat total memberi bahan bakar bersih untuk set pembuka besok.", "Complete rest fuels tomorrow’s very first working set."), MascotExpression.PROUD),
                MascotSpeechLine("c-n-rch-3", t("Jaga baterai tubuh di 100%$n.", "Keep body battery at 100%$n."), t("Atlet sejati menghormati jam tidur seketat jadwal latihannya.", "Real athletes guard sleep hours as fiercely as gym hours."), MascotExpression.DETERMINED)
            )
            else -> listOf(
                MascotSpeechLine(
                    "c-n-chl-1",
                    t("Malam tenang di $dayLabel$n.", "Orderly $dayLabel night$n."),
                    when {
                        ctx.mood == MascotMood.RESTDAY -> t("Malam hari istirahat selesai dengan rapi. Jaga tidur 7–8 jam untuk sesi besok.", "Rest day evening handled well. Lock in 7–8 hours sleep for tomorrow.")
                        ctx.allCompleted -> t("Semua $totalSets set tuntas. Tutup malam dengan peregangan singkat dan tidur tepat waktu.", "All $totalSets sets done. Close with light stretching and timely sleep.")
                        else -> t("Kalau tetap berlatih malam ini, eksekusi bersih dan segera pendinginan.", "If training late tonight, keep execution crisp and cool down promptly.")
                    },
                    MascotExpression.CALM
                ),
                MascotSpeechLine("c-n-chl-2", t("Penutupan malam terkendali$n.", "Controlled night wind-down$n."), t("Evaluasi singkat, rapikan perlengkapan besok, lalu istirahat penuh.", "Brief review, stage tomorrow’s gear, then get full sleep."), MascotExpression.COZY),
                MascotSpeechLine("c-n-chl-3", t("Coach tetap siaga mengawal targetmu$n.", "Coach standing watch over your plan$n."), t("Istirahat cukup malam ini agar besok langsung siap tempur.", "Rest deeply tonight so tomorrow starts at peak readiness."), MascotExpression.DETERMINED)
            )
        }
        return nightPoseLines + coachNightBanter
    }

    fun buildComboPokePool(lang: AppLanguage, n: String, comboCount: Int): List<MascotSpeechLine> {
        fun t(id: String, en: String) = lang.t(id, en)
        return listOf(
            MascotSpeechLine(
                "c-combo-1",
                t("Cukup uji refleksnya$n. (x$comboCount)", "Enough reflex taps$n. (x$comboCount)"),
                t("Arahkan kecepatan tangan itu ke barbel dan set kerjamu sekarang.", "Channel that hand speed into your grip and working sets."),
                MascotExpression.SERIOUS
            ),
            MascotSpeechLine(
                "c-combo-2",
                t("Fokus tetap tajam$n! (x$comboCount)", "Hold your focus$n! (x$comboCount)"),
                t("Energi berlebih bagus — tapi pastikan keluar di repetisi terakhir, bukan layar.", "Extra energy is welcome — spend it on the final reps, not the screen."),
                MascotExpression.DETERMINED
            ),
            MascotSpeechLine(
                "c-combo-3",
                t("Siap tempur x$comboCount$n.", "Locked & ready x$comboCount$n."),
                t("Coach tidak butuh pemanasan di layar. Mulai gerakan pertama dengan disiplin!", "Coach doesn’t need screen taps. Start exercise one with precision!"),
                MascotExpression.SMIRK
            ),
            MascotSpeechLine(
                "c-combo-4",
                t("Kecepatan tangan bagus$n (x$comboCount).", "Sharp hand speed$n (x$comboCount)."),
                t("Sekarang buktikan kecepatan yang sama saat menutup seluruh set terjadwal.", "Now prove the same urgency clearing every scheduled set."),
                MascotExpression.TEASING
            ),
            MascotSpeechLine(
                "c-combo-5",
                t("Jari sudah panas, x$comboCount$n!", "Fingers warmed up, x$comboCount$n!"),
                t("Saatnya angkat beban sungguhan. Tidak ada rekor yang tercipta di menu utama.", "Time for real iron. No personal records get set on the home card."),
                MascotExpression.ENERGETIC
            )
        )
    }

    fun buildPettingPool(lang: AppLanguage, n: String): List<MascotSpeechLine> {
        fun t(id: String, en: String) = lang.t(id, en)
        return listOf(
            MascotSpeechLine(
                "c-pet-1",
                t("Hmm... baiklah$n.", "Hmm... understood$n."),
                t("Aku hargai respeknya. Jaga ketenangan itu dan bawa ke set berikutnya.", "I respect the nod. Keep that composure and bring it to your next set."),
                MascotExpression.CALM
            ),
            MascotSpeechLine(
                "c-pet-2",
                t("Kerja bagus sejauh ini$n.", "Solid work so far$n."),
                t("Tarik napas sebentar, lalu kembali ke fokus penuh. Kita belum selesai.", "Take one steady breath, then lock right back in. We’re not done."),
                MascotExpression.PROUD
            ),
            MascotSpeechLine(
                "c-pet-3",
                t("Satu tim sampai tuntas$n.", "Same team until done$n."),
                t("Tegas bukan berarti jauh — aku di sudutmu memastikan targetmu tercapai.", "Strict doesn’t mean distant — I’m in your corner until the work is finished."),
                MascotExpression.COZY
            ),
            MascotSpeechLine(
                "c-pet-4",
                t("Respek diterima$n.", "Respect acknowledged$n."),
                t("Ketenangan seperti ini penting sebelum menyentuh beban puncakmu.", "Quiet composure like this matters right before hitting top sets."),
                MascotExpression.CALM
            ),
            MascotSpeechLine(
                "c-pet-5",
                t("Tetap tenang, tetap kuat$n.", "Stay steady, stay strong$n."),
                t("Aku mengawal progresmu setiap hari — mari tuntaskan hari ini tanpa cela.", "Watching your numbers climb every week — let’s finish clean today."),
                MascotExpression.PROUD
            )
        )
    }

    fun buildQuickTipPool(lang: AppLanguage, n: String): List<MascotSpeechLine> {
        fun t(id: String, en: String) = lang.t(id, en)
        return listOf(
            MascotSpeechLine(
                "c-tip-1",
                t("Protokol Coach$n: Kontrol Eksentrik", "Coach Protocol$n: Control the Descent"),
                t("Jangan jatuhkan beban — kendalikan fase turun 2–3 detik untuk stimulus dan keamanan sendi.", "Never drop the load — own a 2–3 second descent for max stimulus and joint safety."),
                MascotExpression.FOCUSED
            ),
            MascotSpeechLine(
                "c-tip-2",
                t("Protokol Coach$n: Bracing Perut", "Coach Protocol$n: Core Bracing"),
                t("Kunci napas ke rongga perut 360 derajat sebelum repetisi dimulai, baru dorong penuh.", "Lock 360-degree abdominal pressure before starting the rep, then drive cleanly."),
                MascotExpression.DETERMINED
            ),
            MascotSpeechLine(
                "c-tip-3",
                t("Protokol Coach$n: Disiplin Istirahat", "Coach Protocol$n: Clock Discipline"),
                t("Patuhi timer istirahat antar set. Kepadatan waktu menjaga fokus dan kualitas hipertrofi.", "Obey the rest timer. Tight session density preserves focus and hypertrophy."),
                MascotExpression.SERIOUS
            ),
            MascotSpeechLine(
                "c-tip-4",
                t("Protokol Coach$n: Beban Jujur", "Coach Protocol$n: Honest Progression"),
                t("Naikkan beban hanya saat seluruh repetisi pekan lalu bersih tanpa ayunan badan.", "Bump weight only when every rep last week stayed strict without body swing."),
                MascotExpression.SMIRK
            ),
            MascotSpeechLine(
                "c-tip-5",
                t("Protokol Coach$n: Jeda Bawah", "Coach Protocol$n: Clean Pause"),
                t("Hentikan memantul di dasar gerakan — jeda setengah detik memaksimalkan tenaga otot.", "Kill bottom bounce — a half-second pause builds true mechanical strength."),
                MascotExpression.FOCUSED
            ),
            MascotSpeechLine(
                "c-tip-6",
                t("Protokol Coach$n: Kualitas Tidur", "Coach Protocol$n: Sleep Anchor"),
                t("Program terbaik gagal tanpa tidur 7 jam. Jaga jam tidur seperti jadwal latihan.", "The best program stalls below 7 hours sleep. Guard bedtime like gym time."),
                MascotExpression.CALM
            ),
            MascotSpeechLine(
                "c-tip-7",
                t("Protokol Coach$n: Posisi Pergelangan", "Coach Protocol$n: Stacked Wrists"),
                t("Pastikan pergelangan tangan lurus sejajar lengan bawah agar tenaga dorong tidak bocor.", "Keep wrists stacked directly over forearms so pressing force never leaks."),
                MascotExpression.SERIOUS
            ),
            MascotSpeechLine(
                "c-tip-8",
                t("Protokol Coach$n: Hentikan Ego Lift", "Coach Protocol$n: Zero Ego Weight"),
                t("Repetisi penuh dengan beban tepat selalu menang melawan beban berlebih yang berantakan.", "Full-range reps at honest weight beat sloppy overload every month."),
                MascotExpression.DETERMINED
            )
        )
    }
}
