var app = angular.module("main", ['ngResource']);

app.factory('service', function ($resource) {
        return $resource('/task',
            {},
            {
                'execute': {method: 'POST'}
            }
        );
});


app.controller('mainController', function($scope, service){
    $scope.tasks = [
        {UUID : 123123123, executionTime : 15, localTime : Date.now(), status: 'COMPLETED'},
        {UUID : 123656, executionTime : 15, localTime : Date.now(), status: 'TERMINATED'},
        {UUID : 12786433123, executionTime : 15, localTime : Date.now(), status: 'DELETED'}
    ];

    $scope.model = {javascript:'js'};

    $scope.submitClick = function(){
        //alert($scope.model.javascript);
        service.execute($scope.model.javascript);
    }

    $scope.showTask = true;

    $scope.killTask = function(index){
        $scope.tasks[index].status = 'DELETED';
    }
});

