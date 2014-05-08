<!DOCTYPE html>
<html>
	<head>
	<meta name="layout" content="main" />
	
	<title>Shaddy</title>
	
	<r:require module="application" />
	
	</head>
<body>
	
	<div class="container">

		<g:render template="/shared/alerts" />

		<div class="text-center col-md-6 col-md-offset-3 col-lg-6 col-lg-offset-3"
				ng-controller="MappingCtrl">
			<h1>Shorten your url</h1>

			<div id="result" collapse="currentMapping == null">
				<div class="row">
					<div id="resultUrl" class="alert alert-success slide" ng-cloak>
						<button ng-click="clearResult()" type="button" class="close"
							aria-hidden="true">&times;</button>
						<span id="resultText" sdy-select-on-change="currentMapping" ng-bind="currentMapping.shortUrl"></span>
					</div>
				</div>
				<sec:ifLoggedIn>
					<div id="loggedInOptions" class="row">
						<div class="col-lg-2 col-lg-offset-3">
							<div class="fb-share-button" data-href="https://developers.facebook.com/docs/plugins/" data-type="button"></div>
						</div>
						<div class="col-lg-2">
							<a twitter data-text="Check out this link ! {{currentMapping.shortUrl}}" data-url="{{currentMapping.shortUrl}}"></a>
						</div>
						<div id="edit" class="col-md-3 col-lg-3">
							<g:link controller="mapping" action="show" id="{{currentMapping.shortId}}">More options</g:link>
						</div>
					</div>
				</sec:ifLoggedIn>
				
		
				<sec:ifNotLoggedIn>
					<div id="notLoggedInOptions">
						<div class="well text-center">
							<div class="row">
							<h2><p>Make money with this url !<br/>Create an account ! 100% free !</p></h2>
							</div>
							<div class="row">
								<div class="col-lg-2 vCenterRow">
									<g:link class="btn btn-success lead" controller="user" action="create">Register</g:link>
								</div>
								<div class="col-lg-2 vCenterRow">
									or <g:link controller="mapping" action="show" id="{{currentMapping.shortId}}">login</g:link>
								</div>
							</div>
						</div>
					</div>
				</sec:ifNotLoggedIn>
			</div>
			<br/>
			<div id="valuesDiv" class="row">
				<form class="form" role="form" ng-submit="shortenUrl(longUrlText)">
					<div class="form-group">
						<label class="sr-only" for="">Long url</label> <input type="text"
							class="form-control text-center" id="longUrlText"
							ng-model="longUrlText" placeholder="Your url here...">
					</div>
					<div class="form-group">
						<button type="submit" class="btn btn-lg btn-success lead">Shorten
							me !</button>
					</div>
				</form>
			</div>
		</div>


	</div>

</body>

</html>
