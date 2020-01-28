#import <Cordova/CDV.h>

@interface MHDataBridge : CDVPlugin
{
    CDVInvokedUrlCommand * _rewardedVideoCommand;
}
- (void)fetch:(CDVInvokedUrlCommand*)command;
- (void)fetchMessages:(CDVInvokedUrlCommand*)command;

// Helpshift
- (void) helpshiftInstall :(CDVInvokedUrlCommand*)command;
- (void) helpshiftShowFAQs:(CDVInvokedUrlCommand*)command;
- (void) helpshiftShowConversation:(CDVInvokedUrlCommand*)command;
- (void) helpshiftLogin:(CDVInvokedUrlCommand *)command;
- (void) helpshiftLogout:(CDVInvokedUrlCommand*)command;

@end
