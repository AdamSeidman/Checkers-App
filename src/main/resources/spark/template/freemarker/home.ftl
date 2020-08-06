<!DOCTYPE html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
    <meta http-equiv="refresh" content="10">
    <title>${title} | Web Checkers</title>
    <link rel="stylesheet" type="text/css" href="/css/style.css">
</head>
<body>
  <div class="page">
    <h1>Web Checkers</h1>

    <div class="navigation">
		<#if playerSignedIn>
			<a href="/signout">Sign Out [${currentPlayer.name}]</a>
		<#else>
            <a href="/signin">sign in</a>
		</#if>
	</div>

    <div class="body">
        <#if playerMessage??>
           <p>${playerMessage}</p>
        </#if>

        <#if playerSignedIn>
			<p>Hello, ${currentPlayer.name}!</p>
        </#if>

        <p>Welcome to the world of online Checkers.</p>
        <#if playerSignedIn>
            <p> Click on a <b class="IS_OTHER">green</b> player to start a game with them. <br>
            Click on a player in game (<b class="IN_GAME">red</b>) to spectate their game. <br>
            Click on a spectator (<b class="IS_SPECTATOR">black</b>) to spectate the game they are watching. <br></p>
            <p>Current players signed in:</p>
			<#if playerList?size == 1>
                <p><em>(none)</em></p>
            </#if>
            <#list playerList as item>
                <#assign player = item.getKey()/>
                <#assign nameID = item.getValue()/>
				<#if player == currentPlayer.name>
                    <#continue>
				</#if>
				<p>
					<form name="${player}Form" method="post" action="startGame?opponent=${player}" class="inline">
						<input type="hidden" name="extra_submit_param" value="extra_submit_value">
							<button onclick="saveParam${player}()" type="submit" name="submit_param" value="submit_value" class="link-button">
							<span class="${nameID}"> [${player}] </span>
							</button>
							<#if expire_text?contains("!" + player + "!")><span class="offlineText"> (offline)</span></#if>
					</form>
				</p>
            </#list>
        <#else>
            <p>Current number of users online:</p>
            <p>${playerList?size}</p>
        </#if>
	</div>
</body>
</html>
