'use strict';

angular.module('klavertjevier-app', ['ui.router', 'ui.select', 'ngSanitize'])
	.run(function () {
    })
    .config(function ($stateProvider, $urlRouterProvider) {
    	$urlRouterProvider.when('', '/').otherwise('/404');
    	
        $stateProvider.state('klavertjevier', {
            abstract: true
        });

    	$stateProvider.state('product-state', {
        	parent: 'klavertjevier',
        	url: '/',
        	views: {
        		'content@': {
        			templateUrl: './product.html'
        		}
        	}
        });
    	$stateProvider.state('klanten-state', {
    		parent: 'klavertjevier',
    		url: '/klanten',
    		views: {
    			'content@': {
    				templateUrl: './klant.html'
    			}
    		}
    	});
    	$stateProvider.state('overzicht-state', {
    		parent: 'klavertjevier',
    		url: '/overzicht',
    		views: {
    			'content@': {
    				templateUrl: './overzicht.html'
    			}
    		}
    	});
    	
    	$stateProvider.state('order-state', {
    		parent: 'klavertjevier',
    		url: '/order/:klantId',
    		views: {
    			'content@': {
    				templateUrl: './order.html'
    			}
    		}
    	});

    	$stateProvider.state('afrekenen-state', {
    		parent: 'klavertjevier',
    		url: '/afrekenen/:klantId',
    		views: {
    			'content@': {
    				templateUrl: './afrekenen.html'
    			}
    		}
    	});
 
    	$stateProvider.state('wanbetalers-state', {
    		parent: 'klavertjevier',
    		url: '/wanbetalers',
    		views: {
    			'content@': {
    				templateUrl: './wanbetalers.html'
    			}
    		}
    	});
});
