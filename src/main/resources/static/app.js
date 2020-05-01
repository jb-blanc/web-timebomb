var stompClient = null;
var playerName = null;
var players = [];
var myTurn = false;
var turnEnded = false;
var gameStarted = false;
var gameEnded = false;
var myTeam = null;

var playSoundTurn = false;
var playSoundExplosion = false;
var playSoundVictory = false;

var soundYourTurn = new Audio('/sounds/yourturn.mp3');
var soundExplosion = new Audio('/sounds/bomb.mp3');
var soundVictory = new Audio('/sounds/victory.mp3');

function setConnected(connected) {
    $("#send").prop("disabled", connected);
    if (connected) {
        $("#tmb-game").show();
    }
    else {
        $("#tmb-game").hide();
    }
}

function connect() {
    var socket = new SockJS('/timebomb');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        stompClient.subscribe('/user/server/joined', function (playerInfo) {
            var info = JSON.parse(playerInfo.body)
            if(info.connected){
                registerStompRoutes();
                setConnected(true);
                updatePlayers();
            }
            else{
                window.alert("You can't join during a game.");
                setConnected(false);
            }
        });
    });
}

function registerStompRoutes(){
    stompClient.subscribe('/server/players', function (playerInfo) {
        var info = JSON.parse(playerInfo.body)
        playerList(info);
    });

    stompClient.subscribe('/server/player', function (playerInfo) {
        var info = JSON.parse(playerInfo.body)
        playerDisconnect(info.name)
    });

    stompClient.subscribe('/server/started', function(gameState){
        var info = JSON.parse(gameState.body);
        transformGameStateInfo(info);
        getAllCards();
    });

    stompClient.subscribe('/server/newRound', function(gameState){
        var info = JSON.parse(gameState.body);
        transformGameStateInfo(info);
        getAllCards();
    });

    stompClient.subscribe('/user/server/playerCard', function(playerCardInfo){
        var info = JSON.parse(playerCardInfo.body)
        gameStarted = true;
        setMyTeam(info.myTeam);
        transformGameStateInfo(info.gameState);
        drawMyCards(info.player);
        drawOpponentCards(info.opponents);
    });

    stompClient.subscribe('/user/server/reveal', function(revealInfo){
        var info = JSON.parse(revealInfo.body)
        drawOpponentCards(info.playersCards);
        displayReveal(info.roles);
    });

    stompClient.subscribe('/server/state', function (gameState){
        var info = JSON.parse(gameState.body);
        transformGameStateInfo(info);
    });
}

function cutOpponentCard(opponent, id){
    stompClient.send("/game/cut", {}, JSON.stringify({'player': opponent, 'id': id}));
}

function getAllCards(){
    stompClient.send("/game/playersCards", {}, this.playerName);
}

var startGame = function(){
    stompClient.send("/game/start", {}, JSON.stringify({'playerName': this.playerName}));
}

var updatePlayers = function(){
    stompClient.send("/game/updatePlayers", {}, JSON.stringify({'playerName': this.playerName}));
}

var nextRound = function(){
    if(myTurn){
        stompClient.send("/game/nextRound", {}, JSON.stringify({'playerName': this.playerName}));
    }
}

var askReveal = function(){
    if(gameEnded){
        stompClient.send("/game/askReveal", {}, JSON.stringify({'playerName': this.playerName}));
    }
}

function disconnect() {
    stompClient.send("/game/quit", {}, JSON.stringify({'playerName': this.playerName}));
    stompClient.disconnect();
    setConnected(false);
}

function connectToGame() {
    this.playerName = $("#name").val();
    stompClient.send("/game/join", {}, JSON.stringify({'playerName': this.playerName}));
}

function setMyTeam(team){
    if(myTeam != team){
        myTeam = team;
        $("#tmb-side-card").removeClass("hidden");
        $("#tmb-side-card").removeClass("tmb-MORIARTY tmb-SHERLOCK");
        $("#tmb-side-card").addClass("tmb-"+team);
    }

    $("#tmb-youwin").addClass("hidden");
    $("#tmb-youloose").addClass("hidden");
}

function transformGameStateInfo(info){
    myTurn = info.currentPlayer === playerName;
    turnEnded = info.turnEnded;

    if(myTurn){
        $("#tmb-yourturn").removeClass("hidden");
        playSoundTurn = true;
    }
    else{
        $("#tmb-yourturn").addClass("hidden");
    }

    if(info.gameEnded && !gameEnded){
        gameEnded = info.gameEnded;
        askReveal(playerName);
        if(info.winner){
            if(info.winner == "SHERLOCK"){
                playSoundVictory = true;
            }
            else{
                playSoundExplosion = true;
            }

            if(info.winner == myTeam){
                $("#tmb-youwin").removeClass("hidden");
                $("#tmb-yourturn").addClass("hidden");
            }
            else{
                $("#tmb-youloose").removeClass("hidden");
                $("#tmb-yourturn").addClass("hidden");
            }
        }
    }

    gameEnded = info.gameEnded;

    displayGameInfos(info);

    if(info.cut){
        cardCut(info.cut);
    }

    if(gameEnded){
        gameStarted = false;
    }

    if(turnEnded && !gameEnded){
        $("#tmb-nextRoundBtn").removeClass("disabled");
    }
    else{
        $("#tmb-nextRoundBtn").addClass("disabled");
    }

    playSounds();
}

