-- ============================================================
-- sema Seed Script
-- Curriculum content: 5 phases, 21 units, 105 lessons
-- Exercises and flashcards for Phase 1 only (phases 2-5 TBD)
-- ============================================================

-- ------------------------------------------------------------
-- 1. PHASES (5 rows)
-- ------------------------------------------------------------
INSERT INTO phases (id, code, title_ar, title_en, cefr_level, order_index) VALUES
(1, 'P1', 'الأساسيات', 'Basics', 'A1', 1),
(2, 'P2', 'الحياة اليومية', 'Daily Life', 'A2', 2),
(3, 'P3', 'المجتمع', 'Society', 'B1', 3),
(4, 'P4', 'المستوى المتقدم', 'Advanced', 'B2', 4),
(5, 'P5', 'الإتقان', 'Mastery', 'C1', 5);

-- ------------------------------------------------------------
-- 2. UNITS (21 rows)
-- ------------------------------------------------------------
INSERT INTO units (id, phase_id, title_ar, title_en, description_ar, icon, order_index) VALUES
-- Phase 1: Basics (A1) — 4 units
(1,  1, 'التحيات والتعريف', 'Greetings & Introductions', 'تعلم التحيات الأساسية والتعريف بالنفس', 'wave', 1),
(2,  1, 'الأرقام والعد', 'Numbers & Counting', 'تعلم الأرقام من واحد إلى مائة', 'hash', 2),
(3,  1, 'الألوان والصفات', 'Colors & Adjectives', 'تعلم أسماء الألوان والصفات الشائعة', 'palette', 3),
(4,  1, 'الطعام والمشروبات', 'Food & Drinks', 'تعلم مفردات الطعام والمشروبات اليومية', 'utensils', 4),

-- Phase 2: Daily Life (A2) — 5 units
(5,  2, 'التسوق والملابس', 'Shopping & Clothes', 'تعلم مفردات التسوق والملابس', 'shopping-bag', 1),
(6,  2, 'الصحة والجسم', 'Health & Body', 'تعلم مفردات الصحة وأجزاء الجسم', 'heart-pulse', 2),
(7,  2, 'الطقس والطبيعة', 'Weather & Nature', 'تعلم وصف الطقس والطبيعة', 'cloud-sun', 3),
(8,  2, 'الوقت والتقويم', 'Time & Calendar', 'تعلم التعبير عن الوقت والتاريخ', 'clock', 4),
(9,  2, 'العائلة والعلاقات', 'Family & Relationships', 'تعلم مفردات العائلة والعلاقات الاجتماعية', 'users', 5),

-- Phase 3: Society (B1) — 4 units
(10, 3, 'العمل والمهن', 'Work & Professions', 'تعلم مفردات بيئة العمل والمهن', 'briefcase', 1),
(11, 3, 'السفر والنقل', 'Travel & Transport', 'تعلم مفردات السفر ووسائل النقل', 'plane', 2),
(12, 3, 'الهوايات والترفيه', 'Hobbies & Entertainment', 'تعلم مفردات الهوايات والأنشطة الترفيهية', 'gamepad-2', 3),
(13, 3, 'التعليم والتعلم', 'Education & Learning', 'تعلم مفردات البيئة التعليمية', 'graduation-cap', 4),

-- Phase 4: Advanced (B2) — 4 units
(14, 4, 'الأعمال والمالية', 'Business & Finance', 'تعلم مفردات الأعمال والمالية', 'landmark', 1),
(15, 4, 'العلوم والتكنولوجيا', 'Science & Technology', 'تعلم مفردات العلوم والتكنولوجيا', 'cpu', 2),
(16, 4, 'الثقافة والمجتمع', 'Culture & Society', 'تعلم مفردات الثقافة والمجتمع', 'globe', 3),
(17, 4, 'البيئة والاستدامة', 'Environment & Sustainability', 'تعلم مفردات البيئة والاستدambia', 'leaf', 4),

-- Phase 5: Mastery (C1) — 4 units
(18, 5, 'الأدب والفنون', 'Literature & Arts', 'تعلم مفردات الأدب والفنون', 'book-open', 1),
(19, 5, 'الفلسفة والأخلاق', 'Philosophy & Ethics', 'تعلم مفردات الفلسفة والأخلاق', 'brain', 2),
(20, 5, 'المشكلات العالمية', 'Global Issues', 'تعلم مفردات المشكلات العالمية', 'alert-triangle', 3),
(21, 5, 'التواصل المهني', 'Professional Communication', 'تعلم مفردات التواصل المهني', 'message-square', 4);

-- ------------------------------------------------------------
-- 3. LESSONS (105 rows — 5 per unit)
-- ------------------------------------------------------------
INSERT INTO lessons (id, unit_id, code, title_ar, title_en, cefr_level, hook_text, scene_context, dialogue, focus_rule, order_index, estimated_min, is_published) VALUES
-- ==================== Phase 1: Basics (A1) ====================
-- Unit 1: Greetings & Introductions
(1,  1, 'P1U1L1', 'تحيات يومية', 'Daily Greetings', 'A1', 'كيف تُحيّي الناس في الصباح؟', 'تلتقي بجارك في المبنى السكني صباح يوم الإثنين.', 'Good morning! How are you?', 'استخدام تحية الصباح والسؤال عن الحال', 1, 5, true),
(2,  1, 'P1U1L2', 'التعريف بالنفس', 'Introducing Yourself', 'A1', 'كيف تُعرّف بنفسك للآخرين؟', 'أنت في حفلة وتلتقي بشخص جديد لأول مرة.', 'Hi, my name is Omar. What is your name?', 'استخدام أداة التعريف والسؤال عن الاسم', 2, 5, true),
(3,  1, 'P1U1L3', 'الأسئلة الشخصية', 'Personal Questions', 'A1', 'ما هي الأسئلة الأساسية عن نفسك؟', 'أنت في استبيان تعبئه في универсتك.', 'Where are you from? I am from Egypt.', 'استخدام أداة الربط Where و何ـن', 3, 5, true),
(4,  1, 'P1U1L4', 'الوداع والود', 'Saying Goodbye', 'A1', 'كيف تودّع الأصدقاء؟', 'أنتم تنتهون من لقاء ودي في مقهى.', 'It was nice meeting you. Goodbye!', 'استخدام تعبيرات الوداع المهذبة', 4, 5, true),
(5,  1, 'P1U1L5', 'تحيات رسمية', 'Formal Greetings', 'A1', 'كيف تُحيّي شخصاً في بيئة رسمية؟', 'أنت في أول يوم عمل في مكتب جديد.', 'Good afternoon, sir. It is a pleasure to meet you.', 'استخدام التحية الرسمية وصيغ المجاملة', 5, 5, true),

-- Unit 2: Numbers & Counting
(6,  2, 'P1U2L1', 'الأرقام من واحد إلى عشرة', 'Numbers 1-10', 'A1', 'كيف تحسب الأرقام بالإنجليزية؟', 'أنت في محل تذكّرات وتريد شراء بطاقتين.', 'I would like two tickets, please.', 'استخدام الأرقام في الطلبات', 1, 5, true),
(7,  2, 'P1U2L2', 'الأرقام من أحد عشر إلى عشرين', 'Numbers 11-20', 'A1', 'كيف تقول الأرقام من 11 إلى 20؟', 'تعدّ الأشجار في الحديقة مع صديقك.', 'There are fifteen trees in this garden.', 'استخدام الأرقام في العد والوصف', 2, 5, true),
(8,  2, 'P1U2L3', 'الأرقام من عشرين إلى مائة', 'Numbers 20-100', 'A1', 'كيف تحسب العقود والمئات؟', 'أنت تحاول حساب تكلفة مشترياتك.', 'The book costs thirty-five dollars.', 'استخدام الأرقام في الأسعار', 3, 5, true),
(9,  2, 'P1U2L4', 'الأرقام الترتيبية', 'Ordinal Numbers', 'A1', 'كيف ترتّب الأشياء بالأرقام؟', 'أنت في سباق رياضي وتحتفل بالوصول.', 'She finished in second place!', 'استخدام الأرقام الترتيبية', 4, 5, true),
(10, 2, 'P1U2L5', 'الأرقام في الحياة اليومية', 'Numbers in Daily Life', 'A1', 'متى تستخدم الأرقام كل يوم؟', 'أنت تملأ نموذج في بنك.', 'My phone number is 555-0123.', 'استخدام الأرقام في عناوين وأرقام هواتف', 5, 5, true),

-- Unit 3: Colors & Adjectives
(11, 3, 'P1U3L1', 'الألوان الأساسية', 'Basic Colors', 'A1', 'كيف تصف ألوان الأشياء؟', 'أنت تشتري قميصاً جديداً في متجر ملابس.', 'I like the blue shirt. It looks nice.', 'استخدام أسماء الألوان مع الأسماء', 1, 5, true),
(12, 3, 'P1U3L2', 'الألوان المتقدمة', 'Advanced Colors', 'A1', 'كيف تميّز بين درجات الألوان؟', 'أنت تصف لوحة فنية في معرض.', 'The sunset has shades of orange and pink.', 'استخدام درجات الألوان والصفات', 2, 5, true),
(13, 3, 'P1U3L3', 'صفات الحجم والشكل', 'Size & Shape Adjectives', 'A1', 'كيف تصف حجم وشكل الأشياء؟', 'أنت تشتري طاولة جديدة لمنزلك.', 'This table is round and the big one is in the corner.', 'استخدام صفات الحجم والشكل', 3, 5, true),
(14, 3, 'P1U3L4', 'صفات المزاج والشعور', 'Mood & Feeling Adjectives', 'A1', 'كيف تصف مشاعرك ومزاجك؟', 'تسأل صديقك كيف يشعر اليوم.', 'I am happy today. Yesterday I was tired.', 'استخدام صفات المشاعر مع الأزمنة', 4, 5, true),
(15, 3, 'P1U3L5', 'صفات الوصف العام', 'General Descriptive Adjectives', 'A1', 'كيف تصف الأشياء بشكل عام؟', 'أنت تكتب وصفاً لمنتج على الإنترنت.', 'The phone is new, fast, and expensive.', 'استخدام عدة صفات لوصف شيء واحد', 5, 5, true),

