app.controller('appEditCtrl', function($scope, $http, $mdDialog, data) {
    $scope.appData = data;
    $scope.updateAppData = function() {
        var appData = {
            appId: $scope.appData._id,
            name: $scope.appData.name,
            version: $scope.appData.version,
            comment: $scope.appData.comment
        }
        $http.post('/api/app/updateApp', appData).then(function(res) {
            $mdDialog.hide();
        })
    }
    $scope.close = function() {
        $mdDialog.hide();
    }
});