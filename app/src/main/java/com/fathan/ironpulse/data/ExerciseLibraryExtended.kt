package com.fathan.ironpulse.data

import com.fathan.ironpulse.model.ExerciseTrackingMode
import com.fathan.ironpulse.model.MuscleGroup
import com.fathan.ironpulse.model.TimerDirection

/**
 * Extended movement catalog (+160 movements) combined with GymPresets.BASE_EXERCISE_LIBRARY
 * to bring the total built-in Exercise Library to 327+ unique, anatomically mapped movements.
 */
internal object ExerciseLibraryExtended {

    val EXTRA_EXERCISES: List<PresetExerciseTemplate> = listOf(
        // ==================== DADA (+24) ====================
        PresetExerciseTemplate(
            name = "Barbell Floor Press",
            muscleGroup = MuscleGroup.DADA,
            equipment = "Barbel",
            targetSets = 4,
            targetReps = "6-8",
            targetWeightKg = 60.0,
            restSeconds = 120,
            notes = "Turunkan lengan atas hingga menyentuh lantai sejenak lalu dorong eksplosif."
        ),
        PresetExerciseTemplate(
            name = "Dumbbell Floor Press",
            muscleGroup = MuscleGroup.DADA,
            equipment = "Dumbbell",
            targetSets = 3,
            targetReps = "10-12",
            targetWeightKg = 20.0,
            restSeconds = 90,
            notes = "Membatasi rentang gerak bawah untuk menjaga bahu dan menekan kekuatan dada."
        ),
        PresetExerciseTemplate(
            name = "Guillotine Barbell Press",
            muscleGroup = MuscleGroup.DADA,
            equipment = "Barbel",
            targetSets = 3,
            targetReps = "10-12",
            targetWeightKg = 40.0,
            restSeconds = 90,
            notes = "Gunakan beban ringan-sedang dan turunkan terkontrol ke bagian atas dada."
        ),
        PresetExerciseTemplate(
            name = "Reverse-Grip Barbell Bench Press",
            muscleGroup = MuscleGroup.DADA,
            equipment = "Barbel",
            targetSets = 3,
            targetReps = "8-10",
            targetWeightKg = 45.0,
            restSeconds = 90,
            notes = "Genggaman telapak menghadap wajah untuk mengaktivasi dada atas tanpa nyeri bahu."
        ),
        PresetExerciseTemplate(
            name = "Landmine Chest Press",
            muscleGroup = MuscleGroup.DADA,
            equipment = "Barbel",
            targetSets = 3,
            targetReps = "10-12",
            targetWeightKg = 25.0,
            restSeconds = 90,
            notes = "Dorong ujung barbel ke depan atas sambil meremas dada bagian tengah-atas."
        ),
        PresetExerciseTemplate(
            name = "Svend Press (Plate Squeeze)",
            muscleGroup = MuscleGroup.DADA,
            equipment = "Pelat Beban",
            targetSets = 3,
            targetReps = "12-15",
            targetWeightKg = 10.0,
            restSeconds = 60,
            notes = "Tekan dua pelat di depan dada dan dorong lurus ke depan dengan kontraksi penuh."
        ),
        PresetExerciseTemplate(
            name = "Wide-Grip Push-Up",
            muscleGroup = MuscleGroup.DADA,
            equipment = "Tanpa Alat",
            targetSets = 3,
            targetReps = "15-20",
            targetWeightKg = 0.0,
            restSeconds = 60,
            notes = "Posisi tangan lebih lebar dari bahu untuk peregangan serabut dada lebih dalam.",
            usesWeight = false
        ),
        PresetExerciseTemplate(
            name = "Decline Push-Up",
            muscleGroup = MuscleGroup.DADA,
            equipment = "Tanpa Alat",
            targetSets = 3,
            targetReps = "12-15",
            targetWeightKg = 0.0,
            restSeconds = 60,
            notes = "Kaki diletakkan lebih tinggi di bangku untuk menargetkan dada bagian atas.",
            usesWeight = false
        ),
        PresetExerciseTemplate(
            name = "Incline Push-Up",
            muscleGroup = MuscleGroup.DADA,
            equipment = "Tanpa Alat",
            targetSets = 3,
            targetReps = "15-20",
            targetWeightKg = 0.0,
            restSeconds = 60,
            notes = "Tangan bertumpu di bangku untuk beban lebih ringan dan fokus dada bawah.",
            usesWeight = false
        ),
        PresetExerciseTemplate(
            name = "Archer Push-Up",
            muscleGroup = MuscleGroup.DADA,
            equipment = "Tanpa Alat",
            targetSets = 3,
            targetReps = "8-10/sisi",
            targetWeightKg = 0.0,
            restSeconds = 75,
            notes = "Turunkan badan ke satu sisi sementara lengan berlawanan tetap lurus.",
            usesWeight = false
        ),
        PresetExerciseTemplate(
            name = "Plyometric Clap Push-Up",
            muscleGroup = MuscleGroup.DADA,
            equipment = "Tanpa Alat",
            targetSets = 3,
            targetReps = "8-12",
            targetWeightKg = 0.0,
            restSeconds = 90,
            notes = "Dorong eksplosif hingga telapak tangan terangkat sejenak dari lantai.",
            usesWeight = false
        ),
        PresetExerciseTemplate(
            name = "Chest Dips (Bodyweight)",
            muscleGroup = MuscleGroup.DADA,
            equipment = "Dip Bar",
            targetSets = 3,
            targetReps = "10-15",
            targetWeightKg = 0.0,
            restSeconds = 90,
            notes = "Condongkan torso sedikit ke depan dan lebarkan siku untuk fokus dada bawah.",
            usesWeight = false
        ),
        PresetExerciseTemplate(
            name = "Seated Cable Chest Press",
            muscleGroup = MuscleGroup.DADA,
            equipment = "Mesin Kabel",
            targetSets = 3,
            targetReps = "10-12",
            targetWeightKg = 25.0,
            restSeconds = 75,
            notes = "Duduk tegak membelakangi menara kabel, dorong pegangan menyatu di depan dada."
        ),
        PresetExerciseTemplate(
            name = "Standing Cable Crossover",
            muscleGroup = MuscleGroup.DADA,
            equipment = "Kabel",
            targetSets = 3,
            targetReps = "12-15",
            targetWeightKg = 15.0,
            restSeconds = 60,
            notes = "Langkahkan satu kaki ke depan dan silangkan tangan sedikit di puncak kontraksi."
        ),
        PresetExerciseTemplate(
            name = "Decline Cable Fly",
            muscleGroup = MuscleGroup.DADA,
            equipment = "Kabel",
            targetSets = 3,
            targetReps = "12-15",
            targetWeightKg = 12.5,
            restSeconds = 60,
            notes = "Tarik kabel dari posisi katrol tinggi ke bawah garis pinggang pada bangku decline."
        ),
        PresetExerciseTemplate(
            name = "Single-Arm Machine Chest Press",
            muscleGroup = MuscleGroup.DADA,
            equipment = "Mesin Chest Press",
            targetSets = 3,
            targetReps = "10-12/sisi",
            targetWeightKg = 30.0,
            restSeconds = 75,
            notes = "Latih satu sisi secara bergantian untuk memperbaiki ketidakseimbangan kekuatan."
        ),
        PresetExerciseTemplate(
            name = "Hammer Strength Iso-Lateral Incline Press",
            muscleGroup = MuscleGroup.DADA,
            equipment = "Mesin Chest Press",
            targetSets = 4,
            targetReps = "8-10",
            targetWeightKg = 40.0,
            restSeconds = 90,
            notes = "Dorong pegangan plate-loaded ke atas dengan jalur melengkung alami."
        ),
        PresetExerciseTemplate(
            name = "Hammer Strength Wide Chest Press",
            muscleGroup = MuscleGroup.DADA,
            equipment = "Mesin Chest Press",
            targetSets = 3,
            targetReps = "10-12",
            targetWeightKg = 45.0,
            restSeconds = 90,
            notes = "Jalur tekan lebar untuk memaksimalkan peregangan dan aktivasi dada luar."
        ),
        PresetExerciseTemplate(
            name = "Smith Machine Decline Bench Press",
            muscleGroup = MuscleGroup.DADA,
            equipment = "Mesin Smith",
            targetSets = 3,
            targetReps = "8-10",
            targetWeightKg = 55.0,
            restSeconds = 90,
            notes = "Jalur vertikal stabil untuk fokus beban berat di dada bagian bawah."
        ),
        PresetExerciseTemplate(
            name = "Neutral-Grip Dumbbell Bench Press",
            muscleGroup = MuscleGroup.DADA,
            equipment = "Dumbbell",
            targetSets = 3,
            targetReps = "10-12",
            targetWeightKg = 20.0,
            restSeconds = 90,
            notes = "Telapak tangan saling berhadapan agar sendi bahu tetap nyaman dan stabil."
        ),
        PresetExerciseTemplate(
            name = "Alternating Dumbbell Bench Press",
            muscleGroup = MuscleGroup.DADA,
            equipment = "Dumbbell",
            targetSets = 3,
            targetReps = "10/sisi",
            targetWeightKg = 18.0,
            restSeconds = 75,
            notes = "Tahan satu dumbbell di puncak saat sisi lain turun untuk stabilitas inti."
        ),
        PresetExerciseTemplate(
            name = "Single-Arm Kettlebell Floor Press",
            muscleGroup = MuscleGroup.DADA,
            equipment = "Kettlebell",
            targetSets = 3,
            targetReps = "10/sisi",
            targetWeightKg = 16.0,
            restSeconds = 75,
            notes = "Jaga pergelangan tangan lurus dan inti tubuh terkunci agar pinggul tidak berputar."
        ),
        PresetExerciseTemplate(
            name = "Incline Kettlebell Squeeze Press",
            muscleGroup = MuscleGroup.DADA,
            equipment = "Kettlebell",
            targetSets = 3,
            targetReps = "10-12",
            targetWeightKg = 14.0,
            restSeconds = 75,
            notes = "Rapatkan sisi kettlebell di bangku incline dan dorong ke atas dengan kontrol penuh."
        ),
        PresetExerciseTemplate(
            name = "Deficit Push-Up",
            muscleGroup = MuscleGroup.DADA,
            equipment = "Tanpa Alat",
            targetSets = 3,
            targetReps = "10-14",
            targetWeightKg = 0.0,
            restSeconds = 75,
            notes = "Gunakan push-up handle atau pelat beban agar dada turun lebih dalam di bawah tangan.",
            usesWeight = false
        ),

        // ==================== PUNGGUNG (+25) ====================
        PresetExerciseTemplate(
            name = "Wide-Grip Lat Pulldown",
            muscleGroup = MuscleGroup.PUNGGUNG,
            equipment = "Mesin Kabel",
            targetSets = 4,
            targetReps = "10-12",
            targetWeightKg = 50.0,
            restSeconds = 90,
            notes = "Genggam lebar di lekukan bar dan tarik siku ke samping rusuk untuk lebar sayap."
        ),
        PresetExerciseTemplate(
            name = "V-Bar Lat Pulldown",
            muscleGroup = MuscleGroup.PUNGGUNG,
            equipment = "Mesin Kabel",
            targetSets = 3,
            targetReps = "10-12",
            targetWeightKg = 50.0,
            restSeconds = 90,
            notes = "Genggam netral rapat menggunakan V-bar dan tarik ke arah dada atas."
        ),
        PresetExerciseTemplate(
            name = "Cable Lat Prayer (Kneeling)",
            muscleGroup = MuscleGroup.PUNGGUNG,
            equipment = "Kabel Tali",
            targetSets = 3,
            targetReps = "12-15",
            targetWeightKg = 25.0,
            restSeconds = 60,
            notes = "Berlutut di depan kabel tinggi, tarik tali dengan lengan nyaris lurus ke paha."
        ),
        PresetExerciseTemplate(
            name = "Seal Row (Barbell)",
            muscleGroup = MuscleGroup.PUNGGUNG,
            equipment = "Barbel",
            targetSets = 4,
            targetReps = "8-10",
            targetWeightKg = 50.0,
            restSeconds = 90,
            notes = "Tengkurap di bangku tinggi sehingga momentum pinggang nol dan fokus murni punggung."
        ),
        PresetExerciseTemplate(
            name = "Yates Row (Underhand Barbell Row)",
            muscleGroup = MuscleGroup.PUNGGUNG,
            equipment = "Barbel",
            targetSets = 4,
            targetReps = "8-10",
            targetWeightKg = 65.0,
            restSeconds = 90,
            notes = "Posisi torso agak tegak dengan genggaman terbalik, tarik barbel ke perut bawah."
        ),
        PresetExerciseTemplate(
            name = "T-Bar Row (Landmine)",
            muscleGroup = MuscleGroup.PUNGGUNG,
            equipment = "Barbel",
            targetSets = 4,
            targetReps = "8-10",
            targetWeightKg = 50.0,
            restSeconds = 90,
            notes = "Gunakan pegangan V-bar di bawah ujung barbel, tarik penuh hingga meremas belikat."
        ),
        PresetExerciseTemplate(
            name = "Smith Machine Bent-Over Row",
            muscleGroup = MuscleGroup.PUNGGUNG,
            equipment = "Mesin Smith",
            targetSets = 3,
            targetReps = "10-12",
            targetWeightKg = 55.0,
            restSeconds = 90,
            notes = "Jalur rel vertikal membantu menjaga tarikan konsisten ke garis pusar."
        ),
        PresetExerciseTemplate(
            name = "Dumbbell Seal Row",
            muscleGroup = MuscleGroup.PUNGGUNG,
            equipment = "Dumbbell",
            targetSets = 3,
            targetReps = "10-12",
            targetWeightKg = 20.0,
            restSeconds = 75,
            notes = "Tarik kedua dumbbell bersamaan sambil menekan dada menempel di bangku."
        ),
        PresetExerciseTemplate(
            name = "Kroc Row (Heavy Single-Arm Dumbbell)",
            muscleGroup = MuscleGroup.PUNGGUNG,
            equipment = "Dumbbell",
            targetSets = 3,
            targetReps = "12-15/sisi",
            targetWeightKg = 32.0,
            restSeconds = 90,
            notes = "Gunakan beban berat dengan rentang penuh dari peregangan bawah hingga pinggul."
        ),
        PresetExerciseTemplate(
            name = "Renegade Row (Dumbbell)",
            muscleGroup = MuscleGroup.PUNGGUNG,
            equipment = "Dumbbell",
            targetSets = 3,
            targetReps = "8-10/sisi",
            targetWeightKg = 14.0,
            restSeconds = 75,
            notes = "Posisi plank sambil menarik satu dumbbell ke rusuk tanpa memutar pinggul."
        ),
        PresetExerciseTemplate(
            name = "Single-Arm Kettlebell Row",
            muscleGroup = MuscleGroup.PUNGGUNG,
            equipment = "Kettlebell",
            targetSets = 3,
            targetReps = "10-12/sisi",
            targetWeightKg = 20.0,
            restSeconds = 75,
            notes = "Tarik kettlebell ke arah saku celana sambil menjaga punggung rata."
        ),
        PresetExerciseTemplate(
            name = "Gorilla Row (Kettlebell)",
            muscleGroup = MuscleGroup.PUNGGUNG,
            equipment = "Kettlebell",
            targetSets = 3,
            targetReps = "10/sisi",
            targetWeightKg = 18.0,
            restSeconds = 75,
            notes = "Posisi half-hinge lebar, tarik dua kettlebell bergantian eksplosif dari lantai."
        ),
        PresetExerciseTemplate(
            name = "Dual Kettlebell Bent-Over Row",
            muscleGroup = MuscleGroup.PUNGGUNG,
            equipment = "Kettlebell",
            targetSets = 3,
            targetReps = "10-12",
            targetWeightKg = 16.0,
            restSeconds = 90,
            notes = "Tarik dua kettlebell serentak dengan fokus menjepit tulang belikat di puncak."
        ),
        PresetExerciseTemplate(
            name = "Hammer Strength Iso-Lateral Front Lat Pulldown",
            muscleGroup = MuscleGroup.PUNGGUNG,
            equipment = "Mesin Row",
            targetSets = 3,
            targetReps = "10-12",
            targetWeightKg = 45.0,
            restSeconds = 90,
            notes = "Tarik tuas plate-loaded melengkung ke bawah dengan kontrol di fase naik."
        ),
        PresetExerciseTemplate(
            name = "Machine Seated Row (Neutral Grip)",
            muscleGroup = MuscleGroup.PUNGGUNG,
            equipment = "Mesin Row",
            targetSets = 3,
            targetReps = "10-12",
            targetWeightKg = 50.0,
            restSeconds = 75,
            notes = "Dada menempel di bantalan depan, tarik siku ke belakang dan tahan 1 detik."
        ),
        PresetExerciseTemplate(
            name = "Kelso Shrug (Chest-Supported)",
            muscleGroup = MuscleGroup.PUNGGUNG,
            equipment = "Dumbbell",
            targetSets = 3,
            targetReps = "12-15",
            targetWeightKg = 20.0,
            restSeconds = 60,
            notes = "Dengan lengan lurus di bangku incline, tarik belikat saling mendekat untuk mid-trap."
        ),
        PresetExerciseTemplate(
            name = "Neutral-Grip Pull-Up",
            muscleGroup = MuscleGroup.PUNGGUNG,
            equipment = "Pull-Up Bar",
            targetSets = 3,
            targetReps = "6-10",
            targetWeightKg = 0.0,
            restSeconds = 120,
            notes = "Genggaman telapak saling berhadapan agar nyaman untuk bahu dan pergelangan.",
            usesWeight = false
        ),
        PresetExerciseTemplate(
            name = "Commando Pull-Up",
            muscleGroup = MuscleGroup.PUNGGUNG,
            equipment = "Pull-Up Bar",
            targetSets = 3,
            targetReps = "6-10",
            targetWeightKg = 0.0,
            restSeconds = 90,
            notes = "Posisi tangan segaris di palang dan tarik kepala melewati sisi kanan-kiri bergantian.",
            usesWeight = false
        ),
        PresetExerciseTemplate(
            name = "Scapular Pull-Up",
            muscleGroup = MuscleGroup.PUNGGUNG,
            equipment = "Pull-Up Bar",
            targetSets = 3,
            targetReps = "10-15",
            targetWeightKg = 0.0,
            restSeconds = 60,
            notes = "Lengan tetap lurus, turunkan dan angkat bahu hanya lewat tarikan tulang belikat.",
            usesWeight = false
        ),
        PresetExerciseTemplate(
            name = "Superman Back Hold",
            muscleGroup = MuscleGroup.PUNGGUNG,
            equipment = "Tanpa Alat",
            targetSets = 3,
            targetReps = "30d",
            targetWeightKg = 0.0,
            restSeconds = 45,
            notes = "Angkat dada dan kaki perlahan dari matras untuk menguatkan punggung bawah.",
            trackingMode = ExerciseTrackingMode.TIMER,
            usesWeight = false,
            timerDirection = TimerDirection.COUNTDOWN,
            targetDurationSeconds = 30
        ),
        PresetExerciseTemplate(
            name = "Snatch-Grip Barbell Deadlift",
            muscleGroup = MuscleGroup.PUNGGUNG,
            equipment = "Barbel",
            targetSets = 3,
            targetReps = "5-6",
            targetWeightKg = 80.0,
            restSeconds = 150,
            notes = "Genggaman ekstra lebar untuk meningkatkan aktivasi upper back, lats, dan glutes."
        ),
        PresetExerciseTemplate(
            name = "Deficit Barbell Deadlift",
            muscleGroup = MuscleGroup.PUNGGUNG,
            equipment = "Barbel",
            targetSets = 3,
            targetReps = "5-6",
            targetWeightKg = 85.0,
            restSeconds = 150,
            notes = "Berdiri di atas pelat tipis untuk memperbesar jarak tarikan awal dari lantai."
        ),
        PresetExerciseTemplate(
            name = "Trap Bar Deadlift",
            muscleGroup = MuscleGroup.PUNGGUNG,
            equipment = "Barbel",
            targetSets = 4,
            targetReps = "6-8",
            targetWeightKg = 90.0,
            restSeconds = 150,
            notes = "Berdiri di dalam rangka trap bar, dorong lantai dengan kaki dan kunci pinggul."
        ),
        PresetExerciseTemplate(
            name = "Kneeling Single-Arm Cable Row",
            muscleGroup = MuscleGroup.PUNGGUNG,
            equipment = "Kabel",
            targetSets = 3,
            targetReps = "10-12/sisi",
            targetWeightKg = 22.5,
            restSeconds = 75,
            notes = "Berlutut satu kaki untuk stabilitas, tarik pegangan ke arah pinggul bagian samping."
        ),
        PresetExerciseTemplate(
            name = "Reverse Hyperextension Machine",
            muscleGroup = MuscleGroup.PUNGGUNG,
            equipment = "Mesin / Bench",
            targetSets = 3,
            targetReps = "12-15",
            targetWeightKg = 25.0,
            restSeconds = 75,
            notes = "Tahan torso di bantalan dan ayunkan kaki ke belakang untuk erector spinae dan glutes."
        ),

        // ==================== BAHU (+22) ====================
        PresetExerciseTemplate(
            name = "Push Press (Barbell)",
            muscleGroup = MuscleGroup.BAHU,
            equipment = "Barbel",
            targetSets = 4,
            targetReps = "5-6",
            targetWeightKg = 50.0,
            restSeconds = 120,
            notes = "Bantu dorongan lutut ringan untuk menghentak beban berat ke atas kepala."
        ),
        PresetExerciseTemplate(
            name = "Seated Barbell Overhead Press",
            muscleGroup = MuscleGroup.BAHU,
            equipment = "Barbel",
            targetSets = 4,
            targetReps = "8-10",
            targetWeightKg = 40.0,
            restSeconds = 90,
            notes = "Duduk di bangku bersandaran tegak untuk mengisolasi bahu tanpa ayunan kaki."
        ),
        PresetExerciseTemplate(
            name = "Z-Press (Seated Floor Barbell)",
            muscleGroup = MuscleGroup.BAHU,
            equipment = "Barbel",
            targetSets = 3,
            targetReps = "8-10",
            targetWeightKg = 35.0,
            restSeconds = 90,
            notes = "Duduk di lantai dengan kaki lurus depan agar stabilitas inti dan bahu diuji murni."
        ),
        PresetExerciseTemplate(
            name = "Standing Dumbbell Shoulder Press",
            muscleGroup = MuscleGroup.BAHU,
            equipment = "Dumbbell",
            targetSets = 3,
            targetReps = "8-10",
            targetWeightKg = 14.0,
            restSeconds = 90,
            notes = "Kencangkan perut dan bokong agar punggung bawah tidak melengkung berlebih."
        ),
        PresetExerciseTemplate(
            name = "Single-Arm Kettlebell Overhead Press",
            muscleGroup = MuscleGroup.BAHU,
            equipment = "Kettlebell",
            targetSets = 3,
            targetReps = "8-10/sisi",
            targetWeightKg = 16.0,
            restSeconds = 75,
            notes = "Dorong dari posisi rack ke atas kepala dengan siku terkunci sejajar telinga."
        ),
        PresetExerciseTemplate(
            name = "Bottoms-Up Kettlebell Press",
            muscleGroup = MuscleGroup.BAHU,
            equipment = "Kettlebell",
            targetSets = 3,
            targetReps = "8/sisi",
            targetWeightKg = 10.0,
            restSeconds = 75,
            notes = "Seimbangkan beban kettlebell menghadap ke atas untuk stabilitas rotator cuff."
        ),
        PresetExerciseTemplate(
            name = "Pike Push-Up",
            muscleGroup = MuscleGroup.BAHU,
            equipment = "Tanpa Alat",
            targetSets = 3,
            targetReps = "10-12",
            targetWeightKg = 0.0,
            restSeconds = 75,
            notes = "Bentuk huruf V terbalik dengan pinggul tinggi dan turunkan kepala ke depan tangan.",
            usesWeight = false
        ),
        PresetExerciseTemplate(
            name = "Elevated Pike Push-Up",
            muscleGroup = MuscleGroup.BAHU,
            equipment = "Tanpa Alat",
            targetSets = 3,
            targetReps = "8-10",
            targetWeightKg = 0.0,
            restSeconds = 90,
            notes = "Letakkan ujung kaki di bangku untuk beban bahu lebih berat mendekati handstand.",
            usesWeight = false
        ),
        PresetExerciseTemplate(
            name = "Wall Handstand Hold",
            muscleGroup = MuscleGroup.BAHU,
            equipment = "Tanpa Alat",
            targetSets = 3,
            targetReps = "30d",
            targetWeightKg = 0.0,
            restSeconds = 75,
            notes = "Dorong lantai kuat menjauhi telinga untuk ketahanan isometrik bahu dan core.",
            trackingMode = ExerciseTrackingMode.TIMER,
            usesWeight = false,
            timerDirection = TimerDirection.COUNTDOWN,
            targetDurationSeconds = 30
        ),
        PresetExerciseTemplate(
            name = "Seated Dumbbell Lateral Raise",
            muscleGroup = MuscleGroup.BAHU,
            equipment = "Dumbbell",
            targetSets = 4,
            targetReps = "12-15",
            targetWeightKg = 8.0,
            restSeconds = 60,
            notes = "Duduk tegak untuk meminimalkan ayunan tubuh dan fokus pada deltoid samping."
        ),
        PresetExerciseTemplate(
            name = "Behind-the-Back Cable Lateral Raise",
            muscleGroup = MuscleGroup.BAHU,
            equipment = "Kabel",
            targetSets = 3,
            targetReps = "12-15/sisi",
            targetWeightKg = 7.5,
            restSeconds = 60,
            notes = "Kabel melewati belakang tubuh memberi ketegangan maksimum saat lengan di posisi bawah."
        ),
        PresetExerciseTemplate(
            name = "Egyptian Cable Lateral Raise",
            muscleGroup = MuscleGroup.BAHU,
            equipment = "Kabel",
            targetSets = 3,
            targetReps = "12-15/sisi",
            targetWeightKg = 7.5,
            restSeconds = 60,
            notes = "Pegang tiang kabel dan miringkan badan menjauh untuk jalur tarikan lateral optimal."
        ),
        PresetExerciseTemplate(
            name = "Lu Raise (Full Range Lateral)",
            muscleGroup = MuscleGroup.BAHU,
            equipment = "Pelat Beban",
            targetSets = 3,
            targetReps = "12-15",
            targetWeightKg = 5.0,
            restSeconds = 60,
            notes = "Angkat beban dari samping hingga lurus bertemu di atas kepala dengan kontrol lambat."
        ),
        PresetExerciseTemplate(
            name = "Dumbbell Upright Row",
            muscleGroup = MuscleGroup.BAHU,
            equipment = "Dumbbell",
            targetSets = 3,
            targetReps = "10-12",
            targetWeightKg = 14.0,
            restSeconds = 75,
            notes = "Tarik siku melebar setinggi bahu untuk jalur yang lebih ramah sendi dibanding barbel."
        ),
        PresetExerciseTemplate(
            name = "Cable Upright Row",
            muscleGroup = MuscleGroup.BAHU,
            equipment = "Mesin Kabel",
            targetSets = 3,
            targetReps = "12-15",
            targetWeightKg = 25.0,
            restSeconds = 60,
            notes = "Gunakan bar lurus atau tali pada katrol bawah untuk tensi konstan bahu & traps."
        ),
        PresetExerciseTemplate(
            name = "Chest-Supported Rear Delt Fly",
            muscleGroup = MuscleGroup.BAHU,
            equipment = "Dumbbell",
            targetSets = 3,
            targetReps = "15",
            targetWeightKg = 8.0,
            restSeconds = 60,
            notes = "Tengkurap di bangku incline 30 derajat, sapu lengan ke samping belakang."
        ),
        PresetExerciseTemplate(
            name = "Cross-Cable Rear Delt Fly",
            muscleGroup = MuscleGroup.BAHU,
            equipment = "Kabel",
            targetSets = 3,
            targetReps = "15",
            targetWeightKg = 7.5,
            restSeconds = 60,
            notes = "Tarik dua kabel berseberangan dari katrol atas setinggi bahu ke samping belakang."
        ),
        PresetExerciseTemplate(
            name = "Prone W-Raise (Rotator & Rear Delt)",
            muscleGroup = MuscleGroup.BAHU,
            equipment = "Dumbbell",
            targetSets = 3,
            targetReps = "12-15",
            targetWeightKg = 4.0,
            restSeconds = 60,
            notes = "Siku menekuk 90 derajat membentuk huruf W, putar dan angkat punggung tangan."
        ),
        PresetExerciseTemplate(
            name = "Alternating Dumbbell Front Raise",
            muscleGroup = MuscleGroup.BAHU,
            equipment = "Dumbbell",
            targetSets = 3,
            targetReps = "12/sisi",
            targetWeightKg = 10.0,
            restSeconds = 60,
            notes = "Angkat satu lengan lurus setinggi mata dengan kendali penuh tanpa mengayun."
        ),
        PresetExerciseTemplate(
            name = "Plate Steering Wheel (Bus Driver)",
            muscleGroup = MuscleGroup.BAHU,
            equipment = "Pelat Beban",
            targetSets = 3,
            targetReps = "12-16",
            targetWeightKg = 10.0,
            restSeconds = 60,
            notes = "Tahan pelat di depan wajah dengan lengan lurus lalu putar kiri-kanan seperti setir."
        ),
        PresetExerciseTemplate(
            name = "Smith Machine Shrugs",
            muscleGroup = MuscleGroup.BAHU,
            equipment = "Mesin Smith",
            targetSets = 4,
            targetReps = "12-15",
            targetWeightKg = 70.0,
            restSeconds = 75,
            notes = "Angkat bahu lurus ke arah telinga, tahan 1 detik di puncak kontraksi traps."
        ),
        PresetExerciseTemplate(
            name = "Cable External Rotation",
            muscleGroup = MuscleGroup.BAHU,
            equipment = "Kabel",
            targetSets = 3,
            targetReps = "15/sisi",
            targetWeightKg = 5.0,
            restSeconds = 45,
            notes = "Siku menempel di rusuk 90 derajat, putar lengan bawah keluar untuk kesehatan bahu."
        ),

        // ==================== KAKI (+27) ====================
        PresetExerciseTemplate(
            name = "Pause Barbell Back Squat",
            muscleGroup = MuscleGroup.KAKI,
            equipment = "Barbel & Rack",
            targetSets = 3,
            targetReps = "5-6",
            targetWeightKg = 75.0,
            restSeconds = 150,
            notes = "Tahan 2 detik di dasar squat sambil menjaga ketegangan paha dan inti tubuh."
        ),
        PresetExerciseTemplate(
            name = "Box Barbell Squat",
            muscleGroup = MuscleGroup.KAKI,
            equipment = "Barbel & Rack",
            targetSets = 4,
            targetReps = "6-8",
            targetWeightKg = 75.0,
            restSeconds = 120,
            notes = "Duduk terkontrol menyentuh box setinggi paralel tanpa mengendurkan punggung, lalu dorong."
        ),
        PresetExerciseTemplate(
            name = "Zercher Squat",
            muscleGroup = MuscleGroup.KAKI,
            equipment = "Barbel",
            targetSets = 3,
            targetReps = "8-10",
            targetWeightKg = 50.0,
            restSeconds = 120,
            notes = "Topang barbel di lipatan siku depan tubuh untuk aktivasi quads, core, dan upper back."
        ),
        PresetExerciseTemplate(
            name = "Belt Squat Machine",
            muscleGroup = MuscleGroup.KAKI,
            equipment = "Mesin Squat",
            targetSets = 4,
            targetReps = "10-12",
            targetWeightKg = 60.0,
            restSeconds = 120,
            notes = "Beban terpasang di sabuk pinggul sehingga paha depan bekerja keras tanpa beban tulang belakang."
        ),
        PresetExerciseTemplate(
            name = "V-Squat Machine",
            muscleGroup = MuscleGroup.KAKI,
            equipment = "Mesin Squat",
            targetSets = 3,
            targetReps = "10-12",
            targetWeightKg = 70.0,
            restSeconds = 90,
            notes = "Posisikan kaki agak rendah di platform untuk fokus paha depan lebih dalam."
        ),
        PresetExerciseTemplate(
            name = "Sissy Squat (Bodyweight / Assisted)",
            muscleGroup = MuscleGroup.KAKI,
            equipment = "Tanpa Alat",
            targetSets = 3,
            targetReps = "10-15",
            targetWeightKg = 0.0,
            restSeconds = 75,
            notes = "Dorong lutut jauh ke depan sambil pinggul tetap lurus untuk peregangan quads ekstrem.",
            usesWeight = false
        ),
        PresetExerciseTemplate(
            name = "Cyclist Squat (Heels Elevated)",
            muscleGroup = MuscleGroup.KAKI,
            equipment = "Dumbbell",
            targetSets = 3,
            targetReps = "12-15",
            targetWeightKg = 16.0,
            restSeconds = 90,
            notes = "Tumit diganjal tinggi dengan kaki rapat untuk isolasi paha depan bagian bawah."
        ),
        PresetExerciseTemplate(
            name = "Spanish Squat",
            muscleGroup = MuscleGroup.KAKI,
            equipment = "Tanpa Alat",
            targetSets = 3,
            targetReps = "12-15",
            targetWeightKg = 0.0,
            restSeconds = 75,
            notes = "Tahan posisi punggung tegak saat duduk ke belakang, sangat ramah untuk lutut.",
            usesWeight = false
        ),
        PresetExerciseTemplate(
            name = "Pistol Squat (Assisted / Bodyweight)",
            muscleGroup = MuscleGroup.KAKI,
            equipment = "Tanpa Alat",
            targetSets = 3,
            targetReps = "5-8/sisi",
            targetWeightKg = 0.0,
            restSeconds = 90,
            notes = "Turun satu kaki penuh sambil kaki lain diluruskan ke depan.",
            usesWeight = false
        ),
        PresetExerciseTemplate(
            name = "Smith Machine Split Squat",
            muscleGroup = MuscleGroup.KAKI,
            equipment = "Mesin Smith",
            targetSets = 3,
            targetReps = "10/sisi",
            targetWeightKg = 40.0,
            restSeconds = 90,
            notes = "Stabilitas rel memudahkan fokus penuh pada beban satu kaki tanpa goyah."
        ),
        PresetExerciseTemplate(
            name = "Curtsy Lunge (Dumbbell)",
            muscleGroup = MuscleGroup.KAKI,
            equipment = "Dumbbell",
            targetSets = 3,
            targetReps = "10-12/sisi",
            targetWeightKg = 12.0,
            restSeconds = 75,
            notes = "Langkahkan kaki belakang menyilang ke samping untuk aktivasi gluteus medius."
        ),
        PresetExerciseTemplate(
            name = "Lateral Lunge (Cossack / Dumbbell)",
            muscleGroup = MuscleGroup.KAKI,
            equipment = "Dumbbell",
            targetSets = 3,
            targetReps = "10/sisi",
            targetWeightKg = 12.0,
            restSeconds = 75,
            notes = "Melangkah lebar ke samping untuk melatih paha depan, glutes, dan fleksibilitas adduktor."
        ),
        PresetExerciseTemplate(
            name = "Single-Leg Dumbbell RDL",
            muscleGroup = MuscleGroup.KAKI,
            equipment = "Dumbbell",
            targetSets = 3,
            targetReps = "10/sisi",
            targetWeightKg = 14.0,
            restSeconds = 75,
            notes = "Seimbangkan di satu kaki sambil dorong pinggul ke belakang untuk hamstring & glutes."
        ),
        PresetExerciseTemplate(
            name = "Stiff-Leg Barbell Deadlift",
            muscleGroup = MuscleGroup.KAKI,
            equipment = "Barbel",
            targetSets = 3,
            targetReps = "8-10",
            targetWeightKg = 65.0,
            restSeconds = 120,
            notes = "Lutut nyaris lurus untuk memaksimalkan regangan hamstring dari posisi bawah."
        ),
        PresetExerciseTemplate(
            name = "Barbell Good Morning",
            muscleGroup = MuscleGroup.KAKI,
            equipment = "Barbel",
            targetSets = 3,
            targetReps = "8-10",
            targetWeightKg = 45.0,
            restSeconds = 90,
            notes = "Barbel di punggung atas, tekuk pinggul ke belakang dengan punggung tetap netral."
        ),
        PresetExerciseTemplate(
            name = "Glute-Ham Raise (GHD)",
            muscleGroup = MuscleGroup.KAKI,
            equipment = "Mesin / Bench",
            targetSets = 3,
            targetReps = "8-12",
            targetWeightKg = 0.0,
            restSeconds = 90,
            notes = "Latihan rantai posterior penuh yang menargetkan hamstring, glutes, dan betis.",
            usesWeight = false
        ),
        PresetExerciseTemplate(
            name = "Single-Leg Lying Leg Curl",
            muscleGroup = MuscleGroup.KAKI,
            equipment = "Mesin Leg Curl",
            targetSets = 3,
            targetReps = "10-12/sisi",
            targetWeightKg = 20.0,
            restSeconds = 60,
            notes = "Tekuk satu kaki bergantian untuk menyeimbangkan kekuatan hamstring kiri dan kanan."
        ),
        PresetExerciseTemplate(
            name = "Hamstring Slider Curl",
            muscleGroup = MuscleGroup.KAKI,
            equipment = "Tanpa Alat",
            targetSets = 3,
            targetReps = "10-12",
            targetWeightKg = 0.0,
            restSeconds = 60,
            notes = "Angkat pinggul lalu geser tumit menjauh dan tarik kembali memakai handuk/slider.",
            usesWeight = false
        ),
        PresetExerciseTemplate(
            name = "Single-Leg Hip Thrust",
            muscleGroup = MuscleGroup.KAKI,
            equipment = "Tanpa Alat",
            targetSets = 3,
            targetReps = "12/sisi",
            targetWeightKg = 0.0,
            restSeconds = 60,
            notes = "Dorong pinggul ke atas menggunakan satu kaki dan tahan 1 detik di puncak.",
            usesWeight = false
        ),
        PresetExerciseTemplate(
            name = "Barbell Glute Bridge",
            muscleGroup = MuscleGroup.KAKI,
            equipment = "Barbel",
            targetSets = 4,
            targetReps = "10-12",
            targetWeightKg = 70.0,
            restSeconds = 90,
            notes = "Punggung rata di lantai, fokus kontraksi glutes tanpa ayunan punggung bawah."
        ),
        PresetExerciseTemplate(
            name = "Smith Machine Hip Thrust",
            muscleGroup = MuscleGroup.KAKI,
            equipment = "Mesin Smith",
            targetSets = 4,
            targetReps = "10-12",
            targetWeightKg = 75.0,
            restSeconds = 90,
            notes = "Gunakan bantalan barbel dan bangku stabil untuk beban glutes berat yang aman."
        ),
        PresetExerciseTemplate(
            name = "Cable Glute Kickback",
            muscleGroup = MuscleGroup.KAKI,
            equipment = "Kabel",
            targetSets = 3,
            targetReps = "12-15/sisi",
            targetWeightKg = 15.0,
            restSeconds = 60,
            notes = "Pasang ankle strap di pergelangan kaki, tendang lurus ke belakang dengan kontrol."
        ),
        PresetExerciseTemplate(
            name = "Frog Pump (Glutes Isolation)",
            muscleGroup = MuscleGroup.KAKI,
            equipment = "Dumbbell",
            targetSets = 3,
            targetReps = "20",
            targetWeightKg = 14.0,
            restSeconds = 60,
            notes = "Temukan telapak kaki saling berhadapan dan pompa pinggul ke atas untuk burn glutes."
        ),
        PresetExerciseTemplate(
            name = "Kettlebell Sumo Squat",
            muscleGroup = MuscleGroup.KAKI,
            equipment = "Kettlebell",
            targetSets = 3,
            targetReps = "12-15",
            targetWeightKg = 24.0,
            restSeconds = 75,
            notes = "Posisi kaki ekstra lebar dan jari kaki serong keluar untuk paha dalam & glutes."
        ),
        PresetExerciseTemplate(
            name = "Leg Press Calf Raise",
            muscleGroup = MuscleGroup.KAKI,
            equipment = "Mesin Leg Press",
            targetSets = 4,
            targetReps = "15-20",
            targetWeightKg = 80.0,
            restSeconds = 60,
            notes = "Letakkan ujung jari kaki di tepi bawah platform dan dorong maksimal ke depan."
        ),
        PresetExerciseTemplate(
            name = "Donkey Calf Raise Machine",
            muscleGroup = MuscleGroup.KAKI,
            equipment = "Mesin Calf Raise",
            targetSets = 4,
            targetReps = "15-20",
            targetWeightKg = 50.0,
            restSeconds = 60,
            notes = "Pinggul menekuk 90 derajat untuk meregangkan betis gastrocnemius lebih dalam."
        ),
        PresetExerciseTemplate(
            name = "Tibialis Raise (Wall / Bar)",
            muscleGroup = MuscleGroup.KAKI,
            equipment = "Tanpa Alat",
            targetSets = 3,
            targetReps = "15-20",
            targetWeightKg = 0.0,
            restSeconds = 45,
            notes = "Sandarkan punggung ke dinding dan angkat ujung kaki ke atas untuk otot tulang kering.",
            usesWeight = false
        ),

        // ==================== LENGAN (+23) ====================
        PresetExerciseTemplate(
            name = "Dumbbell Zottman Curl",
            muscleGroup = MuscleGroup.LENGAN,
            equipment = "Dumbbell",
            targetSets = 3,
            targetReps = "10-12",
            targetWeightKg = 10.0,
            restSeconds = 60,
            notes = "Angkat dengan telapak ke atas, putar telapak ke bawah saat menurunkan beban."
        ),
        PresetExerciseTemplate(
            name = "Cross-Body Dumbbell Hammer Curl",
            muscleGroup = MuscleGroup.LENGAN,
            equipment = "Dumbbell",
            targetSets = 3,
            targetReps = "10-12/sisi",
            targetWeightKg = 12.0,
            restSeconds = 60,
            notes = "Ayunkan dumbbell menyilang menuju bahu berlawanan untuk brachialis dan bisep."
        ),
        PresetExerciseTemplate(
            name = "Cable Straight-Bar Bicep Curl",
            muscleGroup = MuscleGroup.LENGAN,
            equipment = "Mesin Kabel",
            targetSets = 3,
            targetReps = "12-15",
            targetWeightKg = 25.0,
            restSeconds = 60,
            notes = "Jaga siku tetap diam di samping tubuh dengan tensi kabel konstan dari bawah."
        ),
        PresetExerciseTemplate(
            name = "Overhead Double Cable Bicep Curl",
            muscleGroup = MuscleGroup.LENGAN,
            equipment = "Kabel",
            targetSets = 3,
            targetReps = "12-15",
            targetWeightKg = 10.0,
            restSeconds = 60,
            notes = "Berdiri di tengah menara kabel, tarik pegangan menuju sisi telinga (pose double bicep)."
        ),
        PresetExerciseTemplate(
            name = "EZ-Bar Preacher Curl",
            muscleGroup = MuscleGroup.LENGAN,
            equipment = "EZ-Bar",
            targetSets = 3,
            targetReps = "10-12",
            targetWeightKg = 25.0,
            restSeconds = 75,
            notes = "Lengan bertumpu di bantalan preacher agar bahu tidak ikut membantu mengangkat."
        ),
        PresetExerciseTemplate(
            name = "Barbell 21s Bicep Curl",
            muscleGroup = MuscleGroup.LENGAN,
            equipment = "Barbel",
            targetSets = 3,
            targetReps = "21",
            targetWeightKg = 20.0,
            restSeconds = 90,
            notes = "7 rep bawah-tengah, 7 rep tengah-atas, dan 7 rep penuh tanpa berhenti."
        ),
        PresetExerciseTemplate(
            name = "Drag Curl (Barbell)",
            muscleGroup = MuscleGroup.LENGAN,
            equipment = "Barbel",
            targetSets = 3,
            targetReps = "10-12",
            targetWeightKg = 25.0,
            restSeconds = 75,
            notes = "Tarik siku ke belakang saat barbel menyusuri torso untuk menargetkan long head bisep."
        ),
        PresetExerciseTemplate(
            name = "Single-Arm Machine Preacher Curl",
            muscleGroup = MuscleGroup.LENGAN,
            equipment = "Mesin Preacher Curl",
            targetSets = 3,
            targetReps = "10-12/sisi",
            targetWeightKg = 15.0,
            restSeconds = 60,
            notes = "Isolasi penuh tiap lengan dengan fase negatif lambat 3 detik."
        ),
        PresetExerciseTemplate(
            name = "Machine Seated Bicep Curl",
            muscleGroup = MuscleGroup.LENGAN,
            equipment = "Mesin Bicep Curl",
            targetSets = 3,
            targetReps = "12-15",
            targetWeightKg = 25.0,
            restSeconds = 60,
            notes = "Sesuaikan poros mesin sejajar sendi siku dan remas penuh di puncak."
        ),
        PresetExerciseTemplate(
            name = "Waiter Curl (Dumbbell)",
            muscleGroup = MuscleGroup.LENGAN,
            equipment = "Dumbbell",
            targetSets = 3,
            targetReps = "12-15",
            targetWeightKg = 14.0,
            restSeconds = 60,
            notes = "Pegang bagian pelat atas satu dumbbell dengan dua telapak terbuka menghadap langit."
        ),
        PresetExerciseTemplate(
            name = "Tricep Bench Dips",
            muscleGroup = MuscleGroup.LENGAN,
            equipment = "Tanpa Alat",
            targetSets = 3,
            targetReps = "12-15",
            targetWeightKg = 0.0,
            restSeconds = 60,
            notes = "Tangan bertumpu di tepi bangku, jaga punggung dekat dengan bangku saat turun.",
            usesWeight = false
        ),
        PresetExerciseTemplate(
            name = "Weighted Tricep Dip (Parallel Bar)",
            muscleGroup = MuscleGroup.LENGAN,
            equipment = "Dip Bar",
            targetSets = 3,
            targetReps = "8-12",
            targetWeightKg = 10.0,
            restSeconds = 90,
            notes = "Posisi badan tegak lurus (tidak condong) dan siku rapat ke belakang untuk trisep."
        ),
        PresetExerciseTemplate(
            name = "Reverse-Grip Cable Tricep Pushdown",
            muscleGroup = MuscleGroup.LENGAN,
            equipment = "Mesin Kabel",
            targetSets = 3,
            targetReps = "12-15",
            targetWeightKg = 20.0,
            restSeconds = 60,
            notes = "Genggaman telapak menghadap ke atas untuk menekankan kerja medial head trisep."
        ),
        PresetExerciseTemplate(
            name = "Cross-Body Single-Arm Cable Tricep Extension",
            muscleGroup = MuscleGroup.LENGAN,
            equipment = "Kabel",
            targetSets = 3,
            targetReps = "12-15/sisi",
            targetWeightKg = 7.5,
            restSeconds = 60,
            notes = "Pegang kabel tanpa attachment dan dorong menyilang turun ke sisi luar paha."
        ),
        PresetExerciseTemplate(
            name = "Katana Cable Tricep Extension",
            muscleGroup = MuscleGroup.LENGAN,
            equipment = "Kabel",
            targetSets = 3,
            targetReps = "12-15",
            targetWeightKg = 12.5,
            restSeconds = 60,
            notes = "Tarik dua kabel di belakang bahu menuju atas kepala seperti menghunus katana."
        ),
        PresetExerciseTemplate(
            name = "Dumbbell Tate Press",
            muscleGroup = MuscleGroup.LENGAN,
            equipment = "Dumbbell",
            targetSets = 3,
            targetReps = "10-12",
            targetWeightKg = 12.0,
            restSeconds = 75,
            notes = "Tekuk siku ke samping saat menurunkan ujung dumbbell menyentuh dada tengah."
        ),
        PresetExerciseTemplate(
            name = "Single-Arm Overhead Dumbbell Extension",
            muscleGroup = MuscleGroup.LENGAN,
            equipment = "Dumbbell",
            targetSets = 3,
            targetReps = "10-12/sisi",
            targetWeightKg = 10.0,
            restSeconds = 60,
            notes = "Turunkan dumbbell ke belakang leher untuk regangan maksimal long head trisep."
        ),
        PresetExerciseTemplate(
            name = "Seated EZ-Bar French Press",
            muscleGroup = MuscleGroup.LENGAN,
            equipment = "EZ-Bar",
            targetSets = 3,
            targetReps = "10-12",
            targetWeightKg = 25.0,
            restSeconds = 75,
            notes = "Duduk tegak dan turunkan EZ-bar terkontrol ke belakang kepala sambil menjaga siku."
        ),
        PresetExerciseTemplate(
            name = "Cable V-Bar Tricep Pushdown",
            muscleGroup = MuscleGroup.LENGAN,
            equipment = "Mesin Kabel",
            targetSets = 4,
            targetReps = "10-12",
            targetWeightKg = 30.0,
            restSeconds = 60,
            notes = "Pegangan V-bar nyaman untuk pergelangan tangan saat mendorong beban lebih berat."
        ),
        PresetExerciseTemplate(
            name = "Reverse Barbell Wrist Curl",
            muscleGroup = MuscleGroup.LENGAN,
            equipment = "Barbel",
            targetSets = 3,
            targetReps = "15-20",
            targetWeightKg = 15.0,
            restSeconds = 45,
            notes = "Telapak menghadap ke bawah, tekuk pergelangan ke atas untuk otot ekstensor lengan bawah."
        ),
        PresetExerciseTemplate(
            name = "Behind-the-Back Barbell Wrist Curl",
            muscleGroup = MuscleGroup.LENGAN,
            equipment = "Barbel",
            targetSets = 3,
            targetReps = "15-20",
            targetWeightKg = 25.0,
            restSeconds = 45,
            notes = "Berdiri tegak memegang barbel di belakang pinggul, gulung jari dan pergelangan."
        ),
        PresetExerciseTemplate(
            name = "Dead Hang (Grip & Forearms)",
            muscleGroup = MuscleGroup.LENGAN,
            equipment = "Pull-Up Bar",
            targetSets = 3,
            targetReps = "45d",
            targetWeightKg = 0.0,
            restSeconds = 60,
            notes = "Bergantung penuh di pull-up bar untuk memperkuat cengkeraman dan dekompresi tulang belakang.",
            trackingMode = ExerciseTrackingMode.TIMER,
            usesWeight = false,
            timerDirection = TimerDirection.COUNTDOWN,
            targetDurationSeconds = 45
        ),
        PresetExerciseTemplate(
            name = "Plate Pinch Hold",
            muscleGroup = MuscleGroup.LENGAN,
            equipment = "Pelat Beban",
            targetSets = 3,
            targetReps = "30d",
            targetWeightKg = 10.0,
            restSeconds = 60,
            notes = "Jepit pelat halus dengan ibu jari dan jari lain selama durasi target untuk kekuatan grip.",
            trackingMode = ExerciseTrackingMode.TIMER,
            usesWeight = true,
            timerDirection = TimerDirection.COUNTDOWN,
            targetDurationSeconds = 30
        ),

        // ==================== CORE (+19) ====================
        PresetExerciseTemplate(
            name = "Toes-to-Bar",
            muscleGroup = MuscleGroup.CORE,
            equipment = "Pull-Up Bar",
            targetSets = 3,
            targetReps = "8-12",
            targetWeightKg = 0.0,
            restSeconds = 75,
            notes = "Angkat kaki lurus terkontrol hingga ujung jari kaki menyentuh palang pull-up.",
            usesWeight = false
        ),
        PresetExerciseTemplate(
            name = "Hanging Oblique Knee Raise",
            muscleGroup = MuscleGroup.CORE,
            equipment = "Pull-Up Bar",
            targetSets = 3,
            targetReps = "12-16",
            targetWeightKg = 0.0,
            restSeconds = 60,
            notes = "Tarik lutut menyilang ke sisi ketiak kiri dan kanan bergantian untuk obliques.",
            usesWeight = false
        ),
        PresetExerciseTemplate(
            name = "Decline Reverse Crunch",
            muscleGroup = MuscleGroup.CORE,
            equipment = "Tanpa Alat",
            targetSets = 3,
            targetReps = "12-15",
            targetWeightKg = 0.0,
            restSeconds = 60,
            notes = "Gulung pinggul terangkat dari bangku decline dengan fokus perut bagian bawah.",
            usesWeight = false
        ),
        PresetExerciseTemplate(
            name = "V-Up (Jackknife Crunch)",
            muscleGroup = MuscleGroup.CORE,
            equipment = "Tanpa Alat",
            targetSets = 3,
            targetReps = "12-15",
            targetWeightKg = 0.0,
            restSeconds = 60,
            notes = "Angkat torso dan kedua kaki lurus serentak membentuk huruf V di puncak gerakan.",
            usesWeight = false
        ),
        PresetExerciseTemplate(
            name = "Windshield Wiper (Floor / Hanging)",
            muscleGroup = MuscleGroup.CORE,
            equipment = "Tanpa Alat",
            targetSets = 3,
            targetReps = "10-12",
            targetWeightKg = 0.0,
            restSeconds = 60,
            notes = "Ayunkan kaki lurus perlahan ke kiri dan kanan seperti wiper kaca tanpa menjatuhkan pinggul.",
            usesWeight = false
        ),
        PresetExerciseTemplate(
            name = "Kneeling Cable Pallof Hold",
            muscleGroup = MuscleGroup.CORE,
            equipment = "Kabel",
            targetSets = 3,
            targetReps = "30d/sisi",
            targetWeightKg = 15.0,
            restSeconds = 60,
            notes = "Tahan pegangan kabel lurus di depan dada melawan tarikan samping secara isometrik.",
            trackingMode = ExerciseTrackingMode.TIMER,
            usesWeight = true,
            timerDirection = TimerDirection.COUNTDOWN,
            targetDurationSeconds = 30
        ),
        PresetExerciseTemplate(
            name = "Low-to-High Cable Woodchopper",
            muscleGroup = MuscleGroup.CORE,
            equipment = "Mesin Kabel",
            targetSets = 3,
            targetReps = "12/sisi",
            targetWeightKg = 12.5,
            restSeconds = 60,
            notes = "Putar torso dari katrol bawah menyilang ke atas bahu berlawanan dengan lengan lurus."
        ),
        PresetExerciseTemplate(
            name = "High-to-Low Cable Woodchopper",
            muscleGroup = MuscleGroup.CORE,
            equipment = "Mesin Kabel",
            targetSets = 3,
            targetReps = "12/sisi",
            targetWeightKg = 15.0,
            restSeconds = 60,
            notes = "Tarik kabel dari atas ke sisi pinggul berlawanan dengan rotasi pinggang terkontrol."
        ),
        PresetExerciseTemplate(
            name = "Landmine 180 (Russian Twist)",
            muscleGroup = MuscleGroup.CORE,
            equipment = "Barbel",
            targetSets = 3,
            targetReps = "12-16",
            targetWeightKg = 15.0,
            restSeconds = 60,
            notes = "Ayunkan ujung barbel dalam busur setengah lingkaran dari sisi ke sisi dengan lengan tegak."
        ),
        PresetExerciseTemplate(
            name = "Kettlebell Windmill",
            muscleGroup = MuscleGroup.CORE,
            equipment = "Kettlebell",
            targetSets = 3,
            targetReps = "8/sisi",
            targetWeightKg = 12.0,
            restSeconds = 75,
            notes = "Tahan kettlebell lurus di atas kepala sambil menekuk pinggul ke sisi berlawanan."
        ),
        PresetExerciseTemplate(
            name = "RKC Plank",
            muscleGroup = MuscleGroup.CORE,
            equipment = "Tanpa Alat",
            targetSets = 3,
            targetReps = "30d",
            targetWeightKg = 0.0,
            restSeconds = 60,
            notes = "Kontraksikan bokong, paha, dan tarik siku ke arah jari kaki untuk ketegangan maksimal.",
            trackingMode = ExerciseTrackingMode.TIMER,
            usesWeight = false,
            timerDirection = TimerDirection.COUNTDOWN,
            targetDurationSeconds = 30
        ),
        PresetExerciseTemplate(
            name = "Plank Shoulder Tap",
            muscleGroup = MuscleGroup.CORE,
            equipment = "Tanpa Alat",
            targetSets = 3,
            targetReps = "16-20",
            targetWeightKg = 0.0,
            restSeconds = 45,
            notes = "Dari posisi push-up plank, sentuh bahu berlawanan bergantian tanpa goyangan pinggul.",
            usesWeight = false
        ),
        PresetExerciseTemplate(
            name = "Body Saw Plank",
            muscleGroup = MuscleGroup.CORE,
            equipment = "Tanpa Alat",
            targetSets = 3,
            targetReps = "12-15",
            targetWeightKg = 0.0,
            restSeconds = 60,
            notes = "Pada posisi forearm plank, geser tubuh maju-mundur perlahan menggunakan engkel.",
            usesWeight = false
        ),
        PresetExerciseTemplate(
            name = "Copenhagen Adductor Plank",
            muscleGroup = MuscleGroup.CORE,
            equipment = "Tanpa Alat",
            targetSets = 3,
            targetReps = "25d/sisi",
            targetWeightKg = 0.0,
            restSeconds = 60,
            notes = "Side plank dengan kaki atas bertumpu di bangku untuk menguatkan obliques dan paha dalam.",
            trackingMode = ExerciseTrackingMode.TIMER,
            usesWeight = false,
            timerDirection = TimerDirection.COUNTDOWN,
            targetDurationSeconds = 25
        ),
        PresetExerciseTemplate(
            name = "Bird-Dog Core Hold",
            muscleGroup = MuscleGroup.CORE,
            equipment = "Tanpa Alat",
            targetSets = 3,
            targetReps = "12/sisi",
            targetWeightKg = 0.0,
            restSeconds = 45,
            notes = "Luruskan tangan kanan dan kaki kiri bersamaan, tahan sejenak dengan punggung netral.",
            usesWeight = false
        ),
        PresetExerciseTemplate(
            name = "Flutter Kicks",
            muscleGroup = MuscleGroup.CORE,
            equipment = "Tanpa Alat",
            targetSets = 3,
            targetReps = "30d",
            targetWeightKg = 0.0,
            restSeconds = 45,
            notes = "Angkat kaki beberapa sentimeter dari matras dan ayunkan naik-turun bergantian.",
            trackingMode = ExerciseTrackingMode.TIMER,
            usesWeight = false,
            timerDirection = TimerDirection.COUNTDOWN,
            targetDurationSeconds = 30
        ),
        PresetExerciseTemplate(
            name = "Heel Touches (Penguins)",
            muscleGroup = MuscleGroup.CORE,
            equipment = "Tanpa Alat",
            targetSets = 3,
            targetReps = "20-24",
            targetWeightKg = 0.0,
            restSeconds = 45,
            notes = "Tekuk lutut di matras, ayunkan bahu ke kiri dan kanan menyentuh tumit.",
            usesWeight = false
        ),
        PresetExerciseTemplate(
            name = "Stomach Vacuum",
            muscleGroup = MuscleGroup.CORE,
            equipment = "Tanpa Alat",
            targetSets = 3,
            targetReps = "20d",
            targetWeightKg = 0.0,
            restSeconds = 45,
            notes = "Hembuskan napas penuh dan tarik pusar masuk ke arah tulang belakang untuk otot transversus.",
            trackingMode = ExerciseTrackingMode.TIMER,
            usesWeight = false,
            timerDirection = TimerDirection.COUNTDOWN,
            targetDurationSeconds = 20
        ),
        PresetExerciseTemplate(
            name = "Garhammer Raise",
            muscleGroup = MuscleGroup.CORE,
            equipment = "Dip Bar",
            targetSets = 3,
            targetReps = "12-15",
            targetWeightKg = 0.0,
            restSeconds = 60,
            notes = "Angkat lutut dari posisi 90 derajat setinggi mungkin ke dada tanpa mengayunkan badan.",
            usesWeight = false
        ),

        // ==================== KARDIO (+20) ====================
        PresetExerciseTemplate(
            name = "Kettlebell Snatch",
            muscleGroup = MuscleGroup.KARDIO,
            equipment = "Kettlebell (Kardio)",
            targetSets = 4,
            targetReps = "10/sisi",
            targetWeightKg = 14.0,
            restSeconds = 75,
            notes = "Ayunkan dari antara paha langsung mengunci lurus di atas kepala dalam satu gerakan mulus."
        ),
        PresetExerciseTemplate(
            name = "Kettlebell Clean & Press",
            muscleGroup = MuscleGroup.KARDIO,
            equipment = "Kettlebell (Kardio)",
            targetSets = 4,
            targetReps = "8/sisi",
            targetWeightKg = 16.0,
            restSeconds = 75,
            notes = "Tarik kettlebell eksplosif ke posisi rack lalu lanjutkan dorong lurus ke atas."
        ),
        PresetExerciseTemplate(
            name = "Dumbbell Thruster",
            muscleGroup = MuscleGroup.KARDIO,
            equipment = "Dumbbell",
            targetSets = 4,
            targetReps = "12-15",
            targetWeightKg = 12.0,
            restSeconds = 75,
            notes = "Gabungkan front squat dalam dengan dorongan overhead press tanpa jeda."
        ),
        PresetExerciseTemplate(
            name = "Man Makers (Dumbbell Conditioning)",
            muscleGroup = MuscleGroup.KARDIO,
            equipment = "Dumbbell",
            targetSets = 3,
            targetReps = "8-10",
            targetWeightKg = 12.0,
            restSeconds = 90,
            notes = "Push-up, row kanan-kiri, lompat squat clean, lalu thruster untuk pembakaran kalori total."
        ),
        PresetExerciseTemplate(
            name = "Turkish Get-Up (Kettlebell)",
            muscleGroup = MuscleGroup.KARDIO,
            equipment = "Kettlebell",
            targetSets = 3,
            targetReps = "4/sisi",
            targetWeightKg = 12.0,
            restSeconds = 90,
            notes = "Bangkit dari posisi telentang ke berdiri tegak sambil menjaga beban lurus di atas."
        ),
        PresetExerciseTemplate(
            name = "Sled Pull (Backward Drag)",
            muscleGroup = MuscleGroup.KARDIO,
            equipment = "Sled Track (Kardio)",
            targetSets = 4,
            targetReps = "40d",
            targetWeightKg = 45.0,
            restSeconds = 90,
            notes = "Berjalan mundur menarik beban sled dengan langkah pendek mantap untuk quads dan lutut.",
            trackingMode = ExerciseTrackingMode.TIMER,
            usesWeight = true,
            timerDirection = TimerDirection.COUNTDOWN,
            targetDurationSeconds = 40
        ),
        PresetExerciseTemplate(
            name = "Bear Crawl Conditioning",
            muscleGroup = MuscleGroup.KARDIO,
            equipment = "Tanpa Alat (Kardio)",
            targetSets = 4,
            targetReps = "30d",
            targetWeightKg = 0.0,
            restSeconds = 60,
            notes = "Merangkak rendah dengan lutut melayang beberapa sentimeter dari lantai.",
            trackingMode = ExerciseTrackingMode.TIMER,
            usesWeight = false,
            timerDirection = TimerDirection.COUNTDOWN,
            targetDurationSeconds = 30
        ),
        PresetExerciseTemplate(
            name = "Jump Squat",
            muscleGroup = MuscleGroup.KARDIO,
            equipment = "Tanpa Alat (Kardio)",
            targetSets = 4,
            targetReps = "15",
            targetWeightKg = 0.0,
            restSeconds = 60,
            notes = "Squat terkontrol lalu lompat vertikal eksplosif dan mendarat empuk di tengah kaki.",
            usesWeight = false
        ),
        PresetExerciseTemplate(
            name = "Alternating Jumping Lunges",
            muscleGroup = MuscleGroup.KARDIO,
            equipment = "Tanpa Alat (Kardio)",
            targetSets = 3,
            targetReps = "16-20",
            targetWeightKg = 0.0,
            restSeconds = 60,
            notes = "Tukar posisi kaki lunge di udara dengan tubuh tetap tegak dan mendarat stabil.",
            usesWeight = false
        ),
        PresetExerciseTemplate(
            name = "High Knees Sprint",
            muscleGroup = MuscleGroup.KARDIO,
            equipment = "Tanpa Alat (Kardio)",
            targetSets = 4,
            targetReps = "30d",
            targetWeightKg = 0.0,
            restSeconds = 45,
            notes = "Lari di tempat secepat mungkin dengan mengangkat lutut setinggi pinggang.",
            trackingMode = ExerciseTrackingMode.TIMER,
            usesWeight = false,
            timerDirection = TimerDirection.COUNTDOWN,
            targetDurationSeconds = 30
        ),
        PresetExerciseTemplate(
            name = "Jumping Jacks",
            muscleGroup = MuscleGroup.KARDIO,
            equipment = "Tanpa Alat (Kardio)",
            targetSets = 3,
            targetReps = "45d",
            targetWeightKg = 0.0,
            restSeconds = 45,
            notes = "Ritme stabil untuk pemanasan jantung atau pemulihan aktif antar interval.",
            trackingMode = ExerciseTrackingMode.TIMER,
            usesWeight = false,
            timerDirection = TimerDirection.COUNTDOWN,
            targetDurationSeconds = 45
        ),
        PresetExerciseTemplate(
            name = "Sprawls Conditioning",
            muscleGroup = MuscleGroup.KARDIO,
            equipment = "Tanpa Alat (Kardio)",
            targetSets = 4,
            targetReps = "12-15",
            targetWeightKg = 0.0,
            restSeconds = 60,
            notes = "Mirip burpee tanpa push-up penuh, fokus pada kecepatan transisi plank ke berdiri.",
            usesWeight = false
        ),
        PresetExerciseTemplate(
            name = "Shuttle Sprint (20m)",
            muscleGroup = MuscleGroup.KARDIO,
            equipment = "Tanpa Alat (Kardio)",
            targetSets = 6,
            targetReps = "25d",
            targetWeightKg = 0.0,
            restSeconds = 60,
            notes = "Lari bolak-balik cepat dengan perubahan arah eksplosif di setiap garis ujung.",
            trackingMode = ExerciseTrackingMode.TIMER,
            usesWeight = false,
            timerDirection = TimerDirection.COUNTDOWN,
            targetDurationSeconds = 25
        ),
        PresetExerciseTemplate(
            name = "Medicine Ball Slam",
            muscleGroup = MuscleGroup.KARDIO,
            equipment = "Slam Ball (Kardio)",
            targetSets = 4,
            targetReps = "15",
            targetWeightKg = 8.0,
            restSeconds = 60,
            notes = "Angkat bola tinggi di atas kepala lalu banting keras ke lantai memakai otot inti."
        ),
        PresetExerciseTemplate(
            name = "Shadowboxing Intervals",
            muscleGroup = MuscleGroup.KARDIO,
            equipment = "Tanpa Alat (Kardio)",
            targetSets = 4,
            targetReps = "2m",
            targetWeightKg = 0.0,
            restSeconds = 60,
            notes = "Kombinasi pukulan cepat jab-cross-hook dan gerak kaki lincah selama 2 menit.",
            trackingMode = ExerciseTrackingMode.TIMER,
            usesWeight = false,
            timerDirection = TimerDirection.COUNTDOWN,
            targetDurationSeconds = 120
        ),
        PresetExerciseTemplate(
            name = "Heavy Bag Boxing Rounds",
            muscleGroup = MuscleGroup.KARDIO,
            equipment = "Samsak (Kardio)",
            targetSets = 5,
            targetReps = "3m",
            targetWeightKg = 0.0,
            restSeconds = 60,
            notes = "Putaran kombinasi pukulan bertenaga dengan pengaturan napas dan rotasi pinggul.",
            trackingMode = ExerciseTrackingMode.TIMER,
            usesWeight = false,
            timerDirection = TimerDirection.COUNTDOWN,
            targetDurationSeconds = 180
        ),
        PresetExerciseTemplate(
            name = "Double-Under Jump Rope",
            muscleGroup = MuscleGroup.KARDIO,
            equipment = "Tali Skipping",
            targetSets = 4,
            targetReps = "30d",
            targetWeightKg = 0.0,
            restSeconds = 60,
            notes = "Putar pergelangan tangan dua kali lebih cepat dalam satu lompatan tinggi yang ringan.",
            trackingMode = ExerciseTrackingMode.TIMER,
            usesWeight = false,
            timerDirection = TimerDirection.COUNTDOWN,
            targetDurationSeconds = 30
        ),
        PresetExerciseTemplate(
            name = "Rowing 500m Sprint",
            muscleGroup = MuscleGroup.KARDIO,
            equipment = "Mesin Dayung",
            targetSets = 3,
            targetReps = "2m",
            targetWeightKg = 0.0,
            restSeconds = 120,
            notes = "Dorong penuh menggunakan kaki terlebih dahulu sebelum menarik pegangan ke dada.",
            trackingMode = ExerciseTrackingMode.TIMER,
            usesWeight = false,
            timerDirection = TimerDirection.COUNTDOWN,
            targetDurationSeconds = 120
        ),
        PresetExerciseTemplate(
            name = "Treadmill Hill Run",
            muscleGroup = MuscleGroup.KARDIO,
            equipment = "Treadmill",
            targetSets = 1,
            targetReps = "15m",
            targetWeightKg = 0.0,
            restSeconds = 60,
            notes = "Lari stabil pada kemiringan 4-6% untuk kekuatan kaki dan daya tahan kardio.",
            trackingMode = ExerciseTrackingMode.TIMER,
            usesWeight = false,
            timerDirection = TimerDirection.COUNTDOWN,
            targetDurationSeconds = 900
        ),
        PresetExerciseTemplate(
            name = "StairMaster HIIT Intervals",
            muscleGroup = MuscleGroup.KARDIO,
            equipment = "Mesin StairMaster",
            targetSets = 6,
            targetReps = "1m",
            targetWeightKg = 0.0,
            restSeconds = 60,
            notes = "Naik tangga kecepatan tinggi 1 menit bergantian tempo pemulihan tanpa bertumpu di pegangan.",
            trackingMode = ExerciseTrackingMode.TIMER,
            usesWeight = false,
            timerDirection = TimerDirection.COUNTDOWN,
            targetDurationSeconds = 60
        )
    )
}
