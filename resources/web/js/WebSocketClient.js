var webSocket;
var keepAlive;

function webSocketInit() {
    new Promise(function(){
        var host = (("https:" === document.location.protocol) ? "wss://" : "ws://" + location.hostname + ":8080");
        webSocket = new WebSocket(host);

        webSocket.onopen = function () {
            keepAlive = setInterval(function () {
                webSocket.send("KeepAlive");
            }, 60000);
            $("#modelicon").css("background-color", "#00e676");
            $("#modetext").html("Server online")
        };

        webSocket.onclose = function () {
            clearInterval(keepAlive);

        }
    })
}