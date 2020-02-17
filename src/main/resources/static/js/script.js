function connect() {

	var socket = new SockJS('/chat-messaging');
	var sessionId = "";
	stompClient = Stomp.over(socket);
	stompClient.connect({}, function(frame) {
	       var url = stompClient.ws._transport.url;
	        console.log("Your current session is: " + url);
             url = url.replace(
               "ws://localhost:8080/chat-messaging/",  "");
             url = url.replace("/websocket", "");
             url = url.replace(/^[0-9]+\//, "");
             console.log("Your current session is: " + url);
             sessionId = url;
	console.log("connected: " + frame);

        stompClient.subscribe('/chat/onlineList', function(response) {
    			var data = JSON.parse(response.body);
    			$('.users').html("");
    			for (var key in data) {
                  drawUsr("left", data[key]);
                }
    		});
			stompClient.subscribe('/user/'+ sessionId +'/messages', function(response) {
        			var data = JSON.parse(response.body);
        			draw("left", data.time + " " + data.from + ": " +data.message);
        			document.getElementById('blockchat').scrollTop = 9999999;
        		});
		stompClient.subscribe('/chat/messages', function(response) {
    			var data = JSON.parse(response.body);
    			draw("left", data.time + " " + data.from + ": " +data.message);
    			document.getElementById('blockchat').scrollTop = 9999999;
    		});

	});
}

function draw(side, text) {
	console.log("drawing...");
    var $message;
    $message = $($('.message_template').clone().html());
    $message.addClass(side).find('.text').html(text);
    $('.messages').append($message);
    return setTimeout(function () {
        return $message.addClass('appeared');
    }, 0);

}

function drawUsr(side, text) {
	console.log("drawing...");
    var $message;
    $message = $($('.message_template').clone().html());
    $message.addClass(side).find('.text').html(text);
    $('.users').append($message);
    return setTimeout(function () {
        return $message.addClass('appeared');
    }, 0);
}


function sendMessage(){
	stompClient.send("/app/message", {}, JSON.stringify({'message': $("#message_input_value").val()}));

}