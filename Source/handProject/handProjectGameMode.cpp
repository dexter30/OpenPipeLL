// Copyright Epic Games, Inc. All Rights Reserved.

#include "handProjectGameMode.h"
#include "handProjectCharacter.h"
#include "UObject/ConstructorHelpers.h"

AhandProjectGameMode::AhandProjectGameMode()
	: Super()
{
	// set default pawn class to our Blueprinted character
	static ConstructorHelpers::FClassFinder<APawn> PlayerPawnClassFinder(TEXT("/Game/FirstPerson/Blueprints/BP_FirstPersonCharacter"));
	DefaultPawnClass = PlayerPawnClassFinder.Class;

}
