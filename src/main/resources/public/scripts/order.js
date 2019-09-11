'use strict';

angular.module('klavertjevier-app').controller('OrderController', function($scope, $http) {
	$http({
		method : 'GET',
		url : '/rest/products'
	}).then(function successCallback(response) {
		$scope.producten = response.data;
	}, function errorCallback(response) {
	});

	$scope.orderLijnen = [];
	
	function round(value, places) {
	    var multiplier = Math.pow(10, places);
	    return (Math.round(value * multiplier) / multiplier);
	}
	
	$scope.productType = function(product) {
		return product.type;
	}
	
	$scope.verwijderOrderLijn = function(orderLijn) {
		$scope.orderLijnen.splice($scope.orderLijnen.indexOf(orderLijn), 1);
	}

	$scope.orderLijnTotaal = function(orderLijn) {
		return round(orderLijn.aantal * orderLijn.product.prijs, 2);
	}
	
	$scope.orderTotaal = function() {
		return round($scope.orderLijnen.map(function(orderLijn) {
			return $scope.orderLijnTotaal(orderLijn);
		}).reduce((a, b) => a + b, 0), 2);
	};
	
	$scope.afrekening = function() {
		var totaalOrder = $scope.orderTotaal();
		if ($scope.voorschot) {
			totaalOrder -= round($scope.voorschot, 2);
		}
		return round(totaalOrder, 2);

	}
	
	$scope.addProduct = function(select, event) {
		if (select.selected) {
			$scope.orderLijnen.push({product: select.selected, aantal : 1});
			select.clear({
				stopPropagation: function(){}
			});
			setTimeout(function() {
				$('input[autofocus]:last').focus();
			});
		}
	};
	
	$scope.saveOrder = function() {
		var order = {
			producten: {},
			voorschot: $scope.voorschot
		};
		$scope.orderLijnen.forEach(function(orderLijn) {
			order.producten[orderLijn.product.code] = orderLijn.aantal;
		});
		
		$http.post('/rest/order', order)
		   .then(function(response){
			   $scope.orderLijnen = []
			   $scope.voorschot = null;
			   $scope.afgedrukt = false;
		    },
		    function(response){});
	}
	
	$scope.onTextFocus = function ($event) {
	  $event.target.select();
	};
	
	$scope.orderLijnAantalKeydown = function($event) {
		if ($event.keyCode === 13) {
			angular.element(document.getElementById('new-order-lijn')).scope().$select.toggle($event);	
		}
	}
	
	$scope.afdrukken = function() {
		$scope.afgedrukt = true;
		window.print();
	}
});