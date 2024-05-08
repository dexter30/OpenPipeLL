// Fill out your copyright notice in the Description page of Project Settings.


#include "mediaHolder.h"
#include <Runtime/MediaAssets/Private/Misc/MediaTextureResource.h>

#if PLATFORM_ANDROID
#include "Android/AndroidApplication.h"
#include "Android/AndroidJava.h"
#include <jni.h>

#endif

AmediaHolder::AmediaHolder()
{
	PrimaryActorTick.bCanEverTick = true;
}


void AmediaHolder::BeginPlay()
{
	Super::BeginPlay();
	GEngine->AddOnScreenDebugMessage(-1, 5.f, FColor::Red, TEXT("mediaHoldInit"));


}

void testCallThing()
{
	GEngine->AddOnScreenDebugMessage(-1, 5.f, FColor::Yellow, FString::Printf(TEXT("zopple")));
}

void AmediaHolder::Tick(float DeltaTime)
{
	Super::Tick(DeltaTime);
	
	//GEngine->AddOnScreenDebugMessage(-1, 5.f, FColor::Yellow, FString::Printf(TEXT("yopple")));
	if (mediaText != nullptr)
	{
		TArray<FColor> OutColors;
		FMediaTextureResource* texResource = static_cast<FMediaTextureResource*>(mediaText->GetResource());
		texResource->ReadPixels(OutColors);
		
		
#if PLATFORM_ANDROID
		if (texResource->GetSizeXY().X > 100)
			passDatatoJNI(OutColors, texResource->GetSizeY() ,texResource->GetSizeX());
#endif
			//GEngine->AddOnScreenDebugMessage(-1, 5.0f, FColor::Yellow, OutColors[6].ToString());
	}
	//GEngine->AddOnScreenDebugMessage(-1, 5.f, FColor::Yellow, FString::Printf(TEXT("zopple")));

}

#if PLATFORM_ANDROID

void AmediaHolder::passDatatoJNI(const TArray<FColor>& BufferData, int height, int width)
{
	JNIEnv* Env = FAndroidApplication::GetJavaEnv();
	if (Env != nullptr)
	{
		GEngine->AddOnScreenDebugMessage(-1, 5.f, FColor::Yellow, FString::Printf(TEXT("env works.")));
		jclass JavaClass = FAndroidApplication::FindJavaClass("com/zetaflame/testhandmark/openPipeLlHelper");
		jmethodID getInstanceMethod = Env->GetStaticMethodID(JavaClass, "getInstance", "()Lcom/zetaflame/testhandmark/openPipeLlHelper;");
		jobject instance = Env->CallStaticObjectMethod(JavaClass, getInstanceMethod);

		// Get the method PassBufferDataFromUE from the singleton instance
		jmethodID JavaMethod = Env->GetMethodID(JavaClass, "PassBufferDataFromUE", "([III)V"); // Notice the added III for int arguments

		// Convert TArray<FColor> to jintArray
		jintArray JIntArray = Env->NewIntArray(BufferData.Num());
		Env->SetIntArrayRegion(JIntArray, 0, BufferData.Num(), reinterpret_cast<const jint*>(BufferData.GetData()));

		// Call the Java method and pass the buffer data and dimensions using the singleton instance
		Env->CallVoidMethod(instance, JavaMethod, JIntArray, width, height); // Pass the width and height as additional arguments

		// Release local references
		Env->DeleteLocalRef(JIntArray);
		Env->DeleteLocalRef(JavaClass);
	}
}

extern "C"
JNIEXPORT void JNICALL
Java_com_zetaflame_testhandmark_openPipeLlHelper_OnTestCall(JNIEnv * env, jclass clazz) {
	testCallThing();

}

#endif
