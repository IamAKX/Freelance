app.controller('apiCtrl', function($window, $scope, $http, $mdDialog) {
    $scope.working = "working";
    $scope.baseurllocal = 'localhost:4000/api'
    $scope.baseurlglobal = 'http://139.59.68.188/api'
    $scope.view = true;

    function init() {
        $http.get('/api/apiData/allApi').then(function(data) {
            $scope.apis = data.data;
        });
    }
    init();
    $scope.saveApiModalOpen = function() {
        $mdDialog.show({
                controller: 'saveModalCtrl',
                templateUrl: 'modal/saveModal.html',
                parent: angular.element(document.body),
                clickOutsideToClose: true,
                locals: {
                    data: "--"
                }
            })
            .finally(function(result) {
                init();
            });
    };

    $scope.editApiModalOpen = function(api) {
        $mdDialog.show({
                controller: 'editModalCtrl',
                templateUrl: 'modal/editModal.html',
                parent: angular.element(document.body),
                clickOutsideToClose: true,
                locals: {
                    data: api
                }
            })
            .then(function(result) {
                if (result) {}
            });
    };
    $scope.delete = function(api, index) {
        var r = $window.confirm("Sure You want to remove");
        if (r === true) {
            $http.post('/api/apiData/deleteApi', api).then(function(data) {
                $scope.apis.splice(index, 1);
            });
        }
    };

    $http.get('/api/apiData/getLastDate').then(function(data) {
        $scope.updateDate = data.data[0].changeDate;
    });

    $scope.sort = function(orderValue) {
        $scope.reverse = ($scope.orderVal === orderValue) ? !$scope.reverse : false;
        $scope.orderVal = orderValue;
    }
    $scope.getTime = function(time) {
        // return 
        // console.log(new mongoose.Types.ObjectId(time));
    }
    $scope.changeView = function() {
        $scope.view = !$scope.view;
    }
});