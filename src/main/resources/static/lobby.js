var $serverView = $(".tmb-server-view");
var $roomView = $(".tmb-room-view");
var $fPlayerName = $("#tmb-player-name");
var $fGameId = $("#tmb-game-id");
var $btnCreateGame = $("#tmb-create-game");
var $btnJoinGame = $("#tmb-join-game");
var $btnJoinServer = $("#tmb-connect-server");
var $btnJoinServer = $("#tmb-connect-server");
var $formConnectServer = $("#tmb-server-connect-form");
var $formConnectGame = $("#tmb-game-form");

var myUUID = null;
var playerName = null;
var stompClient = null;
var gameUUID = null;

$(function(){
    joinServer();
    listenButton();
});

function joinServer() {
    var socket = new SockJS('/timebomb');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        stompClient.subscribe('/user/server/joined', function (joined) {
            var info = JSON.parse(joined.body)
            myUUID = info.uuid;
            playerName = $fPlayerName.val();
            $formConnectServer.addClass("hidden");
            $formConnectGame.removeClass("hidden");
        });
        stompClient.subscribe('/user/server/created', function (created) {
            var info = JSON.parse(created.body)
            joinedRoom(info);
        });
        stompClient.subscribe('/user/server/joinedRoom', function (joinedRoom) {
            var info = JSON.parse(joinedRoom.body);
            if(info.success){
                joinedRoom(info);
            }
        });
    });
}

function joinedRoom(info){
    this.gameUUID = info.gameUUID;
    $serverView.addClass("hidden");
    $roomView.removeClass("hidden");
}

function connectServer(){
    var player = $fPlayerName.val();
    stompClient.send("/game/join", {}, JSON.stringify({'player': player}));
}

function joinGame(){
    var gameUUID = $fGameId.val();
    stompClient.send("/game/joinRoom", {}, JSON.stringify({'playerUUID': myUUID, "gameUUID": gameUUID}));
}

function createGame(){
    stompClient.send("/game/create", {}, JSON.stringify({'playerUUID': myUUID}));
}

function disconnect() {
    stompClient.send("/game/quit", {}, JSON.stringify({'uuid': myUUID}));
    stompClient.disconnect();
    console.log("disconnected");
}

function checkInputJoinServer(){
    var playerName = $fPlayerName.val();
    return (playerName != "" && playerName != null);
}

function checkInputJoinGame(){
    var gameId = $fGameId.val();
    return (gameId != "" && gameId != null);
}

function listenButton(){
    $btnCreateGame.click(function(e){
        createGame();
    });

    $btnJoinServer.click(function(e){
        if(checkInputJoinServer()){
            connectServer();
        }
        else{
            alert("Please enter your name to join the server.");
        }
    });

    $btnJoinGame.click(function(e){
        if(checkInputJoinGame()){
            joinGame();
        }
        else{
            alert("Please enter room ID to join the game.");
        }
    });

    $(window).bind("beforeunload", function(e) {
        disconnect();
    });
}
