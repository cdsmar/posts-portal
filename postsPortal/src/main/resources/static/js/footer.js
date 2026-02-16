document.addEventListener("DOMContentLoaded", function () {
    if (typeof LOGGED_IN_USER_ID === 'undefined' || LOGGED_IN_USER_ID === 0) {
        console.warn("No logged in user ID found, notifications disabled.");
        return;
    }

    var loggedInUserId = LOGGED_IN_USER_ID;

    var socket = new SockJS('/ws');
    var stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        console.log('Connected to WebSocket: ' + frame);

        stompClient.subscribe('/topic/notifications/' + loggedInUserId, function (notification) {
            var data = JSON.parse(notification.body);
            alert("New message from " + data.fromUser + ": " + data.message);
        });
    });
});