import { readFileSync } from 'node:fs';
import { test, before, after, beforeEach } from 'node:test';
import {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} from '@firebase/rules-unit-testing';
import {
  doc,
  getDoc,
  setDoc,
  updateDoc,
  deleteDoc,
  collection,
  query,
  where,
  getDocs,
  serverTimestamp,
} from 'firebase/firestore';

const PROJECT_ID = 'gen-lang-client-0348865145';
const DATABASE_ID = 'ai-studio-android-repmate-99aa60b7-9f0b-4339-9273-37bc921f4d28';

let testEnv;

before(async () => {
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      databaseId: DATABASE_ID,
      rules: readFileSync('firestore.rules', 'utf8'),
      host: '127.0.0.1',
      port: 8085,
    },
  });
});

after(async () => {
  if (testEnv) await testEnv.cleanup();
});

beforeEach(async () => {
  await testEnv.clearFirestore();
});

function dbFor(uid, emailVerified = true) {
  return testEnv
    .authenticatedContext(uid, { email: `${uid}@example.com`, email_verified: emailVerified })
    .firestore(DATABASE_ID);
}

test('unauthenticated users cannot read or write user profiles or schedules', async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore(DATABASE_ID);
  await assertFails(getDoc(doc(unauthDb, 'users/userA')));
  await assertFails(
    setDoc(doc(unauthDb, 'users/userA'), {
      userId: 'userA',
      displayName: 'Atlet',
      email: 'a@example.com',
      defaultRingtoneId: 'builtin_athletic_bell',
      restRingtoneId: 'builtin_kettlebell_chime',
      volume: 0.85,
      notificationsEnabled: false,
      snoozeMinutes: 5,
      autoStartRestTimer: true,
      createdAt: serverTimestamp(),
      updatedAt: serverTimestamp(),
    })
  );
  await assertFails(getDocs(collection(unauthDb, 'users/userA/schedules')));
});

test('owner can create and update profile while cross-user access is denied', async () => {
  const dbA = dbFor('userA');
  const dbB = dbFor('userB');

  await assertSucceeds(
    setDoc(doc(dbA, 'users/userA'), {
      userId: 'userA',
      displayName: 'Fathan',
      email: 'fathan@example.com',
      defaultRingtoneId: 'builtin_athletic_bell',
      restRingtoneId: 'builtin_kettlebell_chime',
      volume: 0.85,
      notificationsEnabled: false,
      snoozeMinutes: 5,
      autoStartRestTimer: true,
      createdAt: serverTimestamp(),
      updatedAt: serverTimestamp(),
    })
  );

  await assertSucceeds(getDoc(doc(dbA, 'users/userA')));
  await assertFails(getDoc(doc(dbB, 'users/userA')));
  await assertFails(
    updateDoc(doc(dbB, 'users/userA'), { displayName: 'Hijacked', updatedAt: serverTimestamp() })
  );

  await assertSucceeds(
    updateDoc(doc(dbA, 'users/userA'), {
      displayName: 'Fathan Pro',
      volume: 0.9,
      updatedAt: serverTimestamp(),
    })
  );

  // Immutable userId cannot be altered
  await assertFails(
    updateDoc(doc(dbA, 'users/userA'), {
      userId: 'userB',
      updatedAt: serverTimestamp(),
    })
  );
});

test('owner can manage schedules, workoutLogs, and ringtones matching Kotlin queries', async () => {
  const dbA = dbFor('userA');
  const dbB = dbFor('userB');

  // Schedule create + query
  await assertSucceeds(
    setDoc(doc(dbA, 'users/userA/schedules/mon'), {
      userId: 'userA',
      dayKey: 'mon',
      isGymDay: true,
      alarmEnabled: true,
      alarmTime: '06:30',
      focusTitle: 'Upper Push',
      ringtoneId: 'default',
      exercises: [],
      createdAt: serverTimestamp(),
      updatedAt: serverTimestamp(),
    })
  );
  await assertSucceeds(
    getDocs(query(collection(dbA, 'users/userA/schedules'), where('userId', '==', 'userA')))
  );
  await assertFails(
    getDocs(query(collection(dbB, 'users/userA/schedules'), where('userId', '==', 'userA')))
  );

  // WorkoutLog create + update
  await assertSucceeds(
    setDoc(doc(dbA, 'users/userA/workoutLogs/2026-11-20'), {
      userId: 'userA',
      dateKey: '2026-11-20',
      dayKey: 'fri',
      focusTitle: 'Pull Day',
      entries: [],
      completed: false,
      createdAt: serverTimestamp(),
      updatedAt: serverTimestamp(),
    })
  );
  await assertSucceeds(
    updateDoc(doc(dbA, 'users/userA/workoutLogs/2026-11-20'), {
      completed: true,
      updatedAt: serverTimestamp(),
    })
  );

  // Custom ringtone create + delete
  await assertSucceeds(
    setDoc(doc(dbA, 'users/userA/ringtones/tone_1'), {
      userId: 'userA',
      name: 'Gym Bell',
      mimeType: 'audio/mpeg',
      sizeBytes: 12000,
      durationSeconds: 12,
      storageMode: 'cloud',
      audioDataUrl: 'data:audio/mpeg;base64,AAA=',
      createdAt: serverTimestamp(),
      updatedAt: serverTimestamp(),
    })
  );
  await assertFails(deleteDoc(doc(dbB, 'users/userA/ringtones/tone_1')));
  await assertSucceeds(deleteDoc(doc(dbA, 'users/userA/ringtones/tone_1')));
});
