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
		
		var aantalOrdersUrl = '/rest/orders/totalen';
		if (angular.isDefined(datum) && datum != '') {
			aantalOrdersUrl += "?datum=" + datum;
		}
		$http({
			method : 'GET',
			url : aantalOrdersUrl
		}).then(function successCallback(response) {
			$scope.aantalOrders = response.data;
		}, function errorCallback(response) {
		});
	}

	$http({
		method : 'GET',
		url : '/rest/products'
	}).then(function successCallback(response) {
		$scope.producten = response.data;
	}, function errorCallback(response) {
	}).then(function() {
		loadTotalen();
	});


	$scope.eenheidsPrijs = function(productCode) {
		return $scope.producten.find((product) => product.code == productCode).prijs;
	}

	$scope.naam = function(productCode) {
		return $scope.producten.find((product) => product.code == productCode).naam;
	}
	
	$scope.totaalBedragVerkocht = function(aantalVerkocht, productCode) {
		return $scope.eenheidsPrijs(productCode) * aantalVerkocht;
	}
	

	$scope.verwijderOrders = function() {
		if (confirm("Bent u zeker dat u alle orders wil verwijderen?")) {
			$http({
				method : 'DELETE',
				url : '/rest/orders'
			}).then(function successCallback(response) {
				loadTotalen($scope.datum);
			}, function errorCallback(response) {
			});
		}

	}
	
	$scope.exporteerLink = function() {
		var url = '/rest/orders/exporteer';
		if (angular.isDefined($scope.datum) && $scope.datum != '') {
			url += "?datum=" + $scope.datum;
		}
		return url;
	}
});