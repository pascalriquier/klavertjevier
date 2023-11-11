'use strict';

angular.module('klavertjevier-app').controller('AfrekenenController', function($scope, $http, $stateParams, $state) {
	$scope.orders = [];
	$scope.cashBetaaldMet = 0;
	$scope.cashTerugTeGeven = 0;
	
	$scope.$watch("cashBetaaldMet", function(newVal){
		$scope.cashTerugTeGeven = round($scope.cashBetaaldMet - $scope.totaalTeBetalen(), 2);
	})
	
	$http({
		method : 'GET',
		url : '/rest/klant/' + $stateParams.klantId
	}).then(function successCallback(response) {
		$scope.klant = response.data;
	}, function errorCallback(response) {
	});

	$http({
		method : 'GET',
		url : '/rest/klant/' + $stateParams.klantId + '/orders'
	}).then(function successCallback(response) {
		$scope.orders = response.data;
	}, function errorCallback(response) {
	});

	$scope.orderLijnTotaal = function(orderLijn) {
		return round(orderLijn.quantity * orderLijn.product.prijs, 2);
	}

	function round(value, places) {
	    var multiplier = Math.pow(10, places);
	    return (Math.round(value * multiplier) / multiplier);
	}

	$scope.orderTotaal = function(order) {
		return round(order.orderLijnen.map(function(orderLijn) {
			return $scope.orderLijnTotaal(orderLijn);
		}).reduce((a, b) => a + b, 0), 2);
	};
	
	$scope.totaalTeBetalen = function()  {
		var nietBetaaldNietGeannuleerd = round($scope.orders.filter(order => !order.verwerkt && !order.betaald && !order.geannuleerd).map(function(order) {
			return $scope.orderTotaal(order);
		}).reduce((a, b) => a + b, 0), 2);
		var terugTeBetalen = round($scope.orders.filter(order => !order.verwerkt && order.betaald && order.geannuleerd).map(function(order) {
			return $scope.orderTotaal(order);
		}).reduce((a, b) => a + b, 0), 2);
		
		return round(nietBetaaldNietGeannuleerd - terugTeBetalen, 2);
	}
	
	$scope.reedsBetaald = function()  {
		return round($scope.orders.filter(order => !order.verwerkt && order.betaald).map(function(order) {
			return $scope.orderTotaal(order);
		}).reduce((a, b) => a + b, 0), 2);
	}
	
	$scope.betaald = function() {
		$http({
			method : 'POST',
			url : '/rest/orders/betaald',
			data: {
				ids: $scope.orders.map(o => o.id)
			}
		}).then(function successCallback(response) {
			$state.go('klanten-state');
		}, function errorCallback(response) {
		});
	}

	$scope.cashVerwerkt = function() {
		$http({
			method : 'POST',
			url : '/rest/orders/verwerkt',
			data: {
				ids: $scope.orders.map(o => o.id),
				betaalWijze: 'CASH'
			}
		}).then(function successCallback(response) {
			$state.go('klanten-state');
		}, function errorCallback(response) {
		});
	}

	$scope.elektronischVerwerkt = function() {
		$http({
			method : 'POST',
			url : '/rest/orders/verwerkt',
			data: {
				ids: $scope.orders.map(o => o.id),
				betaalWijze: 'ELEKTRONISCH'
			}
		}).then(function successCallback(response) {
			$state.go('klanten-state');
		}, function errorCallback(response) {
		});
	}
	
	$scope.orderRechtzetten = function(order) {
		$http({
			method : 'POST',
			url : '/rest/orders/geannuleerd',
			data: order.id
		}).then(function successCallback(response) {
			$state.go('order-rechtzetting-state', {klantId: $scope.klant.id, orderId: order.id});
		}, function errorCallback(response) {
		});
	}
	
	$scope.print = function() {
		window.print();
	}

});