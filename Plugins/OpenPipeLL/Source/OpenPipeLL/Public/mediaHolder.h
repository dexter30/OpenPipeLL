// Fill out your copyright notice in the Description page of Project Settings.

#pragma once

#include "CoreMinimal.h"
#include "GameFramework/Actor.h"
#include "MediaPlayer.h"
#include "MediaTexture.h"
#include "mediaHolder.generated.h"

/**
 * 
 */

class UMediaPlayer;

UCLASS()
class OPENPIPELL_API AmediaHolder : public AActor
{
	GENERATED_BODY()

protected:
	// Called when the game starts or when spawned
	virtual void BeginPlay() override;

public:
	// Called every frame
	virtual void Tick(float DeltaTime) override;

	AmediaHolder();

#if PLATFORM_ANDROID
	void passDatatoJNI(const TArray<FColor>& BufferData, int height, int width);

#endif
protected:

	UPROPERTY(BlueprintReadWrite, Category = "OpenPipe")
	TObjectPtr<UMediaTexture> mediaText = nullptr;

	UPROPERTY(BlueprintReadWrite, Category = "OpenPipe")
	TObjectPtr<UMediaPlayer> mediaItem;


};