-- Unit 4: Food & Drinks
(16, 4, 'P1U4L1', 'الأكل الأساسي', 'Basic Food Items', 'A1', 'كيف تطلب طعاماً في مطعم؟', 'أنت في مطعم وتقرأ القائمة.', 'I would like a salad and some bread, please.', 'استخدام أسماء الطعام في الطلبات', 1, 5, true),
(17, 4, 'P1U4L2', 'الفواكه والخضروات', 'Fruits & Vegetables', 'A1', 'كيف تشتري فواكه وخضروات؟', 'أنت في سوق الفواكه والخضروات.', 'I need two kilos of apples and some tomatoes.', 'استخدام أسماء الفواكه والخضروات', 2, 5, true),
(18, 4, 'P1U4L3', 'المشروبات', 'Drinks & Beverages', 'A1', 'كيف تطلب مشروباً؟', 'أنت في مقهى وتريد طلب مشروب.', 'Can I have a cup of coffee with sugar?', 'استخدام أسماء المشروبات والإضافات', 3, 5, true),
(19, 4, 'P1U4L4', 'الأكل والشرب في المنزل', 'Food & Drink at Home', 'A1', 'كيف تحضر وجبة في المنزل؟', 'أنت تحضّر الفطور مع عائلتك.', 'We need eggs, milk, and butter for breakfast.', 'استخدام مفردات الإعداد والطبخ', 4, 5, true),
(20, 4, 'P1U4L5', 'المطابخ والمطاعم', 'Kitchens & Restaurants', 'A1', 'كيف تصف تجربة تناول الطعام؟', 'أنت تصف تجربتك في مطعم جديد لصديقك.', 'The food was delicious and the service was great.', 'استخدام صفات وصف تجربة الطعام', 5, 5, true),

-- ==================== Phase 2: Daily Life (A2) ====================
-- Unit 5: Shopping & Clothes
(21, 5, 'P2U5L1', 'التسوق في المتاجر', 'Shopping at Stores', 'A2', 'كيف تشتري ملابس في متجر؟', 'أنت في مركز تجاري تبحث عن قميص.', 'How much does this shirt cost? It is on sale.', 'استخدام أسئلة التكلفة والعروض', 1, 5, true),
(22, 5, 'P2U5L2', 'أنواع الملابس', 'Types of Clothing', 'A2', 'كيف تميّز بين أنواع الملابس؟', 'أنت تصفّ خزانة ملابسك لصديقك.', 'I wear a jacket in winter and shorts in summer.', 'استخدام أسماء الملابس مع المناسبات', 2, 5, true),
(23, 5, 'P2U5L3', 'القياسات والأحجام', 'Sizes & Measurements', 'A2', 'كيف تسأل عن القياس المناسب؟', 'أنت في متجر ملابس وتبحث عن المقاس المناسب.', 'Do you have this in medium? I think large is too big.', 'استخدام أسماء القياسات والمقاسات', 3, 5, true),
(24, 5, 'P2U5L4', 'الدفع والمشتريات', 'Payment & Purchases', 'A2', 'كيف تدفع ثمن مشترياتك؟', 'أنت عند كاشف الدفع في متجر.', 'I will pay by credit card. Do you accept cash?', 'استخدام تعبيرات الدفع والشراء', 4, 5, true),
(25, 5, 'P2U5L5', 'الإرجاع والاستبدال', 'Returns & Exchanges', 'A2', 'كيف تُعيد منتجاً شريته؟', 'أنت في خدمة عملاء متجر بإيصال.', 'I bought this yesterday but it is the wrong size. Can I exchange it?', 'استخدام تعبيرات الإرجاع والاستبدال', 5, 5, true),

-- Unit 6: Health & Body
(26, 6, 'P2U6L1', 'أجزاء الجسم', 'Parts of the Body', 'A2', 'كيف تصف أجزاء جسمك للطبيب؟', 'أنت في عيادة الطبيب وتشرح ألمك.', 'I have a pain in my shoulder and my head hurts.', 'استخدام أسماء أجزاء الجسم مع الأعراض', 1, 5, true),
(27, 6, 'P2U6L2', 'الأعراض والمرض', 'Symptoms & Illness', 'A2', 'كيف تصف أعراضك للطبيب؟', 'أنت مريض وتحدث الطبيب عبر الهاتف.', 'I have a fever and a sore throat since yesterday.', 'استخدام تعبيرات وصف الأعراض', 2, 5, true),
(28, 6, 'P2U6L3', 'العلاج والدواء', 'Treatment & Medicine', 'A2', 'كيف تفهم تعليمات الطبيب؟', 'أنت تستلم وصفة طبية من الصيدلي.', 'Take this medicine twice a day after meals.', 'استخدام تعليمات الدواء والعلاج', 3, 5, true),
(29, 6, 'P2U6L4', 'النصيحة الصحية', 'Health Advice', 'A2', 'كيف تعطي نصائح صحية؟', 'أنت تتحدث مع صديقك عن نمط الحياة الصحي.', 'You should drink more water and exercise every day.', 'استخدام Should للنصيحة', 4, 5, true),
(30, 6, 'P2U6L5', 'الوقاية والصحة', 'Prevention & Wellness', 'A2', 'كيف تتحدث عن الوقاية من المرض؟', 'أنت في صيدلية تشتري فيتامينات.', 'It is important to eat healthy food and sleep well.', 'استخدام تعبيرات الأهمية والوقاية', 5, 5, true),

-- Unit 7: Weather & Nature
(31, 7, 'P2U7L1', 'وصف الطقس', 'Describing Weather', 'A2', 'كيف تصف طقس اليوم؟', 'أنت تتحدث مع صديقك في الهاتف عن طقس اليوم.', 'It is sunny and hot today. The temperature is thirty degrees.', 'استخدام أسماء الطقس والحرارة', 1, 5, true),
(32, 7, 'P2U7L2', 'الفصول والمناخ', 'Seasons & Climate', 'A2', 'كيف تميّز بين الفصول؟', 'أنت تكتب بريداً إلكترونياً لصديق في دولة أخرى.', 'I prefer spring because the weather is mild and the flowers bloom.', 'استخدام أسماء الفصول والصفات', 2, 5, true),
(33, 7, 'P2U7L3', 'الحيوانات والنباتات', 'Animals & Plants', 'A2', 'كيف تصف الحيوانات والنباتات؟', 'أنت في حديقة حيوان مع أطفالك.', 'The giraffe has a long neck and it eats leaves from tall trees.', 'استخدام أسماء الحيوانات والنباتات', 3, 5, true),
(34, 7, 'P2U7L4', 'الكوارث الطبيعية', 'Natural Disasters', 'A2', 'كيف تصف الكوارث الطبيعية؟', 'أنت تشاهد نشرة أخبار عن عاصفة.', 'The storm caused heavy rain and strong winds in the region.', 'استخدام مفردات الكوارث الطبيعية', 4, 5, true),
(35, 7, 'P2U7L5', 'البيئة الطبيعية', 'Natural Environment', 'A2', 'كيف تصف المناظر الطبيعية؟', 'أنت في رحلة استكشافية في الجبال.', 'The mountain is very high and the lake is crystal clear.', 'استخدام صفات الوصف الطبيعي', 5, 5, true),

-- Unit 8: Time & Calendar
(36, 8, 'P2U8L1', 'الوقت بالساعات', 'Telling Time', 'A2', 'كيف تسأل عن الوقت وتُجيب؟', 'أنت في محطة القطار وتسأل عن موعد القطار.', 'What time does the train leave? It leaves at half past three.', 'استخدام التعبيرات عن الوقت', 1, 5, true),
(37, 8, 'P2U8L2', 'التوقيت اليومي', 'Daily Routine & Time', 'A2', 'كيف تصف rutine يومك؟', 'أنت تتحدث مع صديقك عن عاداتك اليومية.', 'I wake up at seven o''clock and go to work at eight.', 'استخدام الأزمنة مع الروتين', 2, 5, true),
(38, 8, 'P2U8L3', 'التواريخ والأحداث', 'Dates & Events', 'A2', 'كيف تتحدث عن التواريخ؟', 'أنت تخطط لحفل عيد ميلاد صديقك.', 'My birthday is on the fifteenth of March. When is yours?', 'استخدام التواريخ والتقويم', 3, 5, true),
(39, 8, 'P2U8L4', 'المدة الزمنية', 'Duration & Time Expressions', 'A2', 'كيف تصف مدة شيء ما؟', 'أنت تشرح لصديقك كم استغرقت رحلتك.', 'I studied English for two hours and then watched a movie.', 'استخدام For و Since مع المدة', 4, 5, true),
(40, 8, 'P2U8L5', 'التخطيط للمستقبل', 'Planning for the Future', 'A2', 'كيف تخطط لمواعيدك المستقبلية؟', 'أنت تتحدث مع صديقك عن خططك لعطلة نهاية الأسبوع.', 'I am going to visit my parents next Saturday.', 'استخدام Going to للمستقبل', 5, 5, true),

-- Unit 9: Family & Relationships
(41, 9, 'P2U9L1', 'أفراد العائلة', 'Family Members', 'A2', 'كيف تصف أفراد عائلتك؟', 'أنت تعرض صورة عائلتك لصديقك.', 'This is my mother. She is a teacher. She is very kind.', 'استخدام أسماء أفراد العائلة والصفات', 1, 5, true),
(42, 9, 'P2U9L2', 'العلاقات العائلية', 'Family Relationships', 'A2', 'كيف تصف العلاقات بين أفراد عائلتك؟', 'أنت تتحدث عن عائلتك في حديث عام.', 'My brother is older than me. My sister is a doctor.', 'استخدام المقارنات في العلاقات', 2, 5, true),
(43, 9, 'P2U9L3', 'الأصدقاء والمعارف', 'Friends & Acquaintances', 'A2', 'كيف تصف أصدقاءك وعلاقاتك؟', 'أنت تكتب تعليقاً على صورة لصديق قديم.', 'We have been friends since high school. He is very funny.', 'استخدام صفات الأشخاص والعلاقات', 3, 5, true),
(44, 9, 'P2U9L4', 'الاحتفالات والأعياد', 'Celebrations & Holidays', 'A2', 'كيف تصف احتفالاتك وolidaysك؟', 'أنت تخطط لحفل تخرّج لابنك.', 'We celebrate Eid with our family. We eat special food and wear new clothes.', 'استخدام مفردات الاحتفالات', 4, 5, true),
(45, 9, 'P2U9L5', 'التعبير عن العواطف', 'Expressing Emotions', 'A2', 'كيف تعبر عن مشاعرك للآخرين؟', 'أنت تكتب رسالة شكر لصديق ساعدك في وقت صعب.', 'I am so grateful for your help. You are a true friend.', 'استخدام تعبيرات العواطف والشكر', 5, 5, true),

