'use strict';

angular.module('klavertjevier-app').service('ObjectUtil', function () {
	var findProperty = function(obj, path) {
	    var keys = path.split('.'), result = obj;
	    for (var i = 0, l = keys.length; i < l; i++) {
	        result = result[keys[i]];
	        if (result === undefined) {
	        	console.error('Object ' + JSON.stringify(obj) + ' does not have a property ' + path);
	        }
	        if (result == null) return result;
	    }
	    return result;
	}
	
	var findValue = function(obj, property) {
		if ($.isFunction(property)) {
			return property(obj);
		}
		else {
			return findProperty(obj, property);
		}
	};
	
	this.oneOfPropertiesMatches = function(obj, expr, properties) {
		for (var i = 0; i < properties.length; i++) {
			var value = findValue(obj, properties[i]);
			var matches = value && expr.test(value.toString());
			if (matches) return true;
		}
		return false;
    };
    
    function find(arr, predicate) {
    		for (var i=0; i < arr.length; i++) {
            if (predicate(arr[i])) {
                return arr[i];
            }
        }
    }
    
    this.unique = function(arr, comp) {
    		var result = [];
        $.each(arr, function(i,v){
        		var found = find(result, function(val){
        			if (comp) {
        				return comp(val, v);
        			} else {
        				return val == v;
        			}
        		});
            if (!found) 
            		result.push(v);
        });
        return result;
    };
    
}).filter('propsFilter', function (ObjectUtil, $filter) {
    return function (items, searchString, properties) {
		if (!angular.isArray(items)) {
			return items;
		}
		var escapedSearchString = searchString ? searchString.replace(/[.?*+^$[\]\\(){}|-]/g, "\\$&") : searchString;
		var expr = new RegExp(escapedSearchString, 'i');
		return $filter('filter')(items, function(item) {
			return ObjectUtil.oneOfPropertiesMatches(item, expr, properties);
		});
    }
});