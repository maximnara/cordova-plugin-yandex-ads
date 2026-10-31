import Foundation

extension YandexAdsPlugin {
    struct ErrorData: Encodable {
        var id: String?
        var message: String
    }
    
    public func sendResult(command: CDVInvokedUrlCommand) {
        let pluginResult = CDVPluginResult(
          status: CDVCommandStatus_OK
        )

        self.commandDelegate!.send(
            pluginResult,
            callbackId: command.callbackId
        )
    }

    public func sendError(command: CDVInvokedUrlCommand, code: String, message: String) {
        let errorObj = [
            "code": code,
            "message": message,
            "data": [
                "methodName": command.methodName,
            ]
        ] as [String : Any];

        let pluginResult = CDVPluginResult(
          status: CDVCommandStatus_ERROR,
          messageAs: errorObj
        )
        
        self.commandDelegate!.send(
            pluginResult,
            callbackId: command.callbackId
        )
    }
    
    public func emitWindowEvent(event: String) {
        let js = "cordova.fireWindowEvent('\(event)')"
        
        self.commandDelegate.evalJs(js)
    }
    
    public func emitWindowEvent(event: String, data: Encodable) {
        do {
            let encodedData = try JSONEncoder().encode(data)
            let json = String(decoding: encodedData, as: UTF8.self)

            let js = "cordova.fireWindowEvent('\(event)', \(json))"
            
            self.commandDelegate.evalJs(js)
        } catch {
            print("\(#function) \(error)")
        }
    }
}