-- ==================== Phase 3: Society (B1) ====================
-- Unit 10: Work & Professions
(46, 10, 'P3U10L1', 'المهن والوظائف', 'Jobs & Professions', 'B1', 'كيف تسأل عن مهنة شخص ما؟', 'أنت في حفل ت networking وتتعرف على أناس جدد.', 'What do you do for a living? I am a software engineer.', 'استخدام أسئلة العمل والمهنة', 1, 5, true),
(47, 10, 'P3U10L2', 'بيئة المكتب', 'Office Environment', 'B1', 'كيف تصف بيئة عملك؟', 'أنت في مقابلة عمل وتصف بيئة المكتب.', 'We have an open-plan office with modern equipment.', 'استخدام مفردات بيئة العمل', 2, 5, true),
(48, 10, 'P3U10L3', 'المقابلات والوظائف', 'Interviews & Applications', 'B1', 'كيف تتحدث عن خبراتك في مقابلة عمل؟', 'أنت في مقابلة وظيفية وتصف خبراتك السابقة.', 'In my previous role, I managed a team of five people.', 'استخدام المضارع التام للخبرات', 3, 5, true),
(49, 10, 'P3U10L4', 'المهام والمسؤوليات', 'Tasks & Responsibilities', 'B1', 'كيف تصف مهام عملك اليومية؟', 'أنت تشرح لزميلك الجديد مهامه.', 'Your main responsibilities include answering emails and meeting clients.', 'استخدام المفردات المهنية والمسؤوليات', 4, 5, true),
(50, 10, 'P3U10L5', 'التقدم الوظيفي', 'Career Progress', 'B1', 'كيف تتحدث عن تقدمك المهني؟', 'أنت في مقابلة ترقية وتصف تطورك.', 'I have been working here for three years and I have learned a lot.', 'استخدام المضارع التام المستمر للخبرة', 5, 5, true),

-- Unit 11: Travel & Transport
(51, 11, 'P3U11L1', 'حجز التذاكر', 'Booking Tickets', 'B1', 'كيف تحجز تذكرة سفر؟', 'أنت في مطار وتسأل عن رحلة.', 'I would like to book a one-way ticket to London, please.', 'استخدام تعبيرات الحجز والسفر', 1, 5, true),
(52, 11, 'P3U11L2', 'المطارات وال Flight', 'Airports & Flights', 'B1', 'كيف تمرّ بمطار؟', 'أنت في مطار وتكتب بريداً إلكترونياً.', 'The flight takes about four hours. We will arrive at 3 PM local time.', 'استخدام مفردات المطار والرحلات', 2, 5, true),
(53, 11, 'P3U11L3', 'وسائل النقل العام', 'Public Transport', 'B1', 'كيف تستخدم وسائل النقل العام؟', 'أنت في مدينة جديدة وتحتاج للتنقل.', 'You can take the bus number 12 or the subway to get to the museum.', 'استخدام مفردات النقل العام', 3, 5, true),
(54, 11, 'P3U11L4', 'الإقامة والفندقة', 'Accommodation & Hotels', 'B1', 'كيف تحجز فندقاً؟', 'أنت تتصل بفندق لحجز غرفة.', 'I need a double room for three nights. Is breakfast included?', 'استخدام مفردات الفنادق والإقامة', 4, 5, true),
(55, 11, 'P3U11L5', 'السياحة والاستكشاف', 'Tourism & Exploration', 'B1', 'كيف تستكشف مدينة جديدة؟', 'أنت في رحلة سياحية وتتحدث مع دليل محلي.', 'What are the must-see attractions in this city?', 'استخدام مفردات السياحة والاستكشاف', 5, 5, true),

-- Unit 12: Hobbies & Entertainment
(56, 12, 'P3U12L1', 'الهوايات والاهتمامات', 'Hobbies & Interests', 'B1', 'كيف تتحدث عن هواياتك؟', 'أنت في لقاء اجتماعي وتسأل عن هوايات الآخرين.', 'I enjoy reading books and playing chess in my free time.', 'استخدام enjoy و like مع الاهتمامات', 1, 5, true),
(57, 12, 'P3U12L2', 'الرياضة واللياقة', 'Sports & Fitness', 'B1', 'كيف تتحدث عن نشاطك الرياضي؟', 'أنت في نادي رياضي وتسأل عن البرامج.', 'I go to the gym three times a week and I play football on weekends.', 'استخدام مفردات الرياضة واللياقة', 2, 5, true),
(58, 12, 'P3U12L3', 'الأفلام والموسيقى', 'Movies & Music', 'B1', 'كيف تصف تفضيلاتك في الأفلام والموسيقى؟', 'أنت تناقش فيلم شاهدته مؤخراً مع صديقك.', 'I prefer action movies. The soundtrack was amazing.', 'استخدام مفردات الترفيه والتفضيلات', 3, 5, true),
(59, 12, 'P3U12L4', 'القراءة والمكتبات', 'Reading & Libraries', 'B1', 'كيف تصف تجربتك في القراءة؟', 'أنت في مكتبة تبحث عن كتاب.', 'I am looking for a novel about history. Do you have any recommendations?', 'استخدام مفردات القراءة والمكتبات', 4, 5, true),
(60, 12, 'P3U12L5', 'الخروجات والتسلية', 'Outings & Fun Activities', 'B1', 'كيف تقضي وقت فراغك؟', 'أنت تخطط لخروج مع أصدقاء في عطلة نهاية الأسبوع.', 'We could go to the new exhibition at the art gallery.', 'استخدام Could للاقتراحات والتخطيط', 5, 5, true),

-- Unit 13: Education & Learning
(61, 13, 'P3U13L1', 'البيئة التعليمية', 'Educational Environment', 'B1', 'كيف تصف تجربتك التعليمية؟', 'أنت تتحدث مع طالب دولي عن تجربته.', 'The university has excellent facilities and a large library.', 'استخدام مفردات التعليم والجامعات', 1, 5, true),
(62, 13, 'P3U13L2', 'المواد الدراسية', 'Academic Subjects', 'B1', 'كيف تتحدث عن المواد التي تدرسها؟', 'أنت في حديث أكاديمي مع زميل.', 'Mathematics is challenging but interesting. I prefer literature.', 'استخدام مفردات المواد الدراسية', 2, 5, true),
(63, 13, 'P3U13L3', 'الامتحانات والتقييم', 'Exams & Assessment', 'B1', 'كيف تصف تجربة الامتحان؟', 'أنت بعد امتحان وتتحدث مع زميلك.', 'The exam was difficult but I think I did well on the essay question.', 'استخدام مفردات الامتحانات والتقييم', 3, 5, true),
(64, 13, 'P3U13L4', 'المهارات الأكاديمية', 'Academic Skills', 'B1', 'كيف تصف مهاراتك الأكاديمية؟', 'أنت في ورشة عمل عن مهارات التعلم.', 'I need to improve my research and writing skills.', 'استخدام مفردات المهارات الأكاديمية', 4, 5, true),
(65, 13, 'P3U13L5', 'التعليم المستمر', 'Lifelong Learning', 'B1', 'كيف تتحدث عن التعلم المستمر؟', 'أنت تناقش أهمية التعلم المستمر مع صديقك.', 'Learning new skills is important for personal and professional growth.', 'استخدام مفردات التعليم المستمر', 5, 5, true),

-- ==================== Phase 4: Advanced (B2) ====================
-- Unit 14: Business & Finance
(66, 14, 'P4U14L1', 'المفاهيم المالية الأساسية', 'Basic Financial Concepts', 'B2', 'كيف تشرح المفاهيم المالية؟', 'أنت في ورشة عمل مالية وتشرح للمبتدئين.', 'Budgeting means planning how to spend and save your money wisely.', 'استخدام مفردات المالية والميزانية', 1, 5, true),
(67, 14, 'P4U14L2', 'الاستثمار والادخار', 'Investing & Saving', 'B2', 'كيف تتحدث عن الاستثمار؟', 'أنت تتحدث مع مستشار مالي عن خياراتك.', 'Diversifying your investments can help reduce financial risk.', 'استخدام مفردات الاستثمار والادخار', 2, 5, true),
(68, 14, 'P4U14L3', 'المؤسسات المالية', 'Financial Institutions', 'B2', 'كيف تصف المؤسسات المالية؟', 'أنت في بنك تفتح حساباً جديداً.', 'We offer different types of accounts depending on your financial goals.', 'استخدام مفردات البنوك والمؤسسات المالية', 3, 5, true),
(69, 14, 'P4U14L4', 'إدارة الميزانية', 'Budget Management', 'B2', 'كيف تدير ميزانيتك الشخصية؟', 'أنت تتحدث مع صديقك عن إدارة مصروفاته.', 'I track all my expenses and try to save at least twenty percent of my income.', 'استخدام مفردات إدارة المصروفات', 4, 5, true),
(70, 14, 'P4U14L5', 'المشاريع الناشئة', 'Entrepreneurship', 'B2', 'كيف تتحدث عن بدء مشروع؟', 'أنت في مؤتمر أعمال وتتحدث عن تجربتك في ريادة الأعمال.', 'Starting a business requires careful planning and sufficient capital.', 'استخدام مفردات ريادة الأعمال', 5, 5, true),

-- Unit 15: Science & Technology
(71, 15, 'P4U15L1', 'الابتكار التكنولوجي', 'Technological Innovation', 'B2', 'كيف تصف الابتكارات التكنولوجية؟', 'أنت في مؤتمر تكنولوجي وتتحدث عن تطبيق جديد.', 'Artificial intelligence is transforming many industries around the world.', 'استخدام مفردات التكنولوجيا والابتكار', 1, 5, true),
(72, 15, 'P4U15L2', 'الأبحاث العلمية', 'Scientific Research', 'B2', 'كيف تصف عملية البحث العلمي؟', 'أنت في مؤتمر علمي وتشرح نتائج بحثك.', 'Our research focused on developing sustainable energy solutions.', 'استخدام مفردات البحث العلمي', 2, 5, true),
(73, 15, 'P4U15L3', 'التكنولوجيا في الحياة اليومية', 'Technology in Daily Life', 'B2', 'كيف تصف تأثير التكنولوجيا على حياتك؟', 'أنت تناقش تأثير الهواتف الذكية مع صديقك.', 'Smartphones have changed the way we communicate and access information.', 'استخدام مفردات التكنولوجيا اليومية', 3, 5, true),
(74, 15, 'P4U15L4', 'الأخلاقيات العلمية', 'Scientific Ethics', 'B2', 'كيف تناقش الأخلاقيات العلمية؟', 'أنت في نقاش أكاديمي حول أخلاقيات التكنولوجيا.', 'We must consider the ethical implications of genetic engineering.', 'استخدام مفردات الأخلاقيات العلمية', 4, 5, true),
(75, 15, 'P4U15L5', 'المستقبل التكنولوجي', 'Technological Future', 'B2', 'كيف تتوقع المستقبل التكنولوجي؟', 'أنت في مقال صحفي تتوقع التطورات التكنولوجية.', 'Quantum computing could revolutionize data processing in the next decade.', 'استخدام مفردات المستقبل التكنولوجي', 5, 5, true),

