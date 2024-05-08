// Copyright Epic Games, Inc. All Rights Reserved.

using UnrealBuildTool;

public class OpenPipeLL : ModuleRules
{
	public OpenPipeLL(ReadOnlyTargetRules Target) : base(Target)
	{
		PCHUsage = ModuleRules.PCHUsageMode.UseExplicitOrSharedPCHs;
		
		PublicIncludePaths.AddRange(
			new string[] {
				// ... add public include paths required here ...
			}
			);
				
		
		PrivateIncludePaths.AddRange(
			new string[] {
                "MediaAssets/Public", "MediaAssets/Classes" }
            );
			
		
		PublicDependencyModuleNames.AddRange(
			new string[]
			{
				"Core","MediaAssets",
				// ... add other public dependencies that you statically link with here ...
			}
			);
			
		
		PrivateDependencyModuleNames.AddRange(
			new string[]
			{
				"CoreUObject",
				"Engine",
				"Slate",
				"SlateCore",
				// ... add private dependencies that you statically link with here ...	
			}
			);
		
		
		DynamicallyLoadedModuleNames.AddRange(
			new string[]
			{
				// ... add any modules that your module loads dynamically here ...
			}
			);
        if (Target.Platform == UnrealTargetPlatform.Android)
        {
            AdditionalPropertiesForReceipt.Add("AndroidPlugin", ModuleDirectory + "\\OpenPipe_UPL.xml");
        }

    }
}
