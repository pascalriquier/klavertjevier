'use strict';

angular.module('klavertjevier-app').controller('ProductController', function($scope, $http) {
	$http({
		method : 'GET',
		url : '/rest/products'
	}).then(function successCallback(response) {
		$scope.producten = response.data;
	}, function errorCallback(response) {
	});
	
	$scope.productTypes = ['DRANK', 'VOORGERECHT', 'HOOFDGERECHT_GROOT', 'HOOFDGERECHT_KLEIN', 'DESSERT'];
	$scope.product = {};
	
	$scope.saveProduct = function() {
		$http.post('/rest/product', $scope.product)
		   .then(function(response){
		    	   $scope.producten.push(response.data);
		    	   $scope.product = {};
		       },
		       function(response){});
	}
	
	$scope.verwijderProduct = function(product) {
		if (confirm("Bent u zeker dat u dit product wil verwijderen?")) {
			$http({
				method : 'DELETE',
				url : '/rest/product/' + product.code
			}).then(function successCallback(response) {
				$scope.producten.splice($scope.producten.indexOf(product), 1);
			}, function errorCallback(response) {
			});
		}
	}
	
	$scope.productenVanType = function(type) {
		if (!angular.isDefined($scope.producten)) {
			return [];
		}
		return $scope.producten.filter(function(product){
			return product.type == type;
		});
	}
});