-- Unit 16: Culture & Society
(76, 16, 'P4U16L1', 'التنوع الثقافي', 'Cultural Diversity', 'B2', 'كيف تتحدث عن التنوع الثقافي؟', 'أنت في مؤتمر دولي وتتحدث عن الثقافة.', 'Cultural diversity enriches our understanding of the world.', 'استخدام مفردات التنوع الثقافي', 1, 5, true),
(77, 16, 'P4U16L2', 'التقاليد والعادات', 'Traditions & Customs', 'B2', 'كيف تصف التقاليد والعادات؟', 'أنت تكتب مقالاً عن تقاليد بلدك.', 'Traditional customs play an important role in preserving cultural identity.', 'استخدام مفردات التقاليد والعادات', 2, 5, true),
(78, 16, 'P4U16L3', 'التواصل بين الثقافات', 'Cross-Cultural Communication', 'B2', 'كيف تتعامل مع الاختلافات الثقافية؟', 'أنت في بيئة عمل دولية وتتعامل مع زملاء من ثقافات مختلفة.', 'Understanding cultural differences helps build stronger professional relationships.', 'استخدام مفردات التواصل بين الثقافات', 3, 5, true),
(79, 16, 'P4U16L4', 'المجتمع المدني', 'Civil Society', 'B2', 'كيف تصف دور المجتمع المدني؟', 'أنت في ندوة مجتمعية وتتحدث عن المشاركة المدنية.', 'Active citizenship is essential for a healthy democracy.', 'استخدام مفردات المجتمع المدني', 4, 5, true),
(80, 16, 'P4U16L5', 'التحول الاجتماعي', 'Social Change', 'B2', 'كيف تتحدث عن التحولات الاجتماعية؟', 'أنت في مؤتمر اجتماعي وتناقش التحولات.', 'Technology has accelerated social change in many parts of the world.', 'استخدام مفردات التحولات الاجتماعية', 5, 5, true),

-- ==================== Phase 5: Mastery (C1) ====================
-- Unit 17: Environment & Sustainability
(81, 17, 'P5U17L1', 'التحديات البيئية', 'Environmental Challenges', 'C1', 'كيف تناقش التحديات البيئية العالمية؟', 'أنت في قمة بيئية وتتحدث عن التغير المناخي.', 'Climate change poses a significant threat to global food security.', 'استخدام مفردات التحديات البيئية', 1, 5, true),
(82, 17, 'P5U17L2', 'الطاقة المتجددة', 'Renewable Energy', 'C1', 'كيف تصف حلول الطاقة المتجددة؟', 'أنت في مؤتمر طاقة وتشرح مشروعك.', 'Solar and wind energy are becoming increasingly cost-effective alternatives to fossil fuels.', 'استخدام مفردات الطاقة المتجددة', 2, 5, true),
(83, 17, 'P5U17L3', 'الاستهلاك المستدام', 'Sustainable Consumption', 'C1', 'كيف تتحدث عن الاستهلاك المستدام؟', 'أنت في ورشة عمل عن الاستدامة.', 'Making conscious consumer choices can significantly reduce our environmental footprint.', 'استخدام مفردات الاستهلاك المستدام', 3, 5, true),
(84, 17, 'P5U17L4', 'حماية التنوع البيئي', 'Biodiversity Conservation', 'C1', 'كيف تتحدث عن حماية التنوع البيولوجي؟', 'أنت في منتزه وطني وתتحدث عن الحفاظ على التنوع البيولوجي.', 'Protecting endangered species requires international cooperation and local action.', 'استخدام مفردات الحفاظ على التنوع البيولوجي', 4, 5, true),
(85, 17, 'P5U17L5', 'السياسات البيئية', 'Environmental Policy', 'C1', 'كيف تناقش السياسات البيئية؟', 'أنت في نقاش سياسي عن سياسات البيئة.', 'Effective environmental policies must balance economic growth with ecological preservation.', 'استخدام مفردات السياسات البيئية', 5, 5, true),

-- Unit 18: Literature & Arts
(86, 18, 'P5U18L1', 'تحليل الأدب', 'Literary Analysis', 'C1', 'كيف تحلل النصوص الأدبية؟', 'أنت في درس أدب وتحلل قصيدة.', 'The author uses metaphor to convey the character''s internal struggle.', 'استخدام مفردات التحليل الأدبي', 1, 5, true),
(87, 18, 'P5U18L2', 'الفنون البصرية', 'Visual Arts', 'C1', 'كيف تصف وتحلل الأعمال الفنية؟', 'أنت في معرض فني وتكتب مقالاً.', 'The painting uses bold colors to express raw emotion and vulnerability.', 'استخدام مفردات الفنون البصرية', 2, 5, true),
(88, 18, 'P5U18L3', 'الأدب المقارن', 'Comparative Literature', 'C1', 'كيف تقارن بين أعمال أدبية مختلفة؟', 'أنت في ندوة أكاديمية تقارن بين روايتين.', 'Both novels explore themes of identity and belonging, but from different cultural perspectives.', 'استخدام مفردات الأدب المقارن', 3, 5, true),
(89, 18, 'P5U18L4', 'الإبداع والكتابة', 'Creativity & Writing', 'C1', 'كيف تتحدث عن عملية الإبداع؟', 'أنت في ورشة كتابة وتشير إلى أسلوبك.', 'Writing allows us to explore complex emotions and challenge conventional thinking.', 'استخدام مفردات الإبداع والكتابة', 4, 5, true),
(90, 18, 'P5U18L5', 'فنون الأداء', 'Performing Arts', 'C1', 'كيف تصف عروض فنون الأداء؟', 'أنت في مسرح وتكتب مراجعة.', 'The performance was captivating and the actor delivered a powerful monologue.', 'استخدام مفردات فنون الأداء', 5, 5, true),

-- Unit 19: Philosophy & Ethics
(91, 19, 'P5U19L1', 'المفاهيم الفلسفية الأساسية', 'Basic Philosophical Concepts', 'C1', 'كيف تشرح المفاهيم الفلسفية؟', 'أنت في درس فلسفة وتناقش.', 'Ethics examines the principles that guide human behavior and moral decision-making.', 'استخدام مفردات الفلسفة والأخلاق', 1, 5, true),
(92, 19, 'P5U19L2', 'أخلاقيات المهنة', 'Professional Ethics', 'C1', 'كيف تناقش أخلاقيات المهنة؟', 'أنت في مؤتمر مهني تناقش dilemmas أخلاقية.', 'Journalists must balance the public''s right to know with individuals'' right to privacy.', 'استخدام مفردات أخلاقيات المهنة', 2, 5, true),
(93, 19, 'P5U19L3', 'التفكير النقدي', 'Critical Thinking', 'C1', 'كيف تطبق التفكير النقدي؟', 'أنت في ندوة أكاديمية تحلل حجة.', 'We should examine the underlying assumptions before accepting any argument.', 'استخدام مفردات التفكير النقدي', 3, 5, true),
(94, 19, 'P5U19L4', 'المسؤولية الاجتماعية', 'Social Responsibility', 'C1', 'كيف تتحدث عن المسؤولية الاجتماعية؟', 'أنت في مؤتمر اجتماعي تتحدث.', 'Corporations have a responsibility to contribute positively to the communities they operate in.', 'استخدام مفردات المسؤولية الاجتماعية', 4, 5, true),
(95, 19, 'P5U19L5', 'القيمة الأخلاقية', 'Moral Values', 'C1', 'كيف تناقش القيم الأخلاقية؟', 'أنت في نقاش فلسفي عميق.', 'Integrity means acting in accordance with your principles, even when it is difficult.', 'استخدام مفردات القيم الأخلاقية', 5, 5, true),

-- Unit 20: Global Issues
(96, 20, 'P5U20L1', 'التحديات العالمية', 'Global Challenges', 'C1', 'كيف تناقش التحديات العالمية؟', 'أنت في مؤتمر الأمم المتحدة وتتحدث.', 'Global cooperation is essential to address pressing issues like poverty and inequality.', 'استخدام مفردات التحديات العالمية', 1, 5, true),
(97, 20, 'P5U20L2', 'الصحة العالمية', 'Global Health', 'C1', 'كيف تتحدث عن القضايا الصحية العالمية؟', 'أنت في مؤتمر صحي دولي.', 'Pandemic preparedness requires investment in healthcare infrastructure worldwide.', 'استخدام مفردات الصحة العالمية', 2, 5, true),
(98, 20, 'P5U20L3', 'التنمية المستدامة', 'Sustainable Development', 'C1', 'كيف تصف أهداف التنمية المستدامة؟', 'أنت في ورشة عمل عن أهداف التنمية.', 'The United Nations has established seventeen sustainable development goals for 2030.', 'استخدام مفردات التنمية المستدامة', 3, 5, true),
(99, 20, 'P5U20L4', 'النزاعات والسلام', 'Conflict & Peace', 'C1', 'كيف تناقش قضايا النزاع والسلام؟', 'أنت في ندوة عن الطرق الدبلوماسية لحل النزاعات.', 'Dialogue and negotiation are preferable to military intervention in resolving disputes.', 'استخدام مفردات النزاعات والسلام', 4, 5, true),
(100, 20, 'P5U20L5', 'الحوكمة العالمية', 'Global Governance', 'C1', 'كيف تصف أنظمة الحوكمة العالمية؟', 'أنت في مؤتمر سياسي دولي.', 'Multilateral institutions play a crucial role in maintaining international order.', 'استخدام مفردات الحوكمة العالمية', 5, 5, true),

