# Banner Library 自身混淆规则
# Library 模块开发时的混淆配置，与 consumer-proguard-rules.pro 区分
# consumer 规则会传递给宿主，本文件仅用于 Library 自身打包

# 基础保留规则
-keepattributes Signature, *Annotation*, SourceFile, LineNumberTable
-keep public class * extends java.lang.Exception
