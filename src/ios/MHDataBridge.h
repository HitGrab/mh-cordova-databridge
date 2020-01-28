#import <Cordova/CDV.h>

@interface MHDataBridge : CDVPlugin
{
    CDVInvokedUrlCommand * _rewardedVideoCommand;
}
- (void)fetch:(CDVInvokedUrlCommand*)command;
- (void)fetchMessages:(CDVInvokedUrlCommand*)command;

// Helpshift
- (void)install :(CDVInvokedUrlCommand*)command;
- (void) showFAQs:(CDVInvokedUrlCommand*)command;
- (void) showConversation:(CDVInvokedUrlCommand*)command;
- (void) setUserIdentifier:(CDVInvokedUrlCommand *)command;

@end
