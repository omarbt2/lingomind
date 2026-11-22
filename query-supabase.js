const { createClient } = require('@supabase/supabase-js');

const supabaseUrl = 'https://fpazflweyimrulwwvsdw.supabase.co';
const supabaseKey = 'sb_publishable_1zTxwQY4Vx-jTRaFxKYJQg__Yf703nG';

const supabase = createClient(supabaseUrl, supabaseKey);

async function queryTables() {
  const tables = ['phases', 'units', 'lessons', 'exercises', 'flashcards'];
  
  console.log('=== Row Counts ===\n');
  
  for (const table of tables) {
    try {
      const { count, error } = await supabase
        .from(table)
        .select('*', { count: 'exact', head: true });
      
      if (error) {
        console.log(`${table}: ERROR - ${error.message}`);
      } else {
        console.log(`${table}: ${count} rows`);
      }
    } catch (err) {
      console.log(`${table}: ERROR - ${err.message}`);
    }
  }
  
  console.log('\n=== Foreign Key Relationships ===\n');
  
  // Get foreign key info from information_schema
  try {
    const { data, error } = await supabase
      .rpc('get_foreign_keys');
    if (error) {
      console.log('FK info: ' + error.message);
    } else {
      console.log('FK relationships:', JSON.stringify(data, null, 2));
    }
  } catch (err) {
    console.log('FK info: ERROR - ' + err.message);
  }
  
  console.log('\n=== Orphan Row Checks ===\n');
  
  // Check orphan rows by fetching all IDs from parent tables and checking
  try {
    // Get all phase IDs
    const { data: phases } = await supabase.from('phases').select('id');
    const phaseIds = new Set(phases?.map(p => p.id) || []);
    
    // Check units for orphan phase_ids
    const { data: units } = await supabase.from('units').select('id, phase_id');
    const orphanUnits = units?.filter(u => u.phase_id && !phaseIds.has(u.phase_id)) || [];
    console.log(`units -> phases (orphans): ${orphanUnits.length} orphan rows`);
    if (orphanUnits.length > 0) {
      console.log(`  Orphan unit IDs: ${orphanUnits.map(u => u.id).join(', ')}`);
      console.log(`  Invalid phase_ids: ${[...new Set(orphanUnits.map(u => u.phase_id))].join(', ')}`);
    }
  } catch (err) {
    console.log(`units -> phases: ERROR - ${err.message}`);
  }
  
  try {
    // Get all unit IDs
    const { data: units } = await supabase.from('units').select('id');
    const unitIds = new Set(units?.map(u => u.id) || []);
    
    // Check lessons for orphan unit_ids
    const { data: lessons } = await supabase.from('lessons').select('id, unit_id');
    const orphanLessons = lessons?.filter(l => l.unit_id && !unitIds.has(l.unit_id)) || [];
    console.log(`lessons -> units (orphans): ${orphanLessons.length} orphan rows`);
    if (orphanLessons.length > 0) {
      console.log(`  Orphan lesson IDs: ${orphanLessons.map(l => l.id).join(', ')}`);
      console.log(`  Invalid unit_ids: ${[...new Set(orphanLessons.map(l => l.unit_id))].join(', ')}`);
    }
  } catch (err) {
    console.log(`lessons -> units: ERROR - ${err.message}`);
  }
  
  try {
    // Get all lesson IDs
    const { data: lessons } = await supabase.from('lessons').select('id');
    const lessonIds = new Set(lessons?.map(l => l.id) || []);
    
    // Check exercises for orphan lesson_ids
    const { data: exercises } = await supabase.from('exercises').select('id, lesson_id');
    const orphanExercises = exercises?.filter(e => e.lesson_id && !lessonIds.has(e.lesson_id)) || [];
    console.log(`exercises -> lessons (orphans): ${orphanExercises.length} orphan rows`);
    if (orphanExercises.length > 0) {
      console.log(`  Orphan exercise IDs: ${orphanExercises.map(e => e.id).slice(0, 10).join(', ')}${orphanExercises.length > 10 ? '...' : ''}`);
      console.log(`  Invalid lesson_ids: ${[...new Set(orphanExercises.map(e => e.lesson_id))].join(', ')}`);
    }
  } catch (err) {
    console.log(`exercises -> lessons: ERROR - ${err.message}`);
  }
  
  try {
    // Get all lesson IDs
    const { data: lessons } = await supabase.from('lessons').select('id');
    const lessonIds = new Set(lessons?.map(l => l.id) || []);
    
    // Check flashcards for orphan lesson_ids
    const { data: flashcards } = await supabase.from('flashcards').select('id, lesson_id');
    const orphanFlashcards = flashcards?.filter(f => f.lesson_id && !lessonIds.has(f.lesson_id)) || [];
    console.log(`flashcards -> lessons (orphans): ${orphanFlashcards.length} orphan rows`);
    if (orphanFlashcards.length > 0) {
      console.log(`  Orphan flashcard IDs: ${orphanFlashcards.map(f => f.id).slice(0, 10).join(', ')}${orphanFlashcards.length > 10 ? '...' : ''}`);
      console.log(`  Invalid lesson_ids: ${[...new Set(orphanFlashcards.map(f => f.lesson_id))].join(', ')}`);
    }
  } catch (err) {
    console.log(`flashcards -> lessons: ERROR - ${err.message}`);
  }
}

queryTables().then(() => console.log('\nDone!')).catch(console.error);