-- Unit 21: Professional Communication
(101, 21, 'P5U21L1', 'التواصل الكتابي المهني', 'Professional Writing', 'C1', 'كيف تكتب رسائل مهنية؟', 'أنت في مكتب وتكتب بريداً إلكترونياً رسمياً.', 'I am writing to follow up on our meeting last week regarding the project proposal.', 'استخدام مفردات التواصل الكتابي المهني', 1, 5, true),
(102, 21, 'P5U21L2', 'العروض التقديمية', 'Presentations', 'C1', 'كيف تعقد عرضاً تقديмиاً احترافياً؟', 'أنت في قاعة مؤتمرات تعقد عرضاً.', 'Let me begin by outlining the key findings of our research.', 'استخدام مفردات العروض التقديمية', 2, 5, true),
(103, 21, 'P5U21L3', 'المفاوضات', 'Negotiations', 'C1', 'كيف تشارك في مفاوضات مهنية؟', 'أنت في اجتماع مفاوضات مع شريك تجاري.', 'We propose a partnership that would benefit both organizations equally.', 'استخدام مفردات المفاوضات', 3, 5, true),
(104, 21, 'P5U21L4', 'قيادة Meetings', 'Leading Meetings', 'C1', 'كيف تقود اجتماعاً مهنياً؟', 'أنت في اجتماع عمل وتدير النقاش.', 'Let us move on to the next agenda item. Does anyone have questions?', 'استخدام مفردات إدارة الاجتماعات', 4, 5, true),
(105, 21, 'P5U21L5', 'التمارين الصعبة', 'Difficult Conversations', 'C1', 'كيف تتعامل مع المحادثات الصعبة؟', 'أنت في محادثة هادئة مع زميل حول مشكلة.', 'I understand your concern and I would like to find a solution that works for everyone.', 'استخدام مفردات المحادثات الصعبة والحل', 5, 5, true);

-- ------------------------------------------------------------
-- 4. EXERCISES — Phase 1 only (100 rows: 20 lessons × 5 exercises)
-- Exercises for phases 2-5 should be seeded later.
-- ------------------------------------------------------------
INSERT INTO exercises (id, lesson_id, exercise_type, order_in_lesson, prompt_ar, prompt_en, content_json, correct_answer, explanation_ar, difficulty, skill_tags) VALUES

-- ===== Unit 1: Greetings & Introductions =====
-- P1U1L1: Daily Greetings
(1,   1, 'mcq', 1, 'ما هي تحية الصباح المناسبة؟', 'Which is a good morning greeting?',
 '{"options":["Good night","Good morning","Good evening","Goodbye"],"correct_index":1}',
 'Good morning', 'تحية الصباح هي Good morning', 'easy', 'greetings'),
(2,   1, 'fill_blank', 2, 'أكمل الجملة', 'Complete the sentence',
 '{"sentence":"Good ___! How are you?","word_bank":["morning","night","bye"]}',
 'morning', 'الكلمة المناسبة هي morning في تحية الصباح', 'easy', 'greetings'),
(3,   1, 'reorder', 3, 'رتّب الكلمات لتكوين جملة', 'Reorder the words to make a sentence',
 '{"shuffled_words":["are","you","How","?"]}',
 'How are you?', 'الترتيب الصحيح هو How are you?', 'easy', 'greetings'),
(4,   1, 'transform', 4, 'حوّل الجملة إلى سؤال', 'Transform the sentence into a question',
 '{"original_sentence":"You are fine.","instruction_ar":"حوّل الجملة إلى سؤال"}',
 'Are you fine?', 'لتحويل الجملة إلى سؤال نضع فعل الكون في البداية', 'medium', 'question_formation'),
(5,   1, 'free_writing', 5, 'اكتب تحية لشخص جديد', 'Write a greeting to a new person',
 '{"min_words":20,"topic_ar":"اكتب تحية رسمية لشخص تلتقي به لأول مرة في عمل جديد"}',
 NULL, 'اكتب تحية تبدأ بـ Hello أو Good morning ثم سؤال عن الحال', 'medium', 'writing'),

-- P1U1L2: Introducing Yourself
(6,   2, 'mcq', 1, 'أكمل التعريف: My name ___ Omar.', 'Complete: My name ___ Omar.',
 '{"options":["am","is","are","be"],"correct_index":1}',
 'is', 'نستخدم is مع My name', 'easy', 'introductions'),
(7,   2, 'fill_blank', 2, 'أكمل الجملة', 'Complete the sentence',
 '{"sentence":"Hello, my ___ is Sarah.","word_bank":["name","age","city","job"]}',
 'name', 'في التعريف نستخدم My name is', 'easy', 'introductions'),
(8,   2, 'reorder', 3, 'رتّب الكلمات', 'Reorder the words',
 '{"shuffled_words":["is","name","My","Ali","."]}',
 'My name is Ali.', 'الترتيب: My name is Ali.', 'easy', 'introductions'),
(9,   2, 'transform', 4, 'حوّل الجملة إلى سؤال', 'Transform into a question',
 '{"original_sentence":"Your name is Ahmed.","instruction_ar":"اسأل عن اسم الشخص"}',
 'What is your name?', 'نستخدم What is your name? للسؤال عن الاسم', 'medium', 'question_formation'),
(10,  2, 'free_writing', 5, 'عرّف بنفسك', 'Introduce yourself',
 '{"min_words":20,"topic_ar":"اكتب فقرة قصيرة تعرّف فيها بنفسك: اسمك، عمرك، بلدك، ومهنتك"}',
 NULL, 'استخدم My name is... I am from... I work as...', 'medium', 'writing'),

-- P1U1L3: Personal Questions
(11,  3, 'mcq', 1, 'كيف تسأل عن بلد الشخص؟', 'How do you ask about someone''s country?',
 '{"options":["Where are you?","Where are you from?","What is your country?","Which country?"],"correct_index":1}',
 'Where are you from?', 'السؤال الصحيح هو Where are you from?', 'easy', 'personal_questions'),
(12,  3, 'fill_blank', 2, 'أكمل الجملة', 'Complete the sentence',
 '{"sentence":"I am ___ Egypt.","word_bank":["from","in","at","to"]}',
 'from', 'نستخدم من/from مع بلد المنبع', 'easy', 'prepositions'),
(13,  3, 'reorder', 3, 'رتّب الكلمات', 'Reorder the words',
 '{"shuffled_words":["from","I","am","Saudi Arabia","."]}',
 'I am from Saudi Arabia.', 'الترتيب: I am from Saudi Arabia.', 'easy', 'personal_questions'),
(14,  3, 'transform', 4, 'حوّل الإجابة إلى سؤال', 'Transform answer into question',
 '{"original_sentence":"I am from Morocco.","instruction_ar":"اكتب السؤال الذي يُجاب بهذه الجملة"}',
 'Where are you from?', 'السؤال هو Where are you from?', 'medium', 'question_formation'),
(15,  3, 'free_writing', 5, 'اكتب عن نفسك', 'Write about yourself',
 '{"min_words":20,"topic_ar":"اكتب فقرة ت答ب فيها عن: اسمك، من أين أنت، عمرك، وماذا تعمل"}',
 NULL, 'استخدم I am from... I am ... years old. I work as...', 'medium', 'writing'),

-- P1U1L4: Saying Goodbye
(16,  4, 'mcq', 1, 'أي تعبير يُستخدم للوداع؟', 'Which expression is used for goodbye?',
 '{"options":["Hello","Nice to meet you","Goodbye","Thank you"],"correct_index":2}',
 'Goodbye', 'Goodbye هو تعبير الوداع', 'easy', 'farewells'),
(17,  4, 'fill_blank', 2, 'أكمل الجملة', 'Complete the sentence',
 '{"sentence":"It was nice ___ you.","word_bank":["meeting","meet","to meet","met"]}',
 'meeting', 'نستخدم Nice meeting you للوداع', 'easy', 'farewells'),
(18,  4, 'reorder', 3, 'رتّب الكلمات', 'Reorder the words',
 '{"shuffled_words":["you","Goodbye","nice","meeting","!"]}',
 'Nice meeting you. Goodbye!', 'الترتيب: Nice meeting you. Goodbye!', 'easy', 'farewells'),
(19,  4, 'transform', 4, 'حوّل الجملة إلى وداع', 'Transform into goodbye',
 '{"original_sentence":"I had a great time.","instruction_ar":"استخدم هذه الجملة في توديع شخص"}',
 'I had a great time. It was nice meeting you.', 'نضيف عبارة وداع مناسبة', 'medium', 'farewells'),
(20,  4, 'free_writing', 5, 'اكتب رسالة وداع', 'Write a goodbye message',
 '{"min_words":20,"topic_ar":"اكتب رسالة وداع لزميل في العمل كنت تعمل معه لمدة سنة"}',
 NULL, 'استخدم It was great working with you. I wish you all the best.', 'medium', 'writing'),

-- P1U1L5: Formal Greetings
(21,  5, 'mcq', 1, 'كيف تُحيّي مديرك في الصباح؟', 'How do you greet your manager in the morning?',
 '{"options":["Hey!","What''s up?","Good morning, sir.","Yo!"],"correct_index":2}',
 'Good morning, sir.', 'التحية الرسمية هي Good morning, sir.', 'easy', 'formal_greetings'),
(22,  5, 'fill_blank', 2, 'أكمل الجملة', 'Complete the sentence',
 '{"sentence":"Good ___, Mr. Smith.","word_bank":["morning","man","boss","day"]}',
 'morning', 'نستخدم Good morning مع اللقب', 'easy', 'formal_greetings'),
(23,  5, 'reorder', 3, 'رتّب الكلمات', 'Reorder the words',
 '{"shuffled_words":["to","pleasure","It","you","is","meet","."]}',
 'It is a pleasure to meet you.', 'الترتيب: It is a pleasure to meet you.', 'easy', 'formal_greetings'),
(24,  5, 'transform', 4, 'حوّل التحية إلى صيغة رسمية', 'Make it formal',
 '{"original_sentence":"Hi John, how are you?","instruction_ar":"حوّل هذه التحية إلى صيغة رسمية"}',
 'Good morning, Mr. Smith. How are you today?', 'نستخدم اللقب بدلاً من الاسم الأول', 'medium', 'formal_greetings'),
(25,  5, 'free_writing', 5, 'اكتب تحية رسمية', 'Write a formal greeting',
 '{"min_words":20,"topic_ar":"اكتب تحية رسمية لتبدأ بها أول يوم عمل في مكتب جديد"}',
 NULL, 'استخدم Good morning, sir/madam. It is a pleasure to meet you.', 'medium', 'writing'),

-- ===== Unit 2: Numbers & Counting =====
-- P1U2L1: Numbers 1-10
(26,  6, 'mcq', 1, 'كيف تقول الرقم 5 بالإنجليزية؟', 'What is the English word for 5?',
 '{"options":["three","four","five","six"],"correct_index":2}',
 'five', 'الرقم 5 هو five', 'easy', 'numbers'),
(27,  6, 'fill_blank', 2, 'أكمل الجملة', 'Complete the sentence',
 '{"sentence":"I would like ___ tickets, please.","word_bank":["two","second","too","to"]}',
 'two', 'نستخدم الرقم two للعدد', 'easy', 'numbers'),
(28,  6, 'reorder', 3, 'رتّب الكلمات', 'Reorder the words',
 '{"shuffled_words":["three","I","have","books","."]}',
 'I have three books.', 'الترتيب: I have three books.', 'easy', 'numbers'),
