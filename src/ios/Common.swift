extension YandexAdsPlugin {
    struct ErrorData: Encodable {
        var id: String?
        var message: String
    }
    
    public func sendResult(command: CDVInvokedUrlCommand, status: CDVCommandStatus) {
        var pluginResult = CDVPluginResult(
          status: status
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
            struct emptyObj: Encodable {}
            let encodedData = try JSONEncoder().encode(data)
                
            let js = "cordova.fireWindowEvent('\(event)', '\(data)')"
            
            self.commandDelegate.evalJs(js)
        } catch {
            print("\(#function) \(error)")
        }
    }
}
