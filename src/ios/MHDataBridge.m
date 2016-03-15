#import "MHDataBridge.h"
#import <Cordova/CDV.h>

@implementation MHDataBridge

- (void)fetch:(CDVInvokedUrlCommand*)command
{
    
    NSLog(@"MHDataBridge fetch");
    
    CDVPluginResult* pluginResult = nil;
    
    NSMutableString* output;
    
    [output appendString:@"{"];
    [output appendString:@"isAppNative: true,"];
    [output appendString:@"operatingSystem: android,"];
    [output appendString:@"appStore: apple,"];
    
    NSString* tmp = [NSString stringWithFormat:@"clientVersion: '%@',", [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleVersion"]];
    [output appendString:tmp];
    
    #if (TARGET_IPHONE_SIMULATOR)
        [output appendString:@"isEmulator: true,"];
    #endif
    
    // create JS variable for number of application bootups
	int totalBootUps = (int)[[NSUserDefaults standardUserDefaults]integerForKey:@"TOTAL_BOOTUPS"];
	tmp = [NSString stringWithFormat:@"numNativeBootups: %d}",totalBootUps];
    [output appendString:tmp];
    
    if (output != nil && [output length] > 0) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:output];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}
NSMutableString *insert(NSString *head, NSString *middle, NSString *tail) {
    NSMutableString* output;
    [output appendString:head];
    [output appendString:middle];
    [output appendString:tail];
    return output;
}

@end
