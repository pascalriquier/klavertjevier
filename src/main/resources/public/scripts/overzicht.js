'use strict';

angular.module('klavertjevier-app').controller('OverzichtController', function($scope, $http) {
	
	$scope.totalen = [];
	$scope.orderDatums = [{value: '', text: 'Alle bestellingen'}];
	
	$http({
		method : 'GET',
		url : '/rest/orders/datums'
	}).then(function successCallback(response) {
		response.data.forEach(function(datum) {
			$scope.orderDatums.push({value: datum, text: 'Enkel bestellingen van ' + datum})
		});
	}, function errorCallback(response) {
	});
	
	$scope.datumSelected = function() {
		loadTotalen($scope.datum);
	}

	function loadTotalen(datum) {
		var url = '/rest/orders/totalenperproduct';
		if (angular.isDefined(datum) && datum != '') {
			url += "?datum=" + datum;
		}
		$http({
			method : 'GET',
			url : url
		}).then(function successCallback(response) {
			$scope.totalen = response.data;
		}, function errorCallback(response) {
		});
	}

	loadTotalen();

	$scope.verwijderOrders = function() {
		$http({
			method : 'DELETE',
			url : '/rest/orders'
		}).then(function successCallback(response) {
			loadTotalen($scope.datum);
		}, function errorCallback(response) {
		});

	}
});