(29,  6, 'transform', 4, 'حوّل الرقم إلى كلمة', 'Write the number as a word',
 '{"original_sentence":"I have 4 apples.","instruction_ar":"اكتب الرقم 4 ككلمة"}',
 'I have four apples.', 'الرقم 4 ككلمة هو four', 'easy', 'numbers'),
(30,  6, 'free_writing', 5, 'اكتب عن مشترياتك', 'Write about your purchases',
 '{"min_words":20,"topic_ar":"اكتب فقرة عن المشتريات التي اشتريتها اليوم: الأصناف والأسعار"}',
 NULL, 'استخدم أرقاماً مثل two, five, ten مع أسماء الأشياء', 'medium', 'numbers'),

-- P1U2L2: Numbers 11-20
(31,  7, 'mcq', 1, 'كيف تقول الرقم 15؟', 'What is the word for 15?',
 '{"options":["fifteen","fifty","five","fiveteen"],"correct_index":0}',
 'fifteen', 'الرقم 15 هو fifteen', 'easy', 'numbers'),
(32,  7, 'fill_blank', 2, 'أكمل الجملة', 'Complete the sentence',
 '{"sentence":"There are ___ trees in this garden.","word_bank":["thirteen","thirty","three","third"]}',
 'thirteen', 'نستخدم thirteen للعدد 13', 'easy', 'numbers'),
(33,  7, 'reorder', 3, 'رتّب الكلمات', 'Reorder the words',
 '{"shuffled_words":["eighteen","There","are","students","."]}',
 'There are eighteen students.', 'الترتيب: There are eighteen students.', 'easy', 'numbers'),
(34,  7, 'transform', 4, 'حوّل الرقم إلى كلمة', 'Convert number to word',
 '{"original_sentence":"I have 12 books.","instruction_ar":"اكتب الرقم 12 ككلمة"}',
 'I have twelve books.', 'الرقم 12 هو twelve', 'easy', 'numbers'),
(35,  7, 'free_writing', 5, 'اكتب عن عدّ', 'Write about counting',
 '{"min_words":20,"topic_ar":"اكتب فقرة عن عدّ الأشياء في غرفتك: كم كتاب، كم قلم، كم صورة"}',
 NULL, 'استخدم أرقاماً من 11 إلى 20 مثل eleven, twelve, thirteen', 'medium', 'numbers'),

-- P1U2L3: Numbers 20-100
(36,  8, 'mcq', 1, 'كم تكلفة الكتاب؟', 'How much does the book cost?',
 '{"options":["thirty-five dollars","thirty five dollar","thirty and five","five thirty"],"correct_index":0}',
 'thirty-five dollars', 'التكلفة هي thirty-five dollars', 'easy', 'numbers'),
(37,  8, 'fill_blank', 2, 'أكمل الجملة', 'Complete the sentence',
 '{"sentence":"The shirt costs ___ dollars.","word_bank":["forty","four","fourteen","fortieth"]}',
 'forty', 'الرقم 40 هو forty', 'easy', 'numbers'),
(38,  8, 'reorder', 3, 'رتّب الكلمات', 'Reorder the words',
 '{"shuffled_words":["seventy","The","costs","ticket","dollars","."]}',
 'The ticket costs seventy dollars.', 'الترتيب: The ticket costs seventy dollars.', 'easy', 'numbers'),
(39,  8, 'transform', 4, 'حوّل الأرقام إلى كلمات', 'Convert numbers to words',
 '{"original_sentence":"I need 25 pencils.","instruction_ar":"اكتب الرقم 25 ككلمة"}',
 'I need twenty-five pencils.', 'الرقم 25 هو twenty-five', 'easy', 'numbers'),
(40,  8, 'free_writing', 5, 'اكتب قائمة مشتريات', 'Write a shopping list',
 '{"min_words":20,"topic_ar":"اكتب قائمة مشتريات بأسعار تتراوح بين 10 و 100 دولار"}',
 NULL, 'استخدم أرقاماً مثل twenty, thirty, fifty, eighty', 'medium', 'numbers'),

-- P1U2L4: Ordinal Numbers
(41,  9, 'mcq', 1, 'كيف تقول الرقم الترتيبي الثاني؟', 'What is the ordinal number for second?',
 '{"options":["two","twice","second","secondary"],"correct_index":2}',
 'second', 'الرقم الترتيبي الثاني هو second', 'easy', 'ordinals'),
(42,  9, 'fill_blank', 2, 'أكمل الجملة', 'Complete the sentence',
 '{"sentence":"She finished in ___ place.","word_bank":["second","two","secondly","sec"]}',
 'second', 'نستخدم second للترتيب', 'easy', 'ordinals'),
(43,  9, 'reorder', 3, 'رتّب الكلمات', 'Reorder the words',
 '{"shuffled_words":["won","He","first","prize","."]}',
 'He won first prize.', 'الترتيب: He won first prize.', 'easy', 'ordinals'),
(44,  9, 'transform', 4, 'حوّل العدد التكراري إلى ترتيبي', 'Convert cardinal to ordinal',
 '{"original_sentence":"I live on floor 3.","instruction_ar":"استخدم الرقم الترتيبي"}',
 'I live on the third floor.', 'نستخدم third بدلاً من three', 'medium', 'ordinals'),
(45,  9, 'free_writing', 5, 'اكتب عن ترتيب', 'Write about ordering',
 '{"min_words":20,"topic_ar":"اكتب عن ترتيب أشياء في حياتك: الطابق الذي تسكن فيه، غرفتك المفضلة، etc."}',
 NULL, 'استخدم أرقاماً ترتيبية مثل first, second, third, fourth', 'medium', 'ordinals'),

-- P1U2L5: Numbers in Daily Life
(46, 10, 'mcq', 1, 'ما هو رقم هاتفك؟', 'What is your phone number?',
 '{"options":["My phone number is 555-0123","My phone is 555","Phone number 555-0123","555-0123 is phone"],"correct_index":0}',
 'My phone number is 555-0123', 'الصيغة الصحيحة هي My phone number is', 'easy', 'numbers_daily'),
(47, 10, 'fill_blank', 2, 'أكمل الجملة', 'Complete the sentence',
 '{"sentence":"My phone number is ___-0123.","word_bank":["555","five five five","5 5 5","phone"]}',
 '555', 'نكتب الأرقام بالأرقام في أرقام الهواتف', 'easy', 'numbers_daily'),
(48, 10, 'reorder', 3, 'رتّب الكلمات', 'Reorder the words',
 '{"shuffled_words":["number","My","is","address","123","Main","Street","."]}',
 'My address is 123 Main Street.', 'الترتيب: My address is 123 Main Street.', 'easy', 'numbers_daily'),
(49, 10, 'transform', 4, 'حوّل إلى جملة مكتملة', 'Complete the expression',
 '{"original_sentence":"555-1234","instruction_ar":"اكتب جملة كاملة تتضمن هذا الرقم"}',
 'My phone number is 555-1234.', 'نبدأ بـ My phone number is ثم الرقم', 'medium', 'numbers_daily'),
(50, 10, 'free_writing', 5, 'اكتب معلوماتك الشخصية', 'Write your personal info',
 '{"min_words":20,"topic_ar":"اكتب معلوماتك الشخصية: رقم الهاتف، العنوان، العمر، ورقم الشارع"}',
 NULL, 'استخدم My phone number is... I live at... I am ... years old.', 'medium', 'numbers_daily'),

-- ===== Unit 3: Colors & Adjectives =====
-- P1U3L1: Basic Colors
(51, 11, 'mcq', 1, 'ما هو لون السماء عادةً؟', 'What color is the sky usually?',
 '{"options":["red","blue","green","yellow"],"correct_index":1}',
 'blue', 'لون السماء هو الأزرق', 'easy', 'colors'),
(52, 11, 'fill_blank', 2, 'أكمل الجملة', 'Complete the sentence',
 '{"sentence":"I like the ___ shirt.","word_bank":["blue","blues","bluey","blued"]}',
 'blue', 'نستخدم اللون كصفة قبل الاسم', 'easy', 'colors'),
(53, 11, 'reorder', 3, 'رتّب الكلمات', 'Reorder the words',
 '{"shuffled_words":["is","The","car","red","."]}',
 'The car is red.', 'الترتيب: The car is red.', 'easy', 'colors'),
(54, 11, 'transform', 4, 'أضف لوناً للجملة', 'Add a color to the sentence',
 '{"original_sentence":"The flower is beautiful.","instruction_ar":"أضف لوناً لوصف الزهرة"}',
 'The red flower is beautiful.', 'نضيف اللون قبل الاسم', 'medium', 'colors'),
(55, 11, 'free_writing', 5, 'اكتب عن ألوان', 'Write about colors',
 '{"min_words":20,"topic_ar":"اكتب فقرة عن ألوان غرفتك: لون الحائط، لون السرير، لون الستارة"}',
 NULL, 'استخدم ألواناً مثل red, blue, green, yellow مع أسماء الأشياء', 'medium', 'colors'),

-- P1U3L2: Advanced Colors
(56, 12, 'mcq', 1, 'ما هو الفرق بين red و crimson؟', 'What is the difference between red and crimson?',
 '{"options":["لا يوجد فرق","crimson هو درجة أعمق من red","crimson أفتح","crimson أزرق"],"correct_index":1}',
 'crimson هو درجة أعمق من red', 'crimson هو درجة داكنة من الأحمر', 'medium', 'colors'),
(57, 12, 'fill_blank', 2, 'أكمل الجملة', 'Complete the sentence',
 '{"sentence":"The sunset has shades of ___ and pink.","word_bank":["orange","oranges","oranged","orangely"]}',
 'orange', 'نستخدم لون البرتقالي لوصف الغروب', 'easy', 'colors'),
(58, 12, 'reorder', 3, 'رتّب الكلمات', 'Reorder the words',
 '{"shuffled_words":["has","painting","The","shades","of","blue","."]}',
 'The painting has shades of blue.', 'الترتيب: The painting has shades of blue.', 'easy', 'colors'),
(59, 12, 'transform', 4, 'استخدم درجة لون', 'Use a color shade',
 '{"original_sentence":"The rose is red.","instruction_ar":"استخدم درجة أكثر دقة للون"}',
 'The rose is crimson.', 'نستخدم crimson بدلاً من red للدقة', 'medium', 'colors'),
(60, 12, 'free_writing', 5, 'اكتب عن درجات الألوان', 'Write about color shades',
 '{"min_words":20,"topic_ar":"اكتب فقرة عن الألوان التي تحبها في طبيعتك: ألوان الغروب، ألوان الأزهار"}',
 NULL, 'استخدم صفات مثل light, dark, bright, pale مع الألوان', 'medium', 'colors'),

