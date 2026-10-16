# JARVIS Maximum Android — Starter Project

এই ZIP-এ একটি Android Gradle project আছে, যা GitHub Actions দিয়ে Android ফোন থেকেই build করার চেষ্টা করা যাবে। **এটি APK নয়; এটি APK build করার source project.** এতে basic speech input, Text-to-Speech, time/date, YouTube/Google launch ও web search আছে।

## বাস্তব সীমাবদ্ধতা
- **Hey Jarvis always-on wake word এখনো implement করা নেই।** নির্ভরযোগ্য wake-word detection করতে foreground microphone service, battery/privacy design এবং সাধারণত Porcupine-এর মতো wake-word engine/লাইসেন্স বা নিজস্ব model দরকার। এই ZIP-এ সেটা ভান করে দেখানো হয়নি।
- **Maximum AI chat এখনো যুক্ত নয়।** AI-এর জন্য API provider ও API key প্রয়োজন; API key APK-তে সরাসরি রাখা নিরাপদ নয়। ভালো উপায় হলো secure backend/proxy।
- Phone settings/app automation সীমিত; অনেক কাজের জন্য Android permission, Accessibility service বা device-specific support লাগতে পারে।
- এটি debug build, signed Play Store release নয়। বাংলা speech recognition/voice output ফোনের ভাষা ও speech engine support-এর ওপর নির্ভর করে।

## শুধু Android ফোন দিয়ে APK build
1. GitHub-এ sign in করে নতুন repository বানান।
2. ZIP extract করে সব ফাইল repository-তে upload করুন। `.github/workflows/build-apk.yml`-সহ `.github` folder যেন বাদ না পড়ে।
3. Repository-এর Actions tab খুলে **Build JARVIS APK** workflow চালান।
4. সফল হলে workflow run-এর Artifacts থেকে `JARVIS-AI-debug-apk` ZIP ডাউনলোড করুন।
5. ZIP extract করে `app-debug.apk` ইনস্টল করুন। শুধু নিজের trusted build ইনস্টল করুন; Android-এর install warning মন দিয়ে পড়ুন।

Build ব্যর্থ হলে Actions log-এর error দেখে project ঠিক করতে হবে। ফোনের GitHub UI কিছুটা আলাদা হতে পারে।
