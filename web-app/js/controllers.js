
// load google charts api
google.load('visualization', '1', {packages:['corechart']});
//google.setOnLoadCallback(function() {
//	  angular.bootstrap(document.body, ['shortener']);
//	});


var shortenerControllers = angular.module("shortenerControllers", []);

//Main controller
shortenerControllers.controller("MainCtrl",
		function($scope, $rootScope, Mapping, Alert) {
			
			$rootScope.applicationName = "Shaddy";
			// store alerts in a single place, the $rootScope, accessed by service Alert
			$rootScope.alerts = [];
			$rootScope.alertTopDisplay = true;
			
			$scope.isCollapsed = true;
			$scope.currentMapping = null;
			
//			Mapping.getAllRetribution({},
//					function(data, headers){
//		                $scope.globalRetribution = data;
//					},
//					function(httpResponse){
//						Alert.addAlert({type: httpResponse.data.alert, content:httpResponse.data.message});
//					});
				
			
		}
);

shortenerControllers.controller("MappingCtrl",
		function($scope, GrailsNavigation, Mapping, Alert){
			
			$scope.currentMapping = null;
			$scope.currentMappingStats = null;
			
			$scope.shortenUrl = function(longUrl){
				Alert.clear();
				if($scope.longUrlText != null && $scope.longUrlText != undefined && $scope.longUrlText != ""){
					Mapping.create({target: $scope.longUrlText},
						function(data, headers){
							$scope.currentMapping = data.result;
							$scope.isCollapsed = false;
							
						},
						function(httpResponse){
							Alert.addAlert({type: httpResponse.data.alert, content:httpResponse.data.message}, 0);
						}
					);
				}
				else{
					Alert.addAlert({type: 'danger', content:'You must provide an url !'});
				}
			}
			
			$scope.clearResult = function(){
				$scope.currentMapping = null;
				$scope.longUrlText = '';
			}
			
			$scope.initDetailView = function(shortId){
				$scope.retrieveMapping(shortId);
				
				// mocked chart data 
				var data = google.visualization.arrayToDataTable([
                  ['Year', 'Clicks', 'Earning'],
                  ['2004', 10210, 400],
                  ['2005', 5150, 460],
                  ['2006', 21, 1120],
                  ['2007', 51051, 540]
                ]);
                var options = {
                  title: 'Url stats'
                };
                var chart = {};
                chart.data = data;
                chart.options = options;
                $scope.chart = chart;
			}
			
			/**
			 * Get all mappings of current user
			 */
			$scope.getList = function(){
				Mapping.retrieveAll({},
					function(data, headers){
						$scope.mappings = data.mappings; 
					},
					function(httpResponse){
						Alert.addAlert({type: httpResponse.data.alert, content:httpResponse.data.message});
					}
				);
			}
			
			/**
			 * Retrieve Mapping for given ShortId
			 */
			$scope.retrieveMapping = function(shortId){
				Mapping.retrieve({shortId:shortId},
					function(data, headers){
						$scope.currentMapping = data.mapping;
					},
					function(httpResponse){
						Alert.addAlert({type: httpResponse.data.alert, content:httpResponse.data.message});
					}
				);
			}
			
			/**
			 * Display Mapping of given ShortId
			 */
			$scope.showMapping = function(shortId){
				GrailsNavigation.navigateTo("mapping", "show", shortId);
			}
			
		}
		
);


shortenerControllers.controller("UserCtrl",
		function($scope, $modal, User, Mapping, Alert, Mapping) {
			
			
//			Mapping.getAllStats({},
//				function(data, headers){
//	                var chart = data;
//	                $scope.chart = chart;
//				},
//				function(httpResponse){
//					console.log('problem get all stats');
//					Alert.addAlert({type: httpResponse.data.alert, content:httpResponse.data.message});
//				});
			
			$scope.initUserEditView = function(){
				$scope.getUser();
			}
	
			$scope.initUserView = function(){
				$scope.getUser();
			}
	
			/**
			 * Get user and inject in scope
			 */
			$scope.getUser = function(){
				User.getUser({},
					function(data, headers){
						$scope.user = data.user;
					},
					function(httpResponse){
						Alert.addAlert({type: httpResponse.data.alert, content:httpResponse.data.message});
					});
			}
	
			$scope.register = function(email, password){
				if(email == undefined)
					email = null;
				if(password == undefined)
					password = null;
				User.register({email:email, pwd:password},
					function(data, headers){
						// redirect to main page
						Alert.addAlert({type: data.alert, content:data.message}, -1);
					},
					function(httpResponse){
						Alert.addAlert({type: httpResponse.data.alert, content:httpResponse.data.message});
					}
				);
			}
			
			$scope.updatePWD = function(current, newPWD, newPWDAgain){
				User.updatePWD({current:current, newPWD:newPWD, newPWDAgain:newPWDAgain},
					function(data, headers){
						// reset fields
						$scope.currentPWD = "";
						$scope.newPWD = "";
						$scope.newPWDAgain = "";
						
						if($scope.modalPWD != null){
							$scope.modalPWD.close("ok");
						}

						Alert.addAlert({type: data.alert, content:data.message});
					},
					function(httpResponse){
						Alert.addAlert({type: httpResponse.data.alert, content:httpResponse.data.message});
					});	
			}
			
			$scope.openPWDModal = function () {
				
				Alert.overrideDisplay(false);
				
				var ModalInstanceCtrl = function ($scope, $modalInstance) {
					
					$scope.ok = function (currentPWD, newPWD, newPWDAgain) {
						$scope.updatePWD(currentPWD, newPWD, newPWDAgain)
					};
					
					$scope.cancel = function () {
						$modalInstance.dismiss(false);
					};
				};

				$scope.modalPWD = $modal.open({
					templateUrl: '/partials/user/modal_PWDChange.html',
					controller: ModalInstanceCtrl,
					scope: $scope
				});
				
				$scope.modalPWD.result.then(
						function () {
							Alert.overrideDisplay(true);
						}, 
						function () {
							Alert.overrideDisplay(true);
						});
			};
		}
);