function playSounds(){
    if(playSoundExplosion){
        soundExplosion.play();
    }
    else if(playSoundVictory){
        soundVictory.play();
    }
    else if(playSoundTurn){
        soundYourTurn.play();
    }
    playSoundVictory = false;
    playSoundTurn = false;
    playSoundExplosion = false;
}

function displayReveal(info){
    if(info && gameEnded){
        for(let [player,team] of Object.entries(info)){
            $("#tmb-cards-"+player).append('<div class="tmb-reveal-'+player+' tmb-card tmb-'+team+'"></div>');
        }
    }
}

function displayGameInfos(infos){
    $("#tmb-labelCurrentPlayer").html(infos.currentPlayer);
    $("#tmb-labelRemainingRounds").html(infos.roundRemaining);
    $("#tmb-labelRemainingCuts").html(infos.cutRemaining);
    $("#tmb-labelWinner").html(infos.winner);
    $("#tmb-labelDefusedWires").html(infos.defusedWires);
    $("#tmb-labelRemainingWires").html(infos.remainingWires);
}

function cardCut(info){
    if(info.name == playerName){
        $("#tmb-cards").find(".tmb-my-card[data-cardid='"+info.id+"']").addClass("tmb-cut");
    }
    else{
        var c = $(".tmb-card-"+info.name+"[data-cardid='"+info.id+"']");
        c.data("type",info.type);
        c.removeClass("tmb-NOTVISIBLE")
        c.addClass("tmb-"+info.type)
    }
}

function drawMyCards(cards){
    var html = "";
    cards = shuffle(cards);
    for(var i=0;i<cards.length;i++){
        var c = cards[i];
        html += '<div class="tmb-card tmb-my-card tmb-'+c.type+' '+(c.cut == true ? 'tmb-cut' : '')+'" data-cardid="'+c.id+'"></div>'
    }
    $("#tmb-cards").html(html);
}

function drawOpponentCards(opponents){
    opponents.forEach(o => {
        if(o.name != playerName){
            var html = "";
            var cards = shuffle(o.cards)
            for(var i=0;i<cards.length;i++){
                var c = cards[i];
                html += '<div class="tmb-card tmb-opponent-card tmb-card-'+o.name+' tmb-'+c.type+'" data-type='+c.type+' data-cardid="'+c.id+'" data-player="'+o.name+'"></div>'
            }
            $("#tmb-cards-"+o.name).html(html);
            $("#tmb-cards-"+o.name+" .tmb-card").click(e => {
                if(myTurn && !turnEnded && !gameEnded){
                    var player = $(e.target).data("player");
                    var id = $(e.target).data("cardid");
                    var type = $(e.target).data("type");
                    if(type == "NOTVISIBLE"){
                        cutOpponentCard(player, id);
                    }
                }
            });
        }
    });
}

function playerPresent(player){
    for(i=0;i<players.length;i++){
        if(players[i].name == player){
            return true;
        }
    }
    return false;
}

function playerList(info){
    info.forEach(player => {
        if(!playerPresent(player.name)){
            playerConnect(player.name, player.name != playerName)
        }
    });
}

function playerConnect(player, addView){
    var pObj = {
       name: player,
       cardsAvailable: [],
       cardsCut: []
    };

    players.push(pObj);
    updatePlayerCount();
    addPlayerToList(pObj);
    if(addView){
        addPlayerView(pObj);
    }
}

function addPlayerToList(player){
    $("#tmb-playerList").append('<li id="tmb-list-'+player.name+'">'+player.name+'</li>')
}

function updatePlayerCount(){
    $("#tmb-playerCount").html(players.length);
}

function playerDisconnect(player){
    var remove = -1;
    for(i=0;i<players.length;i++){
        if(players[i].name === player){
            remove = i;
            break;
        }
    }
    if(remove > -1){
        players.splice(i,1);
    }
    $("#tmb-view-"+player).remove();
    $("#tmb-list-"+player).remove();
}

function addPlayerView(player){
    //view-player
    //cards-player
    $("#tmb-opponent-views").append(
        '<div id="tmb-view-'+player.name+'" class="col-6 tmb-opponent-view tmb-player">' +
        '<h4>'+player.name+' cards</h4>'+
        '<div id="tmb-cards-'+player.name+'" class="tmb-opponent-cards">'+
        '</div></div>');
}

function shuffle(array){
    var i = array.length-1
    for(i;i>0;i--){
      var j = Math.floor(Math.random() * i);
      var temp = array[i];
      array[i] = array[j];
      array[j] = temp;
    }
    return array;
}


$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { connectToGame(); });
    connect();
    $( "#tmb-startBtn" ).click(startGame);
    $( "#tmb-nextRoundBtn").click(nextRound);
    $(window).bind("beforeunload", function(e) {
        disconnect();
    });
});