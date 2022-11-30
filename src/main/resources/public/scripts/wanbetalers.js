'use strict';

angular.module('klavertjevier-app').controller('WanbetalersController', function($scope, $http, $state) {
	$http({
		method : 'GET',
		url : '/rest/wanbetalers'
	}).then(function successCallback(response) {
		$scope.wanbetalers = response.data;
	}, function errorCallback(response) {
	});

	$scope.afrekenen = function(klant) {
		$state.go('afrekenen-state', {klantId: klant.id});
	}

});