# Add project specific ProGuard rules here.
# Firebase / Firestore mantém reflection para serialização de modelos:
-keepclassmembers class com.example.wanna_believe.data.model.** {
  <init>();
  <fields>;
}
