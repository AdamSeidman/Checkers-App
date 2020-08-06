<!DOCTYPE html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
    <title>${title} | Web Checkers</title>
    <link rel="stylesheet" type="text/css" href="/css/style.css">
</head>
<body>
	<div class="page">
		<h1>Web Checkers</h1>

		<div class="navigation">
			<#if playerSignedIn>
                <a href="/signout">Sign Out [${currentPlayer.name}]</a>
                <span class="addedSep"> | </span>
			</#if>
			<a href="/">Return to home</a>
		</div>

		<div class="body">
			<#if playerMessage??>
				<h1>${playerMessage}</h1>
			</#if>
		</div>
	</div>
</body>
</html>
