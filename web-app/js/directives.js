/* Directives */


var shortenerDirectives = angular.module('shortenerDirectives', []);

/**
 * Directive affectant les données "chart" présentes dans le scope
 */
shortenerDirectives.directive('sdyChart', 
	function () {
		return {
			restrict: 'A',
			link: function($scope, elm, attrs) {
				$scope.$watch('chart', function() {
					var chart = new google.visualization.LineChart(elm[0]);
					chart.draw($scope.chart.data, $scope.chart.options);
				},true);
			}
		};
	}
);

shortenerDirectives.directive('sdySelectOnChange', ['$timeout', function (timer) {
		return {
			restrict: 'A',
			link: function($scope, elm, attrs) {
				$scope.$watch(attrs.sdySelectOnChange, function() {
					
					// wrapped in a timeout to let DOM loading end
					// 0 just add it to the end of the queue
				    timer(function() {
						// select result url text for easy copy
						if (window.getSelection && document.createRange) {
					        selection = window.getSelection();
					        range = document.createRange();
					        var resultText = elm[0];
					        range.selectNodeContents(resultText);
					        selection.removeAllRanges();
					        selection.addRange(range);
					        resultText.focus();
					    } else if (document.selection && document.body.createTextRange) {
					        range = document.body.createTextRange();
					        var resultText = elm[0];
					        range.moveToElementText(resultText);
					        range.select();
					        resultText.focus();
					    }
				    }, 0);
				},true);
			}
		};
}]);
	

/**
 * Create a twitter share button
 */
shortenerDirectives.directive('twitter', [
     function() {
         return {
             link: function($scope, element, attr) {
            	 $scope.$watch("currentMapping", function() {
            		 var twittNode = element[0];
            		 while (twittNode.firstChild) {
            			 twittNode.removeChild(twittNode.firstChild);
            		 }
	                 setTimeout(function() {
	                         twttr.widgets.createShareButton(
	                             attr.url,
	                             element[0],
	                             function(el) {}, {
	                                 count: 'none',
	                                 text: attr.text
	                             }
	                         );
	                 });
            	 },true);
             }
         }
     }
 ]);