-- P1U3L3: Size & Shape Adjectives
(61, 13, 'mcq', 1, 'أي صفة تصف الحجم؟', 'Which adjective describes size?',
 '{"options":["round","big","red","happy"],"correct_index":1}',
 'big', 'big هي صفة حجم', 'easy', 'adjectives'),
(62, 13, 'fill_blank', 2, 'أكمل الجملة', 'Complete the sentence',
 '{"sentence":"This table is ___ and the big one is in the corner.","word_bank":["round","around","rounding","rounded"]}',
 'round', 'نستخدم round لوصف الشكل', 'easy', 'shapes'),
(63, 13, 'reorder', 3, 'رتّب الكلمات', 'Reorder the words',
 '{"shuffled_words":["is","The","ball","small","and","round","."]}',
 'The ball is small and round.', 'الترتيب: The ball is small and round.', 'easy', 'adjectives'),
(64, 13, 'transform', 4, 'أضف صفة للجملة', 'Add an adjective',
 '{"original_sentence":"The box is big.","instruction_ar":"أضف صفة شكل للصندوق"}',
 'The box is big and square.', 'نضيف صفة شكل إضافية', 'medium', 'adjectives'),
(65, 13, 'free_writing', 5, 'اكتب وصفاً لشيء', 'Write a description',
 '{"min_words":20,"topic_ar":"اكتب وصفاً لطاولة: حجمها، شكلها، لونها، ومادة صنعها"}',
 NULL, 'استخدم صفات الحجم مثل big, small, large مع صفات الشكل مثل round, square', 'medium', 'adjectives'),

-- P1U3L4: Mood & Feeling Adjectives
(66, 14, 'mcq', 1, 'كيف تقول أنك سعيد بالإنجليزية؟', 'How do you say you are happy in English?',
 '{"options":["I am sad","I am angry","I am happy","I am tired"],"correct_index":2}',
 'I am happy', 'سعيد هي happy', 'easy', 'feelings'),
(67, 14, 'fill_blank', 2, 'أكمل الجملة', 'Complete the sentence',
 '{"sentence":"I am ___ today. Yesterday I was tired.","word_bank":["happy","happily","happiness","happier"]}',
 'happy', 'نستخدم happy كصفة بعد فعل الكون', 'easy', 'feelings'),
(68, 14, 'reorder', 3, 'رتّب الكلمات', 'Reorder the words',
 '{"shuffled_words":["I","am","feeling","tired","today","."]}',
 'I am feeling tired today.', 'الترتيب: I am feeling tired today.', 'easy', 'feelings'),
(69, 14, 'transform', 4, 'غيّر المشاعر', 'Change the feeling',
 '{"original_sentence":"I am happy.","instruction_ar":"غيّر الجملة لتقول أنك متعب"}',
 'I am tired.', 'نستخدم tired بدلاً من happy', 'easy', 'feelings'),
(70, 14, 'free_writing', 5, 'اكتب عن مشاعرك', 'Write about your feelings',
 '{"min_words":20,"topic_ar":"اكتب فقرة عن مشاعرك اليوم: كيف تشعر ولماذا"}',
 NULL, 'استخدم صفات المشاعر مثل happy, sad, excited, nervous', 'medium', 'feelings'),

-- P1U3L5: General Descriptive Adjectives
(71, 15, 'mcq', 1, 'أي صفة وصف الهاتف؟', 'Which adjective describes the phone?',
 '{"options":["The phone is new, fast, and expensive","The phone is","Phone new fast","New fast expensive phone"],"correct_index":0}',
 'The phone is new, fast, and expensive', 'الجملة الكاملة تحتوي على عدة صفات', 'easy', 'adjectives'),
(72, 15, 'fill_blank', 2, 'أكمل الجملة', 'Complete the sentence',
 '{"sentence":"The phone is ___, fast, and expensive.","word_bank":["new","news","newly","newness"]}',
 'new', 'نستخدم new كصفة قبل other adjectives', 'easy', 'adjectives'),
(73, 15, 'reorder', 3, 'رتّب الكلمات', 'Reorder the words',
 '{"shuffled_words":["The","is","car","fast","and","shiny","."]}',
 'The car is fast and shiny.', 'الترتيب: The car is fast and shiny.', 'easy', 'adjectives'),
(74, 15, 'transform', 4, 'أضف صفة ثانية', 'Add another adjective',
 '{"original_sentence":"The book is interesting.","instruction_ar":"أضف صفة أخرى لوصف الكتاب"}',
 'The book is interesting and informative.', 'نضيف صفة إضافية', 'medium', 'adjectives'),
(75, 15, 'free_writing', 5, 'اكتب وصفاً كاملاً', 'Write a full description',
 '{"min_words":20,"topic_ar":"اكتب وصفاً لهاتفك الجديد: الحجم، اللون، السرعة، والسعر"}',
 NULL, 'استخدم عدة صفات مثل small, white, fast, expensive', 'medium', 'adjectives'),

-- ===== Unit 4: Food & Drinks =====
-- P1U4L1: Basic Food Items
(76, 16, 'mcq', 1, 'كيف تطلب سلطة في مطعم؟', 'How do you order a salad at a restaurant?',
 '{"options":["I want salad","I would like a salad, please","Give me salad","Salad me"],"correct_index":1}',
 'I would like a salad, please', 'الصيغة المهذبة هي I would like', 'easy', 'food'),
(77, 16, 'fill_blank', 2, 'أكمل الجملة', 'Complete the sentence',
 '{"sentence":"I would like a ___ and some bread, please.","word_bank":["salad","salads","saladly","saladish"]}',
 'salad', 'نستخدم salad كاسم مفرد', 'easy', 'food'),
(78, 16, 'reorder', 3, 'رتّب الكلمات', 'Reorder the words',
 '{"shuffled_words":["like","I","would","chicken","some","please","."]}',
 'I would like some chicken, please.', 'الترتيب: I would like some chicken, please.', 'easy', 'food'),
(79, 16, 'transform', 4, 'حوّل الطلب إلى جملة مهذبة', 'Make it polite',
 '{"original_sentence":"I want pizza.","instruction_ar":"حوّل هذا الطلب إلى صيغة مهذبة"}',
 'I would like some pizza, please.', 'نستخدم I would like بدلاً من I want', 'medium', 'politeness'),
(80, 16, 'free_writing', 5, 'اكتب طعامك المفضل', 'Write about your favorite food',
 '{"min_words":20,"topic_ar":"اكتب فقرة عن طعامك المفضل: ما هو، كيف يُحضّر، ولماذا تحبه"}',
 NULL, 'استخدم I like... My favorite food is... because...', 'medium', 'food'),

-- P1U4L2: Fruits & Vegetables
(81, 17, 'mcq', 1, 'كيف تشتري تفاحاً؟', 'How do you buy apples?',
 '{"options":["I need two kilos of apples","I need apples two","Two apples kilo need","Apples two kilo"],"correct_index":0}',
 'I need two kilos of apples', 'الصيغة الصحيحة تشمل الكمية والوحدة', 'easy', 'food'),
(82, 17, 'fill_blank', 2, 'أكمل الجملة', 'Complete the sentence',
 '{"sentence":"I need two kilos of ___ and some tomatoes.","word_bank":["apples","appleses","applely","appleed"]}',
 'apples', 'نستخدم apples كاسم جمع', 'easy', 'food'),
(83, 17, 'reorder', 3, 'رتّب الكلمات', 'Reorder the words',
 '{"shuffled_words":["I","need","bananas","some","please","."]}',
 'I need some bananas, please.', 'الترتيب: I need some bananas, please.', 'easy', 'food'),
(84, 17, 'transform', 4, 'أضف كمية', 'Add quantity',
 '{"original_sentence":"I want tomatoes.","instruction_ar":"أضف كمية للطلب"}',
 'I want half a kilo of tomatoes.', 'نضيف كمية مثل half a kilo', 'medium', 'food'),
(85, 17, 'free_writing', 5, 'اكتب قائمة خضروات', 'Write a vegetable list',
 '{"min_words":20,"topic_ar":"اكتب قائمة بالخضروات والفواكه التي تحتاجها هذا الأسبوع"}',
 NULL, 'استخدم I need... I want... Some... with quantities', 'medium', 'food'),

-- P1U4L3: Drinks & Beverages
(86, 18, 'mcq', 1, 'كيف تطلب قهوة؟', 'How do you order coffee?',
 '{"options":["Can I have a cup of coffee?","Coffee me give","I coffee want","Give coffee cup"],"correct_index":0}',
 'Can I have a cup of coffee?', 'الصيغة المهذبة هي Can I have', 'easy', 'drinks'),
(87, 18, 'fill_blank', 2, 'أكمل الجملة', 'Complete the sentence',
 '{"sentence":"Can I have a cup of coffee with ___?","word_bank":["sugar","sugars","sugary","sugared"]}',
 'sugar', 'نستخدم sugar كمضاف', 'easy', 'drinks'),
(88, 18, 'reorder', 3, 'رتّب الكلمات', 'Reorder the words',
 '{"shuffled_words":["I","Can","have","tea","some","please","?"]}',
 'Can I have some tea, please?', 'الترتيب: Can I have some tea, please?', 'easy', 'drinks'),
(89, 18, 'transform', 4, 'أضف إضافة للشرب', 'Add extras to drink',
 '{"original_sentence":"I want orange juice.","instruction_ar":"أضف طلباً إضافياً"}',
 'I would like a glass of orange juice with ice, please.', 'نضيف with ice أو other extras', 'medium', 'drinks'),
(90, 18, 'free_writing', 5, 'اكتب عن مشروباتك المفضلة', 'Write about your favorite drinks',
 '{"min_words":20,"topic_ar":"اكتب فقرة عن المشروبات التي تشربها كل يوم: الصباح، الظهر، والمساء"}',
 NULL, 'استخدم I drink... in the morning. I like... because...', 'medium', 'drinks'),

-- P1U4L4: Food & Drink at Home
(91, 19, 'mcq', 1, 'ما تحتاجه للإفطار؟', 'What do you need for breakfast?',
 '{"options":["We need eggs, milk, and butter","We eggs need milk","Eggs milk butter need","Need we eggs milk"],"correct_index":0}',
 'We need eggs, milk, and butter', 'الصيغة الصحيحة هي We need', 'easy', 'food'),
