#import "MHDataBridge.h"
@import HelpshiftX;
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
//    NSString *apiKey = [command argumentAtIndex:0 ];
    NSString *domainName = [command argumentAtIndex:1 ];
    NSString *appId = [command argumentAtIndex:2 ];
    
    [Helpshift installWithPlatformId:appId
                                  domain:domainName
                                  config:[NSMutableDictionary dictionary]];
}

- (void) helpshiftShowFAQs:(CDVInvokedUrlCommand*)command {
    if([command.arguments count] > 0) {
        
        NSDictionary *myarg = [command.arguments objectAtIndex:0];
        
        NSDictionary *config = @{ @"customMetadata" : myarg };
        [Helpshift showFAQsWith:self.viewController config:config];
        
        
    } else {
        [Helpshift showFAQsWith:self.viewController config:[NSMutableDictionary dictionary]];
    }
}

- (void) helpshiftShowConversation:(CDVInvokedUrlCommand*)command {
    if([command.arguments count] > 0) {
        
        NSString *metaData = [command.arguments objectAtIndex:0 ];
//        NSString *prefillText = [command argumentAtIndex:1 ];
        
        NSMutableDictionary *config = [[NSMutableDictionary alloc] init];
        
        config[@"customMetadata"] = metaData;
        /*
         Pre-fill text currently unsupported by new HelpShift SDK X.
         Leaving commented to implement once future support arrives. (~parhamt)
         */
//        if ([prefillText length] != 0) {
//            config[@"conversationPrefillText"] = prefillText;
//        }
        
        [Helpshift showConversationWith:self.viewController config:config];
    } else {
        [Helpshift showConversationWith:self.viewController config:[NSMutableDictionary dictionary]];
    }
}

- (void) helpshiftLogin:(CDVInvokedUrlCommand *)command {
    NSString *userIdentifier = [command argumentAtIndex:0 ];
    
    NSDictionary *userDetails = @{ HelpshiftUserName:@"",
                                   HelpshiftUserEmail:@"",
                                   HelpshiftUserIdentifier:userIdentifier,
                                   HelpshiftUserAuthToken:@"" };
    
    [Helpshift loginUser:userDetails];
}

- (void) helpshiftLogout:(CDVInvokedUrlCommand*)command {
    [Helpshift logout];
}

- (void) registerDeviceToken:(CDVInvokedUrlCommand*)command {
    [Helpshift registerDeviceToken:[command argumentAtIndex:0 ]];
}

@end

