# تسليم ترحيل MyFoody إلى Jetpack Compose

آخر تحقق في 23 نوفمبر 2026 من جذر المشروع `F:\MyFoody`.

## الواجهات والتنقل

- انتقلت شاشات الوصفات والمفضلة والنكتة والفلاتر إلى Compose، مع ثيم نهاري وليلي ومكوّن مشترك لبطاقة الوصفة. تقرأ طلبات الوصفات الفلاتر المحفوظة، ويُلغي البحث الجديد طلب البحث السابق.
- ما زال التنقل الرئيسي يستخدم Fragments وNavigation وSafe Args. تستقبل `DetailsActivity` الوصفة من نفس الوسيط، وتعرض التبويبات الثلاثة باستخدام `HorizontalPager` و`rememberPagerState`. يدعم الشريط الضغط والسحب واستعادة التبويب بعد إعادة إنشاء الـActivity.
- يُعرض محتوى النظرة العامة والمكونات مباشرة في الـPager. تعرض التعليمات WebView داخل AndroidView مع حفظ سجل التصفح واستعادته. اختبار التعليمات يتحقق من بقاء الصفحة المفتوحة عند إعادة التركيب. ويغطي اختبار تنقل مستقل تبديل التبويبات واستعادة تبويب المكونات والحفظ في المفضلة والرجوع.

## إعداد البناء وتشخيص Android Studio

يستخدم المشروع Gradle 7.5 وAGP 7.4.2 وKotlin 1.8.22 ومترجم AndroidX Compose 1.4.8. يفعّل `app/build.gradle` الميزة عبر `buildFeatures.compose` ويحدد الإصدار في `composeOptions.kotlinCompilerExtensionVersion`. يعتمد الـPager على Compose Foundation. أزيل تفعيل Data Binding وView Binding واعتمادا ConstraintLayout وShimmer واعتماد RecyclerView المباشر بعد خروج المستهلكين القدامى من المصدر. [جدول توافق Kotlin وCompose](https://developer.android.com/jetpack/androidx/releases/compose-kotlin)

كان فحص المحرر `ComposableCallWithoutComposePlugin` يطلب إضافة Kotlin الحديثة رغم نجاح المترجم القديم. يستثني بروفايل المشروع نطاق `Legacy Compose sources` من هذا الفحص وحده. بعد Gradle Sync، أعاد تحليل FoodJokeScreen وDetailsActivity وRecipesScreen صفر أخطاء. عند الترقية إلى Kotlin 2.x، راجع إعداد الإضافة واحذف الاستثناء بعد تفعيلها. [توثيق فحص JetBrains](https://www.jetbrains.com/help/inspectopedia/ComposableCallWithoutComposePlugin.html)

## التنظيف والاستعادة

خرج 24 ملفًا من مصدر التطبيق بعد تتبع مراجعها وبناء نسخة مستقلة تستبعدها. شمل ذلك 14 ملف layout بنسخة النكتة الليلية، ومشهد MotionLayout، وأربعة محولات بقائمة تضم PagerAdapter، وأربعة محولات Data Binding، وRecipesDiffUtil. تبقى موارد Navigation والألوان والرسومات والقوائم ومعرّفات المضيف والفلاتر مستخدمة.

تحتوي [النسخة الاحتياطية](build/retired-ui-before-removal.zip) على محتوى الملفات قبل الإزالة بمساراتها الأصلية، بما فيه آخر تعديل محلي على PagerAdapter. تقع النسخة ونتائج الاختبار تحت مجلدات البناء وقد يزيلها Gradle clean.

## إصلاحات تجربة الموبايل

- يستخدم الكارت المشترك في Recipes وFavorites صفًا يقسم العرض بنسبة 45% للصورة و55% للمحتوى. يحدد المحتوى ارتفاع الصف، وتملأ الصورة مساحتها باستخدام `ImageView.ScaleType.CENTER_CROP` مع `clipToBounds()`. تبقى العناوين والوصف والإحصاءات داخل العمود الثاني. أضيفت لقائمة Recipes مساحة سفلية قابلة للتمرير قدرها 80dp لتظهر البطاقة الأخيرة فوق زر الفلاتر.
- يستخدم تبويب Instructions ‏WebView مع JavaScript وDOM Storage وWebViewClient. يقارن مستمع اللمس الإزاحة الأفقية والرأسية بعد تجاوز عتبة الحركة الخاصة بالجهاز، ويثبت الاتجاه لباقي الإيماءة. يطلب إبقاء السحب الرأسي داخل الصفحة، ويسمح للـPager بالسحب الأفقي. يرجع المستمع `false` حتى يعالج WebView الضغطات بنفسه.
- يستخدم اختبار التفاعل صفحة HTML محلية طويلة وزرًا يتغير عنوان الصفحة عند الضغط عليه. يتحقق من تنفيذ JavaScript ومن تحرّك محتوى الصفحة دون تبديل التبويب أثناء السحب الرأسي المائل، ثم من تبديل الصفحة بالسحب الأفقي. ويقارن اختبار الكارت الحد الأيمن للصورة ببداية العنوان والوصف وعدد الإعجابات.

## التحقق النهائي

| الأمر من جذر المشروع | النتيجة |
| --- | --- |
| `.\gradlew.bat assembleDebug testDebugUnitTest` | نجح إنشاء APK ونجح اختبار الوحدة الوحيد. |
| `adb -s emulator-5554 shell am instrument -w com.example.myfoody.test/androidx.test.runner.AndroidJUnitRunner` | بعد تثبيت حزمتَي Debug والاختبار، نجحت الاختبارات الـ11 على محاكي Android 14 في 72 ثانية. |

محاولة `connectedDebugAndroidTest` العامة في هذا التشغيل انتهت بتقارير صفر اختبارات. النتيجة أعلاه من تشغيل AndroidJUnitRunner مباشرة على المحاكي المحدد. تغطي تجربة الزر صفحة الاختبار المحلية، وتظل تجربة نافذة الكوكيز الخاصة بموقع الوصفة على الهاتف خطوة تحقق ميدانية.

[حزمة Debug](app/build/outputs/apk/debug/app-debug.apk) · [تقرير الوحدة](app/build/reports/tests/testDebugUnitTest/index.html)

قورنت سبع واجهات نهارًا وليلًا باستخدام بيانات ثابتة قبل استبدال ViewPager2. عولج قص تسميات التبويبات ولون زر الفلاتر ومقاسات البطاقات وزر التطبيق. تحقق التشغيل الأخير من سلوك الـPager الجديد بالاختبارات الآلية. الصور التالية توثّق المقارنة السابقة، وتستخدم لقطة التعليمات صفحة فارغة محلية.

| الوضع | XML | Compose قبل استبدال الـPager |
| --- | --- | --- |
| نهاري | [المرجع](build/parity-xml-no/sheet.png) | [المقارنة](build/parity-compose-no/sheet.png) |
| ليلي | [المرجع](build/parity-xml-yes/sheet.png) | [المقارنة](build/parity-compose-yes/sheet.png) |