(92, 19, 'fill_blank', 2, 'أكمل الجملة', 'Complete the sentence',
 '{"sentence":"We need ___, milk, and butter for breakfast.","word_bank":["eggs","egg","egged","egging"]}',
 'eggs', 'نستخدم eggs كاسم جمع', 'easy', 'food'),
(93, 19, 'reorder', 3, 'رتّب الكلمات', 'Reorder the words',
 '{"shuffled_words":["We","need","bread","some","for","lunch","."]}',
 'We need some bread for lunch.', 'الترتيب: We need some bread for lunch.', 'easy', 'food'),
(94, 19, 'transform', 4, 'حوّل إلى قائمة تسوق', 'Convert to shopping list',
 '{"original_sentence":"We need eggs and milk.","instruction_ar":"حوّل إلى قائمة تسوق مفصلة"}',
 'Shopping list: eggs (12), milk (1 liter), bread (1 loaf).', 'نضيف الكمية والوحدة', 'medium', 'food'),
(95, 19, 'free_writing', 5, 'اكتب قائمة طعام', 'Write a food list',
 '{"min_words":20,"topic_ar":"اكتب قائمة طعام للإفطار والعشاء لهذا الأسبوع"}',
 NULL, 'استخدم We need... for breakfast/lunch/dinner', 'medium', 'food'),

-- P1U4L5: Kitchens & Restaurants
(96, 20, 'mcq', 1, 'كيف تصف تجربة في مطعم؟', 'How do you describe a restaurant experience?',
 '{"options":["The food was delicious and the service was great","Food delicious service great","Delicious food great service","Great food was delicious"],"correct_index":0}',
 'The food was delicious and the service was great', 'الجملة الكاملة تحتوي على وصف الطعام والخدمة', 'easy', 'restaurants'),
(97, 20, 'fill_blank', 2, 'أكمل الجملة', 'Complete the sentence',
 '{"sentence":"The food was ___ and the service was great.","word_bank":["delicious","deliciously","deliciousness","deliciosed"]}',
 'delicious', 'نستخدم delicious كصفة بعد was', 'easy', 'restaurants'),
(98, 20, 'reorder', 3, 'رتّب الكلمات', 'Reorder the words',
 '{"shuffled_words":["The","was","food","great","and","atmosphere","cozy","."]}',
 'The food was great and the atmosphere was cozy.', 'الترتيب: The food was great and the atmosphere was cozy.', 'easy', 'restaurants'),
(99, 20, 'transform', 4, 'أضف تفاصيل', 'Add details',
 '{"original_sentence":"The restaurant was good.","instruction_ar":"أضف تفاصيل أكثر لوصف المطعم"}',
 'The restaurant was good. The food was fresh and the staff were friendly.', 'نضيف وصفاً للطعام والموظفين', 'medium', 'restaurants'),
(100, 20, 'free_writing', 5, 'اكتب مراجعة مطعم', 'Write a restaurant review',
 '{"min_words":20,"topic_ar":"اكتب مراجعة لمطعم زرته مؤخراً: الطعام، الخدمة، الجو، والسعر"}',
 NULL, 'استخدم The food was... The service was... I would recommend...', 'medium', 'restaurants');

-- ------------------------------------------------------------
-- 5. FLASHCARDS — Phase 1 only (60 rows: 20 lessons × 3 flashcards)
-- Flashcards for phases 2-5 should be seeded later.
-- ------------------------------------------------------------
INSERT INTO flashcards (id, lesson_id, front_en, back_ar, example_en) VALUES
-- ===== Unit 1: Greetings & Introductions =====
-- P1U1L1: Daily Greetings
(1,   1, 'Good morning', 'صباح الخير', 'Good morning! How are you today?'),
(2,   1, 'How are you?', 'كيف حالك؟', 'Hello, how are you? I am fine, thank you.'),
(3,   1, 'Good night', 'تصبح على خير', 'Good night! Sleep well.'),

-- P1U1L2: Introducing Yourself
(4,   2, 'My name is...', 'اسمي...', 'My name is Omar. What is your name?'),
(5,   2, 'What is your name?', 'ما اسمك؟', 'Hi, what is your name? My name is Sara.'),
(6,   2, 'Nice to meet you', 'تشرفت بمعرفتك', 'Nice to meet you! I am happy to be here.'),

-- P1U1L3: Personal Questions
(7,   3, 'Where are you from?', 'من أين أنت؟', 'Where are you from? I am from Egypt.'),
(8,   3, 'I am from...', 'أنا من...', 'I am from Saudi Arabia. It is a beautiful country.'),
(9,   3, 'How old are you?', 'كم عمرك؟', 'How old are you? I am twenty-five years old.'),

-- P1U1L4: Saying Goodbye
(10,  4, 'Goodbye', 'وداعاً', 'Goodbye! It was nice seeing you.'),
(11,  4, 'See you later', 'أراك لاحقاً', 'See you later! Have a good day.'),
(12,  4, 'Take care', ' اعتنِ بنفسك', 'Take care! I will call you tomorrow.'),

-- P1U1L5: Formal Greetings
(13,  5, 'Good afternoon', 'مساء الخير', 'Good afternoon, sir. How may I help you?'),
(14,  5, 'It is a pleasure to meet you', 'تشرفت بمعرفتك', 'It is a pleasure to meet you, Mr. Ahmed.'),
(15,  5, 'How do you do?', 'كيف حالك؟ (رسمي)', 'How do you do? I am the new manager.'),

-- ===== Unit 2: Numbers & Counting =====
-- P1U2L1: Numbers 1-10
(16,  6, 'one', 'واحد', 'I have one brother and two sisters.'),
(17,  6, 'two', 'اثنان', 'I would like two tickets, please.'),
(18,  6, 'five', 'خمسة', 'There are five books on the table.'),

-- P1U2L2: Numbers 11-20
(19,  7, 'eleven', 'أحد عشر', 'There are eleven students in the class.'),
(20,  7, 'fifteen', 'خمسة عشر', 'I have fifteen minutes before the meeting.'),
(21,  7, 'twenty', 'عشرون', 'The book costs twenty dollars.'),

-- P1U2L3: Numbers 20-100
(22,  8, 'thirty', 'ثلاثون', 'The shirt is thirty-five dollars.'),
(23,  8, 'fifty', 'خمسون', 'There are fifty people at the party.'),
(24,  8, 'hundred', 'مائة', 'The building is one hundred meters tall.'),

-- P1U2L4: Ordinal Numbers
(25,  9, 'first', 'الأول', 'She finished in first place.'),
(26,  9, 'second', 'الثاني', 'He came in second in the race.'),
(27,  9, 'third', 'الثالث', 'We live on the third floor.'),

-- P1U2L5: Numbers in Daily Life
(28, 10, 'phone number', 'رقم الهاتف', 'What is your phone number? It is 555-0123.'),
(29, 10, 'address', 'العنوان', 'My address is 123 Main Street, Cairo.'),
(30, 10, 'age', 'العمر', 'My age is twenty-five years old.'),

-- ===== Unit 3: Colors & Adjectives =====
-- P1U3L1: Basic Colors
(31, 11, 'red', 'أحمر', 'The rose is red and beautiful.'),
(32, 11, 'blue', 'أزرق', 'The sky is blue today.'),
(33, 11, 'green', 'أخضر', 'The grass is green in spring.'),

-- P1U3L2: Advanced Colors
(34, 12, 'orange', 'برتقالي', 'The sunset has shades of orange and pink.'),
(35, 12, 'purple', 'بنفسجي', 'She is wearing a purple dress.'),
(36, 12, 'pink', 'وردي', 'The flowers in the garden are pink.'),

-- P1U3L3: Size & Shape Adjectives
(37, 13, 'big', 'كبير', 'The house is very big and spacious.'),
(38, 13, 'small', 'صغير', 'The cat is small and cute.'),
(39, 13, 'round', 'دائري', 'The table is round and wooden.'),

-- P1U3L4: Mood & Feeling Adjectives
(40, 14, 'happy', 'سعيد', 'I am happy today because it is sunny.'),
(41, 14, 'sad', 'حزين', 'She looks sad. What happened?'),
(42, 14, 'tired', 'متعب', 'I am tired after a long day at work.'),

-- P1U3L5: General Descriptive Adjectives
(43, 15, 'new', 'جديد', 'This is my new phone. It is very fast.'),
(44, 15, 'fast', 'سريع', 'The car is fast and comfortable.'),
(45, 15, 'expensive', 'مكلف', 'The restaurant is expensive but the food is delicious.'),

-- ===== Unit 4: Food & Drinks =====
-- P1U4L1: Basic Food Items
(46, 16, 'salad', 'سلطة', 'I would like a salad with my lunch.'),
(47, 16, 'bread', 'خبز', 'Can I have some bread, please?'),
(48, 16, 'chicken', 'دجاج', 'The chicken was delicious and well-cooked.'),

-- P1U4L2: Fruits & Vegetables
(49, 17, 'apple', 'تفاحة', 'I eat an apple every morning.'),
(50, 17, 'tomato', 'طماطم', 'I need two kilos of tomatoes for the salad.'),
(51, 17, 'banana', 'موز', 'The bananas are ripe and sweet.'),

-- P1U4L3: Drinks & Beverages
(52, 18, 'coffee', 'قهوة', 'Can I have a cup of coffee, please?'),
(53, 18, 'tea', 'شاي', 'I drink tea with milk in the morning.'),
(54, 18, 'water', 'ماء', 'Could I have a glass of water?'),

-- P1U4L4: Food & Drink at Home
(55, 19, 'eggs', 'بيض', 'We need eggs for breakfast.'),
(56, 19, 'milk', 'حليب', 'I put milk in my coffee every morning.'),
(57, 19, 'butter', 'زبدة', 'Can you buy some butter from the store?'),

-- P1U4L5: Kitchens & Restaurants
(58, 20, 'delicious', 'لذيذ', 'The food at this restaurant is delicious.'),
(59, 20, 'service', 'خدمة', 'The service was fast and friendly.'),
(60, 20, 'atmosphere', 'أجواء', 'The atmosphere was cozy and relaxing.');

-- ------------------------------------------------------------
-- 6. XP CONFIG
-- ------------------------------------------------------------
INSERT INTO xp_config (event_type, xp_amount, description_ar) VALUES
('lesson_complete', 10, 'إكمال درس'),
('perfect_score', 20, 'درجة مثالية في الدرس'),
('daily_challenge', 15, 'تحدي يومي'),
('milestone_pass', 50, 'اجتياز اختبار المرحلة'),
('flashcard_review', 3, 'مراجعة بطاقة'),
('streak_day', 5, 'الحفاظ على السلسلة يوماً إضافياً');

-- ============================================================
-- END OF SEED SCRIPT
-- ============================================================
