var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#notifications").show();
    }
    else {
        $("#notifications").hide();
    }
    $("#messages").html("");
}

function connect() {
    var socket = new SockJS('/ws-notifications');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/library', function (notification) {
            showNotification(JSON.parse(notification.body));
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function showNotification(notification) {
    let message = `
    <tr><td>
        <div><b>To: ${notification.recipient} | Subject: ${notification.subject} | Notification created at: ${parseTime(notification.createdAt)}</b></div>
        <br>
        <div>${notification.message}</div>
    </td></tr>`;

    $("#messages").append(message);
}

function parseTime(dateString) {
  return new Date(Date.parse(dateString)).toLocaleTimeString();
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $("#connect").click(function() { connect(); });
    $("#disconnect").click(function() { disconnect(); });
});
