'use strict';

angular.module('klavertjevier-app').controller('KlantenController', function($scope, $http, $state) {
	$scope.klant = {};
	$scope.klantZoeken = {};
	$scope.klanten = [];
	$scope.sort = "nummer";
	$scope.editKlant = null;
	
	$scope.edit = function(klant) {
		$scope.editKlant = klant;
	}

	$scope.save = function(klant) {
		klant.naam = $scope.editKlant.naam;
		$http.put('/rest/klant/' + klant.id, klant).then(function successCallback(response) {
			$scope.editKlant = null;
			$scope.klanten = sort($scope.klanten);
		}, function errorCallback(response) {
		});

	}
	
	function sort(klanten) {
		return klanten.sort(function(a, b){
			return a[$scope.sort].toLowerCase().localeCompare(b[$scope.sort].toLowerCase());
		})
	}
	
	$scope.$watch("sort", function(){
		$scope.klanten = sort($scope.klanten);
	});
	
	$scope.saveKlant = function() {
		$http.post('/rest/klant', $scope.klant)
		   .then(function(response){
		    	   $scope.klant = {};
		    	   $scope.klanten = sort([response.data]);
		       },
		       function(response){
		    	   alert('Klant bestaat al');
		       });
	}
	
	$scope.zoekKlant = function() {
		const searchUrl = new URL('/rest/klant', window.location.protocol + "//" + window.location.host);
		if ($scope.klantZoeken.naam) {
			searchUrl.searchParams.append('naam', $scope.klantZoeken.naam);
		}
		if ($scope.klantZoeken.nummer) {
			searchUrl.searchParams.append('nummer', $scope.klantZoeken.nummer);
		}
		$http.get(searchUrl.href)
		   .then(function(response){
		    	   $scope.klanten = sort(response.data);
		       },
		       function(response){
		    	   $scope.klanten = sort([]);
		       });
	}
	
	$scope.bestellingOpnemen = function(klant) {
		$state.go('order-state', {klantId: klant.id});
	}

	$scope.afrekenen = function(klant) {
		$state.go('afrekenen-state', {klantId: klant.id});
	}

	$scope.verwijderAlleKlanten = function(klant) {
		if (confirm("Bent u zeker dat u alle klanten wil verwijderen")) {
			$http({
				method : 'DELETE',
				url : '/rest/klanten'
			}).then(function successCallback(response) {
			}, function errorCallback(response) {
			});
		}
	}

	$scope.verwijderKlant = function(klant) {
		if (confirm("Bent u zeker dat u deze klant wil verwijderen?")) {
			$http({
				method : 'DELETE',
				url : '/rest/klant/' + klant.id
			}).then(function successCallback(response) {
				$scope.klanten.splice($scope.klanten.indexOf(klant), 1);
				$scope.klanten = sort($scope.klanten);
			}, function errorCallback(response) {
			});
		}
	}
});