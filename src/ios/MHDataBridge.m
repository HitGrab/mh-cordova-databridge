#import "MHDataBridge.h"
#import "HelpshiftCore.h"
#import "HelpshiftSupport.h"
#import <Cordova/CDV.h>

@implementation MHDataBridge

- (void)fetch:(CDVInvokedUrlCommand*)command
{
    
    CDVPluginResult* pluginResult = nil;
    
    NSMutableString* output = [NSMutableString stringWithCapacity:2048];
    
    [output appendString:@"{"];
    [output appendString:@"\"isAppNative\": true,"];
    [output appendString:@"\"operatingSystem\": \"ios\","];
    [output appendString:@"\"appStore\": \"apple\","];
    
    NSString* tmp = [NSString stringWithFormat:@"\"clientVersion\": \"%@\",", [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleVersion"]];
    [output appendString:tmp];
    
#if (TARGET_IPHONE_SIMULATOR)
    [output appendString:@"\"isEmulator\": true,"];
#endif
    
    // create JS variable for number of application bootups
    int totalBootUps = (int)[[NSUserDefaults standardUserDefaults]integerForKey:@"TOTAL_BOOTUPS"];
    tmp = [NSString stringWithFormat:@"\"numNativeBootups\": %d}",totalBootUps];
    [output appendString:tmp];
    
    //if (output != nil && [output length] > 0) {
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:output];
    //} else {
    //pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    //}
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)fetchMessages:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = nil;
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)seedWidget: (CDVInvokedUrlCommand*)command {
    NSString* data = [command.arguments objectAtIndex:0];
    
    NSUserDefaults *sharedDefaults = [[NSUserDefaults alloc] initWithSuiteName:@"group.hitgrab.HunterHornSharingDefaults"];
    
    [sharedDefaults setObject:data forKey:@"data"];
    [sharedDefaults synchronize];   // (!!) This is crucial.
}

// Helpshift functions

-(void)helpshiftInstall:(CDVInvokedUrlCommand*)command {
    NSString *apiKey = [command argumentAtIndex:0 ];
    NSString *domainName = [command argumentAtIndex:1 ];
    NSString *appId = [command argumentAtIndex:2 ];
    
    [HelpshiftCore initializeWithProvider:[HelpshiftSupport sharedInstance]];
    HelpshiftInstallConfigBuilder *builder = [[HelpshiftInstallConfigBuilder alloc] init];
    builder.enableAutomaticThemeSwitching = YES;
    [HelpshiftCore installForApiKey:apiKey
                        domainName:domainName
                        appID:appId
                        withConfig:builder.build];
}

- (void) helpshiftShowFAQs:(CDVInvokedUrlCommand*)command {
    if([command.arguments count] > 0) {
        NSMutableDictionary *optionsDict = [[NSMutableDictionary alloc] init];
        optionsDict = [command argumentAtIndex:0 ];
        
        // Add custom metadata
        HelpshiftAPIConfigBuilder *builder = [[HelpshiftAPIConfigBuilder alloc] init];
        builder.customMetaData = [[HelpshiftSupportMetaData alloc] initWithMetaData:optionsDict];
        HelpshiftAPIConfig *apiConfig = [builder build];
        
        [HelpshiftSupport showFAQs:self.viewController withConfig:apiConfig];
    } else {
        [HelpshiftSupport showFAQs:self.viewController withConfig:nil];
    }
}

- (void) helpshiftShowConversation:(CDVInvokedUrlCommand*)command {
    if([command.arguments count] > 0) {
        NSMutableDictionary *optionsDict = [[NSMutableDictionary alloc] init];
        optionsDict = [command argumentAtIndex:0 ];

        // Add custom metadata
        HelpshiftAPIConfigBuilder *builder = [[HelpshiftAPIConfigBuilder alloc] init];
        builder.customMetaData = [[HelpshiftSupportMetaData alloc] initWithMetaData:optionsDict];
        HelpshiftAPIConfig *apiConfig = [builder build];

        [HelpshiftSupport showConversation:self.viewController withConfig:apiConfig];
    } else {
        [HelpshiftSupport showConversation:self.viewController withConfig:nil];
    }
}

- (void) helpshiftLogin:(CDVInvokedUrlCommand *)command {
    NSString *userIdentifier = [command argumentAtIndex:0 ];
    
    HelpshiftUserBuilder *userBuilder = [[HelpshiftUserBuilder alloc] initWithIdentifier:userIdentifier andEmail:@""];
    HelpshiftUser *user = userBuilder.build;
    [HelpshiftCore login:user];
}

- (void) helpshiftLogout:(CDVInvokedUrlCommand*)command {
    [HelpshiftCore logout];
}

- (void) registerDeviceToken:(CDVInvokedUrlCommand*)command {
    [HelpshiftCore registerDeviceToken:[command argumentAtIndex:0 ]];
}

@end

