let stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    } else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    const socket = new SockJS('/socket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);

        const unitIdVal = $("#unitId").val();

        // Note the "/topic" prefix
        stompClient.subscribe('/topic/unit.' + unitIdVal, function (greeting) {
            showGreeting(JSON.parse(greeting.body).content);
        });

        // Note the "/app" prefix
        stompClient.subscribe('/app/unit.updates.' + unitIdVal, function (greeting) {
            showGreeting(JSON.parse(greeting.body).content);
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

function sendName() {
    let unitIdVal = $("#unitId").val();
    let nameVal = $("#name").val();
    let body = JSON.stringify({'name': nameVal});

    // Note the "/app" prefix
    stompClient.send('/app/unit.' + unitIdVal, {}, body);
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $("#connect").click(function () {
        connect();
    });
    $("#disconnect").click(function () {
        disconnect();
    });
    $("#send").click(function () {
        sendName();
    });
});
