<!DOCTYPE html>
<html>
	<head>
		<g:render template="/shared/headConfig" />
		<g:render template="/shared/analytics" />
		<title>Shaddy</title>
		<r:require module="application"/>
		<script type="text/JavaScript">
		window.onload = function(){
			var timeout = 5;
			setTimeout("location.href = '${mapping.target}';",timeout * 1000);
			window.setInterval(function(){
				timeout--;
				document.getElementById("timeout").innerHTML= timeout + "s";
			}, 1000);
			};
		</script>
	</head>
	<body>
		<center>
		<a href="${mapping.target}"><h1>REDIRECTING TO<br/>${mapping.target}<br/>IN <span id="timeout"></span></h1></a>
		<div>^^ Click above if it doesn't work :-S^^</div>
		</center>
	</body>
</